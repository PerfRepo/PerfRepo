(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution')
        .config(config);

    function config($stateProvider) {
        $stateProvider
            .state('app.testExecutionDetail', {
                url: 'test-execution/detail/:id',
                templateUrl: 'app/test_execution/detail/detail-test-execution.view.html',
                controller: 'DetailTestExecutionController',
                controllerAs: 'vm',
                resolve: {
                    _testExecution: function(testExecutionService, $stateParams) {
                        return testExecutionService.getById($stateParams.id);
                    }
                }
            })

            .state('app.testExecutionOverview', {
                url: 'test-execution/search',
                templateUrl: 'app/test_execution/overview/test-execution-overview.view.html',
                controller: 'TestExecutionOverviewController',
                controllerAs: 'vm',
                params: {
                    initialFilters: null
                },
                resolve: {
                    _initialSearchResult: function(testExecutionService, $stateParams) {
                        return testExecutionService.defaultSearch($stateParams.initialFilters);
                    },
                    _initialFilters: function($stateParams) {
                        return $stateParams.initialFilters;
                    }
                }
            });
    }
})();