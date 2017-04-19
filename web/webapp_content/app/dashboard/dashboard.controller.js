(function() {
    'use strict';

    angular
        .module('org.perfrepo.dashboard')
        .controller('DashboardController', DashboardController);

    function DashboardController(_content, Page) {
        var vm = this;
        vm.content = _content;
        Page.setTitle("Dashboard");
    }
})();