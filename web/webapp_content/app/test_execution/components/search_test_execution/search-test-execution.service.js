(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.search')
        .service('testExecutionSearchService', TestExecutionModalService);

    function TestExecutionModalService($filter) {
        var filterFields = [
            {
                id: 'namesFilter',
                title:  'Name',
                placeholder: 'Filter by Name...',
                filterType: 'text'
            },
            {
                id: 'tagQueriesFilter',
                title:  'Tag query',
                placeholder: 'Filter by Tag query...',
                filterType: 'text'
            },
            {
                id: 'parameterQueriesFilter',
                title:  'Parameter query',
                placeholder: 'Filter by Parameter query...',
                filterType: 'text'
            },
            {
                id: 'idsFilter',
                title:  'ID',
                placeholder: 'Filter by ID...',
                filterType: 'number'
            },
            {
                id: 'startedAfterFilter',
                title:  'Started after',
                placeholder: 'Filter by Started after...',
                filterType: 'datetime-local'
            },
            {
                id: 'startedBeforeFilter',
                title:  'Started before',
                placeholder: 'Filter by Started before...',
                filterType: 'datetime-local'
            },
            {
                id: 'testNamesFilter',
                title:  'Test name',
                placeholder: 'Filter by Test name...',
                filterType: 'text'
            },
            {
                id: 'testUniqueIdsFilter',
                title:  'Test UID',
                placeholder: 'Filter by Test UID...',
                filterType: 'text'
            },
            {
                id: 'groupsFilter',
                title:  'Test group',
                placeholder: 'Filter by Test group...',
                filterType: 'text'
            }
        ];

        var sortFields = [
            {
                id: 'NAME',
                title:  'Test name',
                sortType: 'alpha'
            },
            {
                id: 'UID',
                title: 'Test UID',
                sortType: 'alpha'
            },
            {
                id: 'DATE',
                title: 'Started date',
                sortType: 'numeric'
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
                sortConfig: prepareSortConfig(onSortChange, defaultSearchParams),
                actionsConfig: prepareActionsConfig()
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
        
        function prepareActionsConfig() {
            return {
                actionsInclude: true
            };
        }

        function convertSearchParamsToCriteriaParams(searchParams) {
            var criteriaParams = {};

            angular.forEach(searchParams.filters, function(filter) {
                if (criteriaParams[filter.id] == undefined) {
                    criteriaParams[filter.id] = [];
                }

                if (filter.id == 'startedAfterFilter' || filter.id == 'startedBeforeFilter') {
                    criteriaParams[filter.id].push(moment(filter.value).format());
                } else {
                    criteriaParams[filter.id].push(filter.value);
                }
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
                        var value = filterValue;
                        if (filter.id == 'startedAfterFilter' || filter.id == 'startedBeforeFilter') {
                            value = moment(filterValue).format('MMM DD, YYYY HH:mm');
                        }
                        searchParams.filters.push({id: filter.id, title: filter.title, value: value});
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