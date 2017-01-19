package org.perfrepo.web.alerting;

import org.perfrepo.web.model.Metric;
import org.perfrepo.web.model.TestExecution;
import org.perfrepo.web.model.Value;
import org.perfrepo.web.model.ValueParameter;
import org.perfrepo.web.service.search.TestExecutionSearchCriteria;
import org.perfrepo.web.model.user.Group;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Provides some utility functionality for tests. Namely allows to create dummy TestExecution instances for mock tests
 * as well as Metric and all other needed stuff.
 *
 * @author <a href="mailto:manovotn@redhat.com">Matej Novotny</a>
 */
public class TestUtils {

    // private constructor
    private TestUtils() {
    }

    public static TestExecution createTestExecutionWithProvidedResult(Double result) {
        Value value = new Value();
        value.setResultValue(result);
        value.setMetric(createMetric());

        Collection<Value> values = new ArrayList<>();
        values.add(value);

        TestExecution te = new TestExecution();
        //TODO: solve this
        //te.setValues(values);

        return te;
    }

    public static TestExecution createTestExecution1() {
        return createTestExecutionWithProvidedResult(12d);
    }

    public static TestExecution createTestExecution2() {
        return createTestExecutionWithProvidedResult(100d);
    }

    public static TestExecution createTestExecution3() {
        return createTestExecutionWithProvidedResult(150d);
    }

    public static TestExecution createTestExecution4() {
        return createTestExecutionWithProvidedResult(1001d);
    }

    public static org.perfrepo.web.model.Test createTest() {
        org.perfrepo.web.model.Test test = new org.perfrepo.web.model.Test();
        test.setId(1L);
        test.setUid("test_test");

        return test;
    }

    public static Metric createMetric() {
        return createMetricWithGivenName("metric1");
    }
    
    public static Metric createMetricWithGivenName(String name) {
        Metric metric = new Metric();
        metric.setName(name);

        return metric;
    }

    public static TestExecutionSearchCriteria createSearchCriteria(List<Long> ids, String tags, Integer limitFrom, Integer limitHowMany, Date dateFrom, Date dateTo) {
        TestExecutionSearchCriteria searchCriteria = new TestExecutionSearchCriteria();
        searchCriteria.setIds(new HashSet<>(ids));
        searchCriteria.setTagsQuery(tags);
        searchCriteria.setLimitFrom(limitFrom);
        searchCriteria.setLimitHowMany(limitHowMany);
        searchCriteria.setStartedAfter(dateFrom);
        searchCriteria.setStartedBefore(dateTo);

        Group group = new Group();
        group.setName("testuser");
        searchCriteria.setGroups(new HashSet<>(Arrays.asList(group)));

        Group group2 = new Group();
        group.setName("testuser");
        searchCriteria.setGroups(new HashSet<>(Arrays.asList(group2)));

        return searchCriteria;
    }

    public static TestExecution createMultivalueTestExecution() {
        TestExecution testExecution = new TestExecution();
        Metric metric = createMetric();
        Metric additionalMetric = createMetricWithGivenName("useless");
        String paramName = "iteration";
        String additionalParam = "useless";

        for (int i = 1; i < 11; i++) {
            // `i` will be a number of iteration and base for value
            addValue(i * 100d, metric, paramName, String.valueOf(i), testExecution);
            // add another complex value with nonsence params, just for test purposes
            addValue(Double.valueOf(i), additionalMetric, additionalParam, String.valueOf(new Random(i).nextInt()), testExecution);
        }

        return testExecution;
    }
    
    public static TestExecution createMultivalueTestExecutionWithMoreIterations() {
        TestExecution testExecution = new TestExecution();
        Metric metric = createMetric();
        Metric additionalMetric = createMetricWithGivenName("useless");
        String paramName = "iteration";
        String additionalParam = "useless";

        for (int i = 1; i < 20; i++) {
            // `i` will be a number of iteration and base for value
            addValue(i * 100d, metric, paramName, String.valueOf(i), testExecution);
            // add another complex value with nonsence params, just for test purposes
            addValue(Double.valueOf(i), additionalMetric, additionalParam, String.valueOf(new Random(i).nextInt()), testExecution);
        }

        return testExecution;
    }
    
    public static TestExecution createMultivalueTestExecutionWithLowerLastValue() {
        TestExecution testExecution = new TestExecution();
        Metric metric = createMetric();
        Metric additionalMetric = createMetricWithGivenName("useless");
        String paramName = "iteration";
        String additionalParam = "useless";

        for (int i = 1; i < 11; i++) {
            // `i` will be a number of iteration and base for value
            if (i == 9) { 
                // drop in increasing values
                addValue(20d, metric, paramName, String.valueOf(i), testExecution);
            } else {
                addValue(i * 100d, metric, paramName, String.valueOf(i), testExecution);
            }
            // add another complex value with nonsence params, just for test purposes
            addValue(Double.valueOf(i), additionalMetric, additionalParam, String.valueOf(new Random(i).nextInt()), testExecution);
        }

        return testExecution;
    }
    
    public static TestExecution createMultivalueTestExecutionWithZeroValues() {
        TestExecution testExecution = new TestExecution();
        Metric metric = createMetric();
        Metric additionalMetric = createMetricWithGivenName("useless");
        String paramName = "iteration";
        String additionalParam = "useless";

        for (int i = 0; i < 10; i++) {
            // `i` will be a number of iteration and base for value
            addValue(0d, metric, paramName, String.valueOf(i), testExecution);
            // add another complex value with nonsence params, just for test purposes
            addValue(Double.valueOf(i), additionalMetric, additionalParam, String.valueOf(new Random(i).nextInt()), testExecution);
        }

        return testExecution;
    }
    
    public static TestExecution createMultivalueTestExecutionWithVaryingValues() {
        TestExecution testExecution = new TestExecution();
        Metric metric = createMetric();
        String paramName = "iteration";

        Random random = new Random();
        for (int i = 1; i < 20; i++) {
            // randomly generate values from 95 to 105, there will be more than 10 iterations
            double value = random.nextInt((102 - 98) + 1) + 100;
            addValue(value, metric, paramName, String.valueOf(i), testExecution);
        }

        return testExecution;
    }
    
    public static TestExecution createMultivalueTestExecutionWithConstantValues() {
        return createMultivalueTestExecutionWithConstantGivenValue(100d);
    }
    
    public static TestExecution createMultivalueTestExecutionWithConstantGivenValue(Double value) {
        TestExecution testExecution = new TestExecution();
        Metric metric = createMetric();
        Metric additionalMetric = createMetricWithGivenName("useless");
        String paramName = "iteration";
        String additionalParam = "useless";

        for (int i = 1; i < 11; i++) {
            // put 100d all the time
            addValue(value, metric, paramName, String.valueOf(i), testExecution);
            // add another complex value with nonsence params, just for test purposes
            addValue(Double.valueOf(i), additionalMetric, additionalParam, String.valueOf(new Random(i).nextInt()), testExecution);
        }

        return testExecution;
    }

    private static void addValue(double resultValue, Metric metric, String valueParamName, String valueParamValue, TestExecution testExecution) {
        Value value = new Value();
        value.setResultValue(resultValue);
        value.setMetric(metric);

        ValueParameter parameter = new ValueParameter();
        parameter.setName(valueParamName);
        parameter.setParamValue(valueParamValue);
        parameter.setValue(value);

        value.getParameters().put(valueParamName, parameter);
        testExecution.getValues().add(value);
    }
}
