package org.perfrepo.web.adapter.dummy_impl.storage;


import org.perfrepo.dto.user.UserDto;
import org.perfrepo.model.MetricComparator;
import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.group.GroupDto;

import javax.inject.Singleton;
import java.util.HashSet;

/**
 * Temporary in-memory storage interface for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Singleton
public class Storage {

    private final TestStorage testStorage;

    private final MetricStorage metricStorage;

    private final GroupStorage groupStorage;

    private final UserStorage userStorage;

    private final TokenStorage tokenStorage;

    private final AlertStorage alertStorage;

    public Storage() {
        testStorage = new TestStorage();
        metricStorage = new MetricStorage();
        groupStorage = new GroupStorage();
        userStorage = new UserStorage();
        tokenStorage = new TokenStorage();
        alertStorage = new AlertStorage();
        initialize();
    }

    public TestStorage test() {
        return testStorage;
    }

    public MetricStorage metric() {
        return metricStorage;
    }

    public GroupStorage group() {
        return groupStorage;
    }

    public UserStorage user() {
        return userStorage;
    }

    public TokenStorage token() {
        return tokenStorage;
    }

    public AlertStorage alert() {
        return alertStorage;
    }

    private void initialize() {
        //TODO create builders...

        UserDto user1 = new UserDto();
        user1.setUsername("grunwjir");
        user1.setEmail("grunwjir@gmail.com");
        user1.setFirstName("Jiri");
        user1.setLastName("Grunwald");
        user1.setPassword("123456");
        userStorage.create(user1);

        GroupDto group1 = new GroupDto();
        group1.setName("perfrepouser");
        groupStorage.create(group1);

        GroupDto group2 = new GroupDto();
        group2.setName("super user group");
        groupStorage.create(group2);

        MetricDto metric1 = new MetricDto();
        metric1.setComparator(MetricComparator.HIGHER_BETTER);
        metric1.setName("Throughput");
        metric1.setDescription("Amount of transactions produced over time during a test.");
        metricStorage.create(metric1);

        MetricDto metric2 = new MetricDto();
        metric2.setComparator(MetricComparator.LOWER_BETTER);
        metric2.setName("Response time");
        metricStorage.create(metric2);

        TestDto test1 = new TestDto();
        test1.setName("Echo socket test");
        test1.setUid("echo_socket_test");
        test1.setDescription("Bla bla");
        test1.setGroup(groupStorage.getById(1L));
        test1.setMetrics(new HashSet<>());
        test1.getMetrics().add(metricStorage.getById(1L));
        test1.getMetrics().add(metricStorage.getById(2L));
        testStorage.create(test1);

        TestDto test2 = new TestDto();
        test2.setName("Second sample test");
        test2.setUid("sample_second_test");
        test2.setGroup(groupStorage.getById(2L));
        test2.setMetrics(new HashSet<>());
        test2.getMetrics().add(metricStorage.getById(2L));
        testStorage.create(test2);

        TestDto test3 = new TestDto();
        test3.setName("Third sample test");
        test3.setUid("sample_third_test");
        test3.setGroup(groupStorage.getById(1L));
        test3.setMetrics(new HashSet<>());
        test3.getMetrics().add(metricStorage.getById(1L));
        testStorage.create(test3);

        TestDto test4 = new TestDto();
        test4.setName("Sample test 4");
        test4.setUid("sample_4_test");
        test4.setGroup(groupStorage.getById(1L));
        test4.setMetrics(new HashSet<>());
        test4.getMetrics().add(metricStorage.getById(1L));
        testStorage.create(test4);

        TestDto test5 = new TestDto();
        test5.setName("Sample test 5");
        test5.setUid("sample_5_test");
        test5.setGroup(groupStorage.getById(1L));
        test5.setMetrics(new HashSet<>());
        test5.getMetrics().add(metricStorage.getById(1L));
        testStorage.create(test5);

        TestDto test6 = new TestDto();
        test6.setName("Sample test 6");
        test6.setUid("sample_6_test");
        test6.setGroup(groupStorage.getById(1L));
        test6.setMetrics(new HashSet<>());
        test6.getMetrics().add(metricStorage.getById(1L));
        testStorage.create(test6);

        TestDto test7 = new TestDto();
        test7.setName("Sample test 7");
        test7.setUid("sample_7_test");
        test7.setGroup(groupStorage.getById(1L));
        test7.setMetrics(new HashSet<>());
        test7.getMetrics().add(metricStorage.getById(1L));
        testStorage.create(test7);

        TestDto test8 = new TestDto();
        test8.setName("Sample test 8");
        test8.setUid("sample_8_test");
        test8.setGroup(groupStorage.getById(1L));
        test8.setMetrics(new HashSet<>());
        test8.getMetrics().add(metricStorage.getById(1L));
        testStorage.create(test8);
        
        TestDto test9 = new TestDto();
        test9.setName("Sample test 9");
        test9.setUid("sample_9_test");
        test9.setGroup(groupStorage.getById(1L));
        test9.setMetrics(new HashSet<>());
        test9.getMetrics().add(metricStorage.getById(1L));
        testStorage.create(test9);

    }
}



