(function() {
    'use strict';

    angular
        .module('org.perfrepo.test')
        .service('testService', TestService)
        .factory('Test', Test);

    function Test($resource, API_URL) {
        var url = API_URL + '/tests';
        return $resource(
            url + '/:id',
            {id: '@id'},
            {
                'query': {
                    method: 'GET', isArray: false
                },
                'update': {
                    method: 'PUT', isArray: false, url: url, params: {}
                }
            });
    }

    function TestService($http, Test, API_URL) {
        this.search = function(searchParams){
            return $http.post(API_URL + '/tests/search', searchParams);
        }

        this.getById = function(id) {
            return Test.get({id: id}).$promise;
        };

        this.save = function(test) {
            return Test.save(test).$promise;
        };

        this.update = function(test) {
            return Test.update(test).$promise;
        };

        this.delete = function(test) {
            return Test.delete(test).$promise;
        };
    }
})();