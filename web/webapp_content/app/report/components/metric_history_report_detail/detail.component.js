(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.detail')
        .component('metricHistoryReportDetail', {
            bindings: {
                charts: '<'
            },
            controller: MetricHistoryReportController,
            controllerAs: 'vm',
            templateUrl: 'app/report/components/metric_history_report_detail/detail.view.html'
        });

    function MetricHistoryReportController() {
        var vm = this;
    }
})();