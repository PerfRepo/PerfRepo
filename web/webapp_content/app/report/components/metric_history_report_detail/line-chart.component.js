(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.detail')
        .component('metricHistoryLineChart', {
            bindings: {
                chartData: '<'
            },
            controller: MetricHistoryLineChartController,
            controllerAs: 'vm',
            templateUrl: 'app/report/components/metric_history_report_detail/line-chart.view.html'
        });

    function MetricHistoryLineChartController() {
        var vm = this;

        vm.data = [];
        // series
        angular.forEach(vm.chartData.series, function(series) {
            vm.data.push({key: series.name, values: series.values});
        });
        // baselines
        angular.forEach(vm.chartData.baselines, function(baseline) {
            var data = [
                    {
                        x: baseline.value.x1,
                        y: baseline.value.y,
                        executionId: baseline.value.executionId,
                        executionName: baseline.value.executionName
                    },
                    {
                        x: baseline.value.x2,
                        y: baseline.value.y,
                        executionId: baseline.value.executionId,
                        executionName: baseline.value.executionName
                    }
                ];

            vm.data.push({key: baseline.name, values: data});
        });

        vm.options = {
            chart: {
                type: 'lineChart',
                height: 450,
                margin : {
                    top: 40,
                    right: 20,
                    bottom: 40,
                    left: 55
                },
                x: function(d){ return d.x; },
                y: function(d){ return d.y; },
                valueFormat: function(d){
                    return d3.format(',.4f')(d);
                },
                useInteractiveGuideline: true,
                xAxis: {
                    axisLabel: "Test execution"
                },
                yAxis: {
                    axisLabel: "Metric value",
                    tickFormat: function(d){
                        return d3.format('.02f')(d);
                    },
                    axisLabelDistance: -10
                }
            }
        };
    }
})();

