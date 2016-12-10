(function() {
    'use strict';

    angular.module('org.perfrepo.report')
        .controller('ReportController', ReportController);

    function ReportController() {
        var vm = this;
        vm.hello = "report";
    }
})();