(function() {

    angular
        .module('org.perfrepo.test.search')
        .controller('SearchTestController', SearchTestController);

    function SearchTestController(testService, pfViewUtils) {
        var vm = this;

        vm.pagination = {};
        vm.currentPage = 1;
        vm.searchParams = {
            limit: 5,
            offset: 0,
            orderBy: 'NAME_ASC'
        };
        vm.updateSearch = updateSearch;
        update();

        function updateSearch() {
            update();
        }

        function update() {
            vm.searchParams.offset = ((vm.currentPage - 1) * vm.pagination.pageCount) || 0;

            testService.search(vm.searchParams).then(function(response){
                vm.items = response.data;
                vm.filterConfig.resultsCount = response.headers('X-Pagination-Total-Count');
                vm.pagination.pageCount = parseInt(response.headers('X-Pagination-Page-Count'));
                vm.pagination.currentPage = response.headers('X-Pagination-Current-Page');
            });
        }

        var applyFilters = function (filters) {

            vm.searchParams.nameFilters = [];
            vm.searchParams.uidFilters = [];

            filters.forEach(function(filter) {
                if(filter.id == 'name') {
                    vm.searchParams.nameFilters.push(filter.value);
                } else if(filter.id == 'uid') {
                    vm.searchParams.uidFilters.push(filter.value);
                }
            });

            update();

        };

        var filterChange = function (filters) {
            applyFilters(filters);
            vm.toolbarConfig.filterConfig.resultsCount = vm.items.length;
        };

        vm.filterConfig = {
            fields: [
                {
                    id: 'name',
                    title:  'Name',
                    placeholder: 'Filter by Name...',
                    filterType: 'text'
                },
                {
                    id: 'uid',
                    title:  'Uid',
                    placeholder: 'Filter by Uid...',
                    filterType: 'text'
                }
            ],
            //resultsCount: vm.items.length,
            appliedFilters: [],
            onFilterChange: filterChange
        };

        var viewSelected = function(viewId) {
            vm.viewType = viewId
        };

        vm.viewsConfig = {
            views: [pfViewUtils.getListView(), pfViewUtils.getCardView()],
            onViewSelect: viewSelected
        };
        vm.viewsConfig.currentView = vm.viewsConfig.views[0].id;
        vm.viewType = vm.viewsConfig.currentView;


        var sortChange = function (sortFiled, isAscending) {
            vm.searchParams.orderBy = sortFiled.id.toUpperCase() + (isAscending ? "_ASC" : "_DESC");
            update();
        };

        vm.sortConfig = {
            fields: [
                {
                    id: 'name',
                    title:  'Name',
                    sortType: 'alpha'
                },
                {
                    id: 'uid',
                    title: 'Uid',
                    sortType: 'alpha'
                }
            ],
            onSortChange: sortChange
        };

        vm.toolbarConfig = {
            viewsConfig: vm.viewsConfig,
            filterConfig: vm.filterConfig,
            sortConfig: vm.sortConfig
        };

        vm.listConfig = {
            selectionMatchProp: 'name',
            checkDisabled: false
        };
    }
})();