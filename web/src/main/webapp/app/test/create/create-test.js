(function() {
    'use strict';

    var CreateTestController = function($state, testService, userGroups) {

        this.test = {};
        this.userGroups = userGroups;

        if(this.userGroups != undefined && this.userGroups.length > 0) {
            this.test.groupId = this.userGroups[0];
        }

        this.save = function(test) {
            testService.save(test)
                .then(function () {
                    $state.go('app.test');
                });
        };
    };

    angular.module('org.perfrepo.test.create',
        [
            'org.perfrepo.test.form'
        ])
        .controller('CreateTestController', CreateTestController);

})();