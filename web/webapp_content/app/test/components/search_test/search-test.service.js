(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.search')
        .service('testSearchService', TestModalService);

    function TestModalService($filter) {
        var filterFields = [
            {
                id: 'namesFilter',
                title:  'Name',
                placeholder: 'Filter by Name...',
                filterType: 'text'
            },
            {
                id: 'uniqueIdsFilter',
                title:  'UID',
                placeholder: 'Filter by UID...',
                filterType: 'text'
            },
            {
                id: 'groupsFilter',
                title:  'Group',
                placeholder: 'Filter by Group...',
                filterType: 'text'
            }
        ];

        var sortFields = [
            {
                id: 'NAME',
                title:  'Name',
                sortType: 'alpha'
            },
            {
                id: 'UID',
                title: 'UID',
                sortType: 'alpha'
            },
            {
                id: 'GROUP',
                title: 'Group',
                sortType: 'alpha'
            }
        ];

        return {
            getToolbarConfig: getToolbarConfig,
            getSearchOffset: getSearchOffset,
            convertCriteriaParamsToSearchParams: convertCriteriaParamsToSearchParams,
            convertSearchParamsToCriteriaParams: convertSearchParamsToCriteriaParams
        };

        function getToolbarConfig(onFilterChange, onSortChange, defaultSearchParams) {
            return {
                filterConfig: prepareFilterConfig(onFilterChange, defaultSearchParams),
                sortConfig: prepareSortConfig(onSortChange, defaultSearchParams)
            };
        }

        function prepareSortConfig(onSortChange, defaultSearchParams) {
            return  {
                fields: sortFields,
                onSortChange: onSortChange,
                currentField: defaultSearchParams.sortField,
                isAscending: defaultSearchParams.isAscending
            };
        }

        function prepareFilterConfig(onFilterChange, defaultSearchParams) {
            return {
                fields: filterFields,
                appliedFilters: defaultSearchParams.filters,
                onFilterChange: onFilterChange
            }
        }

        function convertSearchParamsToCriteriaParams(searchParams) {
            var criteriaParams = {};

            angular.forEach(searchParams.filters, function(filter) {
                if (criteriaParams[filter.id] == undefined) {
                    criteriaParams[filter.id] = [];
                }

                criteriaParams[filter.id].push(filter.value);
            });

            criteriaParams.orderBy =  searchParams.sortField.id + (searchParams.isAscending ? '_ASC' : '_DESC');
            criteriaParams.limit = searchParams.limit;
            criteriaParams.offset = searchParams.offset;

            return criteriaParams;
        }

        function convertCriteriaParamsToSearchParams(criteriaParams) {
            var searchParams = {
                filters: [],
                limit: criteriaParams.limit,
                offset: criteriaParams.offset
            };

            angular.forEach(filterFields, function(filter) {
                if (criteriaParams[filter.id] != undefined) {
                    angular.forEach(criteriaParams[filter.id], function(filterValue) {
                        searchParams.filters.push({id: filter.id, title: filter.title, value: filterValue});
                    });
                }
            });

            // sort
            var sortName;
            var orderBy = criteriaParams.orderBy;

            if (orderBy.endsWith('_ASC')) {
                searchParams.isAscending = true;
                sortName = searchParams.sortField = orderBy.substr(0, orderBy.length - 4);
                searchParams.sortField = $filter('getByProperty')('id', sortName,  sortFields);
            }

            if (orderBy.endsWith('_DESC')) {
                searchParams.isAscending = false;
                sortName = searchParams.sortField = orderBy.substr(0, orderBy.length - 5);
                searchParams.sortField = $filter('getByProperty')('id', sortName,  sortFields);
            }

            return searchParams;
        }

        function getSearchOffset(currentPage, limit) {
            return ((currentPage - 1) * limit) || 0;
        }
    }
})();