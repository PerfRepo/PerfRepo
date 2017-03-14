(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.detail')
        .controller('ChartComparisonController', ChartComparisonController);

    function ChartComparisonController(_contentCells, _headerCells, _selectedExecutionIndex, _metricName,
                                       $uibModalInstance) {
        var vm = this;
        vm.contentCells = _contentCells;
        vm.headerCells = _headerCells;
        vm.selectedExecutionIndex = _selectedExecutionIndex;
        vm.metricName = _metricName;
        vm.updateChart = updateChart;
        vm.cancel = cancel;
        initialize();

        $uibModalInstance.rendered.then(function () {
            // TODO very bad - but on-ready doesn't work me
            setTimeout(function() {
                window.dispatchEvent(new Event('resize'));
                vm.showChart = true;
            }, 100);
        });

        function updateChart() {
            setChartData();
            vm.options.chart.xAxis.axisLabel = vm.xAxisParameter;
            vm.api.update();
        }

        function initialize() {
            vm.title = 'Test executions comparison - ' + vm.metricName;
            vm.showAll = _selectedExecutionIndex == undefined;
            vm.parameters = Object.keys(vm.contentCells[0].values);
            vm.xAxisParameter = vm.parameters[0];
            setChartData();
            setChartOptions();
        }

        function setChartData() {
            vm.data = [];
            angular.forEach(vm.contentCells, function(contentCell, index) {
                vm.data.push({
                    key: vm.headerCells[index].name,
                    disabled: !vm.showAll && !vm.headerCells[index].baseline && index != vm.selectedExecutionIndex,
                    values: contentCell.values[vm.xAxisParameter]
                });
            });
        }

        function setChartOptions() {
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
                    x: function(d){
                        return d.x;
                    },
                    y: function(d){
                        return d.value;
                    },
                    valueFormat: function(d){
                        return d3.format(',.4f')(d);
                    },
                    useInteractiveGuideline: true,
                    xAxis: {
                        axisLabel: vm.xAxisParameter,
                        tickFormat: function(d){
                            return d3.format('.02f')(d);
                        }
                    },
                    yAxis: {
                        axisLabel: vm.metricName,
                        tickFormat: function(d){
                            return d3.format('.02f')(d);
                        },
                        axisLabelDistance: -10
                    },
                    callback: function(chart){
                        console.log("!!! lineChart callback !!!");
                    }
                }
            };
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();