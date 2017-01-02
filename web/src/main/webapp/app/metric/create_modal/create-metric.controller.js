(function() {
    'use strict';

    angular
        .module('org.perfrepo.metric.create')
        .controller('CreateMetricController', CreateMetricController);

    function CreateMetricController(_testId, _comparators, metricService, validationHelper, $modalInstance) {
        var vm = this;
        vm.comparators = _comparators;
        vm.metric = {};
        vm.testId = _testId;
        vm.save = save;
        vm.cancel = cancel;

        function save(metric, form) {
            metricService.create(metric, vm.testId).then(function () {
                $modalInstance.close(metric);
            }, function(errorResponse) {
                console.log(form);
                validationHelper.setFormErrors(errorResponse, form);
            });
        }

        function cancel() {
            $modalInstance.dismiss('cancel');
        }
    }
})();