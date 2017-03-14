(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.detail')
        .component('tableComparisonReportTable', {
            bindings: {
                table: '<'
            },
            controller: TableComparisonReportTableController,
            controllerAs: 'vm',
            templateUrl: 'app/report/components/table_comparison_report_detail/table.view.html'
        });

    function TableComparisonReportTableController(reportModalService) {
        var vm = this;
        vm.showChartComparison = showChartComparison;

        function showChartComparison(contentCells, selectedExecutionIndex, metricName) {
            reportModalService.showChartForMultiValueTableComparison(contentCells, vm.table.tableHeaderCells,
                selectedExecutionIndex, metricName);
        }
    }
})();