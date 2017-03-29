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
                controller: 'AppController',
                controllerAs: 'app',
                abstract: true,
                data: {
                    requireLogin: true
                },
                resolve: {
                    _comparisonSession: function (comparisonSessionService) {
                        return comparisonSessionService.getTestExecutions();
                    }
                }

            })
            .state('login', {
                url: '/login',
                templateUrl: 'app/login/login.view.html',
                controller: 'LoginController',
                controllerAs: 'login',
                params: {
                    'toState': 'app.dashboard',
                    'toParams': {}
                },
                data: {
                    requireLogin: false
                }
            });
    }

    function run($rootScope, $state, authenticationService, ngProgressFactory){
        var ngProgress = ngProgressFactory.createInstance();
        $rootScope.ngProgress = ngProgress;
        ngProgress.setHeight('5px');
        ngProgress.setColor('#39a5dc');

        if (!authenticationService.isAuthenticated()) {
            $state.go('login');
        }

        $rootScope.$on('$stateChangeStart', function (event, toState, toParams) {
            ngProgress.start();
            var requireLogin = toState.data.requireLogin;

            if (requireLogin && !authenticationService.isAuthenticated()) {
                event.preventDefault();
                $state.go('login', {'toState': toState.name, 'toParams': toParams});
            }
        });

        $rootScope.$on('$stateChangeSuccess', function() {
            ngProgress.complete();
        });

        $rootScope.$on('$stateChangeError', function (event, toState, toParams, fromState, fromParams, error) {
            if (error.status == 401) {
                event.preventDefault();
                $state.go('login', {'toState': toState.name, 'toParams': toParams});
            }
            ngProgress.complete();
        });
    }
})();