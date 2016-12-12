package org.perfrepo.web.alerting;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.to.SearchResultWrapper;
import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.web.dao.TestExecutionDAO;
import org.perfrepo.web.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.perfrepo.web.alerting.TestUtils.createMetric;
import static org.perfrepo.web.alerting.TestUtils.createMultivalueTestExecution;
import static org.perfrepo.web.alerting.TestUtils.createMultivalueTestExecutionWithConstantGivenValue;
import static org.perfrepo.web.alerting.TestUtils.createMultivalueTestExecutionWithConstantValues;
import static org.perfrepo.web.alerting.TestUtils.createMultivalueTestExecutionWithLowerLastValue;
import static org.perfrepo.web.alerting.TestUtils.createMultivalueTestExecutionWithMoreIterations;
import static org.perfrepo.web.alerting.TestUtils.createMultivalueTestExecutionWithVaryingValues;
import static org.perfrepo.web.alerting.TestUtils.createMultivalueTestExecutionWithZeroValues;
import static org.perfrepo.web.alerting.TestUtils.createSearchCriteria;
import static org.perfrepo.web.alerting.TestUtils.createTestExecution1;
import static org.perfrepo.web.alerting.TestUtils.createTestExecution2;
import static org.perfrepo.web.alerting.TestUtils.createTestExecution3;
import static org.perfrepo.web.alerting.TestUtils.createTestExecution4;
import static org.perfrepo.web.alerting.TestUtils.createTestExecutionWithProvidedResult;

/**
 * Tests for {@link org.perfrepo.web.alerting.ConditionChecker}
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Ignore
public class ConditionCheckerTest {

    private ConditionCheckerImpl conditionChecker;

    /**
     * Initial preparation for the test, mocking TestExecutionDAO.
     */
    @Before
    public void init() {
        conditionChecker = new ConditionCheckerImpl();

        TestExecutionDAO mockedTestExecutionDAO = mock(TestExecutionDAO.class);
        List<TestExecution> te1 = Arrays.asList(createTestExecution1());
        List<TestExecution> te2 = Arrays.asList(createTestExecution2());

        List<TestExecution> te1And2 = new ArrayList<>();
        te1And2.addAll(te1);
        te1And2.addAll(te2);

        TestExecutionSearchTO searchTe1 = createSearchCriteria(Arrays.asList(1L), null, null, null, null, null);
        TestExecutionSearchTO searchTe2 = createSearchCriteria(Arrays.asList(2L), null, null, null, null, null);
        TestExecutionSearchTO searchTe1And2 = createSearchCriteria(Arrays.asList(1L, 2L), null, null, null, null, null);

        TestExecutionSearchTO searchLast1 = createSearchCriteria(null, null, 2, 1, null, null);
        TestExecutionSearchTO searchLast10 = createSearchCriteria(null, null, 11, 10, null, null);
        TestExecutionSearchTO search5FromLast10 = createSearchCriteria(null, null, 11, 5, null, null);

        when(mockedTestExecutionDAO.searchTestExecutions(searchTe1, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(te1, te1.size()));
        when(mockedTestExecutionDAO.searchTestExecutions(searchTe2, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(te2, te2.size()));
        when(mockedTestExecutionDAO.searchTestExecutions(searchLast1, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(te2, te2.size()));
        when(mockedTestExecutionDAO.searchTestExecutions(searchLast10, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(te1And2, te1And2.size()));
        when(mockedTestExecutionDAO.searchTestExecutions(search5FromLast10, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(te1And2, te1And2.size()));
        when(mockedTestExecutionDAO.searchTestExecutions(searchTe1And2, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(te1And2, te1And2.size()));

        List<TestExecution> tesWithTags = Arrays.asList(createTestExecution3(), createTestExecution4());

        TestExecutionSearchTO searchTesWithTags = createSearchCriteria(null, "firstTag secondTag", null, null, null, null);
        TestExecutionSearchTO searchTesWithTagsAndLast1 = createSearchCriteria(null, "firstTag secondTag", 2, 1, null, null);
        TestExecutionSearchTO searchTesWithTagsAnd2FromLast3 = createSearchCriteria(null, "firstTag secondTag", 4, 2, null, null);

        when(mockedTestExecutionDAO.searchTestExecutions(searchTesWithTags, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(tesWithTags, tesWithTags.size()));
        when(mockedTestExecutionDAO.searchTestExecutions(searchTesWithTagsAndLast1, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(te1, te1.size()));
        when(mockedTestExecutionDAO.searchTestExecutions(searchTesWithTagsAnd2FromLast3, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(tesWithTags, tesWithTags.size()));

        Calendar calendar = Calendar.getInstance();
        calendar.set(2015, 0, 1, 0, 0, 0);
        Date dateFrom = calendar.getTime();

        calendar.set(2015, 1, 1, 0, 0, 0);
        Date dateTo = calendar.getTime();

        TestExecutionSearchTO searchTesWithOnlyDateFrom = createSearchCriteria(null, null, null, null, dateFrom, null);
        TestExecutionSearchTO searchTesWithOnlyDateTo = createSearchCriteria(null, null, null, null, null, dateTo);
        TestExecutionSearchTO searchTesWithDates = createSearchCriteria(null, null, null, null, dateFrom, dateTo);
        TestExecutionSearchTO searchTesWithTagsAndDates = createSearchCriteria(null, "firstTag secondTag", null, null, dateFrom, dateTo);

        List<TestExecution> tesWithTagsAndDates = Arrays.asList(createTestExecution1(), createTestExecution4());

        when(mockedTestExecutionDAO.searchTestExecutions(argThat(new SearchCriteriaMatcher(searchTesWithOnlyDateFrom)), eq(Arrays.asList("testuser")))).thenReturn(new SearchResultWrapper<>(tesWithTags, tesWithTags.size()));
        when(mockedTestExecutionDAO.searchTestExecutions(argThat(new SearchCriteriaMatcher(searchTesWithOnlyDateTo)), eq(Arrays.asList("testuser")))).thenReturn(new SearchResultWrapper<>(tesWithTags, tesWithTags.size()));
        when(mockedTestExecutionDAO.searchTestExecutions(argThat(new SearchCriteriaMatcher(searchTesWithDates)), eq(Arrays.asList("testuser")))).thenReturn(new SearchResultWrapper<>(tesWithTagsAndDates, tesWithTagsAndDates.size()));
        when(mockedTestExecutionDAO.searchTestExecutions(argThat(new SearchCriteriaMatcher(searchTesWithTagsAndDates)), eq(Arrays.asList("testuser")))).thenReturn(new SearchResultWrapper<>(tesWithTagsAndDates, tesWithTagsAndDates.size()));

        // Multi value test executions
        List<TestExecution> multivalueSimple = Arrays.asList(createMultivalueTestExecution());
        List<TestExecution> multivalueMoreExecInVariable = Arrays.asList(createMultivalueTestExecution(), createMultivalueTestExecutionWithLowerLastValue());
        List<TestExecution> multivalueComplex = Arrays.asList(createMultivalueTestExecutionWithVaryingValues(), createMultivalueTestExecutionWithVaryingValues());
        List<TestExecution> groupingSimple = Arrays.asList(createMultivalueTestExecutionWithConstantGivenValue(95d), createMultivalueTestExecutionWithConstantGivenValue(105d));

        TestExecutionSearchTO multivalueSimpleTO = createSearchCriteria(Arrays.asList(100L), null, null, null, null, null);
        TestExecutionSearchTO multivalueMoreExecInVariableTO = createSearchCriteria(Arrays.asList(100L, 101L), null, null, null, null, null);
        TestExecutionSearchTO multivalueComplexTO = createSearchCriteria(Arrays.asList(110L, 111L), null, null, null, null, null);
        TestExecutionSearchTO groupingSimpleTO = createSearchCriteria(Arrays.asList(112L, 113L), null, null, null, null, null);
        TestExecutionSearchTO multiTasAndLastOneTO = createSearchCriteria(null, "epicTag legendaryTag", 2, 1, null, null);

        when(mockedTestExecutionDAO.searchTestExecutions(multivalueSimpleTO, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(multivalueSimple, multivalueSimple.size()));
        when(mockedTestExecutionDAO.searchTestExecutions(multivalueMoreExecInVariableTO, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(multivalueMoreExecInVariable, multivalueMoreExecInVariable.size()));
        when(mockedTestExecutionDAO.searchTestExecutions(multivalueComplexTO, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(multivalueComplex, multivalueComplex.size()));
        when(mockedTestExecutionDAO.searchTestExecutions(groupingSimpleTO, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(groupingSimple, groupingSimple.size()));
        when(mockedTestExecutionDAO.searchTestExecutions(multiTasAndLastOneTO, Arrays.asList("testuser"))).thenReturn(new SearchResultWrapper<>(multivalueSimple, multivalueSimple.size()));

        UserService mockedUserService = mock(UserService.class);
        //TODO: solve this
        //when(mockedUserService.getLoggedUserGroupNames()).thenReturn(Arrays.asList("testuser"));

        conditionChecker.setTestExecutionDAO(mockedTestExecutionDAO);
        conditionChecker.setUserService(mockedUserService);
    }

    @Test
    public void testWrongSyntax() {
        String condition = "CONDITION x < 1 x = SELECT WHERE id = 1";
        try {
            conditionChecker.checkConditionSyntax(condition, createMetric());
            fail("Query without DEFINE should fail.");
        } catch (IllegalArgumentException ex) {
        } //expected

        condition = "x < 1 DEFINE x = SELECT WHERE id = 1";
        try {
            conditionChecker.checkConditionSyntax(condition, createMetric());
            fail("Query without CONDITION should fail.");
        } catch (IllegalArgumentException ex) {
        } //expected

        condition = "CONDITION x < 1 DEFINE x = (SELECT WHERE id = 1) y = (SELECT WHERE id = 1)";
        try {
            conditionChecker.checkConditionSyntax(condition, createMetric());
            fail("Query, where definitions are not separated by comma, should fail.");
        } catch (IllegalArgumentException ex) {
        } //expected
    }

    @Test
    public void testWrongSyntaxMultivalue() {
        // only one variable in DEFINE section is permitted
        String condition = "MULTIVALUE CONDITION x < 1 DEFINE x = (SELECT WHERE id = 1), y = (SELECT WHERE id = 2)";
        try {
            conditionChecker.checkConditionSyntax(condition, createMetric());
            fail("Query for multivalue test with multiple variables defined should fail.");
        } catch (IllegalArgumentException ex) {
        } //expected

        // badly used STRICT keyword
        condition = "STRICT MULTIVALUE CONDITION x < 1 DEFINE x = (SELECT WHERE id = 1)";
        try {
            conditionChecker.checkConditionSyntax(condition, createMetric());
            fail("Query for multivalue test with misplaced 'STRICT' keyword should fail.");
        } catch (IllegalArgumentException ex) {
        } //expected

        // no DEFINE
        condition = "MULTIVALUE CONDITION x < 1 x = (SELECT WHERE id = 1)";
        try {
            conditionChecker.checkConditionSyntax(condition, createMetric());
            fail("Query for multivalue test with no 'DEFINE' keyword should fail.");
        } catch (IllegalArgumentException ex) {
        } //expected

        // no CONDITION
        condition = "MULTIVALUE STRICT x < 1 DEFINE x = (SELECT WHERE id = 1)";
        try {
            conditionChecker.checkConditionSyntax(condition, createMetric());
            fail("Query for multivalue test with no 'CONDITION' keyword should fail.");
        } catch (IllegalArgumentException ex) {
        } //expected
    }

    @Test
    public void testWrongSyntaxMultivalueGrouping() {
        // using 'STRICT' in grouping multivalue test
        String condition = "MULTIVALUE GROUPING STRICT CONDITION x < 1 DEFINE x = AVG(SELECT WHERE id = 1)";
        try {
            conditionChecker.checkConditionSyntax(condition, createMetric());
            fail("Query for multivalue grouping test with 'STRICT' keyword should fail.");
        } catch (IllegalArgumentException ex) {
        } //expected

        // no CONDITION
        condition = "MULTIVALUE GROUPING x < 1 DEFINE x = AVG(SELECT WHERE id = 1)";
        try {
            conditionChecker.checkConditionSyntax(condition, createMetric());
            fail("Query for multivalue test with no 'CONDITION' keyword should fail.");
        } catch (IllegalArgumentException ex) {
        } //expected

        // no DEFINE
        condition = "MULTIVALUE GROUPING CONDITION x < 1 x = AVG(SELECT WHERE id = 1)";
        try {
            conditionChecker.checkConditionSyntax(condition, createMetric());
            fail("Query for multivalue test with no 'DEFINE' keyword should fail.");
        } catch (IllegalArgumentException ex) {
        } //expected

        // using more than one grouping function in DEFINE
        condition = "MULTIVALUE GROUPING CONDITION x < 1 DEFINE x = AVG(SELECT WHERE id = 1), y = MAX(SELECT WHERE id = 2)";
        try {
            conditionChecker.checkConditionSyntax(condition, createMetric());
            fail("Query for multivalue test with multiple grouping functions should fail.");
        } catch (IllegalArgumentException ex) {
        } //expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongSyntaxMultiSelectWithoutGroupFunction() {
        String condition = "CONDITION x == result DEFINE x = (SELECT WHERE id IN (1,2))";
        conditionChecker.checkConditionSyntax(condition, createMetric());
    }

    @Test
    public void testSimpleSelect() {
        String condition = "CONDITION x > 10 DEFINE x = (SELECT WHERE id = 1)";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(0d), createMetric()));

        condition = "CONDITION x < 10 DEFINE x = (SELECT WHERE id = 1)";
        assertFalse(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(0d), createMetric()));
    }

    @Test
    public void testSimpleSelectSimpleLast() {
        String condition = "CONDITION x > 10 DEFINE x = (SELECT LAST 1)";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(0d), createMetric()));

        condition = "CONDITION x < 10 DEFINE x = (SELECT LAST 1)";
        assertFalse(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(0d), createMetric()));
    }

    @Test
    public void testSimpleSelectOptionalParentheses() {
        String condition = "CONDITION x > 10 DEFINE x = SELECT WHERE id = 1";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(0d), createMetric()));

        condition = "CONDITION x < 10 DEFINE x = SELECT WHERE id = 1";
        assertFalse(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(0d), createMetric()));
    }

    @Test
    public void testManySimpleSelects() {
        String condition = "CONDITION x > 10 && result > 0.95*y DEFINE x = (SELECT WHERE id = 1), y = (SELECT WHERE id = 2)";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(100d), createMetric()));

        condition = "CONDITION x > 10 && result > (0.95*y)+10 DEFINE x = (SELECT WHERE id = 1), y = (SELECT WHERE id = 2)";
        assertFalse(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(100d), createMetric()));
    }

    @Test
    public void testMultiSelectSimpleLast() {
        String condition = "CONDITION x == result DEFINE x = AVG(SELECT LAST 10)";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(56d), createMetric()));
    }

    @Test
    public void testMultiSelectIntervalLast() {
        String condition = "CONDITION x == result DEFINE x = AVG(SELECT LAST 10, 5)";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(56d), createMetric()));
    }

    @Test
    public void testMultiSelectInWhere() {
        String condition = "CONDITION x == result DEFINE x = AVG(SELECT WHERE id IN (1,2))";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(56d), createMetric()));
    }

    @Test
    public void testGroupFunctionMin() {
        String condition = "CONDITION x == result DEFINE x = MIN(SELECT WHERE id IN (1,2))";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(12d), createMetric()));
    }

    @Test
    public void testGroupFunctionMax() {
        String condition = "CONDITION x == result DEFINE x = MAX(SELECT WHERE id IN (1,2))";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(100d), createMetric()));
    }

    @Test
    public void testSelectWithTags() {
        String condition = "CONDITION x == result DEFINE x = MAX(SELECT WHERE tags = \"firstTag secondTag\")";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(1001d), createMetric()));
    }

    @Test
    public void testSelectWithTagsAndSimpleLast() {
        String condition = "CONDITION x == result DEFINE x = MAX(SELECT WHERE tags = \"firstTag secondTag\" LAST 1)";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(12d), createMetric()));
    }

    @Test
    public void testSimpleSelectWithTagsAndSimpleLast() {
        String condition = "CONDITION x == result DEFINE x = (SELECT WHERE tags = \"firstTag secondTag\" LAST 1)";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(12d), createMetric()));
    }

    @Test
    public void testSelectWithTagsAndMultiLast() {
        String condition = "CONDITION x == result DEFINE x = MAX(SELECT WHERE tags = \"firstTag secondTag\" LAST 3, 2)";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(1001d), createMetric()));

        condition = "CONDITION x == result DEFINE x = MIN(SELECT WHERE tags = \"firstTag secondTag\" LAST 3, 2)";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(150d), createMetric()));
    }

    @Test
    public void testSelectWithOnlyDateFrom() {
        String condition = "CONDITION x == result DEFINE x = MAX(SELECT WHERE date >= \"2015-01-01 00:00\")";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(1001d), createMetric()));

        condition = "CONDITION x == result DEFINE x = MIN(SELECT WHERE date >= \"2015-01-01 00:00\")";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(150d), createMetric()));
    }

    @Test
    public void testSelectWithOnlyDateTo() {
        String condition = "CONDITION x == result DEFINE x = MAX(SELECT WHERE date <= \"2015-02-01 00:00\")";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(1001d), createMetric()));

        condition = "CONDITION x == result DEFINE x = MIN(SELECT WHERE date <= \"2015-02-01 00:00\")";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(150d), createMetric()));
    }

    @Test
    public void testSelectWithDates() {
        String condition = "CONDITION x == result DEFINE x = MAX(SELECT WHERE date >= \"2015-01-01 00:00\" AND date <= \"2015-02-01 00:00\")";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(1001d), createMetric()));

        condition = "CONDITION x == result DEFINE x = MIN(SELECT WHERE date >= \"2015-01-01 00:00\" AND date <= \"2015-02-01 00:00\")";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(12d), createMetric()));
    }

    @Test
    public void testSelectWithTagsAndDates() {
        String condition = "CONDITION x == result DEFINE x = MAX(SELECT WHERE tags = \"firstTag secondTag\" AND date >= \"2015-01-01 00:00\" AND date <= \"2015-02-01 00:00\")";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(1001d), createMetric()));

        condition = "CONDITION x == result DEFINE x = MIN(SELECT WHERE tags = \"firstTag secondTag\" AND date >= \"2015-01-01 00:00\" AND date <= \"2015-02-01 00:00\")";
        assertTrue(conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(12d), createMetric()));
    }

    @Test
    public void testMultiValueSimple() {
        // simple multivalue that holds true
        String condition = "MULTIVALUE CONDITION x == result DEFINE x = SELECT WHERE id = 100";
        assertTrue(conditionChecker.checkCondition(condition, createMultivalueTestExecution(), createMetric()));

        // simple multivalue that is false
        condition = "MULTIVALUE CONDITION x == result DEFINE x = SELECT WHERE id = 100";
        assertFalse(conditionChecker.checkCondition(condition, createMultivalueTestExecutionWithLowerLastValue(), createMetric()));
    }

    @Test
    public void testMultiValueMoreExecutionsInVariable() {
        // multivalue with more executions hidden in x variable, holds true
        String condition = "MULTIVALUE CONDITION result < x DEFINE x = SELECT WHERE id IN (100, 101)";
        assertTrue(conditionChecker.checkCondition(condition, createMultivalueTestExecutionWithZeroValues(), createMetric()));

        // multivalue with more executions hidden in x variable, holds false
        condition = "MULTIVALUE CONDITION result > x DEFINE x = SELECT WHERE id IN (100, 101)";
        assertFalse(conditionChecker.checkCondition(condition, createMultivalueTestExecutionWithZeroValues(), createMetric()));
    }

    @Test
    public void testMultiValueStrict() {
        // number of iterations in compared tests are not equal, hence it should fail
        String condition = "MULTIVALUE STRICT CONDITION result >= x DEFINE x = SELECT WHERE id IN (100, 101)";
        assertFalse(conditionChecker.checkCondition(condition, createMultivalueTestExecutionWithMoreIterations(), createMetric()));

        // just to make sure - same case without STRICT - should pass
        condition = "MULTIVALUE CONDITION result >= x DEFINE x = SELECT WHERE id IN (100, 101)";
        assertTrue(conditionChecker.checkCondition(condition, createMultivalueTestExecutionWithMoreIterations(), createMetric()));
    }

    @Test
    public void testMultiValueComplex() {
        // result has to be in 5% tolerance from x, x contains multiple executions; will fail since one execution does not pass STRICT condition
        String condition = "MULTIVALUE STRICT CONDITION result >= (0.95 * x) && result <= (1.05 * x) DEFINE x = SELECT WHERE id IN (110, 111)";
        assertFalse(conditionChecker.checkCondition(condition, createMultivalueTestExecutionWithConstantValues(), createMetric()));

        // non strict version, should pass
        condition = "MULTIVALUE CONDITION result >= (0.95 * x) && result <= (1.05 * x) DEFINE x = SELECT WHERE id IN (110, 111)";
        assertTrue(conditionChecker.checkCondition(condition, createMultivalueTestExecutionWithConstantValues(), createMetric()));

        // condition will not hold in this case
        condition = "MULTIVALUE CONDITION result >= (0.95 * x) && result <= (1.05 * x) DEFINE x = SELECT WHERE id IN (110, 111)";
        assertFalse(conditionChecker.checkCondition(condition, createMultivalueTestExecutionWithZeroValues(), createMetric()));
    }

    @Test
    public void testMultiValueGroupingSimple() {
        String condition = "MULTIVALUE GROUPING CONDITION result == x DEFINE x = AVG(SELECT WHERE id IN (112, 113))";
        assertTrue(conditionChecker.checkCondition(condition, createMultivalueTestExecutionWithConstantGivenValue(100d), createMetric()));

        condition = "MULTIVALUE GROUPING CONDITION result < x DEFINE x = AVG(SELECT WHERE id IN (112, 113))";
        assertFalse(conditionChecker.checkCondition(condition, createMultivalueTestExecutionWithConstantGivenValue(100d), createMetric()));
    }

    @Test
    public void testMultiValueGroupingComplex() {
        String condition = "MULTIVALUE GROUPING CONDITION result == x && result < y DEFINE x = AVG(SELECT WHERE id IN (112, 113)), y = AVG(SELECT WHERE id IN(100, 101))";
        assertTrue(conditionChecker.checkCondition(condition, createMultivalueTestExecutionWithConstantGivenValue(100d), createMetric()));

        // break condition
        condition = "MULTIVALUE GROUPING CONDITION result == x && result > y DEFINE x = AVG(SELECT WHERE id IN (112, 113)), y = AVG(SELECT WHERE id IN(100, 101))";
        assertFalse(conditionChecker.checkCondition(condition, createMultivalueTestExecutionWithConstantGivenValue(100d), createMetric()));
    }

    @Test
    public void testInvalidCombinationOfExecutionTypeWithCondition() {
        String condition = "MULTIVALUE GROUPING CONDITION result == x && result < y DEFINE x = AVG(SELECT WHERE id IN (112, 113)), y = AVG(SELECT WHERE id IN(100, 101))";
        try {
            conditionChecker.checkCondition(condition, createTestExecutionWithProvidedResult(5d), createMetric());
            fail("Condition with MULTIVALUE triggered on a newly added single value test should throw IAE!");
        } catch (IllegalArgumentException e) {//expected
        }

        condition = "CONDITION x < y DEFINE x = AVG(SELECT WHERE id IN (112, 113))";
        try {
            assertFalse(conditionChecker.checkCondition(condition, createMultivalueTestExecution(), createMetric()));
            fail("Condition without MULTIVALUE triggered on a newly added multivalue value test should throw IAE!");
        } catch (IllegalArgumentException e) {//expected
        }
    }

    @Test
    public void testValidSyntaxChecks() {
        // just a few valid syntax-only checks
        String condition = "MULTIVALUE GROUPING CONDITION result == x && result < y DEFINE x = AVG(SELECT WHERE id IN (112, 113)), y = AVG(SELECT WHERE id IN(100, 101))";
        conditionChecker.checkConditionSyntax(condition, createMetric());

        condition = "MULTIVALUE CONDITION result == x DEFINE x = SELECT WHERE tags = \"epicTag legendaryTag\" LAST 1";
        conditionChecker.checkConditionSyntax(condition, createMetric());
        
        condition = "MULTIVALUE STRICT CONDITION result == x DEFINE x = SELECT WHERE tags = \"epicTag legendaryTag\" LAST 1";
        conditionChecker.checkConditionSyntax(condition, createMetric());
    }
}
