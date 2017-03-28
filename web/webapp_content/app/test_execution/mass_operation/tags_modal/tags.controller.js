(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.massOperation')
        .controller('TagsMassOperationController', TagsMassOperationController);

    function TagsMassOperationController(_testExecutionIds, _mode, testExecutionMassOperationService,
                                            validationHelper, $uibModalInstance, $scope) {
        var vm = this;
        vm.testExecutionIds = _testExecutionIds;
        vm.mode = _mode;
        vm.save = save;
        vm.cancel = cancel;

        $scope.$watch('vm.tagsObject.length', function() {
            if (vm.tagsObject != undefined) {
                vm.tags = vm.tagsObject.map(function (item) {
                    return item.text;
                });
            }
        });

        function save(form) {
            if (vm.mode == 'add') {
                testExecutionMassOperationService.addTags(vm.tags, vm.testExecutionIds).then(function () {
                    $uibModalInstance.close();
                }, function (errorResponse) {
                    validationHelper.setFormErrors(errorResponse, form);
                });
            }

            if (vm.mode == 'remove') {
                testExecutionMassOperationService.removeTags(vm.tags, vm.testExecutionIds).then(function () {
                    $uibModalInstance.close();
                }, function (errorResponse) {
                    validationHelper.setFormErrors(errorResponse, form);
                });
            }
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();