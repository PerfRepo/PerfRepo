(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.parameter')
        .controller('RemoveTestExecutionParameterController', RemoveTestExecutionParameterController);

    function RemoveTestExecutionParameterController(_parameter, _parameters, _testExecutionId, testExecutionService, $uibModalInstance) {
        var vm = this;
        vm.parameter = _parameter;
        vm.testExecutionId = _testExecutionId;
        vm.parameters = _parameters;
        vm.remove = remove;
        vm.cancel = cancel;

        function remove() {
            var params = angular.copy(vm.parameters);

            params = params.filter(function(param) {
                return param.name !== vm.parameter.name;
            });

            testExecutionService.updateParameters(vm.testExecutionId, params).then(function () {
                $uibModalInstance.close(vm.parameter);
            });
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();