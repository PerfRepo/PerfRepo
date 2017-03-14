(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.search')
        .component('searchReport', {
            bindings: {
                initialSearchResult: '='
            },
            controller: SearchReportController,
            controllerAs: 'vm',
            templateUrl: 'app/report/components/search_report/search-report.view.html'
        });

    function SearchReportController(reportService, reportSearchService, $rootScope) {
        var vm = this;
        vm.pagination = {};
        vm.currentPage = 1;
        vm.searchParams = reportService.getDefaultSearchParams();

        vm.toolbarConfig = reportSearchService.getToolbarConfig(filterChanged, sortChanged);

        vm.paginationChanged = paginationChanged;
        vm.updateSearch = updateSearch;

        // apply initial search
        applySearchInitialResult(vm.initialSearchResult);

        function paginationChanged() {
            vm.searchParams.offset = reportSearchService.getSearchOffset(vm.currentPage, vm.searchParams.limit);
            updateSearch();
        }

        function filterChanged(filters) {
            var searchFilterParams = reportSearchService.convertFiltersToCriteriaParams(filters);
            angular.extend(vm.searchParams, searchFilterParams);
            updateSearch();
        }

        function sortChanged(sortFiled, isAscending) {
            vm.searchParams.orderBy = sortFiled.id.toUpperCase() + (isAscending ? '_ASC' : '_DESC');
            updateSearch();
        }

        function updateSearch() {
            $rootScope.ngProgress.start();

            reportService.search(vm.searchParams).then(function(searchResult) {
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