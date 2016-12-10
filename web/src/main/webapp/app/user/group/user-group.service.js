(function() {
    'use strict';

    var UserGroupService = function($http, API_URL) {

        this.getUserGroups = function() {
            return $http.get(API_URL + '/groups')
                .then(function (response) {
                    return response.data;
                });
        };

    };

    // TODO create new module
    angular.module('org.perfrepo.test')
        .service('userGroupService', UserGroupService);

})();