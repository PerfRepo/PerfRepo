(function() {
    'use strict';

    angular.module('org.perfrepo.test',
        [
            'ngResource',
            'ui.router',

            'org.perfrepo.test.search',
            'org.perfrepo.test.detail',
            'org.perfrepo.test.create',
            'org.perfrepo.test.edit'
        ])

        .config(function($stateProvider) {
            $stateProvider
                .state('app.testCreate', {
                    url: 'test/create',
                    templateUrl: 'app/test/create/create-test.html',
                    controller: 'CreateTestController',
                    controllerAs: 'vm',
                    resolve: {
                        userGroups: function(userGroupService) {
                            return userGroupService.getUserGroups();
                        },
                        metrics: function (metricService) {
                            return metricService.getAll();
                        }
                    }
                })

                .state('app.testDetail', {
                    url: 'test/detail/:id',
                    templateUrl: 'app/test/detail/detail-test.html',
                    controller: 'DetailTestController',
                    controllerAs: 'vm',
                    resolve: {
                        test: function(testService, $stateParams) {
                            return testService.getById($stateParams.id)
                        }
                    }
                })

                .state('app.testSearch', {
                    url: 'test/search',
                    templateUrl: 'app/test/search/search-test.html',
                    controller: 'SearchTestController',
                    controllerAs: 'vm'
                })

                .state('app.testEdit', {
                    url: 'test/edit/:id',
                    templateUrl: 'app/test/edit/edit-test.html',
                    controller: 'EditTestController',
                    controllerAs: 'vm',
                    resolve: {
                        test: function(testService, $stateParams) {
                            return testService.getById($stateParams.id)
                        },
                        userGroups: function(userGroupService) {
                            return userGroupService.getUserGroups();
                        },
                        metrics: function (metricService) {
                            return metricService.getAll();
                        }
                    }
                });
        });

})();