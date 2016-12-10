(function() {
    'use strict';

    angular.module('org.perfrepo.login')
        .service('loginService', LoginService);

    function LoginService(API_URL, $http, $rootScope, $q) {

        this.login = function(username, password) {
            var deferred = $q.defer();

            $http.post(API_URL + '/authentication',
                {'username': username, 'password': password})
                .then(function (response) {
                    $rootScope.currentUser = {
                        username: response.data.user.username,
                        token: response.data.token
                    }
                    deferred.resolve($rootScope.currentUser);
                }, function () {
                    $rootScope.currentUser = undefined;
                    deferred.reject();
                });

            return deferred.promise;
        };
    }
})();