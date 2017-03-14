(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.detail')
        .component('permissionsTable', {
            bindings: {
                permissions: '<'
            },
            controller: PermissionsTableController,
            controllerAs: 'vm',
            templateUrl: 'app/report/components/permissions_table/permissions-table.view.html'
        });

    function PermissionsTableController() {
        var vm = this;
    }
})();