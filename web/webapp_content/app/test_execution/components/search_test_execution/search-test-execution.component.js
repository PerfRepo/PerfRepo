(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.search')
        .component('searchTestExecution', {
            bindings: {
                initialSearchResult: '<',
                initialSearchCriteria: '<'
            },
            controller: SearchTestExecutionController,
            controllerAs: 'vm',
            templateUrl: 'app/test_execution/components/search_test_execution/search-test-execution.view.html'
        });

    function SearchTestExecutionController(testExecutionService, testExecutionSearchService, $rootScope) {
        var vm = this;
        vm.searchParams = testExecutionSearchService.convertCriteriaParamsToSearchParams(vm.initialSearchCriteria);

        vm.toolbarConfig = testExecutionSearchService.getToolbarConfig(filterChanged, sortChanged, vm.searchParams);

        vm.paginationChanged = paginationChanged;
        vm.updateSearch = updateSearch;

        // apply initial search
        applySearchResult(vm.initialSearchResult);

        function paginationChanged() {
            vm.searchParams.offset = testExecutionSearchService.getSearchOffset(vm.currentPage, vm.searchParams.limit);
            updateSearch();
        }

        function filterChanged(filters) {
            angular.forEach(filters, function (filter) {
                if (filter.id == 'startedAfterFilter' || filter.id == 'startedBeforeFilter') {
                    filter.value = moment(filter.value).format('MMM DD, YYYY HH:mm');
                }
            });

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
            var criteriaParams = testExecutionSearchService.convertSearchParamsToCriteriaParams(vm.searchParams);

            testExecutionService.search(criteriaParams).then(function(searchResult) {
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