(function() {

    var SearchTestController = function(testService) {

        this.page = 1;
        this.searchParams = {name: 'echo*', groupFilter: 'ALL_GROUPS', orderBy: 'NAME_ASC', limit: 5, offset: 0};

        this.search = function() {
            testService.search(this.searchParams)
                .then(function(response) {
                    this.tests = response.data;
                }.bind(this));
        };

    };

    angular.module('org.perfrepo.test.search',
        [
            'org.perfrepo.test.search.components'
        ])

        .controller('SearchTestController', SearchTestController);

})();