package org.perfrepo.web.rest;

import org.perfrepo.web.rest.endpoints.*;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@ApplicationPath("/api/json")
public class RestApiApplication extends Application {

   private Set<Class<?>> classes = new HashSet<>();

   public RestApiApplication() {
      classes.add(TestRestApi.class);
      classes.add(MetricRestApi.class);
      classes.add(AlertRestApi.class);
      classes.add(UserRestApi.class);
      classes.add(GroupRestApi.class);
      classes.add(AuthenticationRestApi.class);
      classes.add(TestExecutionRestApi.class);
   }

   @Override
   public Set<Class<?>> getClasses() {
      return classes;
   }
}