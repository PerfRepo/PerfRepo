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

        .constant('API_URL', 'api/json')

        .config(function($stateProvider, $urlRouterProvider) {
//            $urlRouterProvider.otherwise('/login');

            $stateProvider
                .state('app', {
                    url: '/',
                    templateUrl: 'app/app.html',
                    abstract: true,
                    data: {
                    }
                })
                .state('login', {
                    url: '/login',
                    template: 'login page'
                });
        })

        .controller('AppController', AppController);

})();