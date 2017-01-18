(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution')
        .service('testExecutionService', TestExecutionService);

    function TestExecutionService($http, $resource, API_TEST_EXECUTION_URL, testExecutionSearchService) {

        var testExecutionResource = $resource(API_TEST_EXECUTION_URL + '/:id',
            {
                id: '@id'
            },
            {
                'update': {
                    method: 'PUT',
                    isArray: false,
                    url:  API_TEST_EXECUTION_URL,
                    params: {}
                },
                'save': {
                    method: 'POST',
                    interceptor: {
                        response: function(response) {
                            return response.headers("Location").split("/").pop();
                        }
                    }
                }
            });

        return {
            search: search,
            defaultSearch: defaultSearch,
            getById: getById,
            save: save,
            update: update,
            remove: remove,
            getDefaultSearchParams: getDefaultSearchParams
        };

        function search(searchParams){
            return $http.post(API_TEST_EXECUTION_URL + '/search', searchParams).then(function(response) {
                return {
                    data : response.data,
                    totalCount : parseInt(response.headers('X-Pagination-Total-Count')),
                    pageCount : parseInt(response.headers('X-Pagination-Page-Count')),
                    currentPage : parseInt(response.headers('X-Pagination-Current-Page'))
                };
            });
        }

        function defaultSearch(defaultFilters) {
            var defaultSearchParams =  getDefaultSearchParams();

            if (defaultFilters) {
                var filters = testExecutionSearchService.convertFiltersToCriteriaParams(defaultFilters);
                angular.extend(defaultSearchParams, filters);
            }

            return search(defaultSearchParams);
        }

        function getDefaultSearchParams() {
            return {
                limit: 5,
                offset: 0,
                orderBy: 'NAME_ASC'
            };
        }

        function getById(id) {
            return testExecutionResource.get({id: id}).$promise;
        }

        function save(testExecution) {
            return testExecutionResource.save(testExecution).$promise;
        }

        function update(testExecution) {
            return testExecutionResource.update(testExecution).$promise;
        }

        function remove(testExecution) {
            return testExecutionResource.delete(testExecution).$promise;
        }
    }
})();