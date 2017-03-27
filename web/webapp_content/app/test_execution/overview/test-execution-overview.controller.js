(function() {

    angular
        .module('org.perfrepo.testExecution.overview')
        .controller('TestExecutionOverviewController', TestExecutionOverviewController);

    function TestExecutionOverviewController(_initialSearchResult, _searchCriteria, Page) {
        var vm = this;
        vm.initialSearchResult = _initialSearchResult;
        vm.initialSearchCriteria = _searchCriteria;
        Page.setTitle("Test executions search");
    }
})();
