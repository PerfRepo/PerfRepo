package org.perfrepo.web.rest;

import org.perfrepo.web.rest.test.MetricRestApi;
import org.perfrepo.web.rest.test.TestRestApi;

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
      classes.add(UserRestApi.class);
   }

   @Override
   public Set<Class<?>> getClasses() {
      return classes;
   }
}