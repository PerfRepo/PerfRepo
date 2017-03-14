package org.perfrepo.web.adapter.dummy_impl.storage;

import org.apache.commons.lang.StringUtils;
import org.perfrepo.dto.report.box_plot.BoxPlotReportDto;
import org.perfrepo.dto.report.metric_history.MetricHistoryReportDto;
import org.perfrepo.dto.report.ReportDto;
import org.perfrepo.dto.report.ReportSearchCriteria;
import org.perfrepo.dto.report.table_comparison.TableComparisonReportDto;
import org.perfrepo.dto.util.SearchResult;
import org.perfrepo.enums.report.ReportType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Temporary in-memory report storage for development purpose.
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
public class ReportStorage {

    private Long key = 1L;
    private List<ReportDto> data = new ArrayList<>();

    public ReportDto getById(Long id) {
        Optional<ReportDto> report = data.stream().filter(dto -> dto.getId().equals(id)).findFirst();
        return report.isPresent() ? report.get() : null;
    }

    public ReportDto create(ReportDto dto) {
        dto.setId(getNextId());
        if (dto instanceof TableComparisonReportDto) {
            dto.setTypeName("Table comparison report");
        } else if (dto instanceof MetricHistoryReportDto) {
            dto.setTypeName("Metric history report");
        }
        data.add(dto);
        return dto;
    }

    public ReportDto update(ReportDto dto) {
        ReportDto report = getById(dto.getId());

        if (report != null) {
            report.setName(dto.getName());
            report.setDescription(dto.getDescription());
            report.setPermissions(dto.getPermissions());
            if (report instanceof TableComparisonReportDto) {
                ((TableComparisonReportDto) report).setGroups(((TableComparisonReportDto) dto).getGroups());
            }
            return report;
        } else {
            return null;
        }
    }

    public boolean delete(Long id) {
        return data.removeIf(report -> report.getId().equals(id));
    }

    public List<ReportDto> getAll() {
        return data;
    }

    public SearchResult<ReportDto> search(ReportSearchCriteria searchParams) {
        Comparator<ReportDto> sortComparator;

        switch (searchParams.getOrderBy()) {
            case NAME_ASC:
                sortComparator = (report1, report2) -> report1.getName().compareToIgnoreCase(report2.getName());
                break;
            case NAME_DESC:
                sortComparator = (report1, report2) -> report2.getName().compareToIgnoreCase(report1.getName());
                break;
            case UID_ASC:
                sortComparator = (report1, report2) -> report1.getId().compareTo(report2.getId());
                break;
            case UID_DESC:
                sortComparator = (report1, report2) -> report2.getId().compareTo(report1.getId());
                break;
            default:
                sortComparator = (report1, report2) -> report1.getName().compareToIgnoreCase(report2.getName());
        }

        Predicate<ReportDto> nameFilterPredicate =
                report -> searchParams.getNamesFilter() == null
                        || searchParams.getNamesFilter()
                                .stream().allMatch(nameFilter -> StringUtils.containsIgnoreCase(report.getName(), nameFilter));


        Predicate<ReportDto> typeFilterPredicate =
                report -> searchParams.getTypesFilter() == null || searchParams.getTypesFilter().size() == 0
                        || (searchParams.getTypesFilter().size() == 1 && (reportTypeCheck(searchParams.getTypesFilter().iterator().next(), report)));

        Supplier<Stream<ReportDto>> reportStream = () ->  data.stream()
                .filter(nameFilterPredicate)
                .filter(typeFilterPredicate)
                .sorted(sortComparator);

        int total = (int) reportStream.get().count();
        List<ReportDto> reports = reportStream.get()
                .skip(searchParams.getOffset())
                .limit(searchParams.getLimit())
                .collect(Collectors.toList());

        SearchResult<ReportDto> result =
                new SearchResult<>(reports, total, searchParams.getLimit(), searchParams.getOffset());

        return result;
    }

    private boolean reportTypeCheck(ReportType reportType, ReportDto report) {
        if (reportType.equals(ReportType.METRIC_HISTORY)) {
            return report instanceof MetricHistoryReportDto;
        }

        if (reportType.equals(ReportType.BOX_PLOT)) {
           return report instanceof BoxPlotReportDto;
        }

        if (reportType.equals(ReportType.TABLE_COMPARISON)) {
            return report instanceof TableComparisonReportDto;
        }

        return false;
    }

    private Long getNextId() {
        return key++;
    }
}