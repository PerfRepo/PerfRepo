package org.perfrepo.test.alerting;

import org.perfrepo.model.Metric;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.Value;
import org.perfrepo.model.builder.TestExecutionBuilder;
import org.perfrepo.model.to.TestExecutionSearchTO;

import java.util.*;

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

    public static org.perfrepo.model.Test createTest() {
        org.perfrepo.model.Test test = new org.perfrepo.model.Test();
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

    public static TestExecutionSearchTO createSearchCriteria(List<Long> ids, String tags, Integer limitFrom, Integer limitHowMany, Date dateFrom, Date dateTo) {
        TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
        searchCriteria.setIds(ids);
        searchCriteria.setTags(tags);
        searchCriteria.setLimitFrom(limitFrom);
        searchCriteria.setLimitHowMany(limitHowMany);
        searchCriteria.setStartedFrom(dateFrom);
        searchCriteria.setStartedTo(dateTo);

        return searchCriteria;
    }

    public static TestExecution createMultivalueTestExecution() {
        // laziness rules -> use builder to create multi value executions ;-)
        TestExecutionBuilder builder = TestExecution.builder();
        Metric metric = createMetric();
        Metric additionalMetric = createMetricWithGivenName("useless");
        String paramName = "iteration";
        String additionalParam = "useless";

        for (int i = 1; i < 11; i++) {
            // `i` will be a number of iteration and base for value
            builder.value(metric.getName(), i * 100d, paramName, String.valueOf(i));
            // add another complex value with nonsence params, just for test purposes
            builder.value(additionalMetric.getName(), Double.valueOf(i), additionalParam, String.valueOf(new Random(i).nextInt()));
        }

        TestExecution result = builder.build();
        return result;
    }
    
    public static TestExecution createMultivalueTestExecutionWithMoreIterations() {
        // laziness rules -> use builder to create multi value executions ;-)
        TestExecutionBuilder builder = TestExecution.builder();
        Metric metric = createMetric();
        Metric additionalMetric = createMetricWithGivenName("useless");
        String paramName = "iteration";
        String additionalParam = "useless";

        for (int i = 1; i < 20; i++) {
            // `i` will be a number of iteration and base for value
            builder.value(metric.getName(), i * 100d, paramName, String.valueOf(i));
            // add another complex value with nonsence params, just for test purposes
            builder.value(additionalMetric.getName(), Double.valueOf(i), additionalParam, String.valueOf(new Random(i).nextInt()));
        }

        TestExecution result = builder.build();
        return result;
    }
    
    public static TestExecution createMultivalueTestExecutionWithLowerLastValue() {
        // laziness rules -> use builder to create multi value executions ;-)
        TestExecutionBuilder builder = TestExecution.builder();
        Metric metric = createMetric();
        Metric additionalMetric = createMetricWithGivenName("useless");
        String paramName = "iteration";
        String additionalParam = "useless";

        for (int i = 1; i < 11; i++) {
            // `i` will be a number of iteration and base for value
            if (i == 9) { 
                // drop in increasing values
                builder.value(metric.getName(), 20d, paramName, String.valueOf(i));
            } else {
                builder.value(metric.getName(), i * 100d, paramName, String.valueOf(i));
            }
            // add another complex value with nonsence params, just for test purposes
            builder.value(additionalMetric.getName(), Double.valueOf(i), additionalParam, String.valueOf(new Random(i).nextInt()));
        }

        TestExecution result = builder.build();
        return result;
    }
    
    public static TestExecution createMultivalueTestExecutionWithZeroValues() {
        // laziness rules -> use builder to create multi value executions ;-)
        TestExecutionBuilder builder = TestExecution.builder();
        Metric metric = createMetric();
        Metric additionalMetric = createMetricWithGivenName("useless");
        String paramName = "iteration";
        String additionalParam = "useless";

        for (int i = 0; i < 10; i++) {
            // `i` will be a number of iteration and base for value
            builder.value(metric.getName(), 0d, paramName, String.valueOf(i));
            // add another complex value with nonsence params, just for test purposes
            builder.value(additionalMetric.getName(), Double.valueOf(i), additionalParam, String.valueOf(new Random(i).nextInt()));
        }

        TestExecution result = builder.build();
        return result;
    }
    
    public static TestExecution createMultivalueTestExecutionWithVaryingValues() {
        // laziness rules -> use builder to create multi value executions ;-)
        TestExecutionBuilder builder = TestExecution.builder();
        Metric metric = createMetric();
        String paramName = "iteration";

        Random random = new Random();
        for (int i = 1; i < 20; i++) {
            // randomly generate values from 95 to 105, there will be more than 10 iterations
            double value = random.nextInt((102 - 98) + 1) + 100;
            builder.value(metric.getName(), value, paramName, String.valueOf(i));
        }

        TestExecution result = builder.build();
        return result;
    }
    
    public static TestExecution createMultivalueTestExecutionWithConstantValues() {
        return createMultivalueTestExecutionWithConstantGivenValue(100d);
    }
    
    public static TestExecution createMultivalueTestExecutionWithConstantGivenValue(Double value) {
        // laziness rules -> use builder to create multi value executions ;-)
        TestExecutionBuilder builder = TestExecution.builder();
        Metric metric = createMetric();
        Metric additionalMetric = createMetricWithGivenName("useless");
        String paramName = "iteration";
        String additionalParam = "useless";

        for (int i = 1; i < 11; i++) {
            // put 100d all the time
            builder.value(metric.getName(), value, paramName, String.valueOf(i));
            // add another complex value with nonsence params, just for test purposes
            builder.value(additionalMetric.getName(), Double.valueOf(i), additionalParam, String.valueOf(new Random(i).nextInt()));
        }

        TestExecution result = builder.build();
        return result;
    }
}
