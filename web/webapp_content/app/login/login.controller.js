(function() {
    'use strict';

    angular
        .module('org.perfrepo.login')
        .controller('LoginController', LoginController);

    function LoginController($state, authenticationService,Page) {
        var vm = this;
        vm.login = login;
        Page.setTitle("Login");

        function login() {
            authenticationService.login(vm.username, vm.password).then(function(response){
                $state.go($state.params.toState, $state.params.toParams);
            }, function() {
                console.log("Bad login!!!");
            });
        }
    }
})();