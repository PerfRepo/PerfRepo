(function() {
    'use strict';

    angular
        .module('org.perfrepo')
        .config(config)
        .run(run);

    function config($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise('/dashboard');

        $stateProvider
            .state('app', {
                url: '/',
                templateUrl: 'app/app.view.html',
                abstract: true,
                data: {
                    requireLogin: true
                }
            })
            .state('login', {
                url: '/login',
                templateUrl: 'app/login/login.view.html',
                params: {
                    'toState': 'app.dashboard',
                    'toParams': {}
                },
                data: {
                    requireLogin: false
                }
            });
    }

    function run($rootScope, $state, authenticationService){

        $rootScope.$on('$stateChangeStart', function (event, toState, toParams) {
            var requireLogin = toState.data.requireLogin;

            if (requireLogin && !authenticationService.isAuthenticated()) {
                event.preventDefault();
                $state.go('login', {'toState': toState.name, 'toParams': toParams});
            }
        });

        $rootScope.$on('$stateChangeError', function (event, toState, toParams, fromState, fromParams, error) {
            if (error.status == 401) {
                event.preventDefault();
                $state.go('login', {'toState': toState.name, 'toParams': toParams});
            }
        });
    }
})();