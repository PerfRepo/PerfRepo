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
package org.perfrepo.web.service;

import org.perfrepo.model.Metric;
import org.perfrepo.model.Test;
import org.perfrepo.model.to.SearchResultWrapper;
import org.perfrepo.model.user.User;
import org.perfrepo.web.service.exceptions.DuplicateEntityException;
import org.perfrepo.web.service.exceptions.UnauthorizedException;
import org.perfrepo.web.service.search.TestSearchCriteria;

import java.util.List;
import java.util.Set;

/**
 * Service for tests
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public interface TestService {

   /******** Methods related to test ********/

   /**
    * Create a new test with collection of metrics.
    *
    * @param test
    * @return
    * @throws DuplicateEntityException
    * @throws UnauthorizedException
     */
   public Test createTest(Test test) throws DuplicateEntityException, UnauthorizedException;

   /**
    * Updates the test.
    *
    * @param test
    * @return
    * @throws DuplicateEntityException
     */
   public Test updateTest(Test test) throws DuplicateEntityException;

   /**
    * Delete a test with all it's sub-objects, but first it disassociates all the metrics from it.
    * USE WITH CAUTION!!!
    *
    * @param test
    */
   public void removeTest(Test test);

   /**
    * Get test.
    *
    * @param id Test id
    * @return Test
    */
   public Test getTest(Long id);

   /**
    * Get test.
    *
    * @param uid test uid
    * @return Test
    */
   public Test getTest(String uid);

   /**
    * Returns all tests.
    *
    * @return tests
    */
   public List<Test> getAllTests();

   /**
    * @return list of all test belonging to one of the groups current logged user is in.
    */
   public SearchResultWrapper<Test> getTestsForUser(User user);

   /**
    * Returns list of Tests according to criteria defined by TestSearchCriteria
    *
    * @param search
    * @return result
    */
   public SearchResultWrapper<Test> searchTests(TestSearchCriteria search);

   /**
    * Returns test uids matching prefix
    *
    * @param prefix
    * @return test prefixes
    */
   public List<Test> getTestsByUidPrefix(String prefix);

   /******** Methods related to metric ********/

   /**
    * Adds metric to test. If the metric already exists, it assigns the existing entity. If it doesn't,
    * it creates the entity and then assigns it to test.
    *
    * @param metric
    * @param test
    * @return metric
    */
   public Metric addMetric(Metric metric, Test test);

   /**
    * Update metric.
    *
    * @param metric
    * @return Updated metric
    * @throws DuplicateEntityException
    */
   public Metric updateMetric(Metric metric) throws DuplicateEntityException;

   /**
    * Disassociate metric from the test. If there are no more tests associated with the metric,
    * the metric is automatically removed.
    *
    * @param metric
    */
   public void removeMetricFromTest(Metric metric, Test test);

   /**
    * Retrieves metric.
    *
    * @param id
    * @return metric
    */
   public Metric getMetric(Long id);

   /**
    * Returns all metrics, which are defined on the Test
    *
    * @return metrics
    */
   public Set<Metric> getMetricsForTest(Test test);

   /******** Methods related to subscribers ********/

   /**
    * Adds given user to the subscriber list of the given test.
    *
    * @param user
    * @param test
    */
   public void addSubscriber(User user, Test test);

   /**
    * Removes given user from the subscriber list of the given test
    *
    * @param user
    * @param test
    */
   public void removeSubscriber(User user, Test test);

   /**
    * Returns true if the given user is subscribed to given test
    *
    * @param user
    * @param test
    * @return boolean
    */
   public boolean isUserSubscribed(User user, Test test);

}
