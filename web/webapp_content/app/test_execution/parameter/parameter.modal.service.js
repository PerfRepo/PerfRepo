(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.parameter')
        .service('testExecutionParameterModalService', TestExecutionParameterModalService);

    function TestExecutionParameterModalService($uibModal) {

        return {
            createParameter: createParameter,
            editParameter: editParameter,
            removeParameter: removeParameter
        };

        function removeParameter(parameter, parameters, testExecutionId) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/parameter/remove_modal/remove-parameter.view.html',
                controller: 'RemoveTestExecutionParameterController',
                controllerAs: 'vm',
                size: 'sm',
                resolve : {
                    _parameter: function () {
                        return parameter;
                    },
                    _parameters: function() {
                        return parameters;
                    },
                    _testExecutionId: function () {
                        return testExecutionId;
                    }
                }
            });
        }

        function createParameter(parameters, testExecutionId) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/parameter/create_modal/create-parameter.view.html',
                controller: 'CreateTestExecutionParameterController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _parameters: function() {
                        return parameters;
                    },
                    _testExecutionId: function () {
                        return testExecutionId;
                    }
                }
            });
        }

        function editParameter(parameter, parameters, testExecutionId) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/parameter/edit_modal/edit-parameter.view.html',
                controller: 'EditTestExecutionParameterController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _parameter: function () {
                        return parameter;
                    },
                    _parameters: function() {
                        return parameters;
                    },
                    _testExecutionId: function () {
                        return testExecutionId;
                    }
                }
            });
        }


    }
})();