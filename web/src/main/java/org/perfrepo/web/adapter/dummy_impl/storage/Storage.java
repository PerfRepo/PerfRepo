package org.perfrepo.web.adapter.dummy_impl.storage;


import org.perfrepo.model.MetricComparator;
import org.perfrepo.dto.metric.MetricDto;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.user.GroupDto;

import javax.inject.Singleton;
import java.util.ArrayList;

/**
 * Temporary in-memory storage interface for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
@Singleton
public class Storage {

    private final TestStorage testStorage;
    private final MetricStorage metricStorage;
    private final UserGroupStorage userGroupStorage;

    public Storage() {
        testStorage = new TestStorage();
        metricStorage = new MetricStorage();
        userGroupStorage = new UserGroupStorage();
        initialize();
    }

    public TestStorage test() {
        return testStorage;
    }

    public MetricStorage metric() {
        return metricStorage;
    }

    public UserGroupStorage userGroup() {
        return userGroupStorage;
    }

    private void initialize() {
        //TODO create builders...
        GroupDto group1 = new GroupDto();
        group1.setName("perfrepouser");
        userGroupStorage.create(group1);

        GroupDto group2 = new GroupDto();
        group2.setName("super user group");
        userGroupStorage.create(group2);

        MetricDto metric1 = new MetricDto();
        metric1.setComparator(MetricComparator.HB.name());
        metric1.setName("Throughput");
        metric1.setDescription("Amount of transactions produced over time during a test.");
        metricStorage.create(metric1);

        MetricDto metric2 = new MetricDto();
        metric2.setComparator(MetricComparator.LB.name());
        metric2.setName("Response time");
        metricStorage.create(metric2);

        TestDto test1 = new TestDto();
        test1.setName("Echo socket test");
        test1.setUid("echo_socket_test");
        test1.setDescription("Bla bla");
        test1.setGroup(userGroupStorage.getById(1l));
        test1.setMetrics(new ArrayList<>());
        test1.getMetrics().add(metricStorage.getById(1l));
        test1.getMetrics().add(metricStorage.getById(2l));
        testStorage.create(test1);

        TestDto test2 = new TestDto();
        test2.setName("Second sample test");
        test2.setUid("sample_second_test");
        test2.setGroup(userGroupStorage.getById(2l));
        test2.setMetrics(new ArrayList<>());
        test2.getMetrics().add(metricStorage.getById(2l));
        testStorage.create(test2);

        TestDto test3 = new TestDto();
        test3.setName("Third sample test");
        test3.setUid("sample_third_test");
        test3.setGroup(userGroupStorage.getById(1l));
        test3.setMetrics(new ArrayList<>());
        test3.getMetrics().add(metricStorage.getById(1l));
        testStorage.create(test3);

        TestDto test4 = new TestDto();
        test4.setName("Sample test 4");
        test4.setUid("sample_4_test");
        test4.setGroup(userGroupStorage.getById(1l));
        test4.setMetrics(new ArrayList<>());
        test4.getMetrics().add(metricStorage.getById(1l));
        testStorage.create(test4);

        TestDto test5 = new TestDto();
        test5.setName("Sample test 5");
        test5.setUid("sample_5_test");
        test5.setGroup(userGroupStorage.getById(1l));
        test5.setMetrics(new ArrayList<>());
        test5.getMetrics().add(metricStorage.getById(1l));
        testStorage.create(test5);

        TestDto test6 = new TestDto();
        test6.setName("Sample test 6");
        test6.setUid("sample_6_test");
        test6.setGroup(userGroupStorage.getById(1l));
        test6.setMetrics(new ArrayList<>());
        test6.getMetrics().add(metricStorage.getById(1l));
        testStorage.create(test6);

        TestDto test7 = new TestDto();
        test7.setName("Sample test 7");
        test7.setUid("sample_7_test");
        test7.setGroup(userGroupStorage.getById(1l));
        test7.setMetrics(new ArrayList<>());
        test7.getMetrics().add(metricStorage.getById(1l));
        testStorage.create(test7);

        TestDto test8 = new TestDto();
        test8.setName("Sample test 8");
        test8.setUid("sample_8_test");
        test8.setGroup(userGroupStorage.getById(1l));
        test8.setMetrics(new ArrayList<>());
        test8.getMetrics().add(metricStorage.getById(1l));
        testStorage.create(test8);
        
        TestDto test9 = new TestDto();
        test9.setName("Sample test 9");
        test9.setUid("sample_9_test");
        test9.setGroup(userGroupStorage.getById(1l));
        test9.setMetrics(new ArrayList<>());
        test9.getMetrics().add(metricStorage.getById(1l));
        testStorage.create(test9);

    }
}



