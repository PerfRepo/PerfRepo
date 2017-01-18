(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.search')
        .component('alertListView', {
            bindings: {
                items: '<',
                testMetrics: '<',
                onUpdateList: '&'
            },
            controller: AlertListViewController,
            controllerAs: 'vm',
            templateUrl: 'app/test/components/alert_list_view/alert-list-view.view.html'
        });

    function AlertListViewController($templateCache, alertModalService) {
        var vm = this;
        $templateCache.put('alert-edit-button-template', '<span class="pficon pficon-edit"></span> {{actionButton.name}}');
        $templateCache.put('alert-delete-button-template', '<span class="pficon-delete"></span> {{actionButton.name}}');

        vm.actionButtons = getActionButtons();
        vm.config = getListConfig();

        function getListConfig() {
            return {
                showSelectBox: false,
                selectItems: true,
                selectionMatchProp: 'id'
            };
        }

        function getActionButtons() {
            return [
                {
                    name: 'Edit',
                    include: 'alert-edit-button-template',
                    title: 'Edit alert',
                    actionFn: editAlertAction
                },
                {
                    name: 'Delete',
                    include: 'alert-delete-button-template',
                    title: 'Delete alert',
                    actionFn: deleteAlertAction
                }
            ];
        }

        function editAlertAction(action, item) {
            var modalInstance = alertModalService.editAlert(vm.testMetrics, item.id);

            modalInstance.result.then(function () {
                vm.onUpdateList();
            });
        }

        function deleteAlertAction(action, item) {
            var modalInstance = alertModalService.removeAlert(item);

            modalInstance.result.then(function () {
                vm.onUpdateList();
            });
        }
    }
})();