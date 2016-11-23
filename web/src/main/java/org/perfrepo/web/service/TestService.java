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
import org.perfrepo.model.to.TestSearchTO;
import org.perfrepo.model.user.User;
import org.perfrepo.web.service.exceptions.ServiceException;

import java.util.List;

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
    * @throws ServiceException
    */
   public Test createTest(Test test) throws ServiceException;

   /**
    * Updates the test.
    *
    * @param test
    * @return test
    */
   public Test updateTest(Test test);

   /**
    * Delete a test with all it's sub-objects. Use with caution!
    *
    * @param test
    * @throws ServiceException
    */
   public void removeTest(Test test) throws ServiceException;

   /**
    * Get test.
    *
    * @param testId Test id
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
    * Returns list of Tests according to criteria defined by TestSearchTO
    *
    * @param search
    * @return result
    */
   public SearchResultWrapper<Test> searchTest(TestSearchTO search);

   /**
    * Returns test uids matching prefix
    *
    * @param prefix
    * @return test prefixes
    */
   public List<String> getTestsByPrefix(String prefix);

   /******** Methods related to metric ********/

   /**
    * Add metric.
    *
    * @param metric
    * @param test
    * @return metric
    * @throws ServiceException
    */
   public Metric addMetric(Metric metric, Test test) throws ServiceException;

   /**
    * Update metric.
    *
    * @param metric
    * @return Updated metric
    * @throws ServiceException
    */
   public Metric updateMetric(Metric metric) throws ServiceException;

   /**
    * Delete metric from the test.
    *
    * @param metric
    * @throws ServiceException
    */
   public void removeMetric(Metric metric) throws ServiceException;

   /**
    * Retrieves metric.
    *
    * @param id
    * @return metric
    */
   public Metric getMetric(Long id);

   /**
    * Returns metrics which belong tests with defined group id and are not defined on the defined
    * test
    *
    * @param test
    * @return metrics
    */
   public List<Metric> getAvailableMetrics(Test test);

   /**
    * Returns all metrics, which are defined on the Test
    *
    * @return metrics
    */
   public List<Metric> getMetricsForTest(Test test);

   /******** Methods related to subscribers ********/

   /**
    * Adds given user to the subscriber list of the given test.
    *
    * @param user
    * @param test
    * @throws ServiceException if subscriber already exists
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
