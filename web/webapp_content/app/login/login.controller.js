(function() {
    'use strict';

    angular
        .module('org.perfrepo.login')
        .controller('LoginController', LoginController);

    function LoginController($state, authenticationService, validationHelper, Page) {
        var vm = this;
        vm.login = login;
        Page.setTitle("Login");

        function login(form) {
            authenticationService.login(vm.username, vm.password).then(function(response){
                authenticationService.redirectToAttemptedLocation()
                //$state.go($state.params.toState, $state.params.toParams);
            }, function(errorResponse) {
                validationHelper.setFormErrors(errorResponse, form);
            });
        }
    }
})();