(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution')
        .controller('TestExecutionController', TestExecutionController);

    function TestExecutionController() {
        var vm = this;
        vm.hello = "test execution";
    }
})();