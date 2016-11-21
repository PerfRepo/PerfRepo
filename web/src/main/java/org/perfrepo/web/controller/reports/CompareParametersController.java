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
package org.perfrepo.web.controller.reports;

import org.apache.log4j.Logger;
import org.perfrepo.model.Metric;
import org.perfrepo.model.Tag;
import org.perfrepo.model.Test;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.TestExecutionParameter;
import org.perfrepo.web.controller.BaseController;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.session.TEComparatorSession;
import org.perfrepo.web.viewscope.ViewScoped;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Simple comparison of test execution parameters.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 */
@Named
@ViewScoped
public class CompareParametersController extends BaseController {

   private static Logger log = Logger.getLogger(CompareParametersController.class);

   private static final long serialVersionUID = 1L;
   @Inject
   private TestService service;

   @Inject
   private TEComparatorSession teComparator;

   private List<TestExecution> testExecutions = null;
   private Test test = null;
   private List<String> allParameters = null;

   public Test getTest() {
      return test;
   }

   public List<String> getAllParameters() {
      return allParameters;
   }

   private List<String> extractParams(List<TestExecution> testExecutions) {
      if (testExecutions == null || testExecutions.isEmpty()) {
         return Collections.emptyList();
      }
      Set<String> pset = new HashSet<String>();
      //for (TestExecution te : testExecutions) {
         //TODO: solve this
         //for (TestExecutionParameter p : te.getParameters()) {
         //   pset.add(p.getName());
         // }
      //}
      List<String> r = new ArrayList<String>(pset);
      Collections.sort(r);
      return r;
   }

   public void removeFromComparison(Long execId) {
      teComparator.remove(execId);
      //TODO: solve this
      //TestExecution execToRemove = EntityUtils.findById(testExecutions, execId);
      TestExecution execToRemove = null;
      if (execToRemove != null && testExecutions != null) {
         testExecutions.remove(execToRemove);
      }
   }

   private Long checkCommonTestId(List<TestExecution> execs) {
      if (execs == null || execs.isEmpty()) {
         return null;
      }
      Long commonTestId = execs.iterator().next().getTest().getId();
      for (TestExecution exec : execs) {
         if (!commonTestId.equals(exec.getTest().getId())) {
            return null;
         }
      }
      return commonTestId;
   }

   public String getPermaLink() {
      return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/reports/compare/param?q=" + ParamUtil.generateExecQuery(testExecutions);
   }

   /**
    * called on preRenderView
    */
   public void preRender() {
      reloadSessionMessages();
      if (testExecutions != null) {
         Set<Long> idsInComparator = new HashSet<Long>(teComparator.getExecIds());
         //TODO: solve this
         //Set<Long> idsDisplayed = new HashSet<Long>(EntityUtils.extractIds(testExecutions));
         Set<Long> idsDisplayed = null;
         if (!idsInComparator.equals(idsDisplayed)) {
            testExecutions = null;
            test = null;
            allParameters = null;
         }
      }

      if (testExecutions == null) {
         List<Long> execIdList = ParamUtil.parseExecQuery(getRequestParam("q"));
         if (execIdList == null) {
            execIdList = new ArrayList<Long>(teComparator.getExecIds());
         } else {
            teComparator.clear();
            teComparator.getExecIds().addAll(execIdList);
            execIdList = new ArrayList<Long>(teComparator.getExecIds());
         }
         if (execIdList.isEmpty()) {
            addMessage(INFO, "page.compareExecs.nothingToCompare");
            return;
         }
         Collections.sort(execIdList);
         testExecutions = service.getFullTestExecutions(execIdList);
         Long testId = checkCommonTestId(testExecutions);
         if (testId == null) {
            addMessage(ERROR, "page.compareExecs.errorDifferentTests");
            return;
         }
         test = service.getFullTest(testId);
         allParameters = extractParams(testExecutions);
      }
   }

   public String getParamValue(TestExecution exec, String paramName) {
      //TODO: solve this
      //return ViewUtils.displayValue(exec.findParameter(paramName));
      return null;
   }

   public boolean paramsEqual(String paramName) {
      if (testExecutions == null || testExecutions.isEmpty()) {
         return true;
      }
      //TODO: solve this
      //TestExecutionParameter param = testExecutions.get(0).findParameter(paramName);
      TestExecutionParameter param = null;
      String commonValue = param == null ? null : param.getValue();
      for (TestExecution exec : testExecutions) {
         //TODO: solve this
         //param = exec.findParameter(paramName);
         if (param == null) {
            if (commonValue != null) {
               return false;
            }
         } else {
            String currentValue = param.getValue();
            if (currentValue == null) {
               if (commonValue != null) {
                  return false;
               }
            } else {
               if (!currentValue.equals(commonValue)) {
                  return false;
               }
            }
         }
      }
      return true;
   }

   public List<Metric> getMetrics() {
      return test == null ? Collections.<Metric>emptyList() : new ArrayList<Metric>(test.getMetrics());
   }

   public List<TestExecution> getTestExecutions() {
      return testExecutions;
   }

   public String getTags(TestExecution te) {
      StringBuilder tag = new StringBuilder();
      for (Tag teg : te.getTags()) {
         tag.append(teg.getName()).append("\n");
      }
      return tag.toString().substring(0, tag.length() - 1);
   }
}