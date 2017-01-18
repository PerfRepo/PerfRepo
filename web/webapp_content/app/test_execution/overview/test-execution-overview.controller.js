(function() {

    angular
        .module('org.perfrepo.testExecution.overview')
        .controller('TestExecutionOverviewController', TestExecutionOverviewController);

    function TestExecutionOverviewController(_initialSearchResult, _initialFilters) {
        var vm = this;
        vm.initialSearchResult = _initialSearchResult;
        vm.initialFilters = _initialFilters;


    }
})();
