(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.search')
        .component('searchTestExecution', {
            bindings: {
                initialSearchResult: '=',
                initialFilters: '='
            },
            controller: SearchTestExecutionController,
            controllerAs: 'vm',
            templateUrl: 'app/test_execution/components/search_test_execution/search-test-execution.view.html'
        });

    function SearchTestExecutionController(testExecutionService, testExecutionSearchService, $rootScope) {
        var vm = this;
        vm.pagination = {};
        vm.currentPage = 1;
        vm.searchParams = testExecutionService.getDefaultSearchParams();

        vm.toolbarConfig = testExecutionSearchService.getToolbarConfig(filterChanged, sortChanged);

        vm.paginationChanged = paginationChanged;
        vm.updateSearch = updateSearch;

        // apply initial search
        applySearchInitialResult(vm.initialSearchResult, vm.initialFilters);

        function paginationChanged() {
            vm.searchParams.offset = testExecutionSearchService.getSearchOffset(vm.currentPage, vm.searchParams.limit);
            updateSearch();
        }

        function filterChanged(filters) {
            angular.forEach(filters, function (filter) {
                if (filter.id == 'startedAfter' || filter.id == 'startedBefore') {
                    // TODO
                    filter.value = moment(filter.value).format('MMM DD, YYYY HH:mm');
                }
            });

            var searchFilterParams = testExecutionSearchService.convertFiltersToCriteriaParams(filters);
            angular.extend(vm.searchParams, searchFilterParams);

            updateSearch();
        }

        function sortChanged(sortFiled, isAscending) {
            vm.searchParams.orderBy = sortFiled.id.toUpperCase() + (isAscending ? '_ASC' : '_DESC');
            updateSearch();
        }

        function updateSearch() {
            $rootScope.ngProgress.start();

            testExecutionService.search(vm.searchParams).then(function(searchResult) {
                applySearchResult(searchResult);
                $rootScope.ngProgress.complete();
            });
        }

        function applySearchInitialResult(searchResult, initialFilters) {
            applySearchResult(searchResult);
            vm.toolbarConfig.filterConfig.appliedFilters = initialFilters;
        }

        function applySearchResult(searchResult) {
            vm.items = searchResult.data;
            vm.toolbarConfig.filterConfig.resultsCount = searchResult.totalCount;
            vm.pagination.pageCount = searchResult.pageCount;
            vm.pagination.currentPage = searchResult.currentPage;
        }
    }
})();