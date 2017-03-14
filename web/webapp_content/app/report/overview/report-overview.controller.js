(function() {

    angular
        .module('org.perfrepo.report')
        .controller('ReportOverviewController', ReportOverviewController);

    function ReportOverviewController(_initialSearchResult, reportModalService, $state, Page) {
        var vm = this;
        vm.initialSearchResult = _initialSearchResult;
        vm.newReport = newReport;
        Page.setTitle("Reports search");

        function newReport() {
            var modalInstance = reportModalService.newReport();

            modalInstance.result.then(function (id) {
                $state.go('app.reportDetail', {id: id});
            });
        }
    }
})();