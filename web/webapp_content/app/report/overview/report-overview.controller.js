(function() {

    angular
        .module('org.perfrepo.report')
        .controller('ReportOverviewController', ReportOverviewController);

    function ReportOverviewController(_searchCriteria, _initialSearchResult, reportModalService, $state, Page) {
        var vm = this;
        vm.initialSearchResult = _initialSearchResult;
        vm.searchCriteria = _searchCriteria;
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