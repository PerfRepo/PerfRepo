package org.perfrepo.web.adapter.dummy_impl.storage;

import org.apache.commons.lang.StringUtils;
import org.perfrepo.dto.test_execution.TestExecutionDto;
import org.perfrepo.dto.test_execution.TestExecutionSearchCriteria;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.enums.OrderBy;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Temporary in-memory test execution storage for development purpose.
 * TODO
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class TestExecutionStorage {

    private Long key = 1L;
    private List<TestExecutionDto> data = new ArrayList<>();
    private TestExecutionSearchCriteria testExecutionSearchCriteria;

    public TestExecutionStorage() {
        testExecutionSearchCriteria = new TestExecutionSearchCriteria();
        testExecutionSearchCriteria.setLimit(20);
        testExecutionSearchCriteria.setOrderBy(OrderBy.NAME_ASC);
    }

    public TestExecutionSearchCriteria getSearchCriteria() {
        return testExecutionSearchCriteria;
    }

    public TestExecutionDto getById(Long id) {
        Optional<TestExecutionDto> testExecution = data.stream().filter(dto -> dto.getId().equals(id)).findFirst();
        return testExecution.isPresent() ? testExecution.get() : null;
    }

    public TestExecutionDto create(TestExecutionDto dto) {
        dto.setId(getNextId());
        data.add(dto);
        return dto;
    }

    public List<TestExecutionDto> getAll() {
        return data;
    }

    public boolean delete(Long id) {
        return data.removeIf(testExecution -> testExecution.getId().equals(id));
    }

    public SearchResult<TestExecutionDto> search(TestExecutionSearchCriteria searchParams) {
        testExecutionSearchCriteria = searchParams;
        Comparator<TestExecutionDto> sortComparator;

        switch (searchParams.getOrderBy()) {
            case NAME_ASC:
                sortComparator = (testExecution1, testExecution2) -> testExecution1.getTest().getName().compareToIgnoreCase(testExecution2.getTest().getName());
                break;
            case NAME_DESC:
                sortComparator = (testExecution1, testExecution2) -> testExecution2.getTest().getName().compareToIgnoreCase(testExecution1.getTest().getName());
                break;
            case UID_ASC:
                sortComparator = (testExecution1, testExecution2) -> testExecution1.getTest().getUid().compareToIgnoreCase(testExecution2.getTest().getUid());
                break;
            case UID_DESC:
                sortComparator = (testExecution1, testExecution2) -> testExecution2.getTest().getUid().compareToIgnoreCase(testExecution1.getTest().getUid());
                break;
            case DATE_ASC:
                sortComparator = (testExecution1, testExecution2) -> testExecution1.getStarted().compareTo(testExecution2.getStarted());
                break;
            case DATE_DESC:
                sortComparator = (testExecution1, testExecution2) -> testExecution2.getStarted().compareTo(testExecution1.getStarted());
                break;
            default:
                sortComparator = (testExecution1, testExecution2) -> testExecution1.getTest().getName().compareToIgnoreCase(testExecution2.getTest().getName());
        }

        Predicate<TestExecutionDto> testNameFilterPredicate =
                testExecution -> searchParams.getTestNamesFilter() == null
                        || searchParams.getTestNamesFilter()
                        .stream().allMatch(testNameFilter -> StringUtils.containsIgnoreCase(testExecution.getTest().getName(), testNameFilter));

        Predicate<TestExecutionDto> testUidFilterPredicate =
                testExecution -> searchParams.getTestUniqueIdsFilter() == null
                        || searchParams.getTestUniqueIdsFilter()
                        .stream().allMatch(testUidFilter -> StringUtils.containsIgnoreCase(testExecution.getTest().getUid(), testUidFilter));

        Predicate<TestExecutionDto> tagFilterPredicate =
                testExecution -> searchParams.getTagQueriesFilter() == null
                        || searchParams.getTagQueriesFilter()
                        .stream().allMatch(tagFilter -> testExecution.getTags().contains(tagFilter));

        Supplier<Stream<TestExecutionDto>> testExecutionStream = () ->  data.stream()
                .filter(testNameFilterPredicate)
                .filter(testUidFilterPredicate)
                .filter(tagFilterPredicate)
                .sorted(sortComparator);

        int total = (int) testExecutionStream.get().count();
        List<TestExecutionDto> testExecutions = testExecutionStream.get()
                .skip(searchParams.getOffset())
                .limit(searchParams.getLimit())
                .collect(Collectors.toList());

        return new SearchResult<>(testExecutions, total, searchParams.getLimit(), searchParams.getOffset());

    }

    public TestExecutionDto update(TestExecutionDto dto) {
        TestExecutionDto testExecution = getById(dto.getId());

        if (testExecution != null) {
            testExecution.setName(dto.getName());
            testExecution.setStarted(dto.getStarted());
            testExecution.setComment(dto.getComment());
            testExecution.setTags(dto.getTags());
            return testExecution;
        } else {
            return null;
        }
    }

    private Long getNextId() {
        return key++;
    }
}

