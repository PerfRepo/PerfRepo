(function() {
    'use strict';

    angular
        .module('org.perfrepo.base')
        .component('prExecutionParameterValue', {
            bindings: {
                value: '@'
            },

            controller: ExecutionParameterValueCtrl,
            controllerAs: 'vm',
            templateUrl: 'app/common/components/execution_parameter_value/execution-parameter-value.view.html'
        });

    function ExecutionParameterValueCtrl() {
    }
})();