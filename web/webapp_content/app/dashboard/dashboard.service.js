(function() {
    'use strict';

    angular
        .module('org.perfrepo.dashboard')
        .service('dashboardService', DashboardService);

    function DashboardService($http, API_URL, API_DASHBOARD_URL) {

        return {
            getContent: getContent,
            getPerfRepoInfo: getPerfRepoInfo
        };

        function getContent() {
            return $http.get(API_DASHBOARD_URL + '/content').then(function(response) {
                return response.data;
            });
        }

        function getPerfRepoInfo() {
            return $http.get(API_URL + '/info').then(function(response) {
                return response.data;
            });
        }
    }
})();