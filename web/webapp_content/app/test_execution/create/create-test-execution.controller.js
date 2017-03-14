(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.create')
        .controller('CreateTestExecutionController', CreateTestExecutionController);

    function CreateTestExecutionController(_testId, testExecutionService, validationHelper, $uibModalInstance) {
        var vm = this;
        vm.testExecution = {
            test: {
                id: _testId
            },
            started: new Date()
        };
        vm.save = save;
        vm.cancel = cancel;

        function save(testExecution, form) {
            testExecutionService.save(testExecution)
                .then(function (id) {
                    $uibModalInstance.close(id);
                }, function(errorResponse) {
                    validationHelper.setFormErrors(errorResponse, form);
                });
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();