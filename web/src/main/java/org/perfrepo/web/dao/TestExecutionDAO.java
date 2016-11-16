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
package org.perfrepo.web.dao;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.perfrepo.model.*;
import org.perfrepo.model.to.*;
import org.perfrepo.model.to.TestExecutionSearchTO.ParamCriteria;
import org.perfrepo.model.userproperty.GroupFilter;
import org.perfrepo.web.util.TagUtils;

import javax.inject.Inject;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DAO for {@link TestExecution}
 *
 * @author Pavel Drozd (pdrozd@redhat.com)
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public class TestExecutionDAO extends DAO<TestExecution, Long> {

   @Inject
   private TestExecutionParameterDAO testExecutionParameterDAO;

   public List<TestExecution> getByTest(Long testId) {
      Test test = new Test();
      test.setId(testId);
      return getAllByProperty("test", test);
   }

   /**
    * Returns test executions with property value between selected boundaries. This can be applied only on
    * Comparable values, otherwise the result is undefined.
    *
    * @param propertyName property to be search (filtered) on
    * @param from lower boundary
    * @param to upper boundary
    * @return list of according test executions
    */
   public <T extends Comparable<? super T>> List<TestExecution> getAllByPropertyBetween(String propertyName, T from, T to) {
      CriteriaQuery<TestExecution> resultQuery = createCriteria();
      CriteriaBuilder cb = criteriaBuilder();

      Root<TestExecution> root = resultQuery.from(TestExecution.class);
      resultQuery.select(root);
      resultQuery.where(cb.between(root.<T>get(propertyName), from, to));

      return query(resultQuery).getResultList();
   }

   /**
    * Allows to search test executions by many complex criterias.
    *
    * @param search
    * @param userGroups
    * @return
    */
   public SearchResultWrapper<TestExecution> searchTestExecutions(TestExecutionSearchTO search, List<String> userGroups) {
      CriteriaBuilder cb = criteriaBuilder();

      List<String> tags = TagUtils.parseTags(search.getTags() != null ? search.getTags().toLowerCase() : "");
      List<String> excludedTags = new ArrayList<>();
      List<String> includedTags = new ArrayList<>();
      divideTags(tags, includedTags, excludedTags);

      int lastQueryResultsCount = processSearchCountQuery(search, includedTags, excludedTags, userGroups);

      CriteriaQuery<TestExecution> criteria = (CriteriaQuery) createSearchSubquery(cb.createQuery(TestExecution.class), search, includedTags, excludedTags);
      Root<TestExecution> root = (Root<TestExecution>) criteria.getRoots().toArray()[0];
      criteria.select(root);
      setOrderBy(criteria, search.getOrderBy(), root);

      TypedQuery<TestExecution> query = query(criteria);
      fillParameterValues(query, search, includedTags, excludedTags, userGroups);

      //handle pagination
      int firstResult = search.getLimitFrom() == null ? 0 : search.getLimitFrom();
      query.setFirstResult(firstResult);
      if (search.getLimitHowMany() != null) {
         query.setMaxResults(search.getLimitHowMany());
      }

      List<TestExecution> result = query.getResultList();
      orderResultsByParameters(result, search);

      return new SearchResultWrapper<>(result, lastQueryResultsCount);
   }

   /**
    * Shortcut for getTestExecutions(tags, testUIDs, null, null)
    *
    * @param tags
    * @param testUIDs
    * @return
    */
   public List<TestExecution> getTestExecutions(List<String> tags, List<String> testUIDs) {
      return getTestExecutions(tags, testUIDs, null, null);
   }

   /**
    * This method retrieves all test executions that belong to one of the specified tests and have
    * ALL the tags. The 'lastFrom' and 'howMany' parameters works as a LIMIT in SQL, e.g. lastFrom = 5, howMany = 3
    * will return 3 last test executions shifted by 2 (last 2 test executions will not be in the result)
    *
    * @deprecated Use searchTestExecutions instead
    * @param tags ALL tags that test execution must have
    * @param testUIDs ID's of the tests
    * @param lastFrom see comment above. null = all test executions will be retrieved (both lastFrom and howMany must be set to take effect)
    * @param howMany see comment above. null = all test executions will be retrieved (both lastFrom and howMany must be set to take effect)
    * @return
    */
   @Deprecated
   public List<TestExecution> getTestExecutions(List<String> tags, List<String> testUIDs, Integer lastFrom, Integer howMany) {
      CriteriaBuilder cb = criteriaBuilder();

      Long count = null;
      //we want to use 'last' boundaries, we must compute the number of test executions that
      //match the requirements - have selected tags and belong to selected tests
      if (lastFrom != null && howMany != null) {
         CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
         Root<TestExecution> root = countQuery.from(TestExecution.class);
         countQuery.select(cb.countDistinct(root));

         Subquery<Long> subquery = (Subquery) createSubqueryByTags(countQuery.subquery(Long.class));
         Root<TestExecution> subqueryRoot = (Root<TestExecution>) subquery.getRoots().toArray()[0];
         subquery.select(subqueryRoot.<Long>get("id"));

         countQuery.where(cb.in(root.get("id")).value(subquery));
         TypedQuery<Long> typedCountQuery = createTypedQueryByTags(countQuery, testUIDs, tags);
         count = typedCountQuery.getSingleResult();
      }

      //now we can retrieve the actual result
      CriteriaQuery<TestExecution> criteriaQuery = (CriteriaQuery) createSubqueryByTags(cb.createQuery(TestExecution.class));
      Root<TestExecution> root = (Root<TestExecution>) criteriaQuery.getRoots().toArray()[0];
      criteriaQuery.select(root);
      criteriaQuery.orderBy(cb.asc(root.get("started")));

      TypedQuery<TestExecution> query = createTypedQueryByTags(criteriaQuery, testUIDs, tags);
      //we're using 'last' parameters, set the paging
      //TODO: this might be broken, use search test execution instead
      if (count != null) {
         int firstResult = count.intValue() - lastFrom;
         query.setFirstResult(firstResult < 0 ? 0 : firstResult);
         query.setMaxResults(howMany);
      }

      List<TestExecution> result = query.getResultList();

      return result;
   }

   /**
    * Retrieves result values of the test executions assigned to specific metric.
    * Behaviour on multi-value test execution in undefined
    *
    * @param search search criteria object
    * @param metric
    * @param userGroups
    * @return
    */
   public List<SingleValueResultWrapper> searchValues(TestExecutionSearchTO search, Metric metric, List<String> userGroups) {
      CriteriaBuilder cb = criteriaBuilder();
      CriteriaQuery<SingleValueResultWrapper> criteriaQuery = cb.createQuery(SingleValueResultWrapper.class);

      List<TestExecution> testExecutions = searchTestExecutions(search, userGroups).getResult();
      List<Long> testExecutionIds = testExecutions.stream().map(TestExecution::getId).collect(Collectors.toList());

      Root<TestExecution> testExecution = criteriaQuery.from(TestExecution.class);
      Join<TestExecution, Value> valueJoin = testExecution.join("values");
      Join<Value, Metric> metricJoin = valueJoin.join("metric");

      Predicate selectedMetric = cb.equal(metricJoin.get("id"), metric.getId());
      Predicate selectedTestExecutions = testExecution.get("id").in(testExecutionIds);

      criteriaQuery.select(cb.construct(SingleValueResultWrapper.class, valueJoin.get("resultValue"), testExecution.get("id"), testExecution.get("started")));
      criteriaQuery.where(cb.and(selectedMetric, selectedTestExecutions));
      //TODO: this won't work correctly with ordering by parameter value, fix it according to searchMultiValues
      //TODO: this will be fixed when re-doing Metric history report
      criteriaQuery.orderBy(cb.asc(testExecution.get("started")));
      criteriaQuery.groupBy(testExecution.get("id"), valueJoin.get("resultValue"), testExecution.get("started"));

      TypedQuery<SingleValueResultWrapper> query = query(criteriaQuery);

      return query.getResultList();
   }

   /**
    * Retrieves result multi-values of the test executions assigned to specific metric.
    * Behaviour on singe-value test execution in undefined.
    *
    * @param search search criteria object
    * @param metric
    * @param userGroups
    * @return
    */
   public List<MultiValueResultWrapper> searchMultiValues(TestExecutionSearchTO search, Metric metric, List<String> userGroups) {
      CriteriaBuilder cb = criteriaBuilder();
      CriteriaQuery<Tuple> criteriaQuery = cb.createQuery(Tuple.class);

      List<TestExecution> testExecutions = searchTestExecutions(search, userGroups).getResult();
      List<Long> testExecutionIds = testExecutions.stream().map(TestExecution::getId).collect(Collectors.toList());

      Root<TestExecution> testExecution = criteriaQuery.from(TestExecution.class);
      Join<TestExecution, Value> valueJoin = testExecution.join("values");
      Join<Value, Metric> metricJoin = valueJoin.join("metric");
      Join<Value, ValueParameter> valueParameterJoin = valueJoin.join("parameters");
      Join<TestExecution, TestExecutionParameter> executionParameterJoin = null;

      Predicate selectedMetric = cb.equal(metricJoin.get("id"), metric.getId());
      Predicate selectedTestExecutions = testExecution.get("id").in(testExecutionIds);
      Predicate labelParameter = cb.and();

      Path<?> labelPath = testExecution.get("started");
      // we have to add the "started" to group by, as it's workaround for PostgreSQL 8, because we order by it by default
      // and PostgreSQL 8 requires to have all used columns in the GROUP BY
      List<Expression<?>> groupBy = new ArrayList<>(Arrays.asList(testExecution.get("id"), valueJoin.get("resultValue"), valueParameterJoin.get("name"), valueParameterJoin.get("paramValue"), testExecution.get("started")));

      if (search.getLabelParameter() != null) {
         executionParameterJoin = testExecution.join("parameters");
         labelParameter = cb.equal(executionParameterJoin.get("name"), search.getLabelParameter());
         labelPath = executionParameterJoin.get("value");
         groupBy.add(labelPath); // workaround for PostgreSQL 8, if label used, add it to GROUP BY, see the comment above
      }

      criteriaQuery.multiselect(valueJoin.get("resultValue").alias("resultValue"),
                                valueParameterJoin.get("name").alias("valueParameterName"),
                                valueParameterJoin.get("paramValue").alias("valueParameterValue"),
                                testExecution.get("id").alias("execId"),
                                labelPath.alias("label")
      );

      criteriaQuery.where(cb.and(selectedMetric, selectedTestExecutions, labelParameter));
      criteriaQuery.orderBy(cb.asc(testExecution.get("started")));
      criteriaQuery.groupBy(groupBy);

      TypedQuery<Tuple> query = query(criteriaQuery);
      List<Tuple> queryResult = query.getResultList();

      Map<Long, MultiValueResultWrapper> unsortedResult = new HashMap<>();

      //this way of computing might seem strange, but is necessary. We need to keep ordering by
      //test executions. However, retrieved tuples are one for every value. Therefore, we perform
      //something like "group by test execution ID" on the results and construct result wrappers.
      for (Tuple tuple : queryResult) {
         Long execId = tuple.get("execId", Long.class);

         unsortedResult.putIfAbsent(execId, new MultiValueResultWrapper(execId, tuple.get("label")));

         String valueParameterName = tuple.get("valueParameterName", String.class);
         String valueParameterValue = tuple.get("valueParameterValue", String.class);
         Double resultValue = tuple.get("resultValue", Double.class);

         unsortedResult.get(execId).addValue(valueParameterName, valueParameterValue, resultValue);
      }

      //perform after-sort, the ResultWrappers have to be in the same order as retrieved test executions
      List<MultiValueResultWrapper> finalSortedResult = new ArrayList<>();
      testExecutions.stream().filter(execution -> unsortedResult.keySet().contains(execution.getId())).forEach(execution -> finalSortedResult.add(unsortedResult.get(execution.getId())));

      return finalSortedResult;
   }

   /**
    * Finds all values used for computing MetricHistory report
    *
    * @param testId
    * @param metricName
    * @param tagList
    * @param limitSize
    * @return List of SingleValueResultWrapper objects
    */
   public List<MetricReportTO.DataPoint> searchValues(Long testId, String metricName, List<String> tagList, int limitSize) {
      boolean useTags = tagList != null && !tagList.isEmpty();
      CriteriaBuilder cb = criteriaBuilder();
      CriteriaQuery<MetricReportTO.DataPoint> criteria = cb.createQuery(MetricReportTO.DataPoint.class);
      // test executions
      Root<TestExecution> rExec = criteria.from(TestExecution.class);
      // test joined via test exec.
      Join<TestExecution, Test> rTestExec = rExec.join("test");
      // values
      Join<TestExecution, Value> rValue = rExec.join("values");
      // metrics
      Join<Value, Metric> rMetric = rValue.join("metric");
      // test joined via metric
      Join<Metric, Test> rTestMetric = rMetric.join("tests");
      // tag
      Join<TestExecution, Tag> rTag = null;
      Predicate pTagNameInFixedList = cb.and(); // default for this predicate is true
      Predicate pHavingAllTagsPresent = cb.and();
      if (useTags) {
         rTag = rExec.join("tags");
         pTagNameInFixedList = rTag.get("name").in(cb.parameter(List.class, "tagList"));
         pHavingAllTagsPresent = cb.ge(cb.count(rTag.get("id")), cb.parameter(Long.class, "tagListSize"));
      }

      Predicate pMetricNameFixed = cb.equal(rMetric.get("name"), cb.parameter(String.class, "metricName"));
      Predicate pTestFixed = cb.equal(rTestExec.get("id"), cb.parameter(Long.class, "testId"));
      Predicate pMetricFromSameTest = cb.equal(rTestMetric.get("id"), rTestExec.get("id"));

      //sort by date
      criteria.select(cb.construct(MetricReportTO.DataPoint.class, rExec.get("started"), rValue.get("resultValue"), rExec.get("id")));
      criteria.where(cb.and(pMetricNameFixed, pTagNameInFixedList, pTestFixed, pMetricFromSameTest));
      criteria.groupBy(rValue.get("resultValue"), rExec.get("id"), rExec.get("started"));
      criteria.orderBy(cb.desc(rExec.get("started")));

      criteria.having(pHavingAllTagsPresent);

      TypedQuery<MetricReportTO.DataPoint> query = query(criteria);
      query.setParameter("testId", testId);
      query.setParameter("metricName", metricName);

      if (useTags) {
         query.setParameter("tagList", tagList);
         query.setParameter("tagListSize", new Long(tagList.size()));
      }
      query.setMaxResults(limitSize);
      return query.getResultList();
   }

   public Double getValueForMetric(Long execId, String metricName) {
      CriteriaBuilder cb = criteriaBuilder();
      CriteriaQuery<Double> criteria = cb.createQuery(Double.class);
      // test executions
      Root<TestExecution> rExec = criteria.from(TestExecution.class);
      // test joined via test exec.
      Join<TestExecution, Test> rTestExec = rExec.join("test");
      // values
      Join<TestExecution, Value> rValue = rExec.join("values");
      // metrics
      Join<Value, Metric> rMetric = rValue.join("metric");
      // test joined via metric
      Join<Metric, Test> rTestMetric = rMetric.join("tests");

      Predicate pMetricNameFixed = cb.equal(rMetric.get("name"), cb.parameter(String.class, "metricName"));
      Predicate pExecFixed = cb.equal(rExec.get("id"), cb.parameter(Long.class, "execId"));
      Predicate pMetricFromSameTest = cb.equal(rTestMetric.get("id"), rTestExec.get("id"));
      criteria.multiselect(rValue.get("resultValue"));
      criteria.where(cb.and(pMetricNameFixed, pExecFixed, pMetricFromSameTest));

      TypedQuery<Double> query = query(criteria);
      query.setParameter("execId", execId);
      query.setParameter("metricName", metricName);

      List<Double> r = query.getResultList();
      if (r.isEmpty()) {
         return null;
      } else {
         return r.get(0);
      }
   }

   /**
    * Helper method. Adds ordering to the query depending on the criteria option.
    *
    * @param criteria
    * @param orderBy
    * @param root
    */
   private void setOrderBy(CriteriaQuery criteria, OrderBy orderBy, Root root) {
      CriteriaBuilder cb = criteriaBuilder();

      //ignoring other OrderBy options (like PARAMETER), because it's not possible to order
      //by values for specific parameter name in SQL. Therefore if the the option is PARAMETER(ASC|DESC)
      //it's ordered afterwards, just like filterResultByParameters, see orderResultsByParameters
      Order order;
      switch (orderBy) {
         case DATE_ASC:
            order = cb.asc(root.get("started"));
            break;
         case DATE_DESC:
            order = cb.desc(root.get("started"));
            break;
         case NAME_ASC:
            order = cb.asc(root.get("name"));
            break;
         case NAME_DESC:
            order = cb.desc(root.get("name"));
            break;
         default:
            order = cb.desc(root.get("started"));
      }

      criteria.orderBy(order);
   }

   /**
    * Helper method. Creates a criteria query of Criteria API for searching of test executions. Because we also
    * sometimes need to limit the number of results and it's not possible to easily reuse the query for both
    * count query and actual retrieval, this construction of criteria is extracted into this method to avoid code
    * duplication. Also makes the code more readable.
    *
    * @param search
    */
   private AbstractQuery createSearchSubquery(AbstractQuery criteriaQuery, TestExecutionSearchTO search, List<String> includedTags, List<String> excludedTags) {
      AbstractQuery criteria = criteriaQuery;
      CriteriaBuilder cb = criteriaBuilder();

      //initialize predicates
      Predicate pIds = cb.and();
      Predicate pStartedFrom = cb.and();
      Predicate pStartedTo = cb.and();
      Predicate pTagNameInFixedList = cb.and();
      Predicate pExcludedTags = cb.and();
      Predicate pTestName = cb.and();
      Predicate pTestUID = cb.and();
      Predicate pTestGroups = cb.and();
      Predicate pParamsMatch = cb.and();
      Predicate pHavingAllTagsPresent = cb.and();

      Root<TestExecution> rExec = criteria.from(TestExecution.class);

      // construct criteria
      if (search.getIds() != null && !search.getIds().isEmpty()) {
         pIds = rExec.<Long>get("id").in(cb.parameter(List.class, "ids"));
      }
      if (search.getStartedFrom() != null) {
         pStartedFrom = cb.greaterThanOrEqualTo(rExec.<Date>get("started"), cb.parameter(Date.class, "startedFrom"));
      }
      if (search.getStartedTo() != null) {
         pStartedTo = cb.lessThanOrEqualTo(rExec.<Date>get("started"), cb.parameter(Date.class, "startedTo"));
      }
      if (!includedTags.isEmpty() || !excludedTags.isEmpty()) {
         Join<TestExecution, Tag> rTag = rExec.join("tags");
         if (!includedTags.isEmpty()) {
            pTagNameInFixedList = cb.lower(rTag.<String>get("name")).in(cb.parameter(List.class, "tagList"));
            pHavingAllTagsPresent = cb.ge(cb.count(rTag.get("id")), cb.parameter(Long.class, "tagListSize"));
         }

         if (!excludedTags.isEmpty()) {
            Subquery<Long> sq = criteria.subquery(Long.class);
            Root<TestExecution> sqRoot = sq.from(TestExecution.class);
            Join<TestExecution, Tag> sqTag = sqRoot.join("tags");
            sq.select(sqRoot.<Long>get("id"));
            sq.where(cb.lower(sqTag.<String>get("name")).in(cb.parameter(List.class, "excludedTagList")));

            pExcludedTags = cb.not(rExec.get("id").in(sq));
         }
      }
      if (search.getTestName() != null && !"".equals(search.getTestName())) {
         Join<TestExecution, Test> rTest = rExec.join("test");
         pTestName = cb.like(cb.lower(rTest.<String>get("name")), cb.parameter(String.class, "testName"));
      }
      if (search.getTestUID() != null && !"".equals(search.getTestUID())) {
         Join<TestExecution, Test> rTest = rExec.join("test");
         pTestUID = cb.like(cb.lower(rTest.<String>get("uid")), cb.parameter(String.class, "testUID"));
      }
      if (GroupFilter.MY_GROUPS.equals(search.getGroupFilter())) {
         Join<TestExecution, Test> rTest = rExec.join("test");
         pTestGroups = cb.and(rTest.<String>get("groupId").in(cb.parameter(List.class, "groupNames")));
      }
      if (search.getParameters() != null && !search.getParameters().isEmpty()) {
         for (int pCount = 1; pCount < search.getParameters().size() + 1; pCount++) {
            Join<TestExecution, TestExecutionParameter> rParam = rExec.join("parameters");
            pParamsMatch = cb.and(pParamsMatch, cb.equal(rParam.get("name"), cb.parameter(String.class, "paramName" + pCount)));
            pParamsMatch = cb.and(pParamsMatch, cb.like(rParam.<String>get("value"), cb.parameter(String.class, "paramValue" + pCount)));
         }
      }
      // construct query
      criteria.where(cb.and(pIds, pStartedFrom, pStartedTo, pTagNameInFixedList, pExcludedTags, pTestName, pTestUID, pTestGroups, pParamsMatch));
      criteria.having(pHavingAllTagsPresent);
      // this isn't very elegant, but Postgres 8.4 doesn't allow GROUP BY only with id
      // this feature is allowed only since Postgres 9.1+
      criteria.groupBy(rExec.get("test"), rExec.get("id"), rExec.get("name"), rExec.get("started"), rExec.get("comment"));

      return criteria;
   }

   /**
    * Helper method. During the search of test executions, if there is a limit set, we have to count
    * the test executions suit the conditions.
    *
    * @param search
    * @param includedTags
    * @param excludedTags
    * @param userGroups
    * @return
    */
   private Integer processSearchCountQuery(TestExecutionSearchTO search, List<String> includedTags, List<String> excludedTags, List<String> userGroups) {
      CriteriaBuilder cb = criteriaBuilder();

      CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
      Root<TestExecution> root = countQuery.from(TestExecution.class);
      countQuery.select(cb.countDistinct(root));

      Subquery<Long> subquery = (Subquery) createSearchSubquery(countQuery.subquery(Long.class), search, includedTags, excludedTags);
      Root<TestExecution> subqueryRoot = (Root<TestExecution>) subquery.getRoots().toArray()[0];
      subquery.select(subqueryRoot.<Long>get("id"));

      countQuery.where(cb.in(root.get("id")).value(subquery));
      TypedQuery<Long> typedCountQuery = query(countQuery);
      fillParameterValues(typedCountQuery, search, includedTags, excludedTags, userGroups);

      Long count = typedCountQuery.getSingleResult();
      return count.intValue();
   }

   /**
    * Performs ordering on test executions by values of specified parameter.
    *
    * @param testExecutions
    * @param search
    */
   private void orderResultsByParameters(List<TestExecution> testExecutions, TestExecutionSearchTO search) {
      if (!Arrays.asList(OrderBy.PARAMETER_ASC,
                         OrderBy.PARAMETER_DESC,
                         OrderBy.VERSION_ASC,
                         OrderBy.VERSION_DESC).contains(search.getOrderBy())) {
         return;
      }

      List<Long> executionIds = testExecutions.stream().map(TestExecution::getId).collect(Collectors.toList());
      List<TestExecutionParameter> parameters = testExecutionParameterDAO.find(executionIds, Arrays.asList(search.getOrderByParameter()));
      Map<Long, List<TestExecutionParameter>> parametersByExecution = parameters.stream().collect(Collectors.groupingBy(parameter -> parameter.getTestExecution().getId()));

      Collections.sort(testExecutions,
                       (o1, o2) -> {
                          String o1paramValue = o1.getParameters().get(search.getOrderByParameter()).getValue();
                          int orderCoefficient = search.getOrderBy() == OrderBy.PARAMETER_ASC || search.getOrderBy() == OrderBy.VERSION_ASC ? 1 : -1;
                          if (o1paramValue == null) {
                             return orderCoefficient * 1;
                          }

                          return orderCoefficient * performCompare(o1paramValue, o2.getParameters().get(search.getOrderByParameter()).getValue(), search);
                       });
   }

   /**
    * According to selected ordering mechanism, this method performs the comparison.
    *
    * @param value1
    * @param value2
    * @param search
    * @return
    */
   private int performCompare(String value1, String value2, TestExecutionSearchTO search) {
      if (search.getOrderBy() == OrderBy.VERSION_ASC || search.getOrderBy() == OrderBy.VERSION_DESC) {
         DefaultArtifactVersion version1 = new DefaultArtifactVersion(value1);
         DefaultArtifactVersion version2 = new DefaultArtifactVersion(value2);

         return version1.compareTo(version2);
      }

      return value1.compareTo(value2);
   }

   /**
    * Helper method. Because when trying to retrieve count of test executions according to
    * some restrictions (like tags etc, in general when the query has having, where, group by together) via
    * Criteria API, there is no way to reuse the query even though it differs in two lines.
    *
    * Hence, the basics of the query are extracted to this method to avoid code duplication.
    *
    * @param criteriaQuery
    * @return
    */
   private AbstractQuery createSubqueryByTags(AbstractQuery criteriaQuery) {
      CriteriaBuilder cb = criteriaBuilder();

      AbstractQuery query = criteriaQuery;
      Root<TestExecution> rExec = query.from(TestExecution.class);
      Join<TestExecution, Test> rTest = rExec.join("test");
      Predicate pTestUID = rTest.<String>get("uid").in(cb.parameter(List.class, "testUID"));
      Join<TestExecution, Tag> rTag = rExec.join("tags");
      Predicate pTagNameInFixedList = rTag.get("name").in(cb.parameter(List.class, "tagList"));
      Predicate pHavingAllTagsPresent = cb.ge(cb.count(rTag.get("id")), cb.parameter(Long.class, "tagListSize"));

      query.where(cb.and(pTagNameInFixedList, pTestUID));
      query.having(pHavingAllTagsPresent);
      query.groupBy(rExec.get("test"), rExec.get("id"), rExec.get("name"), rExec.get("started"), rExec.get("comment"));

      return query;
   }

   /**
    * Helper method. Search query is quite complicated and has a lot of parameters. This method
    * assigns the value to every predefined parameter.
    *
    * @param query
    * @param search
    * @param includedTags
    * @param excludedTags
    * @param userGroups
    */
   private void fillParameterValues(TypedQuery query, TestExecutionSearchTO search, List<String> includedTags, List<String> excludedTags, List<String> userGroups) {
      if (search.getIds() != null) {
         query.setParameter("ids", search.getIds());
      }
      if (search.getStartedFrom() != null) {
         query.setParameter("startedFrom", search.getStartedFrom());
      }
      if (search.getStartedTo() != null) {
         query.setParameter("startedTo", search.getStartedTo());
      }
      if (!includedTags.isEmpty()) {
         query.setParameter("tagList", includedTags);
         query.setParameter("tagListSize", new Long(includedTags.size()));
      }
      if (!excludedTags.isEmpty()) {
         query.setParameter("excludedTagList", excludedTags);
      }
      if (search.getTestName() != null && !"".equals(search.getTestName())) {
         if (search.getTestName().endsWith("*")) {
            String pattern = search.getTestName().substring(0, search.getTestName().length() - 1).concat("%").toLowerCase();
            query.setParameter("testName", pattern);
         } else {
            query.setParameter("testName", search.getTestName().toLowerCase());
         }
      }
      if (search.getTestUID() != null && !"".equals(search.getTestUID())) {
         if (search.getTestUID().endsWith("*")) {
            String pattern = search.getTestUID().substring(0, search.getTestUID().length() - 1).concat("%").toLowerCase();
            query.setParameter("testUID", pattern);
         } else {
            query.setParameter("testUID", search.getTestUID().toLowerCase());
         }
      }
      if (GroupFilter.MY_GROUPS.equals(search.getGroupFilter())) {
         query.setParameter("groupNames", userGroups);
      }
      if (search.getParameters() != null && !search.getParameters().isEmpty()) {
         int pCount = 1;
         for (ParamCriteria paramCriteria : search.getParameters()) {
            query.setParameter("paramName" + pCount, paramCriteria.getName());
            query.setParameter("paramValue" + pCount, paramCriteria.getValue());
            pCount++;
         }
      }
   }

   /**
    * Helper method. Because when trying to retrieve count of test executions according to
    * some restrictions (like tags etc, in general when the query has having, where, group by together) via
    * Criteria API, there is no way to reuse the query even though it differs in two lines.
    *
    * Hence, the basics of the query are extracted to this method to avoid code duplication.
    *
    * @param criteriaQuery
    * @param testUIDs
    * @param tags
    * @return
    */
   private TypedQuery createTypedQueryByTags(CriteriaQuery criteriaQuery, List<String> testUIDs, List<String> tags) {
      TypedQuery<TestExecution> query = query(criteriaQuery);
      query.setParameter("testUID", testUIDs);
      query.setParameter("tagList", tags);
      query.setParameter("tagListSize", new Long(tags.size()));

      return query;
   }

   /**
    * Helper method. Divides the list of tags to two groups - included and excluded tags. Excluded tags have
    * prefix '-'. Divides and stores it into the parameters.
    *
    * @param inputTags
    * @param outputExcluded
    * @param outputIncluded
    * @return
    */
   private void divideTags(List<String> inputTags, List<String> outputIncluded, List<String> outputExcluded) {
      Iterator<String> iterator = inputTags.iterator();
      while (iterator.hasNext()) {
         String value = iterator.next();
         if (value.isEmpty()) {
            continue;
         }

         if (value.startsWith("-")) {
            outputExcluded.add(value.substring(1));
         } else {
            outputIncluded.add(value);
         }
      }
   }
}