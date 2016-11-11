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
package org.perfrepo.model.builder;

import org.perfrepo.model.Metric;
import org.perfrepo.model.MetricComparator;

/**
 * {@link Metric} builder.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public class MetricBuilder {

   private TestBuilder parentBuilder;
   private Metric metric;

   public MetricBuilder(TestBuilder parentBuilder, Metric metric) {
      this.parentBuilder = parentBuilder;
      this.metric = metric;
   }

   public MetricBuilder name(String name) {
      metric.setName(name);
      return this;
   }

   public MetricBuilder comparator(MetricComparator comparator) {
      metric.setComparator(comparator);
      return this;
   }

   public MetricBuilder description(String description) {
      metric.setDescription(description);
      return this;
   }

   public TestBuilder test() {
      if (parentBuilder == null) {
         throw new IllegalStateException("Parent builder not defined, this is a standalone metric. Call MetricBuilder.build()");
      }
      return parentBuilder;
   }

   public Metric build() {
      if (parentBuilder != null) {
         throw new IllegalStateException("This metric can't be built as standalone object, call MetricBuilder.test()");
      }
      return metric;
   }
}
