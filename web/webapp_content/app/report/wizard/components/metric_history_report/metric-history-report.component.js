(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .component('prMetricHistoryReportConfiguration', {
            bindings: {
                data: '=',
                currentStep: '='
            },
            controller: MetricHistoryReportConfiguration,
            controllerAs: 'vm',
            templateUrl: 'app/report/wizard/components/metric_history_report/metric-history-report.view.html'
        });

    function MetricHistoryReportConfiguration(wizardService, testService, testExecutionService, validationHelper, $scope) {
        var vm = this;
        vm.addChart = addChart;
        vm.removeChart = removeChart;
        vm.addSeries = addSeries;
        vm.removeSeries = removeSeries;
        vm.addBaseline = addBaseline;
        vm.removeBaseline = removeBaseline;
        vm.refreshTestsList = refreshTestsList;
        vm.seriesTestSelected = seriesTestSelected;
        vm.baselineTestExecutionChanged = baselineTestExecutionChanged;
        vm.validate = validate;
        initialize();

        function initialize() {
            vm.executionFilterOptions = wizardService.getExecutionFilterOptions();

            $scope.$on('next-step', function(event, step) {
                if (step.stepId === 'configuration') {
                    validate();
                }
            });

            vm.testExecutionMetrics = {};
            vm.testMetrics = {};
            angular.forEach(vm.data.charts, function(chart) {
                angular.forEach(chart.series, function(series) {
                    testService.getById(series.testId).then(function(test) {
                        vm.testMetrics[test.id] = test.metrics;
                    });
                });
                angular.forEach(chart.baselines, function(baseline) {
                    testExecutionService.getById(baseline.executionId).then(function(testExecution) {
                        vm.testExecutionMetrics[testExecution.id] = testExecution.test.metrics;
                    });
                });

            });
        }

        function validate() {
            wizardService.validateReportConfigurationStep(vm.data).then(function() {
                vm.currentStep = 'Permissions';
            }, function(errorResponse) {
                vm.formErrors = validationHelper.prepareGlobalFormErrors(errorResponse);
                validationHelper.setFormErrors(errorResponse, vm.wizardMetricHistoryStep);
                return false;
            });
        }

        function addChart() {
            if (vm.data.charts == undefined) {
                vm.data.charts = [];
            }
            vm.data.charts.push({name: 'New chart', series: [], baselines: []}); // add new pane
        }

        function addSeries(chartIndex) {
            if (vm.data.charts[chartIndex].series == undefined) {
                vm.data.charts[chartIndex].series = [];
            }
            vm.data.charts[chartIndex].series.push({filter: 'TAG_QUERY'}); // add new pane
        }

        function addBaseline(chartIndex) {
            if (vm.data.charts[chartIndex].baselines == undefined) {
                vm.data.charts[chartIndex].baselines = [];š
            }
            vm.data.charts[chartIndex].baselines.push({});
        }

        function removeChart(chartIndex) {
            vm.data.charts.splice(chartIndex, 1);
        }

        function removeSeries(chartIndex, seriesIndex) {
            vm.data.charts[chartIndex].series.splice(seriesIndex, 1);
        }

        function removeBaseline(chartIndex, baselineIndex) {
            vm.data.charts[chartIndex].baselines.splice(baselineIndex, 1);
        }

        function refreshTestsList(search) {
            testService.asyncSelectSearch(search).then(function(result) {
                vm.testsList = result.data;
            });
        }

        function seriesTestSelected(test, chartIndex, seriesIndex) {
            vm.data.charts[chartIndex].series[seriesIndex].metricId = undefined;
            vm.testMetrics[test.id] = test.metrics;
        }

        function baselineTestExecutionChanged(testExecutionId, chartIndex, baselineIndex) {
            vm.data.charts[chartIndex].baselines[baselineIndex].metricId = undefined;
            testExecutionService.getById(testExecutionId).then(function(testExecution) {
                vm.testExecutionMetrics[testExecution.id] = testExecution.test.metrics;
            });
        }
    }
})();