(function() {
    'use strict';

    var Test = function($resource, API_URL) {

        return $resource(
            API_URL + '/tests/:id',
            {id: '@id'},
            {
                'query': {
                    method: 'GET', isArray: false
                },
                'update': {
                    method: 'PUT', isArray: false
                }
            });
    };

    var TestService = function($http, Test, API_URL) {

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
    };

    angular.module('org.perfrepo.test')
        .service('testService', TestService)
        .factory('Test', Test);

})();