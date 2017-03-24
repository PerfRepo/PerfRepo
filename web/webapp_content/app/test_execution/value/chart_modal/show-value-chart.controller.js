(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.value')
        .controller('ShowValueChartController', ShowValueChartController);

    function ShowValueChartController(_values, _parameter, _metricName, $uibModalInstance, $filter) {
        var vm = this;
        vm.groupValues = _values;
        vm.parameter = _parameter;
        vm.metricName = _metricName;
        vm.cancel = cancel;

        $uibModalInstance.rendered.then(function () {
            // TODO very bad - but on-ready doesn't work me
            setTimeout(function() {
                window.dispatchEvent(new Event('resize'));
                vm.showChart = true;
            }, 100);
        });

        function getChartValues() {
            var values = [];
            angular.forEach(vm.groupValues, function (valueObject) {
                var parameterObject = $filter('getByProperty')('name', vm.parameter, valueObject.parameters);

                // put value only if the x-axis value exists
                if (parameterObject != null) {
                    values.push({
                        x: parameterObject.value,
                        y: valueObject.value
                    });
                }
            });

            // sort values by x-axis
            values.sort(function(valueA, valueB) {
                if (valueA.x < valueB.x) {
                    return -1;
                } else if (valueA.x > valueB.x) {
                    return 1;
                } else {
                    return 0;
                }
            });

            return values;
        }

        vm.data = [{
            key: vm.metricName,
            values: getChartValues()
        }];

        vm.options = {
            chart: {
                type: 'lineChart',
                height: 450,
                margin : {
                    top: 40,
                    right: 20,
                    bottom: 40,
                    left: 55
                },
                x: function(d){ return d.x; },
                y: function(d){ return d.y; },
                valueFormat: function(d){
                    return d3.format(',.4f')(d);
                },
                useInteractiveGuideline: true,
                xAxis: {
                    axisLabel: vm.parameter
                },
                yAxis: {
                    axisLabel: vm.metricName,
                    tickFormat: function(d){
                        return d3.format('.02f')(d);
                    },
                    axisLabelDistance: -10
                }
            }
        };

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();