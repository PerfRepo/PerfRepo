(function() {
    'use strict';

    angular.module('org.perfrepo.report')
        .config(config);

    function config($stateProvider) {
        $stateProvider.state('app.report', {
            url: 'report',
            templateUrl: 'app/report/report.view.html',
            controller: 'ReportController',
            controllerAs: 'report'
        });
    }
})();