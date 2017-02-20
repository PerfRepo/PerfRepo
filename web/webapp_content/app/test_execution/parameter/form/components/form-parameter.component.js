(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.parameter')
        .component('testExecutionParameterForm', {
            bindings: {
                executionParameter: '<',
                onSave: '&',
                onCancel: '&'
            },
            controller: FormTestExecutionParameterController,
            controllerAs: 'vm',
            templateUrl: 'app/test_execution/parameter/form/components/form-parameter.view.html'
        });

    function FormTestExecutionParameterController() {
        var vm = this;
        vm.save = save;

        function save(form) {
            if (form.$invalid) {
                return;
            }

            this.onSave({parameter: vm.executionParameter, form: form});
        }
    }
})();