(function() {
    'use strict';

    var EditTestController = function($state, testService, test, userGroups, metrics) {

        this.test = test;
        this.groups = userGroups;
        this.metrics = metrics;

        this.update = function(test) {
            testService.update(test)
                .then(function () {
                    $state.go('app.testSearch');
                });
        };
    };

    angular.module('org.perfrepo.test.edit',
        [
            'org.perfrepo.test.form'
        ])
        .controller('EditTestController', EditTestController);

})();