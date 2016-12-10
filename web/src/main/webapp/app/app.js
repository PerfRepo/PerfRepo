(function() {
    'use strict';

    var AppController = function($state) {
        var vm = this;

        vm.navigationItems = [
            {
                title: "Dashboard",
                iconClass: "fa fa-dashboard",
                href: $state.href('app.dashboard')
            },
            {
                title: "Tests",
                iconClass : "fa fa-shield",
                href: $state.href('app.testSearch')
            },
            {
                title: "Create test",
                href: $state.href('app.testCreate')
            },
            {
                title: "Test executions",
                iconClass : "fa fa-cogs",
                href: $state.href('app.testExecution')

            },
            {
                title: "Reports",
                iconClass : "fa fa-line-chart",
                href: $state.href('app.report')
            }
        ];
    };

    var LoginController = function($state, loginService) {
        var vm = this;

        vm.login = login;

        function login() {
            loginService.login(vm.username, vm.password).then(function(response){
                if(response != undefined) {
                    $state.go($state.params.toState, $state.params.toParams);
                }
            }, function() {
                console.log("Bad login!!!");
            });
        }



    };

    angular.module('org.perfrepo',
        [
            'patternfly.navigation',
            'ui.router',

            'org.perfrepo.common',
            'org.perfrepo.dashboard',
            'org.perfrepo.test',
            'org.perfrepo.testExecution',
            'org.perfrepo.report'
        ])

        .constant('API_URL', 'rest/json')

        .config(function($stateProvider, $urlRouterProvider) {

            $urlRouterProvider.otherwise('/dashboard');

            $stateProvider
                .state('app', {
                    url: '/',
                    templateUrl: 'app/app.html',
                    abstract: true,
                    data: {
                        requireLogin: true
                    }
                })
                .state('login', {
                    url: '/login',
                    templateUrl: 'app/login/login.html',
                    params: {
                        'toState': 'app.dashboard',
                        'toParams': {}
                    },
                    data: {
                        requireLogin: false
                    }
                });
        })

        .run(function($rootScope, $state){

            $rootScope.$on('$stateChangeStart', function (event, toState, toParams) {
                var requireLogin = toState.data.requireLogin;
                if (requireLogin && typeof $rootScope.currentUser === 'undefined') {
                    event.preventDefault();
                    $state.go('login', {'toState': toState.name, 'toParams': toParams});
                }
            });

        })

        .controller('AppController', AppController)
        .controller('LoginController', LoginController);

})();