(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.create')
        .controller('CreateTestController', CreateTestController);

    function CreateTestController(_groups, testService, validationHelper, $uibModalInstance) {
        var vm = this;
        vm.test = {};
        vm.groups = _groups;
        vm.save = save;
        vm.cancel = cancel;

        function save(test, form) {
            testService.save(test)
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