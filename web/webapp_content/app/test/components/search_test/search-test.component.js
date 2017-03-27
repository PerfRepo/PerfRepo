(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.search')
        .component('searchTest', {
            bindings: {
                initialSearchResult: '<',
                initialSearchCriteria: '<'
            },
            controller: SearchTestController,
            controllerAs: 'vm',
            templateUrl: 'app/test/components/search_test/search-test.view.html'
        });

    function SearchTestController(testService, testSearchService, $rootScope) {
        var vm = this;

        vm.searchParams = testSearchService.convertCriteriaParamsToSearchParams(vm.initialSearchCriteria);

        vm.toolbarConfig = testSearchService.getToolbarConfig(filterChanged, sortChanged, vm.searchParams);

        vm.paginationChanged = paginationChanged;
        vm.updateSearch = updateSearch;

        // apply initial search
        applySearchResult(vm.initialSearchResult);

        function paginationChanged() {
            vm.searchParams.offset = testSearchService.getSearchOffset(vm.currentPage, vm.searchParams.limit);
            updateSearch();
        }

        function filterChanged(filters) {
            vm.searchParams.filters = filters;
            updateSearch();
        }

        function sortChanged(sortField, isAscending) {
            vm.searchParams.sortField = sortField;
            vm.searchParams.isAscending = isAscending;
            updateSearch();
        }

        function updateSearch() {
            $rootScope.ngProgress.start();
            var criteriaParams = testSearchService.convertSearchParamsToCriteriaParams(vm.searchParams);

            testService.search(criteriaParams).then(function(searchResult) {
                applySearchResult(searchResult);
                $rootScope.ngProgress.complete();
            });
        }

        function applySearchResult(searchResult) {
            vm.items = searchResult.data;
            vm.toolbarConfig.filterConfig.resultsCount = searchResult.totalCount;
            vm.currentPage = searchResult.currentPage;
        }
    }
})();