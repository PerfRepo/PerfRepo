(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.detail')
        .component('boxPlotChart', {
            bindings: {
                boxPlotData: '<'
            },
            controller: BoxPlotChartController,
            controllerAs: 'vm',
            templateUrl: 'app/report/components/box_plot_report_detail/box-plot.view.html'
        });

    function BoxPlotChartController(reportModalService, CHART_COLORS) {
        var vm = this;
        vm.data = vm.boxPlotData.series.values;

        vm.options = {
            chart: {
                type: 'boxPlotChart',
                height: 450,
                margin: {
                    top: 10,
                    right: 20,
                    bottom: 60,
                    left: 40
                },
                boxplot: {
                    wl: function (d) {
                        return d.lowerExtreme;
                    },
                    q1: function (d) {
                        return d.lowerQuartile;
                    },
                    q2: function (d) {
                        return d.median;
                    },
                    q3: function (d) {
                        return d.upperQuartile;
                    },
                    wh: function (d) {
                        return d.upperExtreme;
                    },
                    outliers: function (d) {
                        return d.outliers;
                    },
                    color: CHART_COLORS,
                    x: function (d) {
                        return d.label;
                    },
                    maxBoxWidth: 75
                }
            },
            title: {
                enable: true,
                text: vm.boxPlotData.series.name
            }
        };
    }
})();