(function() {
    'use strict';

    angular
        .module('org.perfrepo.dashboard')
        .controller('DashboardController', DashboardController);

    function DashboardController() {
        var vm = this;
        vm.hello = "dashboard";
    }
})();