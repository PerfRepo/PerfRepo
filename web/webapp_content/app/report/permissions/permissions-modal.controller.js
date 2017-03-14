(function() {
    'use strict';

    angular
        .module('org.perfrepo.report')
        .controller('ShowPermissionsReportController', ShowPermissionsReportController);

    function ShowPermissionsReportController(_permissions, $uibModalInstance) {
        var vm = this;
        vm.permissions = _permissions;
        vm.close = close;

        function close() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();