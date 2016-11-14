(function() {
    'use strict';

    var FormTestController = function() {

        this.save = function(form) {
            if (form.$invalid) {
                return;
            }

            this.onSave({test: this.test});
        };
    };

    angular.module('org.perfrepo.test.form',
        [
            'ui.bootstrap',
            'angularTrix'
        ])

        .component('testForm', {
            bindings: {
                metrics: '=',
                userGroups: '=',
                test: '=',
                onSave: '&'
            },

            controller: FormTestController,
            controllerAs: 'vm',
            templateUrl: 'app/test/form/form-test.html'
        });
})();