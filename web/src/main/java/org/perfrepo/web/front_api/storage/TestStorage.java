package org.perfrepo.web.front_api.storage;

import org.apache.commons.lang.StringUtils;
import org.perfrepo.model.to.TestSearchTO;
import org.perfrepo.web.dto.TestDto;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestStorage {

    private Long key = 1l;
    private List<TestDto> data = new ArrayList<>();

    public TestDto getById(Long id) {
        return data.stream().filter(dto -> dto.getId().equals(id)).findFirst().get();
    }

    public TestDto getByUid(String uid) {
        return data.stream().filter(dto -> dto.getUid().equals(uid)).findFirst().get();
    }

    public TestDto create(TestDto dto) {
        dto.setId(getNextId());
        data.add(dto);
        return dto;
    }

    public TestDto update(TestDto dto) {
        boolean removed = data.removeIf(test -> test.getId().equals(dto.getId()));

        if(removed){
            data.add(dto);
        }else {
            return null;
        }

        return dto;
    }

    public boolean delete(Long id) {
        return data.removeIf(test -> test.getId().equals(id));
    }

    public List<TestDto> getAll(){
        return data;
    }

    public TestSearchResult search(TestSearchTO searchParams){
        Supplier<Stream<TestDto>> testStream = () ->  data.stream()
                .filter(test -> (searchParams.getUid() == null || StringUtils.containsIgnoreCase(test.getUid(), searchParams.getUid())))
                .filter(test -> (searchParams.getName() == null || StringUtils.containsIgnoreCase(test.getName(), searchParams.getName())))
                .filter(test -> (searchParams.getGroupId() == null || test.getGroupId().toString().equals(searchParams.getGroupId())));

        int total = (int) testStream.get().count();
        List<TestDto> tests = testStream.get()
                //TODO add group filter
                .skip(searchParams.getLimitFrom())
                .limit(searchParams.getLimitHowMany())
                //TODO sorting
                .collect(Collectors.toList());

        TestSearchResult result = new TestSearchResult(tests, total, searchParams.getLimitHowMany(), searchParams.getLimitFrom());

        return result;
    }

    private Long getNextId() {
        return key++;
    }

    public class TestSearchResult {
        private List<TestDto> tests;
        private Integer totalCount;
        private Integer currentPage;
        private Integer pageCount;
        private Integer perPage;

        public TestSearchResult(List<TestDto> tests, Integer totalCount, Integer limit, Integer from) {
            this.tests = tests;
            this.totalCount = totalCount;
            this.perPage = limit;
            this.currentPage = (from / limit) + 1;
            this.pageCount = (int) Math.ceil(totalCount / (double)limit);

        }

        public List<TestDto> getTests() {
            return tests;
        }

        public Integer getTotalCount() {
            return totalCount;
        }

        public Integer getCurrentPage() {
            return currentPage;
        }

        public Integer getPageCount() {
            return pageCount;
        }

        public Integer getPerPage() {
            return perPage;
        }
    }

}

