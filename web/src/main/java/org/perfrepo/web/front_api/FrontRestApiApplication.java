package org.perfrepo.web.front_api;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class FrontRestApiApplication extends Application {

   private Set<Class<?>> classes = new HashSet<>();

   public FrontRestApiApplication() {
      classes.add(TestFrontRestApi.class);
      classes.add(MetricFrontRestApi.class);
      classes.add(UserFrontRestApi.class);
   }

   @Override
   public Set<Class<?>> getClasses() {
      return classes;
   }
}