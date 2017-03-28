(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.massOperation')
        .service('testExecutionMassOperationModalService', TestExecutionMassOperationModalService);

    function TestExecutionMassOperationModalService($uibModal) {

        return {
            tagsMassOperation: tagsMassOperation,
            parameterMassOperation: parameterMassOperation,
            testExecutionsMassOperation: testExecutionsMassOperation
        };

        function tagsMassOperation(testExecutionIds, mode) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/mass_operation/tags_modal/tags.view.html',
                controller: 'TagsMassOperationController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _testExecutionIds: function () {
                        return testExecutionIds;
                    },
                    _mode: function () {
                        return mode;
                    }
                }
            });
        }

        function parameterMassOperation(testExecutionIds, mode) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/mass_operation/parameter_modal/parameter.view.html',
                controller: 'ParameterMassOperationController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _testExecutionIds: function () {
                        return testExecutionIds;
                    },
                    _mode: function () {
                        return mode;
                    }
                }
            });
        }

        function testExecutionsMassOperation(testExecutionIds) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/mass_operation/delete_execution_modal/delete-execution.view.html',
                controller: 'DeleteExecutionMassOperationController',
                controllerAs: 'vm',
                size: 'sm',
                resolve : {
                    _testExecutionIds: function () {
                        return testExecutionIds;
                    }
                }
            });
        }
    }
})();