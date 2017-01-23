(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.form')
        .component('testForm', {
            bindings: {
                groups: '=',
                test: '=',
                onSave: '&',
                onCancel: '&'
            },
            controller: FormTestController,
            controllerAs: 'vm',
            templateUrl: 'app/test/components/form_test/form-test.view.html'
        });

    function FormTestController() {
        var vm = this;
        vm.save = save;

        function save(form) {
            if (form.$invalid) {
                return;
            }
            this.onSave({test: vm.test, form:form});
        }

    }
})();