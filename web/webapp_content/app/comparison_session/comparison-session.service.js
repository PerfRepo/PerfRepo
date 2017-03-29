(function() {
    'use strict';

    angular
        .module('org.perfrepo.comparisonSession')
        .service('comparisonSessionService', ComparisonSessionService);

    function ComparisonSessionService($http, API_COMPARISON_SESSION_URL) {

        return {
            getTestExecutions: getTestExecutions,
            addToComparison: addToComparison,
            removeFromComparison: removeFromComparison
        };

        function getTestExecutions() {
            return $http.get(API_COMPARISON_SESSION_URL)
                .then(function (response) {
                    return response.data;
                });
        }

        function addToComparison(testExecutionIds) {
            return $http.post(API_COMPARISON_SESSION_URL + '/addition', testExecutionIds)
                .then(function (response) {
                    return response.data;
                });
        }

        function removeFromComparison(testExecutionIds) {
            return $http.post(API_COMPARISON_SESSION_URL + '/removal', testExecutionIds)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();