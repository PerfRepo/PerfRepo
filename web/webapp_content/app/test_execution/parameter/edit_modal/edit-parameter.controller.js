(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.parameter')
        .controller('EditTestExecutionParameterController', EditTestExecutionParameterController);

    function EditTestExecutionParameterController(_parameters, _testExecutionId, _parameter,
                                                  testExecutionService, validationHelper, $uibModalInstance) {
        var vm = this;
        vm.parameters = _parameters;
        vm.testExecutionId = _testExecutionId;
        vm.parameter = angular.copy(_parameter);
        vm.save = save;
        vm.cancel = cancel;

        function save(parameter, form) {
            var params = angular.copy(vm.parameters);

            params = params.filter(function(param) {
                return param.name !==  _parameter.name && param.name !== parameter.name;
            });

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