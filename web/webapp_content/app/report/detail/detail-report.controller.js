(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.detail')
        .controller('DetailReportController', DetailReportController);

    function DetailReportController(_report, reportService, reportModalService, $state, Page) {
        var vm = this;
        vm.report = _report;
        vm.editReport = editReport;
        vm.removeReport = removeReport;
        vm.updateDetail = updateDetail;
        vm.showPermissions = showPermissions;
        Page.setTitle(vm.report.name + " | Report detail");

        function editReport() {
            $state.go('app.editReportWizard', {id: vm.report.id});
        }

        function removeReport() {
            var modalInstance = reportModalService.removeReport(vm.report);

            modalInstance.result.then(function () {
                $state.go('app.reportOverview');
            });
        }

        function updateDetail() {
            reportService.getById(vm.report.id).then(function(response) {
                vm.report = response;
            });
        }

        function showPermissions() {
            reportModalService.showPermissions(vm.report.permissions);
        }
    }
})();