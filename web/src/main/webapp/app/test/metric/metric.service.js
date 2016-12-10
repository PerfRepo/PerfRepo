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

        this.getById = function(id) {
            return Metric.get({id: id}).$promise;
        };

        this.getAll = function() {
            return Metric.query().$promise;
        };

        this.save = function(metric) {
            return Metric.save(metric).$promise;
        };

        this.update = function(metric) {
            return Metric.update(metric).$promise;
        };

        this.delete = function(metric) {
            return Metric.delete(metric).$promise;
        };
    };

    angular.module('org.perfrepo.test')
        .service('metricService', MetricService)
        .factory('Metric', Metric);

})();