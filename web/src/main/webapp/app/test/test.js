(function() {
    'use strict';

    var TestController = function() {
        var vm = this;
        vm.hello = "test";
    };

    angular.module('org.perfrepo.test',
        [
            'ngResource',
            'ui.router',

            'org.perfrepo.test.search',
            'org.perfrepo.test.create',
            'org.perfrepo.test.edit'
        ])

        .controller('TestController', TestController)

        .config(function($stateProvider) {
            $stateProvider
                .state('app.test', {
                    url: 'test',
                    templateUrl: 'app/test/test.html',
                    controller: 'TestController',
                    controllerAs: 'test'})

                .state('app.test-create', {
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
                    }})

                .state('app.test-search', {
                    url: 'test/search',
                    templateUrl: 'app/test/search/search-test.html',
                    controller: 'SearchTestController',
                    controllerAs: 'vm'
                })

                .state('app.test-edit', {
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