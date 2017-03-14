(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.parameter')
        .component('testExecutionParametersTableView', {
            bindings: {
                parameters: '<',
                testExecutionId: '<',
                onUpdate: '&'
            },
            controller: TestExecutionParametersTableViewController,
            controllerAs: 'vm',
            templateUrl: 'app/test_execution/components/parameters_table_view/parameters-table-view.view.html'
        });

    function TestExecutionParametersTableViewController(testExecutionParameterModalService) {
        var vm = this;
        vm.addParameterAction = addParameterAction;
        vm.editParameterAction = editParameterAction;
        vm.removeParameterAction = removeParameterAction;

        function addParameterAction() {
            var modalInstance = testExecutionParameterModalService.createParameter(vm.parameters, vm.testExecutionId);
            modalInstance.result.then(function () {
                vm.onUpdate();
            });
        }

        function editParameterAction(parameter) {
            var modalInstance = testExecutionParameterModalService.editParameter(parameter, vm.parameters, vm.testExecutionId);
            modalInstance.result.then(function () {
                vm.onUpdate();
            });
        }

        function removeParameterAction(parameter) {
            var modalInstance = testExecutionParameterModalService.removeParameter(parameter, vm.parameters, vm.testExecutionId);
            modalInstance.result.then(function () {
                vm.onUpdate();
            });
        }
    }
})();