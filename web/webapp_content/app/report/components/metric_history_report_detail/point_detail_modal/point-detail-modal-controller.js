(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.detail')
        .controller('LineChartPointDetailController', LineChartPointDetailController);

    function LineChartPointDetailController(_testExecution, _series, $uibModalInstance, comparisonSessionService,
                                            $scope, $state) {
        var vm = this;
        vm.testExecution = _testExecution;
        vm.series = _series;
        vm.close = close;
        vm.goToTestExecution = goToTestExecution;
        vm.goToTest = goToTest;
        vm.addToComparison = addToComparison;


        function close() {
            $uibModalInstance.dismiss('cancel');
        }

        function goToTest() {
            close();
            $state.go('app.testDetail', {id: vm.testExecution.test.id});

        }

        function goToTestExecution() {
            close();
            $state.go('app.testExecutionDetail', {id: vm.testExecution.id});
        }

        function addToComparison() {
            comparisonSessionService.addToComparison([vm.testExecution.id]).then(function(testExecutions) {
                $scope.$parent.$broadcast('comparisonSessionChange', testExecutions);
            });
        }
    }
})();