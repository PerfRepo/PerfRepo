(function() {
    'use strict';

    var DashboardController = function() {
        var vm = this;
        vm.hello = "dashboard";
    };

    angular.module('org.perfrepo.dashboard',
        [
            'ui.router'
        ])

        .controller('DashboardController', DashboardController)

        .config(function($stateProvider) {
            $stateProvider.state('app.dashboard', {
                url: 'dashboard',
                templateUrl: 'app/dashboard/dashboard.html',
                controller: 'DashboardController',
                controllerAs: 'dashboard'
            });
        });

})();