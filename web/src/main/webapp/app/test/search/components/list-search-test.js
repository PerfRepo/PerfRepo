(function() {
    'use strict';

    var SearchTestListController = function() {

    };

    angular.module('org.perfrepo.test.search.components')

        .component('searchTestList', {
            bindings: {
                tests: '='
            },

            controller: SearchTestListController,
            controllerAs: 'vm',
            templateUrl: 'app/test/search/components/list-search-test.html'
        });
})();