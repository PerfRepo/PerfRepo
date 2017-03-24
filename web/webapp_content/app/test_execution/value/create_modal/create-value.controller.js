(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.value')
        .controller('CreateTestExecutionValueController', CreateTestExecutionValueController);

    function CreateTestExecutionValueController(_valuesGroups, _metricsName, _testExecutionId, _multiValue, testExecutionService,
                                                    validationHelper, $uibModalInstance) {
        var vm = this;
        vm.valuesGroups = _valuesGroups;
        vm.metricsName = _metricsName;
        vm.testExecutionId = _testExecutionId;
        vm.multiValue = _multiValue;
        vm.selectedMetricName = _metricsName[0];
        vm.valueObject = {};
        vm.save = save;
        vm.cancel = cancel;

        function save(valueObject, form) {
            testExecutionService.addExecutionValues(vm.testExecutionId, {
                metricName: vm.selectedMetricName,
                values: [valueObject]
            }).then(function () {
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