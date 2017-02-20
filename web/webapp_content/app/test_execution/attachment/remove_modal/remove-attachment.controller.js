(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.attachment')
        .controller('RemoveTestExecutionAttachmentController', RemoveTestExecutionAttachmentController);

    function RemoveTestExecutionAttachmentController(_filename, _attachmentId, testExecutionService, $uibModalInstance) {
        var vm = this;
        vm.filename = _filename;
        vm.attachmentId = _attachmentId;
        vm.remove = remove;
        vm.cancel = cancel;

        function remove() {
            //testExecutionService.updateParameters(vm.testExecutionId, params).then(function () {
            //    $uibModalInstance.close(vm.parameter);
            //}, function(errorResponse) {
            //    validationHelper.setFormErrors(errorResponse, form);
            //});
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();