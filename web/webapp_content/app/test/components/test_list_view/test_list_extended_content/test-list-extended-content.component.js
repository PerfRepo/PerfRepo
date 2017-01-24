(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.search')
        .component('testListExtendedContent', {
            bindings: {
                item: '<'
            },
            controller: TestListExtendedContentController,
            controllerAs: 'vm',
            templateUrl: 'app/test/components/test_list_view/test_list_extended_content/test-list-extended-content.view.html'
        });

    function TestListExtendedContentController(testExecutionService) {
        var vm = this;

        /*
        $scope.$watch('vm.item.isExpanded', function(value) {

        });
        */


        testExecutionService.searchLastForTest(vm.item.uid).then(function(response) {
            vm.testExecutions = response;
        });



    }
})();