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
//package org.perfrepo.web;
//
//import org.apache.log4j.Logger;
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.shrinkwrap.api.Archive;
//import org.jboss.shrinkwrap.api.ArchivePaths;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.asset.EmptyAsset;
//import org.jboss.shrinkwrap.api.spec.WebArchive;
//import org.junit.After;
//import org.junit.Ignore;
//import org.junit.runner.RunWith;
//import org.perfrepo.model.Metric;
//import org.perfrepo.model.MetricComparator;
//import org.perfrepo.model.Test;
//import org.perfrepo.model.builder.TestBuilder;
//import org.perfrepo.model.to.TestExecutionSearchTO;
//import org.perfrepo.web.dao.DAO;
//import org.perfrepo.web.security.Secured;
//import org.perfrepo.web.service.TestService;
//import org.perfrepo.web.service.TestServiceBean;
//import org.perfrepo.web.service.exceptions.ServiceException;
//import org.perfrepo.web.session.TEComparatorSession;
//
//import javax.ejb.EJBException;
//import javax.inject.Inject;
//import javax.security.auth.Subject;
//import javax.security.auth.login.LoginContext;
//import java.security.PrivilegedAction;
//import java.util.concurrent.Callable;
//
///**
// * Tests for {@link TestServiceBean}
// *
// * @author Michal Linhard (mlinhard@redhat.com)
// */
//@RunWith(Arquillian.class)
//@Ignore
//public class TestServiceBeanTest {
//
//   private static final Logger log = Logger.getLogger(TestServiceBeanTest.class);
//
//   private static String testUserRole = System.getProperty("perfrepo.test.role", "testuser");
//
//   @Deployment
//   public static Archive<?> createDeployment() {
//      WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
//      war.addPackage(DAO.class.getPackage());
//      war.addPackage(TestService.class.getPackage());
//      war.addPackage(Secured.class.getPackage());
//      war.addPackage(TEComparatorSession.class.getPackage());
//      war.addPackage(Test.class.getPackage());
//      war.addPackage(TestBuilder.class.getPackage());
//      war.addPackage(TestExecutionSearchTO.class.getPackage());
//      war.addPackage(JBossLoginContextFactory.class.getPackage());
//      war.addAsResource("test-persistence.xml", "META-INF/persistence.xml");
//      war.addAsResource("users.properties");
//      war.addAsResource("roles.properties");
//      war.addAsWebInfResource("test-jbossas-ds.xml");
//      war.addAsWebInfResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
//      return war;
//   }
//
//   @Inject
//   TestService testService;
//
//   @After
//   public void removeTests() throws Exception {
//      for (final Test test : testService.getAllFullTests()) {
//         LoginContext loginContext = JBossLoginContextFactory.createLoginContext(test.getGroup().getName(), test.getGroup().getName());
//         loginContext.login();
//         try {
//            Subject.doAs(loginContext.getSubject(), new PrivilegedAction<Void>() {
//               @Override
//               public Void run() {
//                  try {
//                     testService.removeTest(test);
//                  } catch (ServiceException e) {
//                     log.error("Error while removing test", e);
//                  }
//                  return null;
//               }
//            });
//         } catch (Exception e) {
//            log.error("Error while removing test", e);
//         } finally {
//            loginContext.logout();
//         }
//      }
//   }
//
//   protected void asUser(String username, final Callable<Void> callable) throws Exception {
//      LoginContext loginContext = JBossLoginContextFactory.createLoginContext(username, username);
//      loginContext.login();
//      try {
//         Exception exception = Subject.doAs(loginContext.getSubject(), new PrivilegedAction<Exception>() {
//            @Override
//            public Exception run() {
//               try {
//                  callable.call();
//                  return null;
//               } catch (Exception e) {
//                  return e;
//               }
//            }
//         });
//         if (exception != null) {
//            throw exception;
//         }
//      } finally {
//         loginContext.logout();
//      }
//   }
//
//   protected TestBuilder test(String nameAndUid) {
//      return Test.builder().name(nameAndUid).groupId(testUserRole).uid(nameAndUid).description("description for " + nameAndUid);
//   }
//
//   @org.junit.Test
//   public void testCreateDeleteTest() throws Exception {
//      asUser(testUserRole, new Callable<Void>() {
//         @Override
//         public Void call() throws Exception {
//            assert testService.getAllFullTests().isEmpty();
//            Test createdTest = testService.createTest(test("test1").description("this is a test test").metric("metric1", MetricComparator.HIGHER_BETTER, "this is a test metric 1")
//                                                          .metric("metric2", MetricComparator.HIGHER_BETTER, "this is a test metric 2").metric("multimetric", MetricComparator.HIGHER_BETTER, "this is a metric with multiple values").build());
//            assert testService.getAllFullTests().contains(createdTest);
//            testService.removeTest(createdTest);
//            assert testService.getAllFullTests().isEmpty();
//            return null;
//         }
//      });
//   }
//
//   @org.junit.Test
//   public void testCreateDeleteTestUnknownGroup() throws Exception {
//      asUser("testuser", new Callable<Void>() {
//         @Override
//         public Void call() throws Exception {
//            assert testService.getAllFullTests().isEmpty();
//            try {
//               testService.createTest(Test.builder().name("test1").groupId("unknowngroup").uid("test1").description("this is a test test")
//                                          .metric("metric1", "desc").metric("metric2").build());
//               assert false;
//            } catch (EJBException e) {
//               assert e.getCause() instanceof SecurityException;
//            }
//            assert testService.getAllFullTests().isEmpty();
//            return null;
//         }
//      });
//   }
//
//   @org.junit.Test
//   public void testCreateDuplicateTestUID() throws Exception {
//      asUser(testUserRole, new Callable<Void>() {
//         @Override
//         public Void call() throws Exception {
//            assert testService.getAllFullTests().isEmpty();
//            testService.createTest(test("test1").build());
//            try {
//               testService.createTest(test("test1").build());
//               assert false;
//            } catch (ServiceException e) {
//               // ok
//            }
//            assert testService.getAllFullTests().size() == 1;
//            return null;
//         }
//      });
//   }
//
//   @org.junit.Test
//   public void testAddMetric() throws Exception {
//      asUser(testUserRole, new Callable<Void>() {
//         @Override
//         public Void call() throws Exception {
//            assert testService.getAllFullTests().isEmpty();
//            Test test = testService.createTest(test("test1").metric("metric1", "desc").metric("metric2", "desc").build());
//            testService.addMetric(test, Metric.builder().name("metric3").description("metric 3").build());
//            assert testService.getAllFullTests().size() == 1;
//            return null;
//         }
//      });
//   }
//
//   @org.junit.Test
//   public void testAddDuplicateMetric() throws Exception {
//      asUser(testUserRole, new Callable<Void>() {
//         @Override
//         public Void call() throws Exception {
//            assert testService.getAllFullTests().isEmpty();
//            Test createdTest = testService.createTest(test("test1").metric("A", "desc").build());
//            try {
//               testService.addMetric(createdTest, Metric.builder().name("A").description("desc").build());
//               assert false;
//            } catch (ServiceException e) {
//               // ok
//            }
//            assert testService.getAllFullTests().size() == 1;
//            return null;
//         }
//      });
//   }
//
//   @org.junit.Test
//   public void testAddDuplicateMetricInDifferentTest() throws Exception {
//      asUser(testUserRole, new Callable<Void>() {
//         @Override
//         public Void call() throws Exception {
//            assert testService.getAllFullTests().isEmpty();
//            testService.createTest(test("test1").metric("A", "desc").build());
//            try {
//               testService.createTest(test("test2").metric("A", "desc").build());
//               assert false;
//            } catch (ServiceException e) {
//               // ok
//            }
//            assert testService.getAllFullTests().size() == 1;
//            return null;
//         }
//      });
//   }
//
//   @org.junit.Test
//   public void testAddDuplicateMetricInDifferentTest2() throws Exception {
//      asUser(testUserRole, new Callable<Void>() {
//         @Override
//         public Void call() throws Exception {
//            assert testService.getAllFullTests().isEmpty();
//            testService.createTest(test("test1").metric("A", "desc").build());
//            Test test2 = testService.createTest(test("test2").metric("B", "desc").build());
//            try {
//               testService.addMetric(test2, Metric.builder().name("A").description("desc").build());
//               assert false;
//            } catch (ServiceException e) {
//               // ok
//            }
//            assert testService.getAllFullTests().size() == 1;
//            return null;
//         }
//      });
//   }
//}
