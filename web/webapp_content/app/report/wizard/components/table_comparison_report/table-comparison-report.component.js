(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .component('prTableComparisonReportConfiguration', {
            bindings: {
                data: '=',
                currentStep: '='
            },
            controller: TableComparisonReportConfiguration,
            controllerAs: 'vm',
            templateUrl: 'app/report/wizard/components/table_comparison_report/table-comparison-report.view.html'
        });

    function TableComparisonReportConfiguration(wizardService, testService, validationHelper, $scope) {
        var vm = this;
        vm.addGroup = addGroup;
        vm.removeGroup = removeGroup;
        vm.addComparison = addComparison;
        vm.removeComparison = removeComparison;
        vm.addItem = addItem;
        vm.removeItem = removeItem;
        vm.onBaselineChecked = onBaselineChecked;
        vm.hasBaselineChecked = hasBaselineChecked;
        vm.refreshTestsList = refreshTestsList;
        vm.itemSelectors = wizardService.getItemSelectors();
        vm.validate = validate;

        $scope.$on('next-step', function(event, step) {
            if (step.stepId === 'configuration') {
                validate();
            }
        });

        function validate() {
            wizardService.validateReportConfigurationStep(vm.data).then(function() {
                vm.currentStep = 'Permissions';
            }, function(errorResponse) {
                vm.formErrors = validationHelper.prepareGlobalFormErrors(errorResponse);
                validationHelper.setFormErrors(errorResponse, vm.wizardTableComparisonStep);
                return false;
            });
        }

        function addGroup() {
            if (vm.data.groups == undefined) {
                vm.data.groups = [];
            }
            vm.data.groups.push({name: 'New group', tables:[], threshold: 5}); // add new pane
        }

        function addComparison(groupIndex) {
            if (vm.data.groups[groupIndex].tables == undefined) {
                vm.data.groups[groupIndex].tables = [];
            }
            vm.data.groups[groupIndex].tables.push({name: 'New comparison'}); // add new pane
        }

        function addItem(groupIndex, comparisonIndex) {
            if (vm.data.groups[groupIndex].tables[comparisonIndex].items == undefined) {
                vm.data.groups[groupIndex].tables[comparisonIndex].items = [];
            }
            vm.data.groups[groupIndex].tables[comparisonIndex].items.push({selector: 'TEST_EXECUTION_ID'});
        }

        function removeGroup(groupIndex) {
            vm.data.groups.splice(groupIndex, 1);
        }

        function removeComparison(groupIndex, comparisonIndex) {
            vm.data.groups[groupIndex].tables.splice(comparisonIndex, 1);
        }

        function removeItem(groupIndex, comparisonIndex, itemIndex) {
            vm.data.groups[groupIndex].tables[comparisonIndex].items.splice(itemIndex, 1);
        }

        function onBaselineChecked(groupIndex, comparisonIndex, itemIndex) {
            angular.forEach(vm.data.groups[groupIndex].tables[comparisonIndex].items, function(item, index) {
                if (index != itemIndex) {
                    item.baseline = false;
                }
            });
        }

        function hasBaselineChecked(groupIndex, comparisonIndex) {
            var checked = false;
            angular.forEach(vm.data.groups[groupIndex].tables[comparisonIndex].items, function(item) {
                if (item.baseline) {
                    checked = true;
                }
            });
            return checked;
        }

        function refreshTestsList(search) {
            testService.asyncSelectSearch(search).then(function(result) {
                vm.testsList = result.data;
            });
        }
    }
})();