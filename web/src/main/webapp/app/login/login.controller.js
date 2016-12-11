(function() {
    'use strict';

    angular
        .module('org.perfrepo.login')
        .controller('LoginController', LoginController);

    function LoginController($state, authenticationService) {
        var vm = this;
        vm.login = login;

        function login() {
            authenticationService.login(vm.username, vm.password).then(function(response){
                $state.go($state.params.toState, $state.params.toParams);
            }, function() {
                console.log("Bad login!!!");
            });
        }
    }
})();