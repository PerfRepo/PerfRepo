(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.search')
        .component('searchReport', {
            bindings: {
                initialSearchResult: '<',
                initialSearchCriteria: '<'
            },
            controller: SearchReportController,
            controllerAs: 'vm',
            templateUrl: 'app/report/components/search_report/search-report.view.html'
        });

    function SearchReportController(reportService, reportSearchService, $rootScope) {
        var vm = this;
        vm.searchParams = reportSearchService.convertCriteriaParamsToSearchParams(vm.initialSearchCriteria);

        vm.toolbarConfig = reportSearchService.getToolbarConfig(filterChanged, sortChanged, vm.searchParams);

        vm.paginationChanged = paginationChanged;
        vm.updateSearch = updateSearch;

        // apply initial search
        applySearchResult(vm.initialSearchResult);

        function paginationChanged() {
            vm.searchParams.offset = reportSearchService.getSearchOffset(vm.currentPage, vm.searchParams.limit);
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
            var criteriaParams = reportSearchService.convertSearchParamsToCriteriaParams(vm.searchParams);

            reportService.search(criteriaParams).then(function(searchResult) {
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