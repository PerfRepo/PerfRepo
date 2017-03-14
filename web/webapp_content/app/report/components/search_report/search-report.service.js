(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.search')
        .service('reportSearchService', ReportSearchService);

    function ReportSearchService(wizardService, $filter) {
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
                    title:  'Name',
                    sortType: 'alpha'
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
                    id: 'type',
                    title:  'Type',
                    placeholder: 'Filter by Type...',
                    filterType: 'select',
                    filterValues: wizardService.getReportTypes().map(function(type) {return type.name;})
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
                name: 'namesFilter',
                type: 'typesFilter'
            }
        }

        function convertFiltersToCriteriaParams(filters) {
            var searchParams = {};
            angular.forEach(vm.filterParamsMapping, function(filterName) {
                searchParams[filterName] = [];
            });

            angular.forEach(filters, function(filter) {
                var filterName = vm.filterParamsMapping[filter.id];
                if (filter.id == 'type') {
                    var type = $filter('getByProperty')('name', filter.value,  wizardService.getReportTypes());
                    searchParams[filterName].push(type.type);
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