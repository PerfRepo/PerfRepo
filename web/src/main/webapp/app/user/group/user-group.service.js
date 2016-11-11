(function() {
    'use strict';

    var UserGroupService = function($http, API_URL) {

        this.getUserGroups = function() {
            return $http.get(API_URL + '/users/user-group-names')
                .then(function (response) {
                    return response.data;
                });
        };

    };

    // TODO create new module
    angular.module('org.perfrepo.test')
        .service('userGroupService', UserGroupService);

})();