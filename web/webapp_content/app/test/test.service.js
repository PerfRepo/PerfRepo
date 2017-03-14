(function() {
    'use strict';

    angular
        .module('org.perfrepo.test')
        .service('testService', TestService);

    function TestService($http, $resource, API_TEST_URL) {

        var testResource = $resource(API_TEST_URL + '/:id',
            {
                id: '@id'
            },
            {
                'update': {
                    method: 'PUT',
                    isArray: false,
                    url:  API_TEST_URL,
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
            asyncSelectSearch: asyncSelectSearch,
            getById: getById,
            save: save,
            update: update,
            remove: remove,
            isUserAlertsSubscriber: isUserAlertsSubscriber,
            subscribeAlerts: subscribeAlerts,
            unsubscribeAlerts: unsubscribeAlerts,
            getDefaultSearchParams: getDefaultSearchParams
        };

        function search(searchParams){
            return $http.post(API_TEST_URL + '/search', searchParams).then(function(response) {
                return {
                    data : response.data,
                    totalCount : parseInt(response.headers('X-Pagination-Total-Count')),
                    pageCount : parseInt(response.headers('X-Pagination-Page-Count')),
                    currentPage : parseInt(response.headers('X-Pagination-Current-Page'))
                };
            });
        }

        function defaultSearch() {
            return search(getDefaultSearchParams());
        }

        function getDefaultSearchParams() {
            return {
                limit: 10,
                offset: 0,
                orderBy: 'NAME_ASC'
            };
        }

        function asyncSelectSearch(generalSearch) {
            var params = getDefaultSearchParams();
            params.generalSearch = generalSearch;
            return search(params);
        }

        function getById(id) {
            return testResource.get({id: id}).$promise;
        }

        function save(test) {
            return testResource.save(test).$promise;
        }

        function update(test) {
            return testResource.update(test).$promise;
        }

        function remove(id) {
            return testResource.delete({id: id}).$promise;
        }

        function isUserAlertsSubscriber(testId) {
            return $http.get(API_TEST_URL + '/' + testId + '/subscriber').then(function(response) {
                return response.data;
            });
        }

        function subscribeAlerts(testId) {
            return $http.post(API_TEST_URL + '/' + testId + '/subscriber-addition').then(function(response) {
                return response.data;
            });
        }

        function unsubscribeAlerts(testId) {
            return $http.post(API_TEST_URL + '/' + testId + '/subscriber-removal').then(function(response) {
                return response.data;
            });
        }
    }
})();