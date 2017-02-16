package org.perfrepo.web.service;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.perfrepo.web.model.Test;
import org.perfrepo.web.model.report.Report;
import org.perfrepo.web.model.report.ReportProperty;
import org.perfrepo.web.model.report.ReportType;
import org.perfrepo.web.model.to.SearchResultWrapper;
import org.perfrepo.web.model.user.Group;
import org.perfrepo.web.model.user.User;
import org.perfrepo.web.util.TestUtils;
import org.perfrepo.web.util.UserSessionMock;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link TestService}.
 *
 * @author Jiri Holusa (jholusa@redhat.com)
 */
@RunWith(Arquillian.class)
public class ReportServiceTest {

    @Inject
    private UserService userService;

    @Inject
    private GroupService groupService;

    @Inject
    private ReportService reportService;

    private Group testGroup1;
    private User testUser1;
    private User testUser2;
    private User adminUser;

    @Deployment
    public static Archive<?> createDeployment() {
        return TestUtils.createDeployment();
    }

    @Before
    public void init() {
        adminUser = createUser("admin");
        adminUser.setType(User.UserType.SUPER_ADMIN);
        UserSessionMock.setLoggedUser(adminUser); // hack, because we need some super admin to create a super admin :)
        userService.createUser(adminUser);
        UserSessionMock.setLoggedUser(adminUser);

        Group group1 = createGroup("test_group1");
        groupService.createGroup(group1);
        testGroup1 = group1;
        Group group2 = createGroup("test_group2");
        groupService.createGroup(group2);
        testGroup1 = group2;

        User user1 = createUser("test_user1");
        userService.createUser(user1);
        testUser1 = user1;
        User user2 = createUser("test_user2");
        userService.createUser(user2);
        testUser2 = user2;

        groupService.addUserToGroup(user1, group1);
        groupService.addUserToGroup(user2, group2);
        UserSessionMock.setLoggedUser(user1);
    }

    @After
    public void cleanUp() {
        UserSessionMock.setLoggedUser(adminUser);

        for (Report report: reportService.getAllReports()) {
            reportService.removeReport(report);
        }

        for (User user: userService.getAllUsers()) {
            userService.removeUser(user);
        }

        for (Group group: groupService.getAllGroups()) {
            groupService.removeGroup(group);
        }
    }

    @org.junit.Test
    public void testReportCRUDOperations() {
        Report report = new Report();
        fillReport("report1", report);
        report.setUser(testUser1);
        Report createdReport = reportService.createReport(report);

        Report retrievedReport = reportService.getReport(createdReport.getId());
        assertNotNull(retrievedReport);
        assertReport(createdReport, retrievedReport);

        // test update
        Report reportToUpdate = createdReport;
        fillReport("updated_report1", reportToUpdate);

        Report updatedReport = reportService.updateReport(reportToUpdate);
        assertReport(reportToUpdate, updatedReport);

        // test delete
        Report reportToDelete = updatedReport;
        reportService.removeReport(reportToDelete);
        assertNull(reportService.getReport(reportToDelete.getId()));
    }

    @org.junit.Test
    public void testReportCRUDOperationsWithProperties() {
        Report report = new Report();
        fillReport("report1", report);
        Map<String, ReportProperty> properties = new HashMap<>();
        fillProperties("property", report, properties);
        report.setUser(testUser1);
        report.setProperties(properties);
        Report createdReport = reportService.createReport(report);

        List<ReportProperty> retrievedProperties = reportService.getReportProperties(createdReport);
        assertEquals(properties.size(), retrievedProperties.size());
        assertTrue(properties.values().stream().allMatch(expected -> retrievedProperties.stream()
                .anyMatch(actual -> expected.equals(actual))));

        // test update
        Report reportToUpdate = createdReport;
        Map<String, ReportProperty> propertiesToUpdate = new HashMap<>();
        fillProperties("updated_property", reportToUpdate, propertiesToUpdate);
        reportToUpdate.setProperties(propertiesToUpdate);

        Report updatedReport = reportService.updateReport(reportToUpdate);
        List<ReportProperty> updatedProperties = reportService.getReportProperties(updatedReport);
        assertEquals(propertiesToUpdate.size(), updatedProperties.size());
        assertTrue(propertiesToUpdate.values().stream().allMatch(expected -> updatedProperties.stream()
                .anyMatch(actual -> expected.equals(actual))));

        // test delete
        Report reportToRemovePropertiesFrom = updatedReport;
        report.getProperties().clear();
        Report reportWithRemovedProperties = reportService.updateReport(reportToRemovePropertiesFrom);
        List<ReportProperty> removedProperties = reportService.getReportProperties(reportWithRemovedProperties);
        assertTrue(removedProperties.isEmpty());
    }

    /*** HELPER METHODS ***/

    private void assertReport(Report expected, Report actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getType(), actual.getType());
    }

    private void fillReport(String prefix, Report report) {
        report.setName(prefix + "_report");
        report.setType(ReportType.METRIC_HISTORY);
    }

    private void fillProperties(String prefix, Report report, Map<String, ReportProperty> properties) {
        properties.put(prefix + "_name1", createProperty(prefix + "_name1", prefix + "_value1", report));
        properties.put(prefix + "_name2", createProperty(prefix + "_name2", prefix + "_value2", report));
    }

    private ReportProperty createProperty(String name, String value, Report report) {
        ReportProperty property = new ReportProperty();
        property.setName(name);
        property.setValue(value);
        property.setReport(report);
        return property;
    }

    private User createUser(String prefix) {
        User user = new User();
        user.setFirstName(prefix + "_first_name");
        user.setLastName(prefix + "_last_name");
        user.setEmail(prefix + "@email.com");
        user.setUsername(prefix + "_username");
        user.setPassword(prefix + "_password");

        return user;
    }

    private Group createGroup(String name) {
        Group group = new Group();
        group.setName(name);

        return group;
    }
    
    private void assertSearchResultWithoutOrdering(List<Test> expectedResult, SearchResultWrapper<Test> actualResult) {
        assertEquals(expectedResult.size(), actualResult.getTotalSearchResultsCount());
        assertEquals(expectedResult.size(), actualResult.getResult().size());
        assertTrue(expectedResult.stream().allMatch(expected -> actualResult.getResult().stream()
                .anyMatch(actual -> expected.getId().equals(actual.getId()))));
    }

    private void assertSearchResultWithOrdering(List<Test> expectedResult, SearchResultWrapper<Test> actualResult) {
        assertSearchResultWithOrdering(expectedResult, actualResult, expectedResult.size());
    }

    private void assertSearchResultWithOrdering(List<Test> expectedResult, SearchResultWrapper<Test> actualResult, int expectedTotalCount) {
        assertEquals(expectedTotalCount, actualResult.getTotalSearchResultsCount());
        assertEquals(expectedResult.size(), actualResult.getResult().size());

        IntStream.range(0, expectedResult.size())
                .forEach(index -> assertEquals(expectedResult.get(index), actualResult.getResult().get(index)));
    }

}
