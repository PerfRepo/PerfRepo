(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.detail')
        .component('boxPlotReportDetail', {
            bindings: {
            },
            controller: BoxPlotReportController,
            controllerAs: 'vm',
            templateUrl: 'app/report/components/box_plot_report_detail/detail.view.html'
        });

    function BoxPlotReportController() {
        var vm = this;
    }
})();