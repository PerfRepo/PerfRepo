(function() {
    'use strict';

    angular
        .module('org.perfrepo.report')
        .controller('RemoveReportController', RemoveReportController);

    function RemoveReportController(_report, reportService, $uibModalInstance) {
        var vm = this;
        vm.report = _report;
        vm.remove = remove;
        vm.cancel = cancel;

        function remove() {
            reportService.remove(vm.report.id).then(function () {
                $uibModalInstance.close(vm.report);
            });
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();