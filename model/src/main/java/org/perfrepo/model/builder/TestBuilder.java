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
import org.perfrepo.model.Test;
import org.perfrepo.model.user.Group;

import java.util.Set;

/**
 * {@link Test} builder.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
public class TestBuilder {
   private Test test;

   /**
    * Create new {@link TestBuilder}.
    */
   public TestBuilder() {
      test = new Test();
   }

   /**
    * Set the test name.
    *
    * @param name
    * @return this {@link TestBuilder}
    */
   public TestBuilder name(String name) {
      test.setName(name);
      return this;
   }

   /**
    * Set the test description.
    *
    * @param description
    * @return this {@link TestBuilder}
    */
   public TestBuilder description(String description) {
      test.setDescription(description);
      return this;
   }

   /**
    * Set the test groupId.
    *
    * @param groupId
    * @return this {@link TestBuilder}
    */
   public TestBuilder groupId(String groupId) {
      //TODO: fix this
      Group group = new Group();
      group.setName(groupId);
      test.setGroup(group);
      return this;
   }

   /**
    * Set the test uid.
    *
    * @param uid
    * @return this {@link TestBuilder}
    */
   public TestBuilder uid(String uid) {
      test.setUid(uid);
      return this;
   }

   /**
    * Creates a new {@link Test} based on defined values.
    *
    * @return the {@link Test}
    */
   public Test build() {
      return test;
   }

   /**
    * Shortcut for metric().name(name).comparator(comparator).description(description).test()
    *
    * @param name
    * @param comparator
    * @param description
    * @return this {@link TestBuilder}
    */
   public TestBuilder metric(String name, MetricComparator comparator, String description) {
      return metric().name(name).comparator(comparator).description(description).test();
   }

   /**
    * Shortcut for metric().name(name).description(description).test()
    *
    * @param name
    * @param description
    * @return this {@link TestBuilder}
    */
   public TestBuilder metric(String name, String description) {
      return metric().name(name).description(description).comparator(MetricComparator.HIGHER_BETTER).test();
   }

   /**
    * Shortcut for metric().name(name).test()
    *
    * @param name
    * @return this {@link TestBuilder}
    */
   public TestBuilder metric(String name) {
      return metric().name(name).test();
   }

   /**
    * Add a new {@link Metric}.
    *
    * @return new {@link MetricBuilder}
    */
   public MetricBuilder metric() {
      return new MetricBuilder(this, addMetric(new Metric()));
   }

   private Metric addMetric(Metric metric) {
      Set<Metric> metrics = test.getMetrics();
      Set<Test> tests = metric.getTests();

      metric.getTests().add(test);
      test.getMetrics().add(metric);

      return metric;
   }
}
