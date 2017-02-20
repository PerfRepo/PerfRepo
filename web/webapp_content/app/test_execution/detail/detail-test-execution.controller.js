/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail')
        .controller('DetailTestExecutionController', DetailTestExecutionController);

    function DetailTestExecutionController(_testExecution, testExecutionService, testExecutionModalService, $state) {
        var vm = this;
        vm.testExecution = _testExecution;
        vm.editTestExecution = editTestExecution;
        vm.updateDetail = updateDetail;
        vm.removeTestExecution = removeTestExecution;

        function editTestExecution() {
            var modalInstance = testExecutionModalService.editTestExecution(vm.testExecution.id);

            modalInstance.result.then(function () {
                updateDetail();
            });
        }

        function removeTestExecution() {
            var modalInstance = testExecutionModalService.removeTestExecution(vm.testExecution);

            modalInstance.result.then(function () {
                $state.go('app.testExecutionOverview');
            });
        }

        function updateDetail() {
            return testExecutionService.getById(vm.testExecution.id).then(function(response) {
                vm.testExecution = response;
            });
        }
    }
})();