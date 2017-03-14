(function() {
    'use strict';

    angular
        .module('org.perfrepo.group')
        .service('groupService', GroupService);

    function GroupService($http, API_GROUP_URL) {

        this.getUserGroups = function() {
            return $http.get(API_GROUP_URL)
                .then(function (response) {
                    return response.data;
                });
        };
    }
})();