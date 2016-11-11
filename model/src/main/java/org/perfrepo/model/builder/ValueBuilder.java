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
import org.perfrepo.model.Value;
import org.perfrepo.model.ValueParameter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link Value} builder
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public class ValueBuilder {
   private TestExecutionBuilder parentBuilder;
   private Value value;

   ValueBuilder(TestExecutionBuilder parentBuilder, Value value) {
      super();
      this.parentBuilder = parentBuilder;
      this.value = value;
   }

   private ValueParameter addParameter(ValueParameter param) {
      Map<String, ValueParameter> parameters = value.getParameters();
      parameters.put(param.getName(), param);
      return param;
   }

   public ValueBuilder parameter(String name, String value) {
      addParameter(new ValueParameter(name, value));
      return this;
   }

   public ValueBuilder metricName(String metricName) {
      Metric metric = value.getMetric();
      if (metric == null) {
         metric = new Metric();
         value.setMetric(metric);
      }
      metric.setName(metricName);
      return this;
   }

   public ValueBuilder resultValue(Double resultValue) {
      value.setResultValue(resultValue);
      return this;
   }

   public TestExecutionBuilder execution() {
      return parentBuilder;
   }
}
