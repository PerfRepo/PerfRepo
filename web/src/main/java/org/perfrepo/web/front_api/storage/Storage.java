package org.perfrepo.web.front_api.storage;


import org.perfrepo.model.MetricComparator;
import org.perfrepo.web.dto.MetricDto;
import org.perfrepo.web.dto.TestDto;
import org.perfrepo.web.dto.UserGroupDto;

import javax.inject.Singleton;
import java.util.ArrayList;

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
        UserGroupDto group1 = new UserGroupDto();
        group1.setName("perfrepouser");
        userGroupStorage.create(group1);

        UserGroupDto group2 = new UserGroupDto();
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
        test1.setGroupId(1l);
        test1.setMetrics(new ArrayList<>());
        test1.getMetrics().add(metricStorage.getById(1l));
        test1.getMetrics().add(metricStorage.getById(2l));
        testStorage.create(test1);

        TestDto test2 = new TestDto();
        test2.setName("Second sample test");
        test2.setUid("sample_second_test");
        test1.setGroupId(2l);
        test2.setMetrics(new ArrayList<>());
        test2.getMetrics().add(metricStorage.getById(2l));
        testStorage.create(test2);

        TestDto test3 = new TestDto();
        test3.setName("Third sample test");
        test3.setUid("sample_third_test");
        test1.setGroupId(1l);
        test3.setMetrics(new ArrayList<>());
        test3.getMetrics().add(metricStorage.getById(1l));
        testStorage.create(test3);
    }

}



