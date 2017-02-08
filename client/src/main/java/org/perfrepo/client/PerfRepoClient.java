///**
// * PerfRepo
// * <p>
// * Copyright (C) 2015 the original author or authors.
// * <p>
// * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
// * the License. You may obtain a copy of the License at
// * <p>
// * http://www.apache.org/licenses/LICENSE-2.0
// * <p>
// * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
// * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
// * specific language governing permissions and limitations under the License.
// */
//package org.perfrepo.client;
//
//import org.apache.commons.codec.binary.Base64;
//import org.apache.http.Header;
//import org.apache.http.HttpHeaders;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpDelete;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpRequestBase;
//import org.apache.http.entity.AbstractHttpEntity;
//import org.apache.http.entity.ByteArrayEntity;
//import org.apache.http.entity.InputStreamEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;
//import org.apache.log4j.Logger;
//import org.perfrepo.web.model.Metric;
//import org.perfrepo.web.model.Test;
//import org.perfrepo.web.model.TestExecution;
//import org.perfrepo.web.model.Value;
//import org.perfrepo.web.model.report.Permission;
//import org.perfrepo.web.model.report.Report;
//import org.perfrepo.web.model.to.ListWrapper;
//import org.perfrepo.web.model.to.TestExecutionSearchTO;
//
//import javax.xml.bind.DataBindingException;
//import javax.xml.bind.JAXB;
//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.Unmarshaller;
//import javax.xml.transform.stream.StreamSource;
//import java.io.BufferedReader;
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.InputStreamReader;
//import java.util.List;
//
///**
// * Performance Repository REST API Client.
// *
// * @author Michal Linhard (mlinhard@redhat.com)
// * @author Jiri Holusa (jholusa@redhat.com)
// */
//public class PerfRepoClient {
//
//   private static final Logger log = Logger.getLogger(PerfRepoClient.class);
//
//   private static final String CONTENT_TYPE_XML = "text/xml";
//   private static final String REST_BASE_URL_TEMPLATE = "http://%s%s/deprecated_rest/";
//   private String host;
//   private String url;
//   private String basicAuthHash;
//
//   private HttpClient httpClient;
//
//   /**
//    * Create the client.
//    *
//    * @param host Host (may contain port in form host:port)
//    * @param url url of the PerfRepo instance on the server. If null or empty string provided,
//    * it refers to root of the server. If some context path provided, slash is appended automatically.
//    * I.e. new PerfRepoClient("localhost", "repository", "hash") is bounded to localhost/repository/
//    * @param username login credential
//    * @param password login credential
//    */
//   public PerfRepoClient(String host, String url, String username, String password) {
//      this.host = host;
//      this.url = url;
//      if (this.url != null && !url.isEmpty()) {
//         this.url = "/" + this.url;
//      }
//
//      this.basicAuthHash = Base64.encodeBase64String((username + ":" + password).getBytes()).trim();
//      httpClient = new DefaultHttpClient();
//   }
//
//   /**
//    * Create the client. Deprecated usage!
//    *
//    * @param host Host (may contain port in form host:port)
//    * @param url url of the PerfRepo instance on the server. If null or empty string provided,
//    * it refers to root of the server. If some context path provided, slash is appended automatically.
//    * I.e. new PerfRepoClient("localhost", "repository", "hash") is bounded to localhost/repository/
//    * @param basicAuthHash Base64 encoded username:password for BASIC Authentication
//    */
//   @Deprecated
//   public PerfRepoClient(String host, String url, String basicAuthHash) {
//      this.host = host;
//      this.url = url;
//      if (this.url != null && !url.isEmpty()) {
//         this.url = "/" + this.url;
//      }
//
//      this.basicAuthHash = basicAuthHash;
//      httpClient = new DefaultHttpClient();
//   }
//
//   /**
//    * Shutdown the underlying HTTP client
//    */
//   public void shutdown() {
//      httpClient.getConnectionManager().shutdown();
//   }
//
//   private String restUrl(String urlTemplate, Object... params) {
//      return String.format(String.format(REST_BASE_URL_TEMPLATE, host, url == null ? "" : url) + urlTemplate, params);
//   }
//
//   private void logHttpError(String msg, HttpRequestBase req, HttpResponse resp) throws Exception {
//      log.error(msg + "\nHTTP Status: " + resp.getStatusLine().getStatusCode() + "\nREST API url: " + req.getURI().toString() + "\nResponse:\n"
//                    + EntityUtils.toString(resp.getEntity()));
//   }
//
//   private HttpPost createBasicPost(String relURL, Object... params) {
//      HttpPost post = new HttpPost(restUrl(relURL, params));
//      post.setHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_XML);
//      post.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + this.basicAuthHash);
//      return post;
//   }
//
//   private HttpGet createBasicGet(String relURL, Object... params) {
//      HttpGet get = new HttpGet(restUrl(relURL, params));
//      get.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + this.basicAuthHash);
//      return get;
//   }
//
//   private HttpDelete createBasicDelete(String relURL, Object... params) {
//      HttpDelete delete = new HttpDelete(restUrl(relURL, params));
//      delete.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + this.basicAuthHash);
//      return delete;
//   }
//
//   private void setPostEntity(HttpPost req, Object obj) {
//      ByteArrayOutputStream bos = new ByteArrayOutputStream();
//      JAXB.marshal(obj, bos);
//      req.setEntity(new ByteArrayEntity(bos.toByteArray()));
//   }
//
//   /**
//    * Create a new test with subobjects.
//    *
//    * @param test The new test.
//    * @return ID of new test.
//    * @throws Exception
//    */
//   public Long createTest(Test test) throws Exception {
//      HttpPost post = createBasicPost("test/create");
//      setPostEntity(post, test);
//      HttpResponse resp = httpClient.execute(post);
//      if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
//         logHttpError("Error while creating test", post, resp);
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      }
//      Header[] locations = resp.getHeaders(HttpHeaders.LOCATION);
//      if (locations != null && locations.length > 0) {
//         log.debug("Created new test at: " + locations[0].getValue());
//      }
//      Long id = new Long(EntityUtils.toString(resp.getEntity()));
//      EntityUtils.consume(resp.getEntity());
//      return id;
//   }
//
//   /**
//    * Adds value to existing testExecution
//    *
//    * @param te
//    * @return ID of new value.
//    * @throws Exception
//    */
//   public Long addValue(TestExecution te) throws Exception {
//      HttpPost post = createBasicPost("testExecution/addValue");
//      setPostEntity(post, te);
//      HttpResponse resp = httpClient.execute(post);
//      if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
//         logHttpError("Error while creating value", post, resp);
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      }
//      Header[] locations = resp.getHeaders(HttpHeaders.LOCATION);
//      if (locations != null && locations.length > 0) {
//         log.debug("Added new value at: " + locations[0].getValue());
//      }
//      Long id = new Long(EntityUtils.toString(resp.getEntity()));
//      EntityUtils.consume(resp.getEntity());
//      return id;
//   }
//
//   /**
//    * Get test by id.
//    *
//    * @param id
//    * @return The test
//    * @throws Exception
//    */
//   public Test getTest(Long id) throws Exception {
//      HttpGet get = createBasicGet("test/id/%s", id);
//      HttpResponse resp = httpClient.execute(get);
//      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//         Test obj;
//         try {
//            obj = JAXB.unmarshal(resp.getEntity().getContent(), Test.class);
//         } catch (javax.xml.bind.DataBindingException ex) {
//            log.warn("Error occurred while unmarshalling response, probably empty response.");
//            return null;
//         }
//         EntityUtils.consume(resp.getEntity());
//         return obj;
//      } else if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      } else {
//         logHttpError("Error while getting test", get, resp);
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      }
//   }
//
//   /**
//    * Get test by uid.
//    *
//    * @param uid
//    * @return The test
//    * @throws Exception
//    */
//   public Test getTestByUid(String uid) throws Exception {
//      HttpGet get = createBasicGet("test/uid/%s", uid);
//      HttpResponse resp = httpClient.execute(get);
//      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//         Test obj;
//         try {
//            obj = JAXB.unmarshal(resp.getEntity().getContent(), Test.class);
//         } catch (DataBindingException ex) {
//            log.warn("Error occurred while unmarshalling response, probably empty response.");
//            return null;
//         }
//         EntityUtils.consume(resp.getEntity());
//         return obj;
//      } else if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      } else {
//         logHttpError("Error while getting test", get, resp);
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      }
//   }
//
//   /**
//    * Delete a test.
//    *
//    * @param id
//    * @return True on success
//    * @throws Exception
//    */
//   public boolean deleteTest(Long id) throws Exception {
//      HttpDelete delete = createBasicDelete("test/id/%s", id);
//      HttpResponse resp = httpClient.execute(delete);
//      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
//         EntityUtils.consume(resp.getEntity());
//         log.debug("Deleted test: " + id);
//         return true;
//      } else {
//         logHttpError("Error while deleting test", delete, resp);
//         EntityUtils.consume(resp.getEntity());
//         return false;
//      }
//   }
//
//   /**
//    * Create a new test execution.
//    *
//    * @param testExecution
//    * @return ID of new execution
//    * @throws Exception
//    */
//   public Long createTestExecution(TestExecution testExecution) throws Exception {
//      HttpPost post = createBasicPost("testExecution/create");
//      setPostEntity(post, testExecution);
//      HttpResponse resp = httpClient.execute(post);
//      if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
//         logHttpError("Error while creating test execution", post, resp);
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      }
//      Header[] locations = resp.getHeaders(HttpHeaders.LOCATION);
//      if (locations != null && locations.length > 0) {
//         log.debug("Created new test execution at: " + locations[0].getValue());
//      }
//      Long id = new Long(EntityUtils.toString(resp.getEntity()));
//      EntityUtils.consume(resp.getEntity());
//      return id;
//   }
//
//    /**
//     * Searches through test execution according to criteria.
//     *
//     * @param criteria
//     * @return
//     */
//   public List<TestExecution> searchTestExecutions(TestExecutionSearchTO criteria) throws Exception {
//      List<TestExecution> result = null;
//
//      HttpPost post = createBasicPost("testExecution/search");
//      setPostEntity(post, criteria);
//      HttpResponse resp = httpClient.execute(post);
//
//      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//         //String something = new BufferedReader(new InputStreamReader(resp.getEntity().getContent())).lines().collect(Collectors.joining("\n"));
//         JAXBContext jc = JAXBContext.newInstance(ListWrapper.class, TestExecution.class, Value.class);
//         Unmarshaller unmarshaller = jc.createUnmarshaller();
//
//         ListWrapper<TestExecution> obj = (ListWrapper<TestExecution>) unmarshaller.unmarshal(new StreamSource(resp.getEntity().getContent()), ListWrapper.class).getValue();
//         EntityUtils.consume(resp.getEntity());
//         return obj.getItems();
//      } else {
//         logHttpError("Error while searching for test executions", post, resp);
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      }
//   }
//
//   /**
//    * Updates existing test execution.
//    *
//    * @param testExecution test execution.
//    * @return ID of the test execution.
//    * @throws Exception
//    */
//   public Long updateTestExecution(TestExecution testExecution) throws Exception {
//      if (testExecution == null || testExecution.getId() == null) {
//         throw new IllegalArgumentException("Neither test execution nor test execution ID can be null.");
//      }
//
//      HttpPost post = createBasicPost("testExecution/update/%s", testExecution.getId());
//      setPostEntity(post, testExecution);
//      HttpResponse resp = httpClient.execute(post);
//      if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
//         logHttpError("Error while updating test execution.", post, resp);
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      }
//      Header[] locations = resp.getHeaders(HttpHeaders.LOCATION);
//      if (locations != null && locations.length > 0) {
//         log.debug("Updated test execution at: " + locations[0].getValue());
//      }
//      Long id = new Long(EntityUtils.toString(resp.getEntity()));
//      EntityUtils.consume(resp.getEntity());
//      return id;
//   }
//
//   /**
//    * Get test execution by id.
//    *
//    * @param id
//    * @return Test execution
//    * @throws Exception
//    */
//   public TestExecution getTestExecution(Long id) throws Exception {
//      HttpGet get = createBasicGet("testExecution/%s", id);
//      HttpResponse resp = httpClient.execute(get);
//      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//         TestExecution obj;
//         try {
//            obj = JAXB.unmarshal(resp.getEntity().getContent(), TestExecution.class);
//         } catch (DataBindingException ex) {
//            log.warn("Error occurred while unmarshalling response, probably empty response.");
//            return null;
//         }
//         EntityUtils.consume(resp.getEntity());
//         return obj;
//      } else if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      } else {
//         logHttpError("Error while getting test execution", get, resp);
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      }
//   }
//
//   /**
//    * Delete a test execution.
//    *
//    * @param id
//    * @return True on success
//    * @throws Exception
//    */
//   public boolean deleteTestExecution(Long id) throws Exception {
//      HttpDelete req = createBasicDelete("testExecution/%s", id);
//      HttpResponse resp = httpClient.execute(req);
//      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
//         EntityUtils.consume(resp.getEntity());
//         log.debug("Deleted test execution: " + id);
//         return true;
//      } else {
//         logHttpError("Error while deleting test execution", req, resp);
//         EntityUtils.consume(resp.getEntity());
//         return false;
//      }
//   }
//
//   /**
//    * Add metric to an existing test.
//    *
//    * @param testId Test id
//    * @param metric Metric
//    * @return Id of the new metric.
//    * @throws Exception
//    */
//   public Long addMetric(Long testId, Metric metric) throws Exception {
//      HttpPost post = createBasicPost("test/id/%s/addMetric", testId);
//      setPostEntity(post, metric);
//      HttpResponse resp = httpClient.execute(post);
//      if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
//         logHttpError("Error while adding metric", post, resp);
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      }
//      Header[] locations = resp.getHeaders(HttpHeaders.LOCATION);
//      if (locations != null && locations.length > 0) {
//         log.debug("Created new metric at: " + locations[0].getValue());
//      }
//      Long id = new Long(EntityUtils.toString(resp.getEntity()));
//      EntityUtils.consume(resp.getEntity());
//      return id;
//   }
//
//   /**
//    * Get metric by id.
//    *
//    * @param id
//    * @return The metric
//    * @throws Exception
//    */
//   public Metric getMetric(Long id) throws Exception {
//      HttpGet get = createBasicGet("metric/%s", id);
//      HttpResponse resp = httpClient.execute(get);
//      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//         Metric metric = JAXB.unmarshal(resp.getEntity().getContent(), Metric.class);
//         EntityUtils.consume(resp.getEntity());
//         return metric;
//      } else if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      } else {
//         logHttpError("Error while getting metric", get, resp);
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      }
//   }
//
//   /**
//    * Add attachment to an existing test execution.
//    *
//    * @param testExecutionId Test execution id
//    * @param file File to upload
//    * @param mimeType Mime type
//    * @param fileNameInRepo The name the attachment will have in the perf repo.
//    * @return Id of the new attachment
//    * @throws Exception
//    */
//   public Long uploadAttachment(Long testExecutionId, File file, String mimeType, String fileNameInRepo) throws Exception {
//      InputStreamEntity entity = new InputStreamEntity(new FileInputStream(file), file.length());
//      return uploadAttachment(testExecutionId, entity, mimeType, fileNameInRepo);
//   }
//
//   /**
//    * Add attachment to an existing test execution.
//    *
//    * @param testExecutionId Test execution id
//    * @param content File content to upload
//    * @param mimeType Mime type
//    * @param fileNameInRepo The name the attachment will have in the perf repo.
//    * @return Id of the new attachment
//    * @throws Exception
//    */
//   public Long uploadAttachment(Long testExecutionId, byte[] content, String mimeType, String fileNameInRepo) throws Exception {
//      return uploadAttachment(testExecutionId, new ByteArrayEntity(content), mimeType, fileNameInRepo);
//   }
//
//   private Long uploadAttachment(Long testExecutionId, AbstractHttpEntity entity, String mimeType, String fileNameInRepo) throws Exception {
//      HttpPost post = new HttpPost(restUrl("testExecution/%s/addAttachment", testExecutionId));
//      post.setHeader(HttpHeaders.CONTENT_TYPE, mimeType);
//      post.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + this.basicAuthHash);
//      post.setHeader("filename", fileNameInRepo);
//      post.setEntity(entity);
//      HttpResponse resp = httpClient.execute(post);
//      if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
//         logHttpError("Error while uploading attachment", post, resp);
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      }
//      Header[] locations = resp.getHeaders(HttpHeaders.LOCATION);
//      if (locations != null && locations.length > 0) {
//         log.debug("Uploaded attachment: " + locations[0].getValue());
//      }
//      Long id = new Long(EntityUtils.toString(resp.getEntity()));
//      EntityUtils.consume(resp.getEntity());
//      return id;
//   }
//
//   /**
//    * Downloads attachment to a local file.
//    *
//    * @param attachmentId Attachment id
//    * @param file File to save the attachment to
//    * @return True on success
//    * @throws Exception
//    */
//   public boolean downloadAttachment(Long attachmentId, File file) throws Exception {
//      HttpGet get = createBasicGet("testExecution/attachment/%s", attachmentId);
//      HttpResponse resp = httpClient.execute(get);
//      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//         resp.getEntity().writeTo(new FileOutputStream(file));
//         EntityUtils.consume(resp.getEntity());
//         return true;
//      } else if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
//         EntityUtils.consume(resp.getEntity());
//         return false;
//      } else {
//         logHttpError("Error while downloading attachment", get, resp);
//         EntityUtils.consume(resp.getEntity());
//         return false;
//      }
//   }
//
//   /**
//    * Get report by id.
//    *
//    * @param id
//    * @return The report
//    * @throws Exception
//    */
//   public Report getReport(Long id) throws Exception {
//      HttpGet get = createBasicGet("report/id/%s", id);
//      HttpResponse resp = httpClient.execute(get);
//      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//         Report obj;
//         try {
//            obj = JAXB.unmarshal(resp.getEntity().getContent(), Report.class);
//         } catch (DataBindingException ex) {
//            log.warn("Error occurred while unmarshalling response, probably empty response.");
//            return null;
//         }
//         EntityUtils.consume(resp.getEntity());
//         return obj;
//      } else if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      } else {
//         logHttpError("Error while getting report.", get, resp);
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      }
//   }
//
//   /**
//    * Create a new report.
//    *
//    * @param report The new report.
//    * @return ID of new report.
//    * @throws Exception
//    */
//   public Long createReport(Report report) throws Exception {
//      HttpPost post = createBasicPost("report/create");
//      setPostEntity(post, report);
//      HttpResponse resp = httpClient.execute(post);
//      if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
//         logHttpError("Error while creating report.", post, resp);
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      }
//      Header[] locations = resp.getHeaders(HttpHeaders.LOCATION);
//      if (locations != null && locations.length > 0) {
//         log.debug("Created new report at: " + locations[0].getValue());
//      }
//      Long id = new Long(EntityUtils.toString(resp.getEntity()));
//      EntityUtils.consume(resp.getEntity());
//      return id;
//   }
//
//   /**
//    * Updates existing report.
//    *
//    * @param report report.
//    * @return ID of the report.
//    * @throws Exception
//    */
//   public Long updateReport(Report report) throws Exception {
//      if (report == null || report.getId() == null) {
//         throw new IllegalArgumentException("Neither report nor report ID can be null.");
//      }
//
//      HttpPost post = createBasicPost("report/update/%s", report.getId());
//      setPostEntity(post, report);
//      HttpResponse resp = httpClient.execute(post);
//      if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
//         logHttpError("Error while updating report.", post, resp);
//         EntityUtils.consume(resp.getEntity());
//         return null;
//      }
//      Header[] locations = resp.getHeaders(HttpHeaders.LOCATION);
//      if (locations != null && locations.length > 0) {
//         log.debug("Updated report at: " + locations[0].getValue());
//      }
//      Long id = new Long(EntityUtils.toString(resp.getEntity()));
//      EntityUtils.consume(resp.getEntity());
//      return id;
//   }
//
//   /**
//    * Delete a report.
//    *
//    * @param id
//    * @return True on success
//    * @throws Exception
//    */
//   public boolean deleteReport(Long id) throws Exception {
//      HttpDelete delete = createBasicDelete("report/id/%s", id);
//      HttpResponse resp = httpClient.execute(delete);
//      if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
//         EntityUtils.consume(resp.getEntity());
//         log.debug("Deleted report: " + id);
//         return true;
//      } else {
//         logHttpError("Error while deleting report", delete, resp);
//         EntityUtils.consume(resp.getEntity());
//         return false;
//      }
//   }
//
//   public boolean addReportPermission(Permission permission) throws Exception {
//      if (permission == null || permission.getReportId() == null) {
//         throw new IllegalArgumentException("Permission or its report ID cannot be null.");
//      }
//
//      HttpPost post = createBasicPost("report/id/" + permission.getReportId() + "/addPermission");
//      setPostEntity(post, permission);
//      HttpResponse resp = httpClient.execute(post);
//      if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//         logHttpError("Error while adding permission", post, resp);
//         EntityUtils.consume(resp.getEntity());
//         return false;
//      }
//      Header[] locations = resp.getHeaders(HttpHeaders.LOCATION);
//      if (locations != null && locations.length > 0) {
//         log.debug("Added new permission at: " + locations[0].getValue());
//      }
//      EntityUtils.consume(resp.getEntity());
//      return true;
//   }
//
//   public boolean updateReportPermission(Permission permission) throws Exception {
//      if (permission == null || permission.getReportId() == null) {
//         throw new IllegalArgumentException("Permission or its report ID cannot be null.");
//      }
//
//      HttpPost post = createBasicPost("report/id/" + permission.getReportId() + "/updatePermission");
//      setPostEntity(post, permission);
//      HttpResponse resp = httpClient.execute(post);
//      if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//         logHttpError("Error while updating permission", post, resp);
//         EntityUtils.consume(resp.getEntity());
//         return false;
//      }
//      Header[] locations = resp.getHeaders(HttpHeaders.LOCATION);
//      if (locations != null && locations.length > 0) {
//         log.debug("Updated permission at: " + locations[0].getValue());
//      }
//      EntityUtils.consume(resp.getEntity());
//      return true;
//   }
//
//   public boolean deleteReportPermission(Permission permission) throws Exception {
//      if (permission == null || permission.getReportId() == null) {
//         throw new IllegalArgumentException("Permission or its report ID cannot be null.");
//      }
//
//      HttpPost post = createBasicPost("report/id/" + permission.getReportId() + "/deletePermission");
//      setPostEntity(post, permission);
//      HttpResponse resp = httpClient.execute(post);
//      if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
//         logHttpError("Error while deleting permission", post, resp);
//         EntityUtils.consume(resp.getEntity());
//         return false;
//      }
//      Header[] locations = resp.getHeaders(HttpHeaders.LOCATION);
//      if (locations != null && locations.length > 0) {
//         log.debug("Deleted permission at: " + locations[0].getValue());
//      }
//      EntityUtils.consume(resp.getEntity());
//      return true;
//   }
//
//   public String getServerVersion() throws Exception {
//      HttpGet get = createBasicGet("info/version");
//      HttpResponse resp = httpClient.execute(get);
//      BufferedReader reader = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
//
//      return reader.readLine();
//   }
//}
