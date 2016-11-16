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
package org.perfrepo.web.controller.reports.parametrized;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.perfrepo.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class TestExecutionTable {

   List<String> parameterNames = new ArrayList<String>();

   private List<TestExecution> baseTestExecutions;

   private List<Metric> metrics = new ArrayList<Metric>();

   private Multimap<Long, TestExecution> compareTestExecutions;

   private Multimap<Long, String> tags = HashMultimap.create();

   private Long jobId;

   private List<Long> compareJobIds = new ArrayList<Long>();

   ///** best values stored as coordinates in the table (key, column) **/
   //private Map<MultiKey, Value> bestValues = new HashMap<MultiKey, Value>();

   /**
    * Table represents the TestExecutionscompareValue
    * row key - parameter values, sorted by parameter name
    * column key - jobId + Metric name
    * value - Value
    */
   private HashBasedTable<MultiKey, MultiKey, Value> table = HashBasedTable.create();

   private HashBasedTable<MultiKey, String, Value> bestValues = HashBasedTable.create();

   public void process(Long jobId, List<TestExecution> testExecutions) {
      this.jobId = jobId;
      this.baseTestExecutions = testExecutions;
      for (TestExecution te : testExecutions) {
         for (String paramKey : te.getParameters().keySet()) {
            TestExecutionParameter tep = te.getParameters().get(paramKey);
            if (!parameterNames.contains(tep.getName())) {
               parameterNames.add(tep.getName());
            }
         }
         //TODO: solve this
         /*for (Value value : te.getValues()) {
            Metric metric = value.getMetric();
            if (!metrics.contains(metric)) {
               metrics.add(metric);
            }
         }*/
      }
      addTestExecutions(jobId, testExecutions, true);
   }

   public boolean isBestResult(MultiKey params, String metricName) {
      return isBestResult(jobId, params, metricName);
   }

   public boolean isBestResult(Long jobId, MultiKey params, String metricName) {
      Value bestValue = bestValues.get(params, metricName);
      Value value = getValue(jobId, metricName, params);
      return bestValue.equals(value);
   }

   /**
    * Add TestExecutions, which belongs to specified Job to the table
    *
    * @param jobId - Jenkins Job Id
    * @param testExecutions - List of TestExecution, which belong to job
    */
   public void addTestExecutions(Long jobId, List<TestExecution> testExecutions) {
      addTestExecutions(jobId, testExecutions, false);
   }

   /**
    * Removes TestExecutions, which belongs to specified Job to the table
    *
    * @param jobId - Jenkins Job Id
    */
   public void removeTestExecutions(Long jobId) {
      compareTestExecutions.removeAll(jobId);
      compareJobIds.remove(jobId);
      for (Metric m : metrics) {
         table.remove(null, new MultiKey(jobId, m.getName()));
      }
   }

   public void transferBestValues() {
      for (MultiKey params : table.rowKeySet()) {
         for (Metric metric : metrics) {
            Value best = bestValues.get(params, metric.getName());
            Value origin = getValue(metric.getName(), params);
            if (best != null && !best.equals(origin)) {
               origin.setResultValue(best.getResultValue());
            }
         }
      }
   }

   /**
    * Add TestExecutions, which belongs to specified Job to the table
    *
    * @param jobId - Jenkins Job Id
    * @param testExecutions - List of TestExecution, which belong to job
    * @param baseTEs - indicates if the base/comparison TestExecutions are added to the table.
    */
   private void addTestExecutions(Long jobId, List<TestExecution> testExecutions, boolean baseTEs) {
      if (!baseTEs) {
         compareJobIds.add(jobId);
         if (compareTestExecutions == null) {
            compareTestExecutions = HashMultimap.create();
         }
      }
      for (TestExecution te : testExecutions) {
         String[] paramValues = new String[parameterNames.size()];
         //TODO: solve this
         /*
         for (TestExecutionParameter tep : te.getParameters()) {
            paramValues[parameterNames.indexOf(tep.getName())] = tep.getValue();
         }*/
         MultiKey params = new MultiKey(paramValues);
         //only if we can compare the value with something in the table
         //first TEs (jobId) should always put value to the table
         if (baseTEs || table.containsRow(params)) {
            //TODO: solve this
            /*
            for (Value value : te.getValues()) {
               MultiKey columnKey = new MultiKey(jobId, value.getMetric().getName());
               if (!baseTEs) {
                  compareTestExecutions.put(jobId, te);
               }
               table.put(params, columnKey, value);
               // update best values
               Value bestValue = bestValues.get(params, value.getMetric().getName());
               if (bestValue == null || compareValues(bestValue, value) < 0) {
                  bestValues.put(params, value.getMetric().getName(), value);
               }
            }*/
            tags.putAll(jobId, te.getTags().stream().map(Tag::getName).collect(Collectors.toList()));
         }
      }
   }

   public int getParameterValuesCount(MultiKey parameters, int index) {
      Set<MultiKey> keys = table.rowKeySet();
      int count = 0;
      for (MultiKey key : keys) {
         boolean inc = true;
         for (int i = 0; i <= index; i++) {
            if (!key.getKey(i).equals(parameters.getKey(i))) {
               inc = false;
            }
         }
         if (inc) {
            count++;
         }
      }
      return count;
   }

   /**
    * Returns percentage difference between origin and compare values
    * if the result is negative, the origin value is worst than compare value
    * if the result is positive, the origin value is better than compare value
    *
    * @param compareJobId
    * @param metricName
    * @param parameters
    * @return computed percentage difference
    */
   public float compareValues(Long compareJobId, String metricName, MultiKey parameters) {
      Value originValue = getValue(jobId, metricName, parameters);
      Value compareValue = getValue(compareJobId, metricName, parameters);
      return compareValues(originValue, compareValue);
   }

   private float compareValues(Value v1, Value v2) {
      //TODO: algorithm
      return (float) (((v1.getResultValue() - v2.getResultValue()) * 100f) / v1.getResultValue());
   }

   //list testexecutionparameters
   public Value getValue(Long jobId, String metricName, Map<String, String> parameters) {
      String[] params = new String[parameterNames.size()];
      for (int i = 0; i < parameterNames.size(); i++) {
         params[i] = parameters.get(parameterNames.get(i));
      }
      return getValue(jobId, metricName, parameters);
   }

   public Value getValue(Long jobId, String metricName, String[] parameters) {
      MultiKey rowKey = new MultiKey(parameters);
      MultiKey colKey = new MultiKey(jobId, metricName);
      return table.get(rowKey, colKey);
   }

   public Value getValue(Long jobId, String metricName, MultiKey parameters) {
      MultiKey colKey = new MultiKey(jobId, metricName);
      return table.get(parameters, colKey);
   }

   public Value getValue(String metricName, MultiKey parameters) {
      MultiKey colKey = new MultiKey(jobId, metricName);
      return table.get(parameters, colKey);
   }

   public List<MultiKey> getSortedRowKeys() {
      List<MultiKey> result = Lists.newArrayList(table.rowKeySet());
      Collections.sort(result, new KeyComparator());
      return result;
   }

   public List<String> getParameterNames() {
      return parameterNames;
   }

   public void setParameterNames(List<String> parameterNames) {
      this.parameterNames = parameterNames;
   }

   public List<TestExecution> getBaseTestExecutions() {
      return baseTestExecutions;
   }

   public void setBaseTestExecutions(List<TestExecution> baseTestExecutions) {
      this.baseTestExecutions = baseTestExecutions;
   }

   public List<Metric> getMetrics() {
      return metrics;
   }

   public void setMetrics(List<Metric> metrics) {
      this.metrics = metrics;
   }

   public Multimap<Long, TestExecution> getCompareTestExecutions() {
      return compareTestExecutions;
   }

   public void setCompareTestExecutions(
       Multimap<Long, TestExecution> compareTestExecutions) {
      this.compareTestExecutions = compareTestExecutions;
   }

   public Multimap<Long, String> getTags() {
      return tags;
   }

   public void setTags(Multimap<Long, String> tags) {
      this.tags = tags;
   }

   public Long getJobId() {
      return jobId;
   }

   public void setJobId(Long jobId) {
      this.jobId = jobId;
   }

   public List<Long> getCompareJobIds() {
      return compareJobIds;
   }

   public void setCompareJobIds(List<Long> compareJobIds) {
      this.compareJobIds = compareJobIds;
   }

   private class KeyComparator implements Comparator<MultiKey> {
      @Override
      public int compare(MultiKey key1, MultiKey key2) {
         int compare = 0;
         for (int i = 0; i < key1.getKeys().length; i++) {
            try {
               compare = Double.valueOf(String.valueOf(key1.getKey(i))).compareTo(Double.parseDouble(String.valueOf(key2.getKey(i))));
            } catch (Exception e) {
               compare = String.valueOf(key1.getKey(i)).compareTo(String.valueOf(key2.getKey(i)));
            }
            if (compare != 0) {
               return compare;
            }
         }
         return compare;
      }
   }
}
