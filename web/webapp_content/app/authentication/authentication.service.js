(function() {
    'use strict';

    angular
        .module('org.perfrepo.authentication')
        .service('authenticationService', AuthService);

    function AuthService($http, API_URL, $state, $window, $q) {
        var vm = this;
        vm.login = login;
        vm.logout = logout;
        vm.isAuthenticated = isAuthenticated;
        vm.getToken = getToken;
        vm.getUser = getUser;

        function login(username, password) {
            var deferred = $q.defer();

            $http.post(API_URL + '/authentication',
                {'username': username, 'password': password})
                .then(function(response) {
                    var authData = response.data;
                    $window.localStorage.authData = JSON.stringify(authData);
                    deferred.resolve(authData);
                }, function(error) {
                    deferred.reject(error);
                });

            return deferred.promise;
        }

        function logout() {
            $http.post(API_URL + '/logout')
                .then(function(response) {
                    delete $window.localStorage.authData;
                    $state.go('login');
                    return response.data;
                });
        }

        function isAuthenticated() {
            return $window.localStorage.authData != undefined;
        }

        function getToken() {
            if (vm.isAuthenticated()) {
                return JSON.parse($window.localStorage.authData).token;
            }
        }

        function getUser() {
            if (vm.isAuthenticated()) {
                return JSON.parse($window.localStorage.authData).user;
            }
        }
    }
})();