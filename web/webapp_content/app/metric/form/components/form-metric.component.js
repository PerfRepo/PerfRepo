(function() {
    'use strict';

    angular
        .module('org.perfrepo.metric.form')
        .component('metricForm', {
            bindings: {
                metric: '=',
                comparators: '<',
                metrics: '<',
                onSave: '&',
                onCancel: '&'
            },

            controller: FormMetricController,
            controllerAs: 'vm',
            templateUrl: 'app/metric/form/components/form-metric.view.html'
        });

    function FormMetricController() {
        var vm = this;
        vm.metricOnSelect = metricOnSelect;
        vm.save = save;

        function metricOnSelect(item) {
            vm.metric.description = item.description;
            vm.metric.comparator = item.comparator;
        }

        function save(form) {
            if (form.$invalid) {
                return;
            }
            this.onSave({metric: vm.metric, form: form});
        }
    }
})();