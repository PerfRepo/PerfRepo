(function() {
    'use strict';

    var EditTestController = function($state, testService, test, userGroups, metrics) {

        this.test = test;
        this.userGroups = userGroups;
        this.metrics = metrics;

        this.update = function(test) {
            testService.update(test)
                .then(function () {
                    $state.go('app.test');
                });
        };
    };

    angular.module('org.perfrepo.test.edit',
        [
            'org.perfrepo.test.form'
        ])
        .controller('EditTestController', EditTestController);

})();