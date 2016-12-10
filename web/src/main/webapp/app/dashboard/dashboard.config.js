(function() {
    'use strict';

    angular
        .module('org.perfrepo.dashboard')
        .config(config);

    function config($stateProvider) {
        $stateProvider.state('app.dashboard', {
            url: 'dashboard',
            templateUrl: 'app/dashboard/dashboard.view.html',
            controller: 'DashboardController',
            controllerAs: 'dashboard'
        });
    }
})();