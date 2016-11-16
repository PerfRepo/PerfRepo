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
package org.perfrepo.web.alerting;


import org.perfrepo.model.Metric;
import org.perfrepo.model.TestExecution;

import java.util.Map;

/**
 * API for checking of condition for alerting.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
public interface ConditionChecker {

   /**
    * Checks the condition for alerting
    *
    * @param condition specification of condition in String in our custom DSL that should hold
    * @param currentResult the test execution object that is currently being processed, e.g. against which the
    *                      condition should hold. This value will replace the "result" variable in condition!
    * @param metric metric that the condition is linked to
    * @return true if condition still hold | false if condition is broken
    */
   public boolean checkCondition(String condition, TestExecution currentResult, Metric metric);

   /**
    * Check the condition syntax only. Invokes a grammar tree parser which detects errors.
    * When an error is found, exception is thrown.
    * @param condition String version of condition in our custom DSL that should hold true
    * @param metric Metric linked to the condition
    */
   public void checkConditionSyntax(String condition, Metric metric);
   
   /**
    * Retrieves a map of variables used in the condition (as a key) with the values assigned to them (as a value).
    *
    * @return
    */
   public Map<String, Object> getEvaluatedVariables();
}
