(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution')
        .service('testExecutionModalService', TestExecutionModalService);

    function TestExecutionModalService($uibModal) {

        return {
            createTestExecution: createTestExecution,
            editTestExecution: editTestExecution,
            removeTestExecution: removeTestExecution
        };

        function createTestExecution(testId) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/create/create-test-execution.view.html',
                controller: 'CreateTestExecutionController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _testId: function () {
                        return testId;
                    }
                }
            });
        }

        function editTestExecution(id) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/edit/edit-test-execution.view.html',
                controller: 'EditTestExecutionController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _testExecution: function (testExecutionService) {
                        return testExecutionService.getById(id);
                    }
                }
            });
        }

        function removeTestExecution(testExecution) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/remove/remove-test-execution.view.html',
                controller: 'RemoveTestExecutionController',
                controllerAs: 'vm',
                size: 'sm',
                resolve : {
                    _testExecution: testExecution
                }
            });
        }
    }
})();