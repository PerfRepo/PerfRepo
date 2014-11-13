/* 
 * Copyright 2013 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.qa.perfrepo.controller;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.jboss.qa.perfrepo.model.TestExecutionParameter;
import org.jboss.qa.perfrepo.service.TestService;
import org.jboss.qa.perfrepo.viewscope.ViewScoped;

/**
 * Displays value of {@link TestExecutionParameter}
 * 
 * @author Michal Linhard (mlinhard@redhat.com)
 * 
 */
@Named
@ViewScoped
public class ParameterShowController extends BaseController {

   private static final long serialVersionUID = 370202307562230671L;
   private static final Logger log = Logger.getLogger(ParameterShowController.class);

   private Long paramId;

   @Inject
   private TestService testService;

   private TestExecutionParameter param = null;

   public Long getParamId() {
      return paramId;
   }

   public void setParamId(Long paramId) {
      this.paramId = paramId;
   }

   public TestExecutionParameter getParam() {
      return param;
   }

   /**
    * called on preRenderView
    */
   public void preRender() throws Exception {
      reloadSessionMessages();
      if (paramId == null) {
         log.error("No parameter ID supplied");
         redirectWithMessage("/", ERROR, "page.showParam.errorNoParamId");
      } else {
         param = testService.getFullParameter(paramId);
         if (param == null) {
            log.error("Can't find parameter with id " + paramId);
            redirectWithMessage("/", ERROR, "page.showParam.errorParamNotFound", paramId);
         }
      }
   }

}
