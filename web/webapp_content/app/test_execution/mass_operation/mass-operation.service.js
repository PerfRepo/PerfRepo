(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.massOperation')
        .service('testExecutionMassOperationService', TestExecutionMassOperationService);

    function TestExecutionMassOperationService($http, API_TEST_EXECUTION_URL) {

        return {
            addTags: addTags,
            removeTags: removeTags,
            addParameter: addParameter,
            removeParameter: removeParameter,
            removeTestExecutions: removeTestExecutions
        };

        function addTags(tags, testExecutionIds) {
            return $http.post(API_TEST_EXECUTION_URL + '/mass-operation/tags-addition', {
                tags: tags,
                testExecutionIds: testExecutionIds
            });
        }

        function removeTags(tags, testExecutionIds) {
            return $http.post(API_TEST_EXECUTION_URL + '/mass-operation/tags-removal', {
                tags: tags,
                testExecutionIds: testExecutionIds
            });
        }

        function addParameter(parameter, testExecutionIds) {
            return $http.post(API_TEST_EXECUTION_URL + '/mass-operation/parameter-addition', {
                parameter: parameter,
                testExecutionIds: testExecutionIds
            });
        }

        function removeParameter(parameter, testExecutionIds) {
            return $http.post(API_TEST_EXECUTION_URL + '/mass-operation/parameter-removal', {
                parameter: parameter,
                testExecutionIds: testExecutionIds
            });
        }

        function removeTestExecutions(testExecutionIds) {
            return $http.post(API_TEST_EXECUTION_URL + '/mass-operation/test-execution-removal', testExecutionIds);
        }
    }
})();