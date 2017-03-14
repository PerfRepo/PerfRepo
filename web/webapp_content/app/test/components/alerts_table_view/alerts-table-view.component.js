(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.detail')
        .component('alertsTableView', {
            bindings: {
                alerts: '<',
                testMetrics: '<',
                testId: '<',
                onUpdate: '&'
            },
            controller: AlertsTableViewController,
            controllerAs: 'vm',
            templateUrl: 'app/test/components/alerts_table_view/alerts-table-view.view.html'
        });

    function AlertsTableViewController(alertModalService) {
        var vm = this;
        vm.addAlertAction = addAlertAction;
        vm.editAlertAction = editAlertAction;
        vm.removeAlertAction = removeAlertAction;

        function addAlertAction() {
            var modalInstance = alertModalService.createAlert(vm.testMetrics, vm.testId);

            modalInstance.result.then(function () {
                vm.onUpdate();
            });
        }

        function editAlertAction(alert) {
            var modalInstance = alertModalService.editAlert(vm.testMetrics, alert.id);

            modalInstance.result.then(function () {
                vm.onUpdate();
            });
        }

        function removeAlertAction(alert) {
            var modalInstance = alertModalService.removeAlert(alert);

            modalInstance.result.then(function () {
                vm.onUpdate();
            });
        }
    }
})();