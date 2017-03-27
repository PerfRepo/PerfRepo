(function() {

    angular
        .module('org.perfrepo.test.overview')
        .controller('TestOverviewController', TestOverviewController);

    function TestOverviewController(_initialSearchResult, _searchCriteria, testModalService, $state, Page) {
        var vm = this;
        vm.initialSearchResult = _initialSearchResult;
        vm.initialSearchCriteria = _searchCriteria;
        vm.createTest = createTest;
        Page.setTitle("Tests search");

        function createTest() {
            var modalInstance = testModalService.createTest();

            modalInstance.result.then(function (id) {
                $state.go('app.testDetail', {id: id});
            });
        }
    }
})();