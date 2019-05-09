# Meet PerfRepo

Performance result repository (PerfRepo) is a web application tool that intends to make it easy to capture and archive performance test results and simplify comparison of performance run results and detect a performance regression from one build to the next in an automated fashion.

Get involved by entering bugs, feature requests etc into our JIRA: https://issues.jboss.org/browse/PERFREPO/

## Features

More detailed Wiki page on PerfRepo features is coming soon.

* track performance result
* sort them by tags, tests etc.
* generate history charts, groupped metric charts
* compare builds 
* ... and much more

# Try it out

Here we describe process how to set up your own PerfRepo instance.

## Prerequisites

List of suggested components to be able to run PerfRepo easily. Possibly other application servers or databases can be used, but this combination is used and tested.

* Java 8 
* Maven 3+ (http://maven.apache.org/)
* WildFly 9.0.1+ (http://wildfly.org/downloads/)
* PostgreSQL 9+ (http://www.postgresql.org/)

## Configure Maven

Configure Maven to use JBoss Nexus repository. Follow the steps at https://developer.jboss.org/wiki/MavenGettingStarted-Users in section "Configuring Maven to use the JBoss Repository".

## Set up the database

1. Create a database (e.g. named `perfrepo`)
2. Script `db_schema_creation.sql` in `model/src/main/sql` creates all necessary tables and structures

## Set up the application server

Following text assumes PostgreSQL installed on localhost and WildFly's `standalone/configuration/standalone.xml`. 

* Before configuring the datasource, you have to install JDBC driver for PostgreSQL. You can follow first three steps (create a directory in /modules, downloading driver, creating modules.xml) for example here: https://gesker.wordpress.com/2016/02/09/adding-postgresql-9-5-datasources-to-wildfly-10/ 

* Add PostgreSQL datasource driver, e.g.
```xml    
    <drivers>
        <driver name="postgresql" module="org.postgresql">
            <xa-datasource-class>org.postgresql.xa.PGXADataSource</xa-datasource-class>
        </driver>        
    </drivers>
```

* Add datasource `PerfRepoDS` pointing to the database, e.g.
```xml
    <datasource jndi-name="java:jboss/datasources/PerfRepoDS" pool-name="PerfRepoDS" enabled="true" use-java-context="true">
    	<connection-url>jdbc:postgresql://localhost:5432/perfrepo</connection-url>
        <driver-class>org.postgresql.Driver</driver-class>
        <driver>postgresql</driver>
        <security>
            <user-name>perfrepo</user-name>
            <password>perfrepo</password>
        </security>        
    </datasource>
    <drivers>...(described above)...</drivers>
```

* Add security domain `perfrepo`, e.g.
```xml
    <security-domain name="perfrepo" cache-type="default">
        <authentication>
            <login-module code="Database" flag="required">
                <module-option name="dsJndiName" value="java:jboss/datasources/PerfRepoDS"/>
                <module-option name="principalsQuery" value="select password from public.user where username = ?"/>
                <module-option name="rolesQuery" value="select g.name, 'Roles'  from public.user u, public.user_group ug, public.group g where u.username = ? and ug.user_id=u.id and ug.group_id=g.id"/>
                <module-option name="hashUserPassword" value="true"/>
                <module-option name="hashAlgorithm" value="MD5"/>
                <module-option name="hashEncoding" value="base64"/>
            </login-module>
        </authentication>
    </security-domain>
```
Note: [WildFly Elyton](https://docs.jboss.org/author/display/WFLY/WildFly+Elytron+Security) is a new security framework to WildFly, which is by default used since WildFly 11.
Older JAAS/PicketLink configuration can be still used, but you have to place it under `<subsystem xmlns="urn:jboss:domain:security:2.0">` subsystem (you don't have to create it, it's alreadt present in default config).

* Add SMTP server configuration for alerting
```xml
    <subsystem xmlns="urn:jboss:domain:mail:2.0">
        <mail-session name="java:jboss/mail/perfreposmtp" jndi-name="java:jboss/mail/perfreposmtp" from="<set the FROM header here if wanted>">
            <smtp-server outbound-socket-binding-ref="mail-smtp"/>
        </mail-session>
    </subsystem>
    
    and add this into <socket-binding-group> tag
    
    <outbound-socket-binding name="mail-smtp">
        <remote-destination host="<your SMTP host>" port="25"/>
    </outbound-socket-binding>
```

* Start server
* Install PerfRepo using Maven, e.g. run `mvn clean install -DskipTests` in the root directory of PerfRepo
* Deploy the WAR, i.e. go to `web` directory and run `mvn clean package wildfly:deploy -DskipTests`
* PerfRepo should be running on the `/` URL. For testing purposes, `db_schema_creation.sql` creates a default user
	* login: `perfrepouser`
	* password: `perfrepouser1.`

* If you need more users, you have to add them manually into database.
```
insert into public.user (id, username, first_name, last_name, email, password) values (nextVal('user_sequence'), 'userName', 'first', 'last', 'email', encode(decode(md5('password'),'hex'),'base64'));
insert into user_group (user_id, group_id) values ((select id from public.user where username='userName'), (select id from public.group where name='perfrepouser'));
```

# Running tests

Integration tests uses Arquillian WildFly remote container, which means you have to have WidlFly running on your test machine and listening on default ports.
Also you have to add test datasource `PerfRepoTestDS`into the WildFly, ideally pointing to test DB, e.g.
```xml
                <datasource jndi-name="java:jboss/datasources/PerfRepoTestDS" pool-name="PerfRepoTestDS" enabled="true" use-java-context="true">
                    <connection-url>jdbc:postgresql://localhost:5432/perfrepotest</connection-url>
                    <driver-class>org.postgresql.Driver</driver-class>
                    <driver>postgresql</driver>
                    <security>
                        <user-name>perfrepo</user-name>
                        <password>perfrepo</password>
                    </security>
                </datasource>
```

For testing session bean, authentication is require and thus you have to also add appropriate security domain:
```xml
                <security-domain name="Arquillian-Testing" cache-type="default">
                    <authentication>
                        <login-module code="UsersRoles" flag="required">
                            <module-option name="usersProperties" value="users.properties"/>
                            <module-option name="rolesProperties" value="roles.properties"/>
                        </login-module>
                    </authentication>
                </security-domain>
```
