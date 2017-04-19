(function() {
    'use strict';

    angular
        .module('org.perfrepo.dashboard')
        .component('favouriteReportsDashboard', {
            bindings: {
                reports: '<'
            },
            controller: FavouriteReportsController,
            controllerAs: 'vm',
            templateUrl: 'app/dashboard/components/favourite_reports/favourite-reports.view.html'
        });

    function FavouriteReportsController() {
        var vm = this;
    }
})();