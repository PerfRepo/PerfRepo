(function() {
    'use strict';

    angular
        .module('org.perfrepo.login')
        .controller('LoginController', LoginController);

    function LoginController($state, loginService) {
        var vm = this;
        vm.login = login;

        function login() {
            loginService.login(vm.username, vm.password).then(function(response){
                if(response != undefined) {
                    $state.go($state.params.toState, $state.params.toParams);
                }
            }, function() {
                console.log("Bad login!!!");
            });
        }
    }
})();