(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.search')
        .component('testExecutionListView', {
            bindings: {
                items: '=',
                onUpdateList: '&'
            },
            controller: TestExecutionListViewController,
            controllerAs: 'vm',
            templateUrl: 'app/test_execution/components/test_execution_list_view/test-execution-list-view.view.html'
        });

    function TestExecutionListViewController($state,  $templateCache, testExecutionModalService) {
        var vm = this;
        $templateCache.put('test-execution-detail-button-template', '<span class="fa fa-th-list"></span> {{actionButton.name}}');
        $templateCache.put('test-execution-edit-button-template', '<span class="pficon pficon-edit"></span> {{actionButton.name}}');
        $templateCache.put('test-execution-delete-button-template', '<span class="pficon-delete"></span> {{actionButton.name}}');

        vm.actionButtons = getActionButtons();
        vm.menuActions = getMenuActions();
        vm.config = getListConfig();

        function getListConfig() {
            return {
                showSelectBox: true,
                selectItems: false,
                selectionMatchProp: 'id'
            };
        }

        function getActionButtons() {
            return [
                {
                    name: 'Detail',
                    include: 'test-execution-detail-button-template',
                    title: 'Test execution detail',
                    class: 'btn btn-default',
                    actionFn: showTestExecutionDetailAction
                },
                {
                    name: 'Edit',
                    include: 'test-execution-edit-button-template',
                    title: 'Edit test execution',
                    actionFn: editTestExecutionAction
                },
                {
                    name: 'Delete',
                    include: 'test-execution-delete-button-template',
                    title: 'Delete test execution',
                    actionFn: deleteTestExecutionAction
                }
            ];
        }

        function getMenuActions() {
            return [
            ];
        }

        function showTestExecutionDetailAction(action, item) {
            $state.go('app.testExecutionDetail', {id: item.id});
        }

        function editTestExecutionAction(action, item) {
            var modalInstance = testExecutionModalService.editTestExecution(item.id);

            modalInstance.result.then(function () {
                vm.onUpdateList();
            });
        }

        function deleteTestExecutionAction(action, item) {
            var modalInstance = testExecutionModalService.removeTestExecution(item);

            modalInstance.result.then(function () {
                vm.onUpdateList();
            });
        }
    }
})();