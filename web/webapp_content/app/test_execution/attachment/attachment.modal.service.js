(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.attachment')
        .service('testExecutionAttachmentModalService', TestExecutionAttachmentModalService);

    function TestExecutionAttachmentModalService($uibModal) {

        return {
            createAttachment: createAttachment,
            removeAttachment: removeAttachment
        };

        function removeAttachment(filename, attachmentId) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/attachment/remove_modal/remove-attachment.view.html',
                controller: 'RemoveTestExecutionAttachmentController',
                controllerAs: 'vm',
                size: 'sm',
                resolve : {
                    _filename: function () {
                        return filename;
                    },
                    _attachmentId: function () {
                        return attachmentId;
                    }
                }
            });
        }

        function createAttachment(testExecutionId) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test_execution/attachment/create_modal/create-attachment.view.html',
                controller: 'CreateTestExecutionAttachmentController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _testExecutionId: function () {
                        return testExecutionId;
                    }
                }
            });
        }
    }
})();