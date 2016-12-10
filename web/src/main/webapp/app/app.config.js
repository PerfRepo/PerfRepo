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

    function run($rootScope, $state){
        $rootScope.$on('$stateChangeStart', function (event, toState, toParams) {
            var requireLogin = toState.data.requireLogin;
            if (requireLogin && typeof $rootScope.currentUser === 'undefined') {
                event.preventDefault();
                $state.go('login', {'toState': toState.name, 'toParams': toParams});
            }
        });
    }
})();