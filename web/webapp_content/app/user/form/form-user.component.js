(function() {
    'use strict';

    angular
        .module('org.perfrepo.user')
        .component('userForm', {
            bindings: {
                user: '<',
                onSave: '&',
                onCancel: '&'
            },

            controller: FormUserController,
            controllerAs: 'vm',
            templateUrl: 'app/user/form/form-user.view.html'
        });

    function FormUserController() {
        var vm = this;
        vm.save = save;

        function save(form) {
            vm.onSave({user: vm.user, form: form});
        }
    }
})();