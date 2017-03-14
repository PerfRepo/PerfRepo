(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.detail')
        .component('tableComparisonReportDetail', {
            bindings: {
                groups: '<'
            },
            controller: TableComparisonReportController,
            controllerAs: 'vm',
            templateUrl: 'app/report/components/table_comparison_report_detail/detail.view.html'
        });

    function TableComparisonReportController() {
        var vm = this;
    }
})();