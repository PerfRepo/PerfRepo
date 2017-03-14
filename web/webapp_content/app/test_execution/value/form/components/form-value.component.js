(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.value')
        .component('testExecutionValueForm', {
            bindings: {
                valueObject: '<',
                valuesGroups: '<',
                metrics: '<',
                selectedMetricId: '<',
                disableMetric: '<',
                onSave: '&',
                onCancel: '&'
            },
            controller: FormTestExecutionValueController,
            controllerAs: 'vm',
            templateUrl: 'app/test_execution/value/form/components/form-value.view.html'
        });

    function FormTestExecutionValueController($filter) {
        var vm = this;
        vm.getValuesGroupByMetricId = getValuesGroupByMetricId;
        vm.removeParameter = removeParameter;
        vm.addParameter = addParameter;
        vm.save = save;
        vm.groupsParameters = getGroupsParameters();

        function save(form) {
            vm.valueObject.parameters = vm.groupsParameters[vm.selectedMetricId];

            if (form.$invalid) {
                return;
            }

            this.onSave({valueObject: vm.valueObject, metricId: vm.selectedMetricId, form: form});
        }

        function removeParameter(index) {
            vm.groupsParameters[vm.selectedMetricId].splice(index, 1);
        }

        function addParameter() {
            // values group parameters are missing for this metric
            if (vm.groupsParameters[vm.selectedMetricId] == undefined) {
                vm.groupsParameters[vm.selectedMetricId] = [];
            }
            vm.groupsParameters[vm.selectedMetricId].push({});
        }

        function getGroupsParameters() {
            var parameters = {};

            // get parameters for every values group
            angular.forEach(vm.valuesGroups, function(valuesGroup) {
                var groupParameters = [];
                // iterate over all group parameters
                // each value object have to contains all parameters
                angular.forEach(valuesGroup.parameterNames, function(parameterName) {
                    var parameterObject = $filter('getByProperty')('name', parameterName, vm.valueObject.parameters);
                    if (!parameterObject) {
                        // value object does not contain parameter - create it
                        groupParameters.push({name: parameterName});
                    } else {
                        // value object contains parameter - add it
                        groupParameters.push({name: parameterName, value: parameterObject.value});
                    }
                });
                parameters[valuesGroup.metricId] = groupParameters;
            });
            return parameters;
        }

        function getValuesGroupByMetricId(metricId) {
            return $filter('getByProperty')('metricId', metricId, vm.valuesGroups);
        }
    }
})();