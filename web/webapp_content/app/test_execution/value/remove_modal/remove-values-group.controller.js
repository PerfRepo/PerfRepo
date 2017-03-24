(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.value')
        .controller('RemoveTestExecutionValuesGroupController', RemoveTestExecutionValuesGroupController);

    function RemoveTestExecutionValuesGroupController(_executionValuesGroup, _testExecutionId,
                                                testExecutionService, validationHelper, $uibModalInstance) {
        var vm = this;
        vm.executionValuesGroup = _executionValuesGroup;
        vm.testExecutionId = _testExecutionId;
        vm.remove = remove;
        vm.cancel = cancel;

        function remove() {
            testExecutionService.setExecutionValues(vm.testExecutionId, {
                metricName: vm.executionValuesGroup,
                values: []
            }).then(function () {
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