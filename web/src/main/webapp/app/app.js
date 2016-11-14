(function() {
    'use strict';

    angular.module('org.perfrepo',
        [
            'ui.router',

            'org.perfrepo.common',
            'org.perfrepo.dashboard',
            'org.perfrepo.test',
            'org.perfrepo.testExecution',
            'org.perfrepo.report'
        ])

        .constant('API_URL', 'api')

        .config(function($stateProvider, $urlRouterProvider) {
            $urlRouterProvider.otherwise('/login');

            $stateProvider
                .state('app', {
                    url: '/',
                    templateUrl: 'app/app.html',
                    abstract: true,
                    controller: function ($document) {
                        angular.element($document).ready(function () {
                            $().setupVerticalNavigation(true);
                        });
                    },
                    data: {
                    }
                })
                .state('login', {
                    url: '/login',
                    template: 'login page'
                });
        });

})();