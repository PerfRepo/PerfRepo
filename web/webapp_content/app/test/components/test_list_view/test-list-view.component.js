(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.search')
        .component('testListView', {
            bindings: {
                items: '<',
                onUpdateList: '&'
            },
            controller: TestListViewController,
            controllerAs: 'vm',
            templateUrl: 'app/test/components/test_list_view/test-list-view.view.html'
        });

    function TestListViewController($state,  $templateCache, testModalService, testExecutionModalService) {
        var vm = this;
        $templateCache.put('test-detail-button-template', '<span class="fa fa-th-list"></span> {{actionButton.name}}');
        $templateCache.put('test-edit-button-template', '<span class="pficon pficon-edit"></span> {{actionButton.name}}');
        $templateCache.put('test-delete-button-template', '<span class="pficon-delete"></span> {{actionButton.name}}');

        vm.actionButtons = getActionButtons();
        vm.menuActions = getMenuActions();
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
                    name: 'Detail',
                    include: 'test-detail-button-template',
                    title: 'Test detail',
                    class: 'btn btn-default',
                    actionFn: showTestDetailAction
                },
                {
                    name: 'Edit',
                    include: 'test-edit-button-template',
                    title: 'Edit test',
                    actionFn: editTestAction
                },
                {
                    name: 'Delete',
                    include: 'test-delete-button-template',
                    title: 'Delete test',
                    actionFn: deleteTestAction
                }
            ];
        }

        function getMenuActions() {
            return [
                {
                    name: 'Create test execution',
                    title: 'Create test execution',
                    actionFn: createTestExecutionAction
                },
                {
                    name: 'Show test executions',
                    title: 'Show test executions',
                    actionFn: showTestExecutionsAction
                }
            ];
        }

        function showTestDetailAction(action, item) {
            $state.go('app.testDetail', {id: item.id});
        }

        function createTestExecutionAction(action, item) {
            var modalInstance = testExecutionModalService.createTestExecution(item.id);

            modalInstance.result.then(function (id) {
                $state.go('app.testExecutionDetail', {id: id});
            });
        }

        function showTestExecutionsAction(action, item) {
            alert('Not yet implemented.');
        }

        function editTestAction(action, item) {
            var modalInstance = testModalService.editTest(item.id);

            modalInstance.result.then(function () {
                vm.onUpdateList();
            });
        }

        function deleteTestAction(action, item) {
            alert('Delete test - not yet implemented.');
        }
    }
})();