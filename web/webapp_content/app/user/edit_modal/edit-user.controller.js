(function() {
    'use strict';

    angular
        .module('org.perfrepo.user')
        .controller('EditUserController', EditUserController);

    function EditUserController(_user, userService, validationHelper, $uibModalInstance) {
        var vm = this;
        vm.user = _user;
        vm.save = save;
        vm.cancel = cancel;

        function save(user, form) {
            userService.update(user).then(function () {
                $uibModalInstance.close(user);
            }, function(errorResponse) {
                validationHelper.setFormErrors(errorResponse, form);
            });
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();