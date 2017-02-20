(function() {
    'use strict';

    angular
        .module('org.perfrepo.metric.create')
        .controller('CreateMetricController', CreateMetricController);

    function CreateMetricController(_testId, _metrics, _comparators, metricService, validationHelper, $uibModalInstance) {
        var vm = this;
        vm.comparators = _comparators;
        vm.metric = {};
        vm.metrics = _metrics;
        vm.testId = _testId;
        vm.save = save;
        vm.cancel = cancel;

        function save(metric, form) {
            metricService.create(metric, vm.testId).then(function () {
                $uibModalInstance.close(metric);
            }, function(errorResponse) {
                validationHelper.setFormErrors(errorResponse, form);
            });
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();