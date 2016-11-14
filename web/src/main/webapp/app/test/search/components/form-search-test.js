(function() {
    'use strict';

    var SearchTestFormController = function() {

        this.search = function(form) {
            if (form.$invalid) {
                return;
            }

            this.onSearch({searchParams: this.searchParams});
        };
    };

    angular.module('org.perfrepo.test.search.components',
        [

        ])
        .component('searchTestForm', {
            bindings: {
                searchParams: '=',
                onSearch: '&'
            },

            controller: SearchTestFormController,
            controllerAs: 'vm',
            templateUrl: 'app/test/search/components/form-search-test.html'
        });
})();