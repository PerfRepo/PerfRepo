(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .component('prBoxPlotReportConfiguration', {
            bindings: {
                data: '=',
                currentStep: '='
            },
            controller: BoxPlotReportConfiguration,
            controllerAs: 'vm',
            templateUrl: 'app/report/wizard/components/box_plot_report/box-plot-report.view.html'
        });

    function BoxPlotReportConfiguration(wizardService, testService, testExecutionService, validationHelper, $scope) {
        var vm = this;
        vm.addBoxPlot = addBoxPlot;
        vm.removeBoxPlot = removeBoxPlot;
        vm.refreshTestsList = refreshTestsList;
        vm.seriesTestSelected = seriesTestSelected;
        vm.validate = validate;

        initialize();

        function initialize() {
            vm.executionFilterOptions = wizardService.getExecutionFilterOptions();
            vm.labelTypeOptions = wizardService.getBoxPlotLabelTypes();
            vm.sortTypeOptions = wizardService.getBoxPlotSortTypes();

            $scope.$on('next-step', function(event, step) {
                if (step.stepId === 'configuration') {
                    validate();
                }
            });

            vm.testMetrics = {};
            angular.forEach(vm.data.boxPlots, function(boxPlot) {
                testService.getById(boxPlot.series.testId).then(function(test) {
                    vm.testMetrics[test.id] = test.metrics;
                });
            });
        }

        function validate() {
            wizardService.validateReportConfigurationStep(vm.data).then(function() {
                vm.currentStep = 'Permissions';
            }, function(errorResponse) {
                vm.formErrors = validationHelper.prepareGlobalFormErrors(errorResponse);
                validationHelper.setFormErrors(errorResponse, vm.wizardBoxPlotStep);
                return false;
            });
        }

        function addBoxPlot() {
            if (vm.data.boxPlots === undefined) {
                vm.data.boxPlots = [];
            }
            // add new pane
            vm.data.boxPlots.push({
                name: 'New boxplot',
                labelType: 'DATE',
                sortType: 'DATE',
                series: {
                    filter: 'TAG_QUERY'
                }
            });
        }

        function removeBoxPlot(boxPlotIndex) {
            vm.data.boxPlots.splice(boxPlotIndex, 1);
        }

        function refreshTestsList(search) {
            testService.asyncSelectSearch(search).then(function(result) {
                vm.testsList = result.data;
            });
        }

        function seriesTestSelected(test, boxPlotIndex) {
            vm.data.boxPlots[boxPlotIndex].series.metricName = undefined;
            vm.testMetrics[test.id] = test.metrics;
        }
    }
})();