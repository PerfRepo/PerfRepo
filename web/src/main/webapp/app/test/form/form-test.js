(function() {
    'use strict';

    var testFormController = function() {

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
                userGroups: '=',
                test: '=',
                onSave: '&'
            },

            controller: testFormController,
            controllerAs: 'vm',
            templateUrl: 'app/test/form/form-test.html'
        });
})();