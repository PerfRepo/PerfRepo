(function() {
    'use strict';

    var CreateTestController = function($state, testService, userGroups, metrics) {

        this.test = {};
        this.groups = userGroups;
        this.metrics = metrics;

        if(this.groups != undefined && this.groups.length > 0) {
            this.test.group = this.groups[0];
        }

        this.save = function(test) {
            testService.save(test)
                .then(function () {
                    $state.go('app.testSearch');
                });
        };
    };

    angular.module('org.perfrepo.test.create',
        [
            'org.perfrepo.test.form'
        ])
        .controller('CreateTestController', CreateTestController);

})();