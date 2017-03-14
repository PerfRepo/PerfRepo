(function() {

    angular
        .module('org.perfrepo.testExecution.overview')
        .controller('TestExecutionOverviewController', TestExecutionOverviewController);

    function TestExecutionOverviewController(_initialSearchResult, _initialFilters, Page) {
        var vm = this;
        vm.initialSearchResult = _initialSearchResult;
        vm.initialFilters = _initialFilters;
        Page.setTitle("Test executions search");
    }
})();
