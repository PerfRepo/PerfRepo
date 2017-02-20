package org.perfrepo.web.adapter.dummy_impl.storage;

import org.apache.commons.lang.time.DateUtils;
import org.perfrepo.dto.test_execution.AttachmentDto;
import org.perfrepo.enums.MeasuredValueType;
import org.perfrepo.enums.MetricComparator;
import org.perfrepo.web.adapter.dummy_impl.builders.*;

import javax.inject.Singleton;
import java.util.Date;

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

    private final TestToAlertStorage testToAlertStorage;

    private final TestExecutionStorage testExecutionStorage;

    private final AttachmentStorage attachmentStorage;

    public Storage() {
        testStorage = new TestStorage();
        metricStorage = new MetricStorage();
        groupStorage = new GroupStorage();
        userStorage = new UserStorage();
        tokenStorage = new TokenStorage();
        alertStorage = new AlertStorage();
        testToAlertStorage = new TestToAlertStorage();
        testExecutionStorage = new TestExecutionStorage();
        attachmentStorage = new AttachmentStorage();
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

    public TestToAlertStorage testToAlert() {
        return testToAlertStorage;
    }

    public TestExecutionStorage testExecution() {
        return testExecutionStorage;
    }

    public AttachmentStorage attachment() {
        return attachmentStorage;
    }

    private void initialize() {
        // ***** USERS *****
        // user 1
        userStorage.create(new UserDtoBuilder()
                .username("grunwjir")
                .email("grunwjir@gmail.com")
                .firstName("Jiri")
                .lastName("Grunwald")
                .password("123456").build());

        // ***** GROUPS *****
        // group 1
        groupStorage.create(new GroupDtoBuilder()
                .name("perfrepouser")
                .build());
        // group 2
        groupStorage.create(new GroupDtoBuilder()
                .name("super user group")
                .build());

        // ***** METRICS *****
        // metric 1
        metricStorage.create(new MetricDtoBuilder()
                .comparator(MetricComparator.HIGHER_BETTER)
                .name("Throughput")
                .description("Amount of transactions produced over time during a test.")
                .build());
        // metric 2
        metricStorage.create(new MetricDtoBuilder()
                .comparator(MetricComparator.LOWER_BETTER)
                .name("Response time")
                .build());

        initializeTests();
        initializeTestExecutions();
    }

    private void initializeTests() {
        // test 1
        testStorage.create(new TestDtoBuilder()
                .name("Echo socket test")
                .uid("echo_socket_test")
                .description("This is echo socket test")
                .group(groupStorage.getById(1L))
                .metric(metricStorage.getById(1L))
                .metric(metricStorage.getById(2L))
                .build());
        // test 2
        testStorage.create(new TestDtoBuilder()
                .name("Second sample test")
                .uid("sample_second_test")
                .group(groupStorage.getById(2L))
                .metric(metricStorage.getById(2L))
                .build());
        // test 3
        testStorage.create(new TestDtoBuilder()
                .name("Third sample test")
                .uid("sample_third_test")
                .group(groupStorage.getById(2L))
                .metric(metricStorage.getById(1L))
                .metric(metricStorage.getById(2L))
                .build());
        // test 4
        testStorage.create(new TestDtoBuilder()
                .name("Sample test 4")
                .uid("sample_test_4")
                .group(groupStorage.getById(1L))
                .metric(metricStorage.getById(2L))
                .build());
        // test 5
        testStorage.create(new TestDtoBuilder()
                .name("Sample test 5")
                .uid("sample_test_5")
                .group(groupStorage.getById(1L))
                .metric(metricStorage.getById(2L))
                .build());
        // test 6
        testStorage.create(new TestDtoBuilder()
                .name("Sample test 6")
                .uid("sample_test_6")
                .group(groupStorage.getById(2L))
                .metric(metricStorage.getById(1L))
                .build());
        // test 7
        testStorage.create(new TestDtoBuilder()
                .name("Sample test 7")
                .uid("sample_test_7")
                .group(groupStorage.getById(2L))
                .metric(metricStorage.getById(1L))
                .metric(metricStorage.getById(2L))
                .build());
        // test 8
        testStorage.create(new TestDtoBuilder()
                .name("Sample test 8")
                .uid("sample_test_8")
                .group(groupStorage.getById(2L))
                .metric(metricStorage.getById(2L))
                .build());
        // test 9
        testStorage.create(new TestDtoBuilder()
                .name("Sample test 9")
                .uid("sample_test_9")
                .group(groupStorage.getById(1L))
                .metric(metricStorage.getById(2L))
                .metric(metricStorage.getById(1L))
                .build());
    }

    private void initializeTestExecutions() {
        initializeTestExecutionsMultiValue();
        initializeTestExecutionsSingleValue();
    }

    private void initializeTestExecutionsSingleValue() {
        // test execution 2 - single-value
        for (int i = 0; i < 10; i++) {
            testExecutionStorage.create(new TestExecutionDtoBuilder()
                    .name("Execution " + i + " of " + testStorage.getById(2L).getName())
                    .test(testStorage.getById(2L))
                    .tag("test")
                    .tag("example")
                    .tag(i + ".0")
                    .started(DateUtils.addDays(new Date(), -(9 - i)))
                    .executionParameter("environment", "test")
                    .executionValuesGroup(new ValuesGroupDtoBuilder()
                            .metric(metricStorage.getById(2L))
                            .value(new ValueDtoBuilder()
                                    .value(10.0 + i)
                                    .build())
                            .valueType(MeasuredValueType.SINGLE_VALUE)
                            .build())
                    .comment("Execution comment...")
                    .build());
        }

        // test execution 3 - single-value
        for (int i = 0; i < 10; i++) {
            testExecutionStorage.create(new TestExecutionDtoBuilder()
                    .name("Execution " + i + " of " + testStorage.getById(3L).getName())
                    .test(testStorage.getById(3L))
                    .tag("test")
                    .tag("example")
                    .tag(i + ".0")
                    .started(DateUtils.addDays(new Date(), -(9 - i)))
                    .executionParameter("environment", "test")
                    .executionValuesGroup(new ValuesGroupDtoBuilder()
                            .metric(metricStorage.getById(1L))
                            .value(new ValueDtoBuilder()
                                    .value(10.0 + i)
                                    .build())
                            .valueType(MeasuredValueType.SINGLE_VALUE)
                            .build())
                    .executionValuesGroup(new ValuesGroupDtoBuilder()
                            .metric(metricStorage.getById(2L))
                            .value(new ValueDtoBuilder()
                                    .value(600.0 - i)
                                    .build())
                            .valueType(MeasuredValueType.SINGLE_VALUE)
                            .build())
                    .comment("Execution comment...")
                    .build());
        }
    }

    private void initializeTestExecutionsMultiValue() {
        AttachmentDto attachment = new AttachmentDto();
        attachment.setFilename("log.txt");
        attachment.setContent("Hello!".getBytes());
        attachment.setSize(attachment.getContent().length);
        attachment.setMimeType("plain/text");
        attachmentStorage.create(attachment);
        // multi-value test execution for test id 1
        for (int i = 0; i < 10; i++) {
            testExecutionStorage.create(new TestExecutionDtoBuilder()
                    .name("Execution " + i + " of " + testStorage.getById(1L).getName())
                    .test(testStorage.getById(1L))
                    .tag("echo")
                    .tag("socket")
                    .tag(i + ".0")
                    .started(DateUtils.addDays(new Date(), -(9 - i)))
                    .executionParameter("environment", "test")
                    .executionParameter("server", "technecium")
                    .executionParameter("lib-version", "1.0.3")
                    .executionValuesGroup(new ValuesGroupDtoBuilder()
                            .metric(metricStorage.getById(1L))
                            .value(new ValueDtoBuilder()
                                    .value(10.0 + i)
                                    .parameter("time", 10)
                                    .parameter("percent", 30)
                                    .build())
                            .value(new ValueDtoBuilder()
                                    .value(15.0 + i)
                                    .parameter("time", 20)
                                    .parameter("percent", 60)
                                    .build())
                            .value(new ValueDtoBuilder()
                                    .value(17.0 + i)
                                    .parameter("time", 30)
                                    .parameter("percent", 90)
                                    .build())
                            .valueType(MeasuredValueType.MULTI_VALUE)
                            .parameterNames("time", "percent")
                            .build())
                    .comment("Nightly build of Echo socket test, version: " + i + ".0")
                    .executionAttachment(attachment)
                    .build());
        }
    }
}



