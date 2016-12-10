(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.edit')
        .controller('EditTestController', EditTestController);

    function EditTestController($state, testService, test, groups, metrics) {
        var vm = this;
        vm.test = test;
        vm.groups = groups;
        vm.metrics = metrics;
        vm.update = update;

        function update(test) {
            testService.update(test)
                .then(function () {
                    $state.go('app.testSearch');
                });
        }
    }
})();