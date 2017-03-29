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

    function SearchTestExecutionController(testExecutionService, testExecutionSearchService,
                                           testExecutionMassOperationModalService, comparisonSessionService,
                                           $rootScope, $scope) {
        var vm = this;
        vm.searchParams = testExecutionSearchService.convertCriteriaParamsToSearchParams(vm.initialSearchCriteria);
        vm.toolbarConfig = testExecutionSearchService.getToolbarConfig(filterChanged, sortChanged, vm.searchParams);
        vm.selectedChecked = false;
        vm.selectAllText = 'Select All';
        vm.getSelected = getSelected;

        vm.paginationChanged = paginationChanged;
        vm.updateSearch = updateSearch;

        vm.addToComparison = addToComparison;
        vm.selectAll = selectAll;
        vm.addTags = addTags;
        vm.removeTags = removeTags;
        vm.addParameter = addParameter;
        vm.removeParameter = removeParameter;
        vm.deleteExecution = deleteExecution;


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

        function selectAll() {
            angular.forEach(vm.items, function(item) {
                item.selected = vm.selectedChecked;
            });
        }

        function addToComparison() {
            comparisonSessionService.addToComparison(getSelected()).then(function(testExecutions) {
                $scope.$emit('comparisonSessionChange', testExecutions);
            });
        }

        function addTags() {
            var modalInstance = testExecutionMassOperationModalService.tagsMassOperation(getSelected(), 'add');

            modalInstance.result.then(function () {
                updateSearch();
            });
        }

        function removeTags() {
            var modalInstance = testExecutionMassOperationModalService.tagsMassOperation(getSelected(), 'remove');

            modalInstance.result.then(function () {
                updateSearch();
            });
        }

        function addParameter() {
            var modalInstance = testExecutionMassOperationModalService.parameterMassOperation(getSelected(), 'add');

            modalInstance.result.then(function () {
                updateSearch();
            });
        }

        function removeParameter() {
            var modalInstance = testExecutionMassOperationModalService.parameterMassOperation(getSelected(), 'remove');

            modalInstance.result.then(function () {
                updateSearch();
            });
        }

        function deleteExecution() {
            var modalInstance = testExecutionMassOperationModalService.testExecutionsMassOperation(getSelected());

            modalInstance.result.then(function () {
                updateSearch();
            });
        }

        function getSelected() {
            var selectedExecutionIds = [];
            angular.forEach(vm.items, function(item) {
                if (item.selected) {
                    selectedExecutionIds.push(item.id);
                }
            });

            return selectedExecutionIds;
        }

        $scope.$watch(function() {
            return getSelected().length;
        }, function(selected) {
            vm.selectedItems = selected;
            vm.selectedChecked = (selected == vm.items.length);
        });
    }
})();