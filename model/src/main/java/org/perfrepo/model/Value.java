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
package org.perfrepo.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents one value measured in a {@link TestExecution}.
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@javax.persistence.Entity
@Table(name = "value")
@NamedQueries({@NamedQuery(name = Value.GET_TEST, query = "SELECT test from Value v inner join v.testExecution te inner join te.test test where v= :entity")})
public class Value implements Entity<Value> {

   private static final long serialVersionUID = 1227873698917395252L;

   public static final String GET_TEST = "Value.getTest";

   @Id
   @SequenceGenerator(name = "VALUE_ID_GENERATOR", sequenceName = "VALUE_SEQUENCE", allocationSize = 1)
   @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VALUE_ID_GENERATOR")
   private Long id;

   @ManyToOne(optional = false)
   @JoinColumn(name = "metric_id", referencedColumnName = "id")
   private Metric metric;

   @Column(name = "result_value")
   private Double resultValue;

   @ManyToOne(optional = false)
   @JoinColumn(name = "test_execution_id", referencedColumnName = "id")
   private TestExecution testExecution;

   @OneToMany(mappedBy = "value")
   @MapKey(name = "name")
   private Map<String, ValueParameter> parameters = new HashMap<>();

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public void setMetric(Metric metric) {
      this.metric = metric;
   }

   public Metric getMetric() {
      return this.metric;
   }

   public void setResultValue(Double resultValue) {
      this.resultValue = resultValue;
   }

   public Double getResultValue() {
      return this.resultValue;
   }

   public void setTestExecution(TestExecution testExecution) {
      this.testExecution = testExecution;
   }

   public TestExecution getTestExecution() {
      return this.testExecution;
   }

   public Map<String, ValueParameter> getParameters() {
      return parameters;
   }

   public void setParameters(Map<String, ValueParameter> parameters) {
      this.parameters = parameters;
   }

   public String getMetricName() {
      return metric == null ? null : metric.getName();
   }

   public void setMetricName(String metricName) {
      if (metric == null) {
         metric = new Metric();
      }
      metric.setName(metricName);
   }

   public MetricComparator getMetricComparator() {
      return metric == null ? null : metric.getComparator();
   }

   public void setMetricComparator(MetricComparator metricComparator) {
      if (metric == null) {
         metric = new Metric();
      }
      metric.setComparator(metricComparator);
   }

   public boolean hasParameters() {
      return parameters != null && !parameters.isEmpty();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Value)) return false;

      Value value = (Value) o;

      return getId() != null ? getId().equals(value.getId()) : value.getId() == null;
   }

   @Override
   public int hashCode() {
      return getId() != null ? getId().hashCode() : 0;
   }

   @Override
   public String toString() {
      return "Value{" +
              "id=" + id +
              ", metric=" + metric +
              ", resultValue=" + resultValue +
              ", parameters=" + parameters +
              '}';
   }
}