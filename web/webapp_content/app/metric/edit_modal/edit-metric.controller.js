(function() {
    'use strict';

    angular
        .module('org.perfrepo.metric.edit')
        .controller('EditMetricController', EditMetricController);

    function EditMetricController(_metric, _metrics, _comparators, metricService, validationHelper, $uibModalInstance) {
        var vm = this;
        vm.comparators = _comparators;
        vm.metric = _metric;
        vm.metrics = _metrics;
        vm.save = save;
        vm.cancel = cancel;

        function save(metric, form) {
            metricService.update(metric).then(function () {
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