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
package org.perfrepo.web.controller;

import org.perfrepo.web.service.ApplicationConfiguration;
import org.perfrepo.web.viewscope.ViewScoped;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;

/**
 * Backing bean home screen.
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Named
@ViewScoped
public class HomeController extends BaseController {

   @Inject
   private ApplicationConfiguration applicationConfiguration;

   /**
    * called on preRenderView
    */
   public void preRender() throws Exception {
      reloadSessionMessages();
   }

   /**
    * Returns current version of PerfRepo extracted from pom.xml
    *
    * @return
    */
   public String getPerfRepoVersion() throws IOException {
      return applicationConfiguration.getPerfRepoVersion();
   }
}
