(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.massOperation')
        .controller('DeleteExecutionMassOperationController', DeleteExecutionMassOperationController);

    function DeleteExecutionMassOperationController(_testExecutionIds, testExecutionMassOperationService,
                                                    $uibModalInstance) {
        var vm = this;
        vm.testExecutionIds = _testExecutionIds;
        vm.save = save;
        vm.cancel = cancel;

        function save() {
            testExecutionMassOperationService.removeTestExecutions(vm.testExecutionIds).then(function () {
                $uibModalInstance.close();
            });
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();