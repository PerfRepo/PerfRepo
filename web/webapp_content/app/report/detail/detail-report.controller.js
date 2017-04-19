(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.detail')
        .controller('DetailReportController', DetailReportController);

    function DetailReportController(_report, reportService, reportModalService, $state, Page) {
        var vm = this;
        vm.report = _report;
        vm.favouriteButtonEnabled = true;
        vm.editReport = editReport;
        vm.removeReport = removeReport;
        vm.updateDetail = updateDetail;
        vm.showPermissions = showPermissions;
        vm.markFavourite = markFavourite;
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
        
        function markFavourite() {
            vm.favouriteButtonEnabled = false;
            reportService.markFavourite(vm.report.id, !vm.report.favourite).then(function() {
                vm.favouriteButtonEnabled = true;
                vm.report.favourite = !vm.report.favourite;
            });
        }
    }
})();