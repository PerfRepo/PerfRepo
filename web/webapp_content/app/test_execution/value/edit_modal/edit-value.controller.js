(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.value')
        .controller('EditTestExecutionValueController', EditTestExecutionValueController);

    function EditTestExecutionValueController(_valuesGroup, _testExecutionId, _index,
                                                  testExecutionService, validationHelper, $uibModalInstance) {
        var vm = this;
        vm.valuesGroups = [_valuesGroup];
        vm.metricsName = [_valuesGroup.metricName];
        vm.testExecutionId = _testExecutionId;
        vm.selectedMetricName = _valuesGroup.metricName;
        vm.valueObject = angular.copy(_valuesGroup.values[_index]);
        vm.index = _index;
        vm.save = save;
        vm.cancel = cancel;

        function save(valueObject, form) {
            var valuesObjects =  angular.copy(vm.valuesGroups[0].values);
            valuesObjects[vm.index] = valueObject;
            testExecutionService.setExecutionValues(vm.testExecutionId, {
                metricName: vm.selectedMetricName,
                values: valuesObjects
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