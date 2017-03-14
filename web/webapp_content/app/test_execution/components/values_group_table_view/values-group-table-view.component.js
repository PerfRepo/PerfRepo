(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.value')
        .component('testExecutionValuesGroupTableView', {
            bindings: {
                executionValuesGroups: '<',
                testExecutionMetrics: '<',
                testExecutionId: '<',
                onUpdateTable: '&'
            },
            controller: TestExecutionValuesGroupTableViewController,
            controllerAs: 'vm',
            templateUrl: 'app/test_execution/components/values_group_table_view/values-group-table-view.view.html'
        });

    function TestExecutionValuesGroupTableViewController(testExecutionValueModalService, $filter) {
        var vm = this;

        vm.addValueObject = addValueObject;
        vm.deleteValuesGroup = deleteValuesGroup;
        vm.editValuesGroup = editValuesGroup;
        vm.showMultiValueData = showMultiValueData;
        vm.getMetricById = getMetricById;
        vm.getValueObjectParameter = getValueObjectParameter;

        function addValueObject() {
            var modalInstance = testExecutionValueModalService.createValue(vm.executionValuesGroups,
                vm.testExecutionMetrics, vm.testExecutionId, false);

            modalInstance.result.then(function () {
                vm.onUpdateTable();
            });
        }

        function showMultiValueData(valuesGroup) {
            testExecutionValueModalService.showMultiValueData(valuesGroup, getMetricById(valuesGroup.metricId),
                vm.testExecutionId,  vm.onUpdateTable);
        }

        function editValuesGroup(valuesGroup) {
            if (valuesGroup.valueType == 'SINGLE_VALUE') {
                // single value
                var modalInstance = testExecutionValueModalService.editValue(valuesGroup,
                    getMetricById(valuesGroup.metricId), vm.testExecutionId);

                modalInstance.result.then(function () {
                    vm.onUpdateTable();
                });
            }
        }

        function deleteValuesGroup(valuesGroup) {
            var modalInstance =  testExecutionValueModalService.removeValuesGroup(valuesGroup, vm.testExecutionId);

            modalInstance.result.then(function () {
                vm.onUpdateTable();
            });
        }

        function getValueObjectParameter(parameterName, parameters) {
            return $filter('getByProperty')('name', parameterName, parameters);
        }

        function getMetricById(metricId) {
            return $filter('getByProperty')('id', metricId, vm.testExecutionMetrics);
        }
    }
})();