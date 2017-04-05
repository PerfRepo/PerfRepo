(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.preview')
        .controller('PreviewReportController', PreviewReportController);

    function PreviewReportController(_report, $state, Page) {
        var vm = this;
        vm.report = _report;
        vm.saveAsReport = saveAsReport;
        Page.setTitle(vm.report.name + " | Report preview");

        function saveAsReport() {
            $state.go('app.newPreparedReportWizard');
        }
    }
})();