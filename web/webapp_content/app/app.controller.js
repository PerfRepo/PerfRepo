(function() {
    'use strict';

    angular
        .module('org.perfrepo')
        .controller('AppController', AppController);

    function AppController($state, authenticationService) {
        var vm = this;
        vm.logout = logout;
        vm.user = authenticationService.getUser();

        vm.navigationItems = [
            {
                title: "Dashboard",
                iconClass: "fa fa-dashboard",
                href: $state.href('app.dashboard')
            },
            {
                title: "Tests",
                iconClass : "fa fa-shield",
                href: $state.href('app.testOverview')
            },
            {
                title: "Test executions",
                iconClass : "fa fa-flash",
                href: $state.href('app.testExecutionOverview')

            },
            {
                title: "Reports",
                iconClass : "fa fa-line-chart",
                href: $state.href('app.reportOverview')
            }
        ];

        function logout() {
            authenticationService.logout();
        }
    }
})();