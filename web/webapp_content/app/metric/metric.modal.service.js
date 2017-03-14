(function() {
    'use strict';

    angular
        .module('org.perfrepo.metric')
        .service('metricModalService', MetricModalService);

    function MetricModalService($uibModal) {

        return {
            createMetric: createMetric,
            editMetric: editMetric,
            removeMetric: removeMetric
        };

        function removeMetric(metric, testId) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/metric/remove_modal/remove-metric.view.html',
                controller: 'RemoveMetricController',
                controllerAs: 'vm',
                size: 'sm',
                resolve : {
                    _metric: function() {
                        return metric;
                    },
                    _testId: function () {
                        return testId;
                    }
                }
            });
        }

        function createMetric(testId) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/metric/create_modal/create-metric.view.html',
                controller: 'CreateMetricController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _comparators: function(metricService) {
                        return metricService.getComparators();
                    },
                    _testId: function () {
                        return testId;
                    },
                    _metrics: function (metricService) {
                        return metricService.getAll();
                    }
                }
            });
        }

        function editMetric(metricId) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/metric/edit_modal/edit-metric.view.html',
                controller: 'EditMetricController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _comparators: function(metricService) {
                        return metricService.getComparators();
                    },
                    _metric: function (metricService) {
                        return metricService.getById(metricId);
                    },
                    _metrics: function (metricService) {
                        return metricService.getAll();
                    }
                }
            });
        }


    }
})();