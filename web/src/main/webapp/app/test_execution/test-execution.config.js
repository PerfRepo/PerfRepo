(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution')
        .config(config);

    function config($stateProvider) {
        $stateProvider.state('app.testExecution', {
            url: 'test-execution',
            templateUrl: 'app/test_execution/test-execution.view.html',
            controller: 'TestExecutionController',
            controllerAs: 'testExecution'
        });
    }
})();