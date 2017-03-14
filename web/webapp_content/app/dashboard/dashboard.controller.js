(function() {
    'use strict';

    angular
        .module('org.perfrepo.dashboard')
        .controller('DashboardController', DashboardController);

    function DashboardController(Notifications, Page) {
        var vm = this;
        vm.hello = "dashboard";
        vm.addError = addError;
        Page.setTitle("Dashboard");

        function addError() {
            Notifications.error("Error");
        }
    }
})();