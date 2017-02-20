(function() {
    'use strict';

    angular
        .module('org.perfrepo.alert')
        .service('alertModalService', AlertModalService);

    function AlertModalService($uibModal) {

        return {
            createAlert: createAlert,
            editAlert: editAlert,
            removeAlert: removeAlert
        };

        function removeAlert(alert) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/alert/remove_modal/remove-alert.view.html',
                controller: 'RemoveAlertController',
                controllerAs: 'vm',
                size: 'sm',
                resolve : {
                    _alert: function() {
                        return alert;
                    }
                }
            });
        }

        function createAlert(testMetrics, testId) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/alert/create_modal/create-alert.view.html',
                controller: 'CreateAlertController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _metrics: function() {
                        return testMetrics;
                    },
                    _testId: function () {
                        return testId;
                    }
                }
            });
        }

        function editAlert(testMetrics, alertId) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/alert/edit_modal/edit-alert.view.html',
                controller: 'EditAlertController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _metrics: function() {
                        return testMetrics;
                    },
                    _alert: function (alertService) {
                        return alertService.getById(alertId);
                    }
                }
            });
        }


    }
})();