(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.edit')
        .controller('EditTestExecutionController', EditTestExecutionController);

    function EditTestExecutionController(_testExecution, testExecutionService, validationHelper, $uibModalInstance) {
        var vm = this;
        vm.testExecution = _testExecution;
        vm.save = save;
        vm.cancel = cancel;

        function save(testExecution, form) {
            testExecutionService.update(testExecution)
                .then(function (id) {
                    $uibModalInstance.close(id);
                }, function (errorResponse) {
                    validationHelper.setFormErrors(errorResponse, form);
                });
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();