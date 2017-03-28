(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.massOperation')
        .controller('ParameterMassOperationController', ParameterMassOperationController);

    function ParameterMassOperationController(_testExecutionIds, _mode, testExecutionMassOperationService,
                                            validationHelper, $uibModalInstance) {
        var vm = this;
        vm.testExecutionIds = _testExecutionIds;
        vm.mode = _mode;
        vm.save = save;
        vm.cancel = cancel;

        function save(form) {
            if (vm.mode == 'add') {
                testExecutionMassOperationService.addParameter(vm.parameter, vm.testExecutionIds).then(function () {
                    $uibModalInstance.close();
                }, function (errorResponse) {
                    validationHelper.setFormErrors(errorResponse, form);
                });
            }

            if (vm.mode == 'remove') {
                testExecutionMassOperationService.removeParameter(vm.parameter, vm.testExecutionIds).then(function () {
                    $uibModalInstance.close();
                }, function (errorResponse) {
                    validationHelper.setFormErrors(errorResponse, form);
                });
            }
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();