/**
 * PerfRepo
 * <p>
 * Copyright (C) 2015 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.perfrepo.test;

import javax.security.auth.Subject;
import javax.security.auth.callback.*;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * From https://github.com/sfcoy/demos/blob/master/arquillian-security-demo/src/test/java/org/jboss/
 * arquillian/secureejb/JBossLoginContextFactory.java
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public class JBossLoginContextFactory {

   static class NamePasswordCallbackHandler implements CallbackHandler {
      private final String username;
      private final String password;

      private NamePasswordCallbackHandler(String username, String password) {
         this.username = username;
         this.password = password;
      }

      public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
         for (Callback current : callbacks) {
            if (current instanceof NameCallback) {
               ((NameCallback) current).setName(username);
            } else if (current instanceof PasswordCallback) {
               ((PasswordCallback) current).setPassword(password.toCharArray());
            } else {
               throw new UnsupportedCallbackException(current);
            }
         }
      }
   }

   static class JBossJaasConfiguration extends Configuration {
      private final String configurationName;

      JBossJaasConfiguration(String configurationName) {
         this.configurationName = configurationName;
      }

      @Override
      public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
         if (!configurationName.equals(name)) {
            throw new IllegalArgumentException("Unexpected configuration name '" + name + "'");
         }

         return new AppConfigurationEntry[]{

             createUsersRolesLoginModuleConfigEntry(),

             createClientLoginModuleConfigEntry(),
         };
      }

      /**
       * The {@link org.jboss.security.auth.spi.UsersRolesLoginModule} creates the association
       * between users and roles.
       *
       * @return
       */
      private AppConfigurationEntry createUsersRolesLoginModuleConfigEntry() {
         Map<String, String> options = new HashMap<String, String>();
         return new AppConfigurationEntry("org.jboss.security.auth.spi.UsersRolesLoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);
      }

      /**
       * The {@link org.jboss.security.ClientLoginModule} associates the user credentials with the
       * {@link org.jboss.security.SecurityContext} where the JBoss security runtime can find it.
       *
       * @return
       */
      private AppConfigurationEntry createClientLoginModuleConfigEntry() {
         Map<String, String> options = new HashMap<String, String>();
         options.put("multi-threaded", "true");
         options.put("restore-login-identity", "true");

         return new AppConfigurationEntry("org.jboss.security.ClientLoginModule", AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options);
      }
   }

   /**
    * Obtain a LoginContext configured for use with the ClientLoginModule.
    *
    * @return the configured LoginContext.
    */
   public static LoginContext createLoginContext(final String username, final String password) throws LoginException {
      final String configurationName = "Arquillian Testing";

      CallbackHandler cbh = new JBossLoginContextFactory.NamePasswordCallbackHandler(username, password);
      Configuration config = new JBossJaasConfiguration(configurationName);

      return new LoginContext(configurationName, new Subject(), cbh, config);
   }
}