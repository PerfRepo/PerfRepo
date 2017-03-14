(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.parameter')
        .controller('CreateTestExecutionParameterController', CreateTestExecutionParameterController);

    function CreateTestExecutionParameterController(_parameters, _testExecutionId, testExecutionService,
                                                    validationHelper, $uibModalInstance) {
        var vm = this;
        vm.parameter = {};
        vm.parameters = _parameters;
        vm.testExecutionId = _testExecutionId;
        vm.save = save;
        vm.cancel = cancel;

        function save(parameter, form) {
            var params = angular.copy(vm.parameters);
            params.push(parameter);

            testExecutionService.updateParameters(vm.testExecutionId, params).then(function () {
                $uibModalInstance.close(parameter);
            }, function(errorResponse) {
                validationHelper.setFormErrors(errorResponse, form);
            });
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();