(function() {
    'use strict';

    var Metric = function($resource, API_URL) {

        return $resource(
            API_URL + '/metrics/:id',
            {id: '@id'},
            {
                'update': {
                    method: 'PUT'
                }
            });
    };

    var MetricService = function(Metric, API_URL) {

        this.getAll = function() {
            return Metric.query().$promise;
        };
    };

    angular.module('org.perfrepo.test')
        .service('metricService', MetricService)
        .factory('Metric', Metric);

})();