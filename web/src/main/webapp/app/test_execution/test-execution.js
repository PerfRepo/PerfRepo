(function() {
    'use strict';

    var TestExecutionController = function() {
        var vm = this;
        vm.hello = "test execution";
    };

    angular.module('org.perfrepo.testExecution',
        [
            'ui.router'
        ])

        .controller('TestExecutionController', TestExecutionController)

        .config(function($stateProvider) {
            $stateProvider.state('app.testExecution', {
                url: 'test-execution',
                templateUrl: 'app/test_execution/test-execution.html',
                controller: 'TestExecutionController',
                controllerAs: 'testExecution'
            });
        });

})();