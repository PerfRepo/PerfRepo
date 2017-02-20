(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.search')
        .service('testExecutionSearchService', TestExecutionModalService);

    function TestExecutionModalService() {
        var vm = this;
        vm.filterParamsMapping = getFilterParamsMapping();

        return {
            getToolbarConfig: getToolbarConfig,
            getSearchOffset: getSearchOffset,
            convertFiltersToCriteriaParams: convertFiltersToCriteriaParams
        };

        function getToolbarConfig(onFilterChange, onSortChange) {
            return {
                filterConfig: prepareFilterConfig(onFilterChange),
                sortConfig: prepareSortConfig(onSortChange)
            };
        }

        function prepareSortConfig(onSortChange) {
            var sortFields = [
                {
                    id: 'name',
                    title:  'Test name',
                    sortType: 'alpha'
                },
                {
                    id: 'uid',
                    title: 'Test UID',
                    sortType: 'alpha'
                },
                {
                    id: 'date',
                    title: 'Started date',
                    sortType: 'numeric'
                }
            ];

            return  {
                fields: sortFields,
                onSortChange: onSortChange,
                currentField: sortFields[0],
                isAscending: true
            };
        }

        function prepareFilterConfig(onFilterChange) {
            var fields = [
                {
                    id: 'name',
                    title:  'Name',
                    placeholder: 'Filter by Name...',
                    filterType: 'text'
                },
                {
                    id: 'tagQuery',
                    title:  'Tag query',
                    placeholder: 'Filter by Tag query...',
                    filterType: 'text'
                },
                {
                    id: 'parameterQuery',
                    title:  'Parameter query',
                    placeholder: 'Filter by Parameter query...',
                    filterType: 'text'
                },
                {
                    id: 'id',
                    title:  'ID',
                    placeholder: 'Filter by ID...',
                    filterType: 'number'
                },
                {
                    id: 'startedAfter',
                    title:  'Started after',
                    placeholder: 'Filter by Started after...',
                    filterType: 'datetime-local'
                },
                {
                    id: 'startedBefore',
                    title:  'Started before',
                    placeholder: 'Filter by Started before...',
                    filterType: 'datetime-local'
                },
                {
                    id: 'testName',
                    title:  'Test name',
                    placeholder: 'Filter by Test name...',
                    filterType: 'text'
                },
                {
                    id: 'testUID',
                    title:  'Test UID',
                    placeholder: 'Filter by Test UID...',
                    filterType: 'text'
                },
                {
                    id: 'group',
                    title:  'Test group',
                    placeholder: 'Filter by Test group...',
                    filterType: 'text'
                }
            ];

            return {
                fields: fields,
                appliedFilters: [],
                onFilterChange: onFilterChange
            }
        }

        function getFilterParamsMapping() {
            return {
                id: 'idsFilter',
                name: 'namesFilter',
                testName: 'testNamesFilter',
                testUID: 'testUIDsFilter',
                group: 'groupsFilter',
                tagQuery: 'tagQueriesFilter',
                parameterQuery: 'parameterQueriesFilter',
                startedAfter: 'startedAfterFilter',
                startedBefore: 'startedBeforeFilter'
            }
        }

        function convertFiltersToCriteriaParams(filters) {
            var searchParams = {};
            angular.forEach(vm.filterParamsMapping, function(filterName) {
                searchParams[filterName] = [];
            });

            angular.forEach(filters, function(filter) {
                var filterName = vm.filterParamsMapping[filter.id];
                if (filterName == 'startedAfterFilter' || filterName == 'startedBeforeFilter') {
                    searchParams[filterName].push(moment(filter.value).format());
                } else {
                    searchParams[filterName].push(filter.value);
                }
            });

            return searchParams;
        }

        function getSearchOffset(currentPage, limit) {
            return ((currentPage - 1) * limit) || 0;
        }
    }
})();