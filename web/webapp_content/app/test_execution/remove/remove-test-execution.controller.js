(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.remove')
        .controller('RemoveTestExecutionController', RemoveTestExecutionController);

    function RemoveTestExecutionController(_testExecution, testExecutionService, $uibModalInstance) {
        var vm = this;
        vm.testExecution = _testExecution;
        vm.remove = remove;
        vm.cancel = cancel;

        function remove() {
            testExecutionService.remove(vm.testExecution.id).then(function () {
                $uibModalInstance.close(vm.testExecution);
            });
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();