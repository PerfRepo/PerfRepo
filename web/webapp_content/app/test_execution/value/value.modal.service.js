(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.value')
        .service('testExecutionValueModalService', TestExecutionValueModalService);

    function TestExecutionValueModalService($uibModal) {

        return {
            showChart: showChart,
            removeValue: removeValue,
            createValue: createValue,
            editValue: editValue,
            showMultiValueData: showMultiValueData,
            removeValuesGroup: removeValuesGroup
        };

        function removeValue(executionValuesGroup, testExecutionId, index) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/value/remove_modal/remove-value.view.html',
                controller: 'RemoveTestExecutionValueController',
                controllerAs: 'vm',
                size: 'sm',
                resolve : {
                    _executionValuesGroup: function() {
                        return executionValuesGroup;
                    },
                    _testExecutionId: function() {
                        return testExecutionId;
                    },
                    _index: function() {
                        return index;
                    }
                }
            });
        }

        function removeValuesGroup(executionValuesGroup, testExecutionId) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/value/remove_modal/remove-values-group.view.html',
                controller: 'RemoveTestExecutionValuesGroupController',
                controllerAs: 'vm',
                size: 'sm',
                resolve : {
                    _executionValuesGroup: function() {
                        return executionValuesGroup;
                    },
                    _testExecutionId: function() {
                        return testExecutionId;
                    }
                }
            });
        }

        function createValue(valuesGroups, metricsName, testExecutionId, multiValue) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/value/create_modal/create-value.view.html',
                controller: 'CreateTestExecutionValueController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _valuesGroups: function() {
                        return valuesGroups;
                    },
                    _metricsName: function() {
                        return metricsName;
                    },
                    _testExecutionId: function() {
                        return testExecutionId;
                    },
                    _multiValue: function () {
                        return multiValue;
                    }
                }
            });
        }

        function editValue(valuesGroup, testExecutionId, index) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/value/edit_modal/edit-value.view.html',
                controller: 'EditTestExecutionValueController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _valuesGroup: function() {
                        return valuesGroup;
                    },
                    _testExecutionId: function() {
                        return testExecutionId;
                    },
                    _index: function() {
                        if (index == undefined) {
                            // single value is editing
                            return 0;
                        }
                        return index;
                    }
                }
            });
        }

        function showChart(values, parameter, metricName) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/value/chart_modal/show-value-chart.view.html',
                controller: 'ShowValueChartController',
                controllerAs: 'vm',
                size: 'lg',
                resolve : {
                    _values: function() {
                        return values;
                    },
                    _parameter: function () {
                        return parameter;
                    },
                    _metricName: function() {
                        return metricName;
                    }
                }
            });
        }

        function showMultiValueData(valuesGroup, testExecutionId, onUpdateTable) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/value/multi_value_data_modal/show-multi-value-data.view.html',
                controller: 'ShowMultiValueDataController',
                controllerAs: 'vm',
                size: 'lg',
                resolve : {
                    _valuesGroup: function() {
                        return valuesGroup;
                    },
                    _testExecutionId: function () {
                        return testExecutionId;
                    },
                    _onUpdateTable: function () {
                        return onUpdateTable;
                    }
                }
            });
        }
    }
})();