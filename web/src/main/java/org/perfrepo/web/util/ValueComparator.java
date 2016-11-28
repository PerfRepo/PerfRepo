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
package org.perfrepo.web.util;

import org.perfrepo.model.Value;

public class ValueComparator {

   private ValueComparator() { }

   public static int compare(Value value1, Value value2) {
      if (value1.getMetric() != null && value1.getMetric().equals(value2.getMetric())) {
         switch (value1.getMetricComparator()) {
            case HIGHER_BETTER:
               if (value1.getResultValue() != null && value2.getResultValue() != null) {
                  return value1.getResultValue().compareTo(value2.getResultValue());
               } else if (value1.getResultValue() == null) {
                  return -1;
               } else {
                  return 1;
               }

            case LOWER_BETTER:
               if (value1.getResultValue() != null && value2.getResultValue() != null) {
                  return -value1.getResultValue().compareTo(value2.getResultValue());
               } else if (value1.getResultValue() == null) {
                  return -1;
               } else {
                  return 1;
               }
            default:
               if (value1.getResultValue() != null && value2.getResultValue() != null) {
                  return value1.getResultValue().compareTo(value2.getResultValue());
               } else if (value1.getResultValue() == null) {
                  return -1;
               } else {
                  return 1;
               }
         }
      }
      return 0;
   }
}
