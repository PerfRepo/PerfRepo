(function() {
    'use strict';

    angular
        .module('org.perfrepo.user')
        .service('userService', UserService);

    function UserService($http, API_USER_URL) {

        return {
            getAll: getAll
        };

        function getAll() {
            return $http.get(API_USER_URL)
                .then(function (response) {
                    return response.data;
                });
        }
    }
})();