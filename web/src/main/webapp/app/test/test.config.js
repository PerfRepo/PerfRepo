(function() {
    'use strict';

    angular
        .module('org.perfrepo.test')
        .config(config);

    function config($stateProvider) {
        $stateProvider
            .state('app.testCreate', {
                url: 'test/create',
                templateUrl: 'app/test/create/create-test.view.html',
                controller: 'CreateTestController',
                controllerAs: 'vm',
                resolve: {
                    groups: function(groupService) {
                        return groupService.getUserGroups();
                    },
                    metrics: function (metricService) {
                        return metricService.getAll();
                    }
                }
            })
            .state('app.testDetail', {
                url: 'test/detail/:id',
                templateUrl: 'app/test/detail/detail-test.view.html',
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
                templateUrl: 'app/test/search/search-test.view.html',
                controller: 'SearchTestController',
                controllerAs: 'vm'
            })

            .state('app.testEdit', {
                url: 'test/edit/:id',
                templateUrl: 'app/test/edit/edit-test.view.html',
                controller: 'EditTestController',
                controllerAs: 'vm',
                resolve: {
                    test: function(testService, $stateParams) {
                        return testService.getById($stateParams.id)
                    },
                    groups: function(groupService) {
                        return groupService.getUserGroups();
                    },
                    metrics: function (metricService) {
                        return metricService.getAll();
                    }
                }
            });
    }
})();