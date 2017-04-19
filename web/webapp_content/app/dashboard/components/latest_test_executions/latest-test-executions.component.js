(function() {
    'use strict';

    angular
        .module('org.perfrepo.dashboard')
        .component('latestTestExecutionsDashboard', {
            bindings: {
                executions: '<'
            },
            controller: LatestTestExecutionsController,
            controllerAs: 'vm',
            templateUrl: 'app/dashboard/components/latest_test_executions/latest-test-executions.view.html'
        });

    function LatestTestExecutionsController($scope, comparisonSessionService) {
        var vm = this;
        vm.addToComparison = addToComparison;

        function addToComparison(id) {
            comparisonSessionService.addToComparison([id]).then(function(testExecutions) {
                $scope.$emit('comparisonSessionChange', testExecutions);
            });
        }
    }
})();