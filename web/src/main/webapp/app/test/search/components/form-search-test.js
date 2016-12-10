(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.search')
        .component('searchTestForm', {
            bindings: {
                searchParams: '=',
                onSearch: '&'
            },

            controller: SearchTestFormController,
            controllerAs: 'vm',
            templateUrl: 'app/test/search/components/form-search-test.view.html'
        });

    function SearchTestFormController() {
        var vm = this;
        vm.search = search;

        function search(form) {
            if (form.$invalid) {
                return;
            }
            this.onSearch({searchParams: this.searchParams});
        }
    }
})();