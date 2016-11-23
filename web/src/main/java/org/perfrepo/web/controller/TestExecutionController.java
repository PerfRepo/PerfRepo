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

import org.apache.log4j.Logger;
import org.perfrepo.model.FavoriteParameter;
import org.perfrepo.model.Metric;
import org.perfrepo.model.Tag;
import org.perfrepo.model.Test;
import org.perfrepo.model.TestExecution;
import org.perfrepo.model.TestExecutionAttachment;
import org.perfrepo.model.TestExecutionParameter;
import org.perfrepo.model.Value;
import org.perfrepo.model.ValueParameter;
import org.perfrepo.model.builder.TestExecutionBuilder;
import org.perfrepo.model.user.User;
import org.perfrepo.web.controller.reports.charts.RfChartSeries;
import org.perfrepo.web.service.AlertingService;
import org.perfrepo.web.service.TestExecutionService;
import org.perfrepo.web.service.TestService;
import org.perfrepo.web.service.UserService;
import org.perfrepo.web.service.exceptions.ServiceException;
import org.perfrepo.web.session.UserSession;
import org.perfrepo.web.util.MultiValue;
import org.perfrepo.web.util.MultiValue.ParamInfo;
import org.perfrepo.web.util.MultiValue.ValueInfo;
import org.perfrepo.web.util.TagUtils;
import org.perfrepo.web.util.ViewUtils;
import org.perfrepo.web.viewscope.ViewScoped;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.model.ChartDataModel;
import org.richfaces.model.ChartDataModel.ChartType;
import org.richfaces.model.NumberChartDataModel;
import org.richfaces.model.UploadedFile;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Details of {@link TestExecution}
 *
 * @author Michal Linhard (mlinhard@redhat.com)
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@Named
@ViewScoped
public class TestExecutionController extends BaseController {

   private static final long serialVersionUID = 3012075520261954430L;
   private static final Logger log = Logger.getLogger(TestExecutionController.class);

   @Inject
   private TestService testService;

   @Inject
   private TestExecutionService testExecutionService;

   @Inject
   private UserSession userSession;

   @Inject
   private UserService userService;

   @Inject
   private AlertingService alertingService;

   private TestExecution testExecution = null;
   private Test test = null;

   private TestExecutionParameter editedParameter = null;
   private Value editedValue = null;
   private Long editedValueMetricSelectionId = null;
   private TestExecution editedTestExecution = null;
   private FavoriteParameter editedFavoriteParameter = null;

   private List<ValueInfo> values = null;
   private List<FavoriteParameter> favoriteParameters = null;

   private List<ParamInfo> selectedMultiValueList = null;
   private List<String> selectedMultiValueParamSelectionList = null;
   private String selectedMultiValueParamSelection = null;
   private ValueInfo selectedMultiValue = null;
   private List<RfChartSeries> multiValueChart;

   private boolean showMultiValueTable = false;

   private Long createForTest;
   private Long testExecutionId;

   private Long attachmentId;

   public List<FavoriteParameter> getFavoriteParameters() {
      return favoriteParameters;
   }

   public FavoriteParameter getEditedFavoriteParameter() {
      return editedFavoriteParameter;
   }

   public void setEditedFavoriteParameter(String paramName) {
      editedFavoriteParameter = new FavoriteParameter();

      Test testEntity = testService.getTest(test.getId());
      editedFavoriteParameter.setTest(testEntity);

      User user = userService.getUser(userSession.getUser().getId());
      editedFavoriteParameter.setUser(user);

      editedFavoriteParameter.setParameterName(paramName);
      FavoriteParameter fp = findFavoriteParameter(paramName);
      if (fp != null) {
         editedFavoriteParameter.setLabel(fp.getLabel());
      } else {
         editedFavoriteParameter.setLabel("New label " + (favoriteParameters == null ? 0 : favoriteParameters.size()));
      }
   }

   public void unsetEditedFavoriteParameter() {
      editedFavoriteParameter = null;
   }

   public void saveEditedFavoriteParameter() {
      //TODO: solve this
      //userService.createFavoriteParameter(editedFavoriteParameter.getTest(), editedFavoriteParameter.getParameterName(), editedFavoriteParameter.getLabel());
      userSession.refresh();
      //TODO: solve this
      //favoriteParameters = userService.getFavoriteParametersForTest(test);
   }

   public void removeFromFavorites(String paramName) {
      if (paramName == null || !isFavorite(paramName)) {
         log.error("incorrect request for removeFromFavorites");
         return;
      }
      //TODO: solve this
      //userService.removeFavoriteParameter(test, paramName);
      userSession.refresh();
      //TODO: solve this
      //favoriteParameters = userService.getFavoriteParametersForTest(test);
   }

   public Long getCreateForTest() {
      return createForTest;
   }

   public void setCreateForTest(Long createForTest) {
      this.createForTest = createForTest;
   }

   public String getRawTags() {
      return TagUtils.rawTags(editedTestExecution == null ? null : editedTestExecution.getSortedTags().stream().map(Tag::getName).collect(Collectors.toList()));
   }

   public void setRawTags(String rawTags) {
      if (editedTestExecution == null) {
         return;
      }
      List<String> tags = TagUtils.parseTags(rawTags);
      TestExecutionBuilder b = TestExecution.builder();
      for (String tag : tags) {
         b.tag(tag);
      }
      editedTestExecution.setTags(b.build().getTags());
   }

   public boolean isDisplayComment() {
      return testExecution != null && testExecution.getComment() != null && !testExecution.getComment().equals("");
   }

   public String getDisplayedComment() {
      if (isDisplayComment()) {
         return testExecution.getComment();
      } else {
         return "&nbsp;";
      }
   }

   public void setEditedTestExecution() {
      this.editedTestExecution = testExecution;
   }

   public void unsetEditedTestExecution() {
      if (createForTest != null) {
         redirect("/test/" + createForTest);
      } else {
         this.editedTestExecution = null;
         redirect("/exec/" + testExecution.getId());
      }
   }

   public TestExecution getEditedTestExecution() {
      return editedTestExecution;
   }

   public void updateEditedTestExecution() {
      if (editedTestExecution != null) {
         try {
            if (editedTestExecution.getId() == null) {
               testExecution = testExecutionService.createTestExecution(editedTestExecution);
               redirectWithMessage("/exec/" + testExecution.getId(), INFO, "page.exec.successfullyCreated", testExecution.getName());
            } else {
               testExecution = testExecutionService.updateTestExecution(editedTestExecution);
               showMultiValue(null);
               redirectWithMessage("/exec/" + testExecution.getId(), INFO, "page.exec.successfullyUpdated", testExecution.getName());
            }
         } catch (ServiceException e) {
            addMessage(e);
         }
      }
   }

   public Long getTestExecutionId() {
      return testExecutionId;
   }

   public void setTestExecutionId(Long testExecutionId) {
      this.testExecutionId = testExecutionId;
   }

   public void preRender() {
      reloadSessionMessages();
      if (testExecutionId == null) {
         if (createForTest == null) {
            log.error("No execution ID supplied");
            redirectWithMessage("/", ERROR, "page.exec.errorNoExecId");
         } else {
            if (testExecution == null) {
               test = testService.getTest(createForTest);
               if (test == null) {
                  log.error("Can't find test with id " + testExecution.getTest().getId());
                  redirectWithMessage("/", ERROR, "page.test.errorTestNotFound", testExecution.getTest().getId());
               }
               testExecution = new TestExecution();
               testExecution.setTest(test);
               testExecution.setStarted(new Date());
               setEditedTestExecution();
            }
         }
      } else {
         if (testExecution == null) {
            testExecution = testExecutionService.getTestExecution(testExecutionId);
            if (testExecution == null) {
               log.error("Can't find execution with id " + testExecutionId);
               redirectWithMessage("/", ERROR, "page.exec.errorExecNotFound", testExecutionId);
            } else {
               test = testService.getTest(testExecution.getTest().getId());
               if (test == null) {
                  log.error("Can't find test with id " + testExecution.getTest().getId());
                  redirectWithMessage("/", ERROR, "page.test.errorTestNotFound", testExecution.getTest().getId());
               } else {
                  values = MultiValue.createFrom(testExecution);
                  //TODO: solve this
                  //favoriteParameters = userService.getFavoriteParametersForTest(test);
               }
               setEditedTestExecution();
            }
         }
      }
   }

   public TestExecution getTestExecution() {
      return testExecution;
   }

   public Test getTest() {
      return test;
   }

   public TestExecutionParameter getEditedParameter() {
      return editedParameter;
   }

   public void setEditedParameter(TestExecutionParameter param) {
      this.editedParameter = param == null ? null : param;
   }

   public void unsetEditedParameter() {
      this.editedParameter = null;
   }

   public void unsetEditedParameterAndRemove() {
      removeFromFavorites(editedFavoriteParameter.getParameterName());
      editedFavoriteParameter = null;
   }

   public void createEditedParameter() {
      this.editedParameter = new TestExecutionParameter();
   }

   public Value getEditedValue() {
      return editedValue;
   }

   public String update() {
      if (testExecution != null) {
         try {
            testExecutionService.updateTestExecution(testExecution);
         } catch (ServiceException e) {
            addMessage(e);
         }
      }
      return "/testExecution/detail.xhtml?testExecutionId=";
   }

   public List<TestExecutionParameter> getTestExecutionParameters() {
      //TODO: solve this
      // return testExecution.getSortedParameters();
      return null;
   }

   public List<Tag> getTags() {
      return testExecution.getSortedTags();
   }

   public Collection<TestExecutionAttachment> getAttachments() {
      // TODO: solve this
      //return testExecution == null ? Collections.<TestExecutionAttachment>emptyList() : testExecution.getAttachments();
      return null;
   }

   public String delete() {
      TestExecution objectToDelete = testExecution;
      if (testExecution == null) {
         objectToDelete = new TestExecution();
         objectToDelete.setId(new Long(getRequestParam("testExecutionId")));
      }
      try {
         testExecutionService.removeTestExecution(objectToDelete);
      } catch (org.perfrepo.web.service.exceptions.ServiceException e) {
         addMessage(e);
      }
      return "Search";
   }

   public void deleteParameter(TestExecutionParameter param) {
      if (param != null) {
         try {
            testExecutionService.removeParameter(param);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      }
   }

   public void updateEditedParameter() {
      if (editedParameter != null) {
         TestExecution idHolder = new TestExecution();
         idHolder.setId(testExecutionId);
         editedParameter.setTestExecution(idHolder);
         try {
            TestExecutionParameter freshParam = testExecutionService.updateParameter(editedParameter);
            testExecution.getParameters().put(freshParam.getName(), freshParam);
            editedParameter = null;
         } catch (ServiceException e) {
            addMessage(e);
            editedParameter = null;
         }
      }
   }

   public void setEditedValue(Value value) {
      this.editedValue = value == null ? null : value;
      if (editedValue != null || editedValue.getParameters() != null) {
         if (editedValue.getParameters() instanceof List) {
            Collections.sort((List<ValueParameter>) editedValue.getParameters());
         }
      }
   }

   public Long getEditedValueMetricSelectionId() {
      return editedValueMetricSelectionId;
   }

   public void setEditedValueMetricSelectionId(Long editedValueMetricSelectionId) {
      this.editedValueMetricSelectionId = editedValueMetricSelectionId;
   }

   public void createEditedValue() {
      editedValue = new Value();
   }

   public void unsetEditedValue() {
      editedValue = null;
   }

   public void addEditedValueParameter() {
      if (editedValue == null) {
         log.error("Can't add parameter, editedValue not set");
         return;
      }

      ValueParameter vp = new ValueParameter();

      editedValue.getParameters().put(vp.getName(), vp);
   }

   public void removeEditedValueParameter(ValueParameter vp) {
      if (editedValue == null) {
         log.error("can't remove parameter, editedValue not set");
         return;
      }
      if (editedValue.getParameters() == null) {
         return;
      }
      editedValue.getParameters().remove(vp);
      updateEditedValue();
      redirectWithMessage("/exec/" + testExecutionId, INFO, "page.exec.value.successfullyUpdated");
   }

   // this is also create method for value
   public void updateEditedValue() {
      if (editedValue == null) {
         return;
      }

      TestExecution idHolder = new TestExecution();
      idHolder.setId(testExecutionId);
      editedValue.setTestExecution(idHolder);

      Value freshValue = null;
      try {
         if (editedValue.getId() == null) {
            //TODO: solve this
            //Metric selectedMetric = EntityUtils.findById(test.getMetrics(), editedValueMetricSelectionId);
            Metric selectedMetric = null;

            if (selectedMetric == null) {
               addMessage(ERROR, "page.exec.errorMetricMandatory");
               return;
            }

            editedValue.setMetric(selectedMetric);
            freshValue = testExecutionService.addValue(editedValue);
         } else {
            freshValue = testExecutionService.updateValue(editedValue);
            //TODO: solve this
            // EntityUtils.removeById(testExecution.getValues(), freshValue.getId());
         }

         //TODO: solve this
         // testExecution.getValues().add(freshValue);

         alertingService.processAlerts(testExecution);

         editedValue = null;

         ValueInfo prevValueInfo = MultiValue.find(values, freshValue);
         values = MultiValue.createFrom(testExecution);

         showMultiValue(prevValueInfo == null ? null : prevValueInfo.getMetricName());
      } catch (ServiceException e) {
         addMessage(e);
      }
   }

   public List<Metric> getTestMetric() {
      /*if (testExecution != null) {
         return testService.getTestMetrics(testExecution.getTest());
      }*/
      return null;
   }

   public void deleteValue(Value value) {
      if (value != null) {
         TestExecution idHolder = new TestExecution();
         idHolder.setId(testExecutionId);
         value.setTestExecution(idHolder);
         /*try {
            //testService.removeValue(value);
            //TODO: solve this
            //EntityUtils.removeById(testExecution.getValues(), value.getId());
            ValueInfo prevValueInfo = MultiValue.find(values, value);
            values = MultiValue.createFrom(testExecution);
            showMultiValue(prevValueInfo.getMetricName());
         } catch (ServiceException e) {
            addMessage(e);
         }*/

         redirectWithMessage("/exec/" + testExecutionId, INFO, "page.exec.value.successfullyRemoved");
      }
   }

   /**
    * Produce download link for an attachment. It will be an URL for the
    * {@link org.perfrepo.web.rest.TestExecutionREST#getAttachment(Long)} method.
    *
    * @param attachment
    * @return The download link.
    */
   public String getDownloadLink(TestExecutionAttachment attachment) {
      HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
      return request.getContextPath() + "/exec/attachment/" + attachment.getId();
   }

   public List<ValueInfo> getValues() {
      return values;
   }

   public void updateParamSelection() {
      if (selectedMultiValue == null || !selectedMultiValue.isMultiValue()) {
         addMessage(ERROR, "page.exec.notMultiValue");
         return;
      }
      selectedMultiValueList = selectedMultiValue.getComplexValueByParamName(selectedMultiValueParamSelection);
      computeMultiValueChart();
   }

   private void clearSelectedMultiValue() {
      selectedMultiValueParamSelectionList = null;
      selectedMultiValueParamSelection = null;
      selectedMultiValue = null;
      selectedMultiValueList = null;
   }

   public void showMultiValue(String metricName) {
      if (metricName == null) {
         clearSelectedMultiValue();
         return;
      }
      ValueInfo value = MultiValue.find(values, metricName);
      if (value == null || !value.isMultiValue()) {
         clearSelectedMultiValue();
         return;
      }
      selectedMultiValueParamSelectionList = value.getComplexValueParams();
      if (selectedMultiValueParamSelectionList.isEmpty()) {
         clearSelectedMultiValue();
         return;
      }
      Collections.sort(selectedMultiValueParamSelectionList);
      if (selectedMultiValueParamSelection == null || selectedMultiValueParamSelection.isEmpty()) {
         selectedMultiValueParamSelection = selectedMultiValueParamSelectionList.get(0);
      }
      selectedMultiValueList = value.getComplexValueByParamName(selectedMultiValueParamSelection);
      selectedMultiValue = value;
      computeMultiValueChart();
   }

   private void computeMultiValueChart() {
      multiValueChart = new ArrayList<RfChartSeries>();

      ChartDataModel chartDataModel = new NumberChartDataModel(ChartType.line);

      for (ParamInfo item : selectedMultiValueList) {
         chartDataModel.put(Integer.parseInt(item.getParamValue()), item.getValue());
      }

      RfChartSeries newSeries = new RfChartSeries(chartDataModel);
      newSeries.setName(testExecution.getName());
      multiValueChart.add(newSeries);
   }

   public List<ParamInfo> getSelectedMultiValueList() {
      return selectedMultiValueList;
   }

   public String getSelectedMultiValueParamSelection() {
      return selectedMultiValueParamSelection;
   }

   public List<RfChartSeries> getMultiValueChart() {
      return multiValueChart;
   }

   public void setSelectedMultiValueParamSelection(String selectedMultiValueParamSelection) {
      this.selectedMultiValueParamSelection = selectedMultiValueParamSelection;
   }

   public List<String> getSelectedMultiValueParamSelectionList() {
      return selectedMultiValueParamSelectionList;
   }

   public String displayValueFavParam(String param) {
      //TODO: solve this
      //return displayValueTable(testExecution.findParameter(param));
      return null;
   }

   public String displayValueTable(TestExecutionParameter param) {
      return ViewUtils.displayValue(param);
   }

   public boolean isShowMultiValueTable() {
      return showMultiValueTable;
   }

   public ValueInfo getSelectedMultiValue() {
      return selectedMultiValue;
   }

   public void setShowMultiValueTable(boolean showMultiValueTable) {
      this.showMultiValueTable = showMultiValueTable;
   }

   public Long getAttachmentId() {
      return attachmentId;
   }

   public void setAttachmentId(Long attachmentId) {
      this.attachmentId = attachmentId;
   }

   private FavoriteParameter findFavoriteParameter(String paramName) {
      if (paramName == null || favoriteParameters == null) {
         return null;
      }
      for (FavoriteParameter fp : favoriteParameters) {
         if (fp.getParameterName().equals(paramName)) {
            return fp;
         }
      }
      return null;
   }

   public boolean isFavorite(String paramName) {
      return findFavoriteParameter(paramName) != null;
   }

   public void downloadAttachment() {
      TestExecutionAttachment attachment = testExecutionService.getAttachment(attachmentId);
      if (attachment == null) {
         addMessage(ERROR, "Attachment not found.");
      }

      HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

      response.reset();
      response.setContentType(attachment.getMimetype());
      response.setHeader("Content-Disposition", "attachment; filename=" + attachment.getFilename());

      try (BufferedInputStream input = new BufferedInputStream(new ByteArrayInputStream(attachment.getContent()));
           BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream())
      ) {

         byte[] buffer = new byte[10240];
         for (int length = input.read(buffer); length > 0;) {
            output.write(buffer, 0, length);
            length = input.read(buffer);
         }
      } catch (IOException ex) {
         addMessage(ERROR, "Error occurred while downloading attachment.");
      }

      FacesContext.getCurrentInstance().responseComplete();
   }

   public void uploadAttachment(FileUploadEvent event) throws Exception {
      UploadedFile item = event.getUploadedFile();
      TestExecutionAttachment attachment = new TestExecutionAttachment();
      attachment.setFilename(item.getName());
      attachment.setMimetype(item.getContentType());
      attachment.setContent(item.getData());
      attachment.setTestExecution(new TestExecution());
      attachment.getTestExecution().setId(testExecutionId);
      try {
         testExecutionService.addAttachment(attachment);
         testExecution = testExecutionService.getTestExecution(testExecutionId);
         showMultiValue(null);
      } catch (ServiceException e) {
         addMessage(e);
      }
   }

   public void deleteAttachment(TestExecutionAttachment attachment) {
      try {
         attachment.setTestExecution(new TestExecution());
         attachment.getTestExecution().setId(testExecutionId);
         testExecutionService.removeAttachment(attachment);
         testExecution = testExecutionService.getTestExecution(testExecutionId);
         showMultiValue(null);
      } catch (ServiceException e) {
         addMessage(e);
      }
   }
}