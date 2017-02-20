(function() {
    'use strict';

    angular
        .module('org.perfrepo.metric.remove')
        .controller('RemoveMetricController', RemoveMetricController);

    function RemoveMetricController(_testId, _metric, metricService, $uibModalInstance) {
        var vm = this;
        vm.metric = _metric;
        vm.testId = _testId;
        vm.remove = remove;
        vm.cancel = cancel;

        function remove() {
            metricService.remove(vm.metric.id, vm.testId).then(function () {
                $uibModalInstance.close(vm.metric);
            });
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();