(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.value')
        .controller('RemoveTestExecutionValueController', RemoveTestExecutionValueController);

    function RemoveTestExecutionValueController(_executionValuesGroup, _metric, _testExecutionId, _index,
                                                testExecutionService, validationHelper, $uibModalInstance) {
        var vm = this;
        vm.executionValuesGroup = _executionValuesGroup;
        vm.parameterNames = _executionValuesGroup.parameterNames;
        vm.index = _index;
        vm.valueObject = _executionValuesGroup.values[_index];
        vm.metric = _metric;
        vm.testExecutionId = _testExecutionId;
        vm.remove = remove;
        vm.cancel = cancel;

        function remove() {
            var valueObjects =  angular.copy(vm.executionValuesGroup.values);
            valueObjects.splice(vm.index, 1);
            testExecutionService.setExecutionValues(vm.testExecutionId, vm.metric.id, valueObjects).then(function () {
                $uibModalInstance.close();
            }, function(errorResponse) {
                validationHelper.setFormErrors(errorResponse, form);
            });
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();