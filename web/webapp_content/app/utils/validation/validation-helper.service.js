(function() {
    'use strict';

    angular
        .module('org.perfrepo.utils')
        .service('validationHelper', ValidationHelper);

    function ValidationHelper() {

        return {
            setFormErrors: setFormErrors,
            prepareGlobalFormErrors: prepareGlobalFormErrors
        };

        function setFormErrors(response, form) {
            // better solution? reset whole form
            angular.forEach(form.$error.server, function (ctrl) {
                ctrl.$setValidity('server', true);
            });

            angular.forEach(response.data.fieldErrors, function (error) {
                form[error.name].$setValidity('server', false);
                form[error.name].$serverValidationMessage = error.message;
            });
        }

        function prepareGlobalFormErrors(response) {
            var errors = {};
            angular.forEach(response.data.formErrors, function (error) {
                errors[error.name] = error.message;
            });

            return errors;
        }
    }
})();