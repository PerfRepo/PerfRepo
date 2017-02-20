(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.edit')
        .controller('EditTestController', EditTestController);

    function EditTestController(_test, _groups, testService, validationHelper, $uibModalInstance) {
        var vm = this;
        vm.test = _test;
        vm.groups = _groups;
        vm.save = save;
        vm.cancel = cancel;

        function save(test, form) {
            testService.update(test)
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