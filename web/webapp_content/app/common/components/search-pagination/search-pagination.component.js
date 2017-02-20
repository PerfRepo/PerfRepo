(function() {
    'use strict';

    angular
        .module('org.perfrepo.base')
        .component('prSearchPagination', {
            bindings: {
                itemsCount: '=',
                currentPage: '=',
                pageSize: '=',
                onChange: '&'
            },

            controller: SearchPaginationController,
            controllerAs: 'vm',
            templateUrl: 'app/common/components/search-pagination/search-pagination.view.html'
        });

    function SearchPaginationController($scope) {
        var vm = this;

        $scope.$watch('vm.pageSize', function(newValue, oldValue) {
            if (newValue !== oldValue) {
                vm.onChange();
            }
        });

        $scope.$watch('vm.currentPage', function(newValue, oldValue) {
            if (newValue !== oldValue) {
                vm.onChange();
            }
        });
    }
})();