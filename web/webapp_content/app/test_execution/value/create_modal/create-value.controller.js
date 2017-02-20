(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.value')
        .controller('CreateTestExecutionValueController', CreateTestExecutionValueController);

    function CreateTestExecutionValueController(_valuesGroups, _metrics, _testExecutionId, _multiValue, testExecutionService,
                                                    validationHelper, $uibModalInstance) {
        var vm = this;
        vm.valuesGroups = _valuesGroups;
        vm.metrics = _metrics;
        vm.testExecutionId = _testExecutionId;
        vm.multiValue = _multiValue;
        vm.selectedMetricId = _metrics[0].id;
        vm.valueObject = {};
        vm.save = save;
        vm.cancel = cancel;

        function save(valueObject, metricId, form) {
            testExecutionService.addExecutionValues(vm.testExecutionId, metricId, [valueObject]).then(function () {
                $uibModalInstance.close(valueObject);
            }, function(errorResponse) {
                validationHelper.setFormErrors(errorResponse, form);
            });
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();