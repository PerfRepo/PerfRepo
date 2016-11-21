package org.perfrepo.web.alerting;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.perfrepo.model.Metric;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.builder.TestExecutionBuilder;
import org.perfrepo.model.to.TestExecutionSearchTO;
import org.perfrepo.web.dao.TestExecutionDAO;
import org.perfrepo.web.service.UserService;
import org.perfrepo.web.util.MultiValue;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of @link{ConditionChecker}
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 * @author Matej Novotny (manovotn@redhat.com)
 */
@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ConditionCheckerImpl implements ConditionChecker {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Inject
    private TestExecutionDAO testExecutionDAO;

    @Inject
    private UserService userService;

    private String expression;
    // maps variable name to the list of relevant (sorted) values from executions, only for multi valued tests
    private Map<String, List<MultiValue.ValueInfo>> varToListOfValues;
    // latest execution stored as a List of ValueInfo
    private MultiValue.ValueInfo currentExecutionValueInfo;
    // results of NON multivalue conditions are stored here as well as results of multivaure grouping conditions
    private Map<String, Object> simpleVariables;
    // a parameter bound to metric which we are evaluating, there might be multiple, ATM we take first
    private String paramBoundToMetric;
    private Metric metric;

    // variables in condition which failed, used in reporting
    private Map<String, Object> failedEvaluationVariables;

    // following values help to determine what kind of evaluation are we dealing with
    private boolean isMultivalue;
    private boolean isMultivalueGrouping;
    private boolean isStrict;

    @Override
    public void checkConditionSyntax(String condition, Metric metric) {
        // creates dummy execution and triggers evaluation against it
        // if we had a 'perfect' grammar, we would only need to call parseTree(condition);
        // but ATM we need script engine to evaluate CONDITION and tell us if there were any errors
        // e.g. current grammar cannot catch nonsenses such as: CONDITION x <!= 10
        TestExecution testExecution;
        TestExecutionBuilder builder = TestExecution.builder();
        if (condition.trim().startsWith("MULTIVALUE")) {           
            String paramName = "foo";
            builder.value(metric.getName(), 0d, paramName, "0");          
            builder.value(metric.getName(), 1d, paramName, "1");          
        } else {
            builder.value(metric.getName(), 0d);
        }
        testExecution = builder.build();
        checkCondition(condition, testExecution, metric);
    }

    @Override
    public boolean checkCondition(String condition, TestExecution currentResult, Metric metric) {
        this.metric = metric;
        this.isMultivalue = false;
        this.isMultivalueGrouping = false;
        this.isStrict = false;
        // store current result separately to easily access it
        currentExecutionValueInfo = MultiValue.find(MultiValue.createFrom(currentResult), this.metric.getName());
        if (!(currentExecutionValueInfo.isMultiValue() == condition.contains("MULTIVALUE"))) {
            throw new IllegalArgumentException("Multivalue execution cannot be bound to non-multivalue condition and vice versa.");
        }
        // TODO - currently we will forcibly use the first param found, this could be enhanced in the future
        if (currentExecutionValueInfo.isMultiValue() && currentExecutionValueInfo.getComplexValueParams().size() >= 1) {
            paramBoundToMetric = currentExecutionValueInfo.getComplexValueParams().get(0);
        }
        
        expression = null;
        varToListOfValues = new HashMap<>();
        simpleVariables = new HashMap<>();
        failedEvaluationVariables = new HashMap<>();

        CommonTree ast = parseTree(condition);
        walkTree(ast);

        if (expression == null) {
            throw new IllegalStateException("Condition was not correctly parsed.");
        }

        // depending on what was the alert expression, the evaluation differs
        if (isMultivalue) {
            if (isMultivalueGrouping) {
                // at this point we have calculated grouping functions for current execution and all variables
                return evaluate(expression, simpleVariables);
            }
            // list of values in current execution
            List<MultiValue.ParamInfo> resultValueList = currentExecutionValueInfo.getComplexValueByParamName(paramBoundToMetric);
            // check the strict requirement
            if (isStrict) {
                for (String var : varToListOfValues.keySet()) {
                    for (MultiValue.ValueInfo valInfo : varToListOfValues.get(var)) {
                        // we need to check if the given param is present, when checking conditon SYNTAX only, the param might not be present
                        if (!(valInfo.getComplexValueParams().contains(paramBoundToMetric)) || valInfo.getComplexValueByParamName(paramBoundToMetric).size() != resultValueList.size()) {
                            // STRICT condition was not met, return false;
                            return false;
                        }
                    }
                }
            }
            // here we compare multivalue result to var x containing 1..n multivalue tests
            for (int i = 0; i < resultValueList.size(); i++) {
                // establish a var values for this iteration's evaluation
                Map<String, Object> currentIterationVars = new HashMap<>();
                currentIterationVars.put("result", resultValueList.get(i).getValue()); // Nth iteration value of "result"

                // there is only one variable allowed for now but within it there are multiple executions hidden
                for (String key : varToListOfValues.keySet()) {
                    // for each execution hidden in variable
                    for (MultiValue.ValueInfo valInfo : varToListOfValues.get(key)) {
                        // TODO again pick the first param we encounter, could be enhanced
                        List<MultiValue.ParamInfo> listOfvalues = valInfo.getComplexValueByParamName(valInfo.getComplexValueParams().get(0));
                        // check if this iteration exists
                        if (listOfvalues.size() >= i + 1) {
                            currentIterationVars.put(key, listOfvalues.get(i).getValue());
                            if (!evaluate(expression, currentIterationVars)) {
                                // condition was broken
                                return false;
                            }
                        }
                    }
                }
            }
            // reaching this point means we haven't encountered a problem
            return true;

        } else {
            return evaluate(expression, simpleVariables);
        }
    }

    /**
     * Evaluates the String expression with the given Map of variables. Used repeatedly for multivalue tests.
     *
     * @param expression String expression to be used
     * @param variables  map, where keys are variable names and values are actual variable values
     * @return evaluated condition, true if it holds, false otherwise
     */
    private boolean evaluate(String expression, Map<String, Object> variables) {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
        Object result;
        try {
            result = engine.eval(expression, new SimpleBindings(variables));
        } catch (ScriptException e) {
            throw new IllegalArgumentException("Error occurred while evaluating the expression.", e);
        }

        if (!(result instanceof Boolean)) {
            throw new IllegalStateException("Result of the expression is not boolean.");
        }

        boolean returnValue = ((Boolean) result).booleanValue();
        if (!returnValue) {
            // here we can determine failed expression and store its' variables for reporting
            failedEvaluationVariables.putAll(variables);
        }
        return returnValue;
    }

    /**
     * Walks through the AST and extract condition expression and it's parameters
     *
     * @param tree
     */
    private void walkTree(CommonTree tree) {
        if (tree == null) {
            throw new IllegalArgumentException("Tree cannot be null.");
        }
        // due to grammar being very strict and throwing errors upon parsing problems, most checks below are just my paranoia

        // tree has to start with MULTIVALUE or CONDITION
        if (!tree.getChild(0).getText().equalsIgnoreCase("MULTIVALUE") && !tree.getChild(0).getText().equalsIgnoreCase("CONDITION")) {
            throw new IllegalArgumentException("Tree has to start with either MULTIVALUE or CONDITION.");
        }

        // number of child node in a tree where the CONDITION keyword resides
        int conditionKeywordIndex;

        // determine multivalue condition
        if (tree.getChild(0).getText().equalsIgnoreCase("MULTIVALUE")) {
            this.isMultivalue = true;
            String nextNode = tree.getChild(1).getText();
            if (!(nextNode.equalsIgnoreCase("STRICT") || nextNode.equalsIgnoreCase("GROUPING") || nextNode.equalsIgnoreCase("CONDITION"))) {
                throw new IllegalArgumentException("After MULTIVALUE there has to one of these: STRICT, GROUPING, CONDITION.");
            }
            if (tree.getChild(1).getText().equalsIgnoreCase("STRICT")) {
                nextNode = tree.getChild(2).getText();
                if (!nextNode.equalsIgnoreCase("CONDITION")) {
                    throw new IllegalArgumentException("After MULTIVALUE STRICT there has to be CONDITION statement.");
                }
                // multivalue strict condition 
                // MULTIVALUE STRICT CONDITION ...
                conditionKeywordIndex = 2;
                this.isStrict = true;
            } else {
                if (tree.getChild(1).getText().equalsIgnoreCase("GROUPING")) {
                    nextNode = tree.getChild(2).getText();
                    if (!nextNode.equalsIgnoreCase("CONDITION")) {
                        throw new IllegalArgumentException("After MULTIVALUE GROUPING there has to be a CONDITION statement.");
                    }
                    // multivalue grouping condition
                    // MULTIVALUE GROUPING CONDITION ... 
                    conditionKeywordIndex = 2;
                    isMultivalueGrouping = true;
                } else {
                    // plain multivalue condition
                    // MULTIVALUE CONDITION ...
                    conditionKeywordIndex = 1;
                }
            }

        } else {
            // single value test execution condition
            // good old CONDITION ... DEFINE ...
            conditionKeywordIndex = 0;
        }

        // there is always CONDITION ... DEFINE ... EOF
        // their position in the tree depends on the expression structure we have
        int defineKeywordIndex = conditionKeywordIndex + 1;
        int eofIndex = conditionKeywordIndex + 2;

        if (!tree.getChild(conditionKeywordIndex).getText().equalsIgnoreCase("CONDITION") || !tree.getChild(defineKeywordIndex).getText().equalsIgnoreCase("DEFINE")) {
            throw new IllegalArgumentException("Doesn't have exactly 1 CONDITION and 1 DEFINE statements.");
        }

        //<missing EOF>. I haven't figured out any better way to recognize this error.
        // TODO: figure something out
        if (tree.getChild(eofIndex).getText().contains("missing")) {
            throw new IllegalArgumentException("Unexpected end of condition.");
        }

        String equation = tree.getChild(conditionKeywordIndex).getChild(0).getText(); //equation after condition
        expression = equation;

        //process variables
        Tree defineNode = tree.getChild(defineKeywordIndex);

        for (int i = 0; i < defineNode.getChildCount(); i++) {
            Tree variableAssignment = defineNode.getChild(i);
            processVariableAssignment(variableAssignment);
        }
    }

    /**
     * Processes every single assignment, e.g. x = (SELECT WHERE id = 1) or x = AVG(SELECT LAST 10) etc.
     *
     * @param assignmentRoot subtree with '=' char as a root
     */
    private void processVariableAssignment(Tree assignmentRoot) {
        if (!assignmentRoot.getText().equalsIgnoreCase("=")) {
            throw new IllegalArgumentException("Wrong syntax, expected '='.");
        }

        String variableName = assignmentRoot.getChild(0).getText();
        Tree groupFunctionOrSelect = assignmentRoot.getChild(1);
        if (groupFunctionOrSelect == null) {
            throw new IllegalArgumentException("Wrong syntax, expected SELECT or grouping function.");
        }

        List<TestExecution> testExecutions = null;
        Double variableValue = null;

        // there is a grouping function and multi-select will follow
        // result of grouping function is always only Double, need to handle that specifically
        if (DslGroupingFunctions.contains(groupFunctionOrSelect.getText())) {
            DslGroupingFunctions groupingFunction = DslGroupingFunctions.parseString(groupFunctionOrSelect.getText());
            Tree select = groupFunctionOrSelect.getChild(0);

            // retrieves list of test exections - possibly even multi valued!
            testExecutions = handleSelect(select);
            if (testExecutions == null) {
                throw new IllegalArgumentException("Error occurred during getting test executions.");
            }

            if (isMultivalue) {
                // this will contain results of grouping function applied to each test execution
                List<Double> groupingResults = new ArrayList<Double>();

                // multi value test requires different processing
                for (TestExecution execution : testExecutions) {
                    // return ValueInfo for metric that this alert condition belongs to
                    MultiValue.ValueInfo valueInfo = MultiValue.find(MultiValue.createFrom(execution), this.metric.getName());
                    // create a list containing all values within one multivalue test
                    List<Double> valuesWithinOneExecution = new ArrayList<Double>();
                    // TODO, again we extract first param from the test execution, we cannot even use the same as in current test execution
                    for (MultiValue.ParamInfo paramInfo : valueInfo.getComplexValueByParamName(valueInfo.getComplexValueParams().get(0))) {
                        valuesWithinOneExecution.add(paramInfo.getValue());
                    }

                    // now apply grouping function as usually and store result
                    groupingResults.add(groupingFunction.compute(valuesWithinOneExecution));
                }

                // now apply the same grouping function again on results from separate execution
                variableValue = groupingFunction.compute(groupingResults);

                // apply grouping function to current execution as well (e.g. to "result")
                // since this branch handles multivalue we can be sure there is only one type of grouping function present
                if (simpleVariables.get("result") == null) {
                    List<Double> currentExecutionValues = new ArrayList<>();
                    for (MultiValue.ParamInfo oneParam : currentExecutionValueInfo.getComplexValueByParamName(paramBoundToMetric)) {
                        currentExecutionValues.add(oneParam.getValue());
                    }
                    simpleVariables.put("result", groupingFunction.compute(currentExecutionValues));
                }
            } else {
                // single value test
                List<Double> values = getValuesFromTestExecutions(testExecutions);
                variableValue = (values == null || values.isEmpty()) ? null : groupingFunction.compute(values);
                simpleVariables.put("result", currentExecutionValueInfo.getSimpleValue());
            }
            simpleVariables.put(variableName, variableValue);

        } else { //select with no grouping function
            testExecutions = handleSelect(groupFunctionOrSelect);
            if (isMultivalue) {
                // each item in the list is a series of values from one execution
                List<MultiValue.ValueInfo> seriesList = new ArrayList<>();
                for (TestExecution execution : testExecutions) {
                    seriesList.add(MultiValue.find(MultiValue.createFrom(execution), this.metric.getName()));
                }
                varToListOfValues.put(variableName, seriesList);
            } else {
                // only one test execution is allowed when dealing with single value tests
                if (testExecutions == null || testExecutions.size() > 1) {
                    throw new IllegalArgumentException("Error occurred or there is more than one test execution found, but no grouping function applied.");
                }
                if (testExecutions.isEmpty()) {
                    // if no execution was found, store it as null
                    simpleVariables.put(variableName, null);
                } else {
                    // select of single value with no grouping
                    simpleVariables.put(variableName, MultiValue.find(MultiValue.createFrom(testExecutions.get(0)), this.metric.getName()).getSimpleValue());
                }
                // add current test execution value to simpleVariables as well
                simpleVariables.put("result", currentExecutionValueInfo.getSimpleValue());
            }
        }
    }

    /**
     * Handles SELECT clause
     *
     * @param select root of the tree with SELECT as a root keyword
     * @return list of test executions according to the SELECT query
     */
    private List<TestExecution> handleSelect(Tree select) {
        if (!select.getText().equalsIgnoreCase("SELECT")) {
            throw new IllegalArgumentException("Wrong syntax, expected SELECT.");
        }

        TestExecutionSearchTO searchCriteria = new TestExecutionSearchTO();
        List<TestExecution> testExecutions = null;
        Tree whereOrLast = select.getChild(0);
        Map<String, Integer> parsedLast = null;

        if (select.getChildCount() == 1 && whereOrLast.getText().equalsIgnoreCase("LAST")) { //SELECT only with LAST
            parsedLast = processLast(whereOrLast);
            searchCriteria.setLimitFrom(parsedLast.get("lastFrom"));
            searchCriteria.setLimitHowMany(parsedLast.get("howMany"));
        } else if (select.getChildCount() == 2 && select.getChild(1).getText().equalsIgnoreCase("LAST")) { //SELECT with WHERE and LAST
            parsedLast = processLast(select.getChild(1));
            //this information about LAST will be used in retrieving of test executions
            //because there is also WHERE clause present
        }

        if (whereOrLast.getText().equalsIgnoreCase("WHERE")) { //WHERE
            for (int i = 0; i < whereOrLast.getChildCount(); i++) { //going through all AND clauses
                Tree simpleOrIn = whereOrLast.getChild(i);

                if (simpleOrIn.getText().equalsIgnoreCase("=") || simpleOrIn.getText().equalsIgnoreCase(">=") || simpleOrIn.getText().equalsIgnoreCase("<=")) { //WHERE with '=', '>=' or '<='
                    String property = simpleOrIn.getChild(0).getText();
                    String propertyValue = simpleOrIn.getChild(1).getText();

                    addCriteriaByPropertyName(searchCriteria, property, propertyValue, parsedLast, simpleOrIn.getText());
                } else if (simpleOrIn.getText().equalsIgnoreCase("IN")) { //IN where
                    String propertyName = simpleOrIn.getChild(0).getText();
                    Collection<String> values = new ArrayList<>();

                    for (int j = 1; j < simpleOrIn.getChildCount(); j++) { //starting from 1 because child(0) is property name
                        values.add(simpleOrIn.getChild(j).getText());
                    }

                    addCriteriaByPropertyIn(searchCriteria, propertyName, values);
                } else {
                    throw new IllegalArgumentException("Wrong syntax, expected '=', '<=', '>=' or IN.");
                }
            }
        }

        //TODO: solve this
        //testExecutions = testExecutionDAO.searchTestExecutions(searchCriteria, userService.getLoggedUserGroupNames()).getResult();

        return testExecutions;
    }

    /**
     * Extracts actual double values directly from test executions
     *
     * @param testExecutions
     * @return
     */
    private List<Double> getValuesFromTestExecutions(List<TestExecution> testExecutions) {
        List<Double> results = new ArrayList<>();
        for (TestExecution testExecution : testExecutions) {
            Double result = getValueFromMetric(testExecution);
            if (result != null) {
                results.add(result);
            }
        }

        return results;
    }

    /**
     * Helper method. Returns value from test execution assigned to the given metric. Works only for single-valued test
     * executions.
     *
     * @param testExecution
     * @return
     */
    private Double getValueFromMetric(TestExecution testExecution) {
        //TODO: solve this
        /*
        for (Value value : testExecution.getValues()) {
            if (value.getMetricName().equals(metric.getName())) {
                return value.getResultValue();
            }
        }*/

        return null;
    }

    /**
     * Parses the string in our DSL into Abstract Syntax Tree
     *
     * @param string
     * @return AST
     */
    private CommonTree parseTree(String string) {
        //lexer splits input into tokens
        ANTLRStringStream input = new ANTLRStringStream(string);
        TokenStream tokens = new CommonTokenStream(new AlertingDSLLexer(input));

        //parser generates abstract syntax tree
        AlertingDSLParser parser = new AlertingDSLParser(tokens);

        AlertingDSLParser.expression_return ret;
        try {
            ret = parser.expression();
        } catch (RecognitionException ex) {
            throw new IllegalStateException("Recognition exception is never thrown, only declared.");
        }
        // retieve stored parser errors and force exception if there are any, this will easily allow to verify condition input
        // based solely on grammar.
        List<String> parserErrors = parser.getErrors();
        if (!parserErrors.isEmpty()) {
            StringBuilder exceptionMsgBuilder = new StringBuilder(100);
            for (String error : parserErrors) {
                exceptionMsgBuilder.append(error + System.lineSeparator());
            }
            throw new IllegalArgumentException("Following errors were encountered during condition parsing process: " + exceptionMsgBuilder.toString());
        }
        return (CommonTree) ret.tree;
    }

    /**
     * Helper method. Retrieves test executions according to the name of the property that is in the WHERE condition, e.g. there
     * is a different process of retrieving test execution by ID and with specific tags
     *
     * @param propertyName  name of the property
     * @param propertyValue value that is supplied for the property, might have different meaning with different property
     * @param parsedLast    if there is also LAST clause, it's parsed in this Map. See processLast() method for details.
     * @param operator      operator of the condition, e.g. '=', '>=', '<=' ... @ return
     */
    private void addCriteriaByPropertyName(TestExecutionSearchTO searchCriteria, String propertyName, String propertyValue, Map<String, Integer> parsedLast, String operator) {
        if (propertyName.equalsIgnoreCase("tags")) {
            searchCriteria.setTags(propertyValue);

            if (parsedLast != null) { //LAST is present
                searchCriteria.setLimitFrom(parsedLast.get("lastFrom"));
                searchCriteria.setLimitHowMany(parsedLast.get("howMany"));
            }
        } else if (propertyName.equalsIgnoreCase("id")) {
            List<Long> ids = Arrays.asList(Long.parseLong(propertyValue));
            searchCriteria.setIds(ids);
        } else if (propertyName.equalsIgnoreCase("date")) {
            Date parsedDate = null;
            try {
                parsedDate = DATE_FORMAT.parse(propertyValue);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Date is in a wrong format. Accepted format is " + DATE_FORMAT.toPattern());
            }

            if (operator.equals(">=")) {
                searchCriteria.setStartedFrom(parsedDate);
            } else if (operator.equals("<=")) {
                searchCriteria.setStartedTo(parsedDate);
            }
        } else {
            throw new UnsupportedOperationException("Currently supported properties with operator '=' are 'id', 'tags', 'date'.");
        }
    }

    /**
     * Adds IDs to search criteria using the IN operator, i.e. ID must belong to the set of IDs provided.
     *
     * @param searchCriteria
     * @param propertyName
     * @param values
     * @return
     */
    private void addCriteriaByPropertyIn(TestExecutionSearchTO searchCriteria, String propertyName, Collection<String> values) {
        if (propertyName.equalsIgnoreCase("id")) {
            List<Long> ids = new ArrayList<>();
            for (String id : values) {
                ids.add(Long.parseLong(id));
            }
            searchCriteria.setIds(ids);

            return;
        }

        throw new UnsupportedOperationException("Only property 'id' is supported with IN operator right now.");
    }

    /**
     * Helper method. Parses the LAST clause and retrieves information about it. It retrieves a Map of size 2 with keys
     * "lastFrom" and "howMany". "lastFrom" is the first parameter of LAST, "howMany" is the second. See documentation for
     * alerting for details.
     *
     * @param last root of the LAST clause in AST
     * @return
     */
    private Map<String, Integer> processLast(Tree last) {
        Map<String, Integer> result = new HashMap<>();

        if (last.getChildCount() == 1) { // LAST x | it's the same as LAST x, x
            int numberOfLast = Integer.valueOf(last.getChild(0).getText());
            result.put("lastFrom", numberOfLast);
            result.put("howMany", numberOfLast);
        } else if (last.getChildCount() == 2) { // LAST x, y
            int lastFrom = Integer.valueOf(last.getChild(0).getText());
            int howMany = Integer.valueOf(last.getChild(1).getText());
            result.put("lastFrom", lastFrom);
            result.put("howMany", howMany);
        } else {
            throw new IllegalArgumentException("Wrong syntax, LAST has to have exactly one or two arguments.");
        }

        /**
         * -----IMPORTANT------ because we assume that we have already entered the test execution that we want to process alert
         * with, we don't want to include it into the last X test executions, e.g. CONDITION result > x DEFINE x = (SELECT LAST
         * 1) would always fail, since the last test execution is the currently entered one. As a solution we do lastFrom - 1
         *
         */
        result.put("lastFrom", result.get("lastFrom") + 1);

        return result;
    }

    public void setTestExecutionDAO(TestExecutionDAO testExecutionDAO) {
        this.testExecutionDAO = testExecutionDAO;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Map<String, Object> getEvaluatedVariables() {
        // within evaluate() method we detect failure and in such case store the variables for reporting
        return Collections.unmodifiableMap(failedEvaluationVariables);
    }
}
