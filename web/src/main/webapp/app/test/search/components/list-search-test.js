(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.search')
        .component('searchTestList', {
            bindings: {
                tests: '='
            },
            controller: SearchTestListController,
            controllerAs: 'vm',
            templateUrl: 'app/test/search/components/list-search-test.view.html'
        });

    function SearchTestListController() {

    }
})();