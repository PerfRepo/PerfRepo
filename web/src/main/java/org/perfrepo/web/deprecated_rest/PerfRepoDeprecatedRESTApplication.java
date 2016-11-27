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
package org.perfrepo.web.deprecated_rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/deprecated_rest")
public class PerfRepoDeprecatedRESTApplication extends Application {

   private Set<Class<?>> classes = new HashSet<Class<?>>();

   public PerfRepoDeprecatedRESTApplication() {
      classes.add(MetricDeprecatedREST.class);
      classes.add(TestExecutionDeprecatedREST.class);
      classes.add(TestDeprecatedREST.class);
      classes.add(ReportDeprecatedREST.class);
      classes.add(InfoDeprecatedREST.class);
   }

   @Override
   public Set<Class<?>> getClasses() {
      return classes;
   }
}