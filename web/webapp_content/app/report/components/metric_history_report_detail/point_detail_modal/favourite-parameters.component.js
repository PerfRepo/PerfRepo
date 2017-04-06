(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.detail')
        .component('favouriteParametersTable', {
            bindings: {
                parameters: '<'
            },
            controller: FavouriteParametersTableController,
            controllerAs: 'vm',
            templateUrl: 'app/report/components/metric_history_report_detail/point_detail_modal/favourite-parameters.view.html'
        });

    function FavouriteParametersTableController() {
        var vm = this;
    }
})();