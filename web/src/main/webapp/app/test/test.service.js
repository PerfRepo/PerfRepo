(function() {

    var Test = function($resource, API_URL) {

        return $resource(
            API_URL + '/tests/:id',
            {id: '@id'},
            {
                'query': {
                    method: 'GET', isArray: false
                }
            });
    };

    var TestService = function($http, Test) {

        this.getById = function(id) {
            return Test.get({id: id}).$promise;
        };

        this.save = function(test) {
            return Test.save(test).$promise;
        };

        this.delete = function(test) {
            return Test.delete(test).$promise;
        };
    };

    angular.module('org.perfrepo.test')
        .service('testService', TestService)
        .factory('Test', Test);

})();