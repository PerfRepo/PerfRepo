(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.search')
        .service('testExecutionSearchService', TestExecutionModalService);

    function TestExecutionModalService() {

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
                    title:  'Test name',
                    placeholder: 'Filter by Test name...',
                    filterType: 'text'
                },
                {
                    id: 'uid',
                    title:  'Test UID',
                    placeholder: 'Filter by Test UID...',
                    filterType: 'text'
                },
                {
                    id: 'tag',
                    title:  'Tag',
                    placeholder: 'Filter by Tag...',
                    filterType: 'text'
                }
            ];

            return {
                fields: fields,
                appliedFilters: [],
                onFilterChange: onFilterChange
            }
        }

        function convertFiltersToCriteriaParams(filters) {
            var params = {
                testNameFilters: [],
                testUidFilters: [],
                tagFilters: []
            };

            filters.forEach(function(filter) {
                if(filter.id == 'name') {
                    params.testNameFilters.push(filter.value);
                } else if(filter.id == 'uid') {
                    params.testUidFilters.push(filter.value);
                } else if(filter.id == 'tag') {
                    params.tagFilters.push(filter.value);
                }
            });

            return params;
        }

        function getSearchOffset(currentPage, limit) {
            return ((currentPage - 1) * limit) || 0;
        }
    }
})();