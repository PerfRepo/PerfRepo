(function() {
    'use strict';

    angular
        .module('org.perfrepo.authentication')
        .service('authenticationService', AuthService);

    function AuthService($http, $location, API_URL, $window, $q) {
        var vm = this;
        vm.login = login;
        vm.logout = logout;
        vm.isAuthenticated = isAuthenticated;
        vm.getToken = getToken;
        vm.getUser = getUser;
        vm.redirectToLogin = redirectToLogin;
        vm.saveAttemptLocation = saveAttemptLocation;
        vm.redirectToAttemptedLocation = redirectToAttemptedLocation;
        vm.location = '/dashboard';

        function login(username, password) {
            var deferred = $q.defer();

            $http.post(API_URL + '/authentication', {
                username: username,
                password: password
            }).then(function(response) {
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
                    redirectToLogin();
                    return response.data;
                });
        }

        function isAuthenticated() {
            return $window.localStorage.authData !== undefined;
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

        function redirectToLogin() {
            $location.path('/login');
        }

        function redirectToAttemptedLocation() {
            $location.path(vm.location);
        }

        function saveAttemptLocation() {
            if ($location.path() !==  '/login') {
                vm.location = $location.path();
            } else {
                vm.location = '/dashboard';
            }
        }
    }
})();