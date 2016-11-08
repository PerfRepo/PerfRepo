(function() {
    'use strict';

    var ReportController = function() {
        var vm = this;
        vm.hello = "report";
    };

    angular.module('org.perfrepo.report',
        [
            'ui.router'
        ])

        .controller('ReportController', ReportController)

        .config(function($stateProvider) {
            $stateProvider.state('app.report', {
                url: 'report',
                templateUrl: 'app/report/report.html',
                controller: 'ReportController',
                controllerAs: 'report'
            });
        });

})();