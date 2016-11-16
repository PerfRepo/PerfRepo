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

import org.apache.log4j.Logger;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.Value;
import org.perfrepo.model.ValueParameter;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Structured representation of Multi-value, parsed from {@link Value} and {@link ValueParameter}
 * entities extracted from fully loaded {@link TestExecution}.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public class MultiValue {

   private static final Logger log = Logger.getLogger(MultiValue.class);

   private MultiValue() { }

   /**
    * Finds {@link ValueInfo} for {@link Value} by id.
    *
    * @param value
    * @return {@link ValueInfo}
    */
   public static ValueInfo find(List<ValueInfo> valueInfos, Value value) {
      if (valueInfos == null || value == null) {
         return null;
      }
      for (ValueInfo vInfo : valueInfos) {
         if (vInfo.simpleValue != null && vInfo.simpleValue.getId().equals(value.getId())) {
            return vInfo;
         } else if (vInfo.complexValueByParamName != null) {
            for (List<ParamInfo> pInfoList : vInfo.complexValueByParamName.values()) {
               for (ParamInfo pInfo : pInfoList) {
                  if (pInfo.value.getId().equals(value.getId())) {
                     return vInfo;
                  }
               }
            }
         }
      }
      return null;
   }

   /**
    * Find value info by metric name.
    *
    * @param valueInfos
    * @param metricName
    * @return {@link ValueInfo}
    */
   public static ValueInfo find(List<ValueInfo> valueInfos, String metricName) {
      if (valueInfos == null || metricName == null) {
         return null;
      }
      for (ValueInfo vInfo : valueInfos) {
         if (vInfo.metricName.equals(metricName)) {
            return vInfo;
         }
      }
      return null;
   }

    /**
     * Determines whether is provided test execution multivalue. Also checks the case that the multivalue is correctly
     * formed.
     *
     * @param testExecution
     * @throws IllegalStateException if there are multiple values for one metric, but the values are not parametrized.
     * @return true if test execution is multivalue, false otherwise
     */
   public static boolean isMultivalue(TestExecution testExecution) throws IllegalStateException {
      Map<String, List<Value>> valuesByMetric = getValuesByMetric(testExecution);

      // metrics that are multivalue
      List<String> multiValueMetrics = valuesByMetric.keySet().stream()
              .filter(key -> valuesByMetric.get(key).size() > 1)
              .collect(Collectors.toList());

      if (!multiValueMetrics.isEmpty()) { // if there's any multivalue metric, validate only them
         for (String metric: multiValueMetrics) {
            List<Value> values = valuesByMetric.get(metric);
            boolean areAllValuesParametrized = values.stream().allMatch(Value::hasParameters);
            if (!areAllValuesParametrized) {
               throw new IllegalStateException("Test execution contains multiple values for one metric, hence it should be multivalue."
                       + "However, the values are not parametrized, so this test execution '" + testExecution.getName() + "' is not valid!");
            }
         }

         return true;
      }

      return false;
   }

   /**
    * Create list of multi-values for single test execution, one for each metric.
    *
    * @param testExecution
    * @return List of {@link ValueInfo}
    */
   public static List<ValueInfo> createFrom(TestExecution testExecution) {
      // group by metric
      Map<String, List<Value>> valuesByMetric = getValuesByMetric(testExecution);

      List<ValueInfo> r = new ArrayList<ValueInfo>();
      for (Map.Entry<String, List<Value>> entry : valuesByMetric.entrySet()) {
         ValueInfo vInfo = new ValueInfo();
         vInfo.metricName = entry.getKey();
         if (entry.getValue().size() == 1) {
            vInfo.simpleValue = entry.getValue().get(0);
         } else {
            vInfo.complexValueByParamName = new TreeMap<String, List<ParamInfo>>();
            for (Value v : entry.getValue()) {
               if (v.getParameters() == null || v.getParameters().isEmpty()) {
                  log.error("Multi-value " + v.getResultValue() + " for metric " + vInfo.metricName + " is not parametrized");
               }
               //TODO: solve this
               /*
               for (ValueParameter vp : v.getParameters()) {
                  List<ParamInfo> paramInfos = vInfo.complexValueByParamName.get(vp.getName());
                  if (paramInfos == null) {
                     paramInfos = new ArrayList<ParamInfo>();
                     vInfo.complexValueByParamName.put(vp.getName(), paramInfos);
                  }
                  ParamInfo paramInfo = new ParamInfo();
                  paramInfo.param = vp;
                  paramInfo.value = v;
                  paramInfos.add(paramInfo);
               }*/
            }
         }
         if (vInfo.complexValueByParamName != null) {
            for (List<ParamInfo> paramInfoList : vInfo.complexValueByParamName.values()) {
               Collections.sort(paramInfoList);
            }
         }
         r.add(vInfo);
      }
      return r;
   }

    /**
     * Basically groups values by metric
     *
     * @param testExecution
     * @return
     */
   private static Map<String, List<Value>> getValuesByMetric(TestExecution testExecution) {
      //TODO: solve this
      /*
      if (testExecution.getValues() == null || testExecution.getValues().isEmpty()) {
         return new HashMap<>();
      }

      Map<String, List<Value>> valuesByMetric = testExecution.getValues().stream().collect(Collectors.groupingBy(Value::getMetricName));

      return valuesByMetric;
      */
      return null;
   }

   private static final DecimalFormat FMT = new DecimalFormat("0.000");

   public static class ParamInfo implements Comparable<ParamInfo>, Serializable {
      private ValueParameter param;
      private Value value;

      public String getParamValue() {
         return param == null ? null : param.getParamValue();
      }

      public Double getValue() {
         return value == null ? null : value.getResultValue();
      }

      @Override
      public int compareTo(ParamInfo o) {
         try {
            return Double.valueOf(this.getParamValue()).compareTo(Double.valueOf(o.getParamValue()));
         } catch (NumberFormatException e) {
            return this.getParamValue().compareTo(o.getParamValue());
         }
      }

      public String getFormattedValue() {
         return getValue() == null ? null : FMT.format(getValue());
      }

      public Value getEntity() {
         return value;
      }
   }

   public static class ValueInfo implements Comparable<ValueInfo>, Serializable {
      private String metricName;
      private Value simpleValue;
      private SortedMap<String, List<ParamInfo>> complexValueByParamName;

      public String getMetricName() {
         return metricName;
      }

      public Value getEntity() {
         return simpleValue;
      }

      public Double getSimpleValue() {
         return simpleValue == null ? null : simpleValue.getResultValue();
      }

      public List<ParamInfo> getComplexValueByParamName(String paramName) {
         return complexValueByParamName == null ? null : complexValueByParamName.get(paramName);
      }

      @Override
      public int compareTo(ValueInfo o) {
         return this.metricName.compareTo(o.metricName);
      }

      public boolean isMultiValue() {
         return complexValueByParamName != null;
      }

      public String getFormattedSimpleValue() {
         return simpleValue == null ? null : FMT.format(simpleValue.getResultValue());
      }

      public List<String> getComplexValueParams() {
         return complexValueByParamName == null ? Collections.<String>emptyList() : new ArrayList<String>(complexValueByParamName.keySet());
      }
   }
}
