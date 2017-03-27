(function() {
    'use strict';

    angular
        .module('org.perfrepo.test')
        .config(config);

    function config($stateProvider) {
        $stateProvider
            .state('app.testDetail', {
                url: 'test/detail/:id',
                templateUrl: 'app/test/detail/detail-test.view.html',
                controller: 'DetailTestController',
                controllerAs: 'vm',
                resolve: {
                    _test: function(testService, $stateParams) {
                        return testService.getById($stateParams.id);
                    },
                    _isUserAlertsSubscriber: function(testService, $stateParams) {
                        return testService.isUserAlertsSubscriber($stateParams.id);
                    }
                }
            })

            .state('app.testOverview', {
                url: 'test/search',
                templateUrl: 'app/test/overview/test-overview.view.html',
                controller: 'TestOverviewController',
                controllerAs: 'vm',
                resolve: {
                    _searchCriteria: function(testService) {
                        return testService.getSearchCriteria();
                    },
                    _initialSearchResult: function(testService, _searchCriteria) {
                        return testService.search(_searchCriteria);
                    }
                }
            });
    }
})();