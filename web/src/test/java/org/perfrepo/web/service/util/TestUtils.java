package org.perfrepo.web.service.util;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.io.File;

/**
 * TODO: document this
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TestUtils {

    public static WebArchive createDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");

        // we need to exclude the actual UserSessionBean and substitute it with test one
        war.addPackages(true, Filters.exclude(".*UserSessionBean.*"),"org.perfrepo.web");
        war.addClass(UserSessionMock.class);
        war.addPackages(true, "org.perfrepo.model");

        war.addAsLibraries(Maven.resolver().resolve("commons-codec:commons-codec:1.9").withTransitivity().asFile());
        war.addAsLibraries(Maven.resolver().resolve("org.antlr:antlr:3.5.2").withTransitivity().asFile());
        war.addAsLibraries(Maven.resolver().resolve("org.apache.maven:maven-artifact:3.0.3").withTransitivity().asFile());

        war.addAsResource("test-persistence.xml", "META-INF/persistence.xml");
        war.addAsResource(new File("src/main/resources/lang"));

        war.addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));

        return war;
    }
}
