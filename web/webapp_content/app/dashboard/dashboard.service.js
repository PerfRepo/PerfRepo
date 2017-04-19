(function() {
    'use strict';

    angular
        .module('org.perfrepo.dashboard')
        .service('dashboardService', DashboardService);

    function DashboardService($http, API_DASHBOARD_URL) {

        return {
            getContent: getContent
        };

        function getContent() {
            return $http.get(API_DASHBOARD_URL + '/content').then(function(response) {
                return response.data;
            });
        }
    }
})();