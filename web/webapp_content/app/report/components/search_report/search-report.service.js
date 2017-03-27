(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.search')
        .service('reportSearchService', ReportSearchService);

    function ReportSearchService(wizardService, $filter) {
        var filterFields = [
            {
                id: 'namesFilter',
                title:  'Name',
                placeholder: 'Filter by Name...',
                filterType: 'text'
            },
            {
                id: 'typesFilter',
                title:  'Type',
                placeholder: 'Filter by Type...',
                filterType: 'select',
                filterValues: wizardService.getReportTypes().map(function(type) {return type.name;})
            }
        ];

        var sortFields = [
            {
                id: 'NAME',
                title:  'Name',
                sortType: 'alpha'
            },
            {
                id: 'REPORT_TYPE',
                title: 'Type',
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

                if (filter.id == 'typesFilter') {
                    var reportType = $filter('getByProperty')('name', filter.value,  wizardService.getReportTypes());
                    criteriaParams[filter.id].push(reportType.type);
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
                        if (filter.id == 'typesFilter') {
                            value = $filter('getByProperty')('type', filterValue,  wizardService.getReportTypes()).name;
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