(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.detail')
        .component('metricsTableView', {
            bindings: {
                testId: '<',
                metrics: '<',
                onUpdate: '&'
            },
            controller: MetricsTableViewController,
            controllerAs: 'vm',
            templateUrl: 'app/test/components/metrics_table_view/metrics-table-view.view.html'
        });

    function MetricsTableViewController(metricModalService) {
        var vm = this;
        vm.addMetricAction = addMetricAction;
        vm.editMetricAction = editMetricAction;
        vm.removeMetricAction = removeMetricAction;

        function addMetricAction() {
            var modalInstance = metricModalService.createMetric(vm.testId);

            modalInstance.result.then(function () {
                vm.onUpdate();
            });
        }

        function editMetricAction(metric) {
            var modalInstance = metricModalService.editMetric(metric.id);

            modalInstance.result.then(function () {
                vm.onUpdate();
            });
        }

        function removeMetricAction(metric) {
            var modalInstance = metricModalService.removeMetric(metric, vm.testId);

            modalInstance.result.then(function () {
                vm.onUpdate();
            });
        }
    }
})();