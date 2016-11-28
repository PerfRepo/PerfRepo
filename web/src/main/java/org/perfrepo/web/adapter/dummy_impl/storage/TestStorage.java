package org.perfrepo.web.adapter.dummy_impl.storage;

import org.apache.commons.lang.StringUtils;
import org.perfrepo.dto.test.TestSearchParams;
import org.perfrepo.dto.test.TestDto;
import org.perfrepo.dto.SearchResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Temporary in-memory test storage for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
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

    public SearchResult search(TestSearchParams searchParams){

        Comparator<TestDto> sortComparator;

        switch(searchParams.getOrderBy()) {
            case "NAME_ASC":
                sortComparator = (test1, test2) -> test1.getName().compareToIgnoreCase(test2.getName());
                break;
            case "NAME_DESC":
                sortComparator = (test1, test2) -> test2.getName().compareToIgnoreCase(test1.getName());
                break;
            case "UID_ASC":
                sortComparator = (test1, test2) -> test1.getUid().compareToIgnoreCase(test2.getUid());
                break;
            case "UID_DESC":
                sortComparator = (test1, test2) -> test2.getUid().compareToIgnoreCase(test1.getUid());
                break;
            default:
                sortComparator = (test1, test2) -> test1.getName().compareToIgnoreCase(test2.getName());
        }

        Predicate<TestDto> nameFilterPredicate =
                test -> searchParams.getNameFilters() == null ||
                        searchParams.getNameFilters()
                                .stream().allMatch(nameFilter -> StringUtils.containsIgnoreCase(test.getName(), nameFilter));



        Predicate<TestDto> uidFilterPredicate =
                test -> searchParams.getUidFilters() == null ||
                        searchParams.getUidFilters()
                                .stream().allMatch(uidFilter -> StringUtils.containsIgnoreCase(test.getUid(), uidFilter));

        Supplier<Stream<TestDto>> testStream = () ->  data.stream()
                .filter(nameFilterPredicate)
                .filter(uidFilterPredicate)
                .sorted(sortComparator);


        int total = (int) testStream.get().count();
        List<TestDto> tests = testStream.get()
                .skip(searchParams.getOffset())
                .limit(searchParams.getLimit())
                .collect(Collectors.toList());

        SearchResult result = new SearchResult(tests, total, searchParams.getLimit(), searchParams.getOffset());

        return result;
    }

    private Long getNextId() {
        return key++;
    }
}

