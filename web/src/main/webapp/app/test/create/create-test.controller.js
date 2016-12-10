(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.create')
        .controller('CreateTestController', CreateTestController);

    function CreateTestController($state, testService, groups, metrics) {
        var vm = this;
        vm.test = {};
        vm.groups = groups;
        vm.metrics = metrics;
        vm.save = save;

        if (vm.groups != undefined && vm.groups.length > 0) {
            vm.test.group = vm.groups[0];
        }

        function save(test) {
            testService.save(test)
                .then(function () {
                    $state.go('app.testSearch');
                });
        }
    }
})();