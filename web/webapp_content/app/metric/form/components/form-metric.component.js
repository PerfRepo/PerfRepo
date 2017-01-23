(function() {
    'use strict';

    angular
        .module('org.perfrepo.metric.form')
        .component('metricForm', {
            bindings: {
                metric: '=',
                comparators: '=',
                onSave: '&',
                onCancel: '&'
            },

            controller: FormMetricController,
            controllerAs: 'vm',
            templateUrl: 'app/metric/form/components/form-metric.view.html'
        });

    function FormMetricController() {
        var vm = this;
        vm.save = save;

        function save(form) {
            if (form.$invalid) {
                return;
            }
            this.onSave({metric: vm.metric, form: form});
        }
    }
})();