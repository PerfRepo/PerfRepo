(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.value')
        .controller('EditTestExecutionValueController', EditTestExecutionValueController);

    function EditTestExecutionValueController(_valuesGroup, _metric, _testExecutionId, _index,
                                                  testExecutionService, validationHelper, $uibModalInstance) {
        var vm = this;
        vm.valuesGroups = [_valuesGroup];
        vm.metrics = [_metric];
        vm.testExecutionId = _testExecutionId;
        vm.selectedMetricId = _metric.id;
        vm.valueObject = angular.copy(_valuesGroup.values[_index]);
        vm.index = _index;
        vm.save = save;
        vm.cancel = cancel;

        function save(valueObject, form) {
            var valuesObjects =  angular.copy(vm.valuesGroups[0].values);
            valuesObjects[vm.index] = valueObject;
            testExecutionService.setExecutionValues(vm.testExecutionId, vm.metrics[0].id, valuesObjects).then(function () {
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