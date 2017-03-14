(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.attachment')
        .component('testExecutionAttachmentsTableView', {
            bindings: {
                attachments: '<',
                testExecutionId: '<',
                onUpdate: '&'
            },
            controller: TestExecutionAttachmentsTableViewController,
            controllerAs: 'vm',
            templateUrl: 'app/test_execution/components/attachments_table_view/attachments-table-view.view.html'
        });

    function TestExecutionAttachmentsTableViewController(testExecutionAttachmentModalService, testExecutionService) {
        var vm = this;
        vm.removeAttachmentAction = removeAttachmentAction;
        vm.downloadAttachmentLink = downloadAttachmentLink;
        vm.addAttachmentAction = addAttachmentAction;

        function addAttachmentAction() {
            var modalInstance = testExecutionAttachmentModalService.createAttachment(vm.testExecutionId);
            modalInstance.result.then(function () {
                updateDetail();
            });
        }

        function removeAttachmentAction(attachment) {
            var modalInstance = testExecutionAttachmentModalService.removeAttachment(attachment.filename, attachment.id);
            modalInstance.result.then(function () {
                vm.onUpdateList();
            });
        }

        function downloadAttachmentLink(attachment) {
            return testExecutionService.downloadAttachmentLink(attachment.id, attachment.hash);
        }
    }
})();