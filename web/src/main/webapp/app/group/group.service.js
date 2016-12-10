(function() {
    'use strict';

    angular
        .module('org.perfrepo.group')
        .service('groupService', GroupService);

    function GroupService($http, API_URL) {

        this.getUserGroups = function() {
            return $http.get(API_URL + '/groups')
                .then(function (response) {
                    return response.data;
                });
        };
    }
})();