(function() {

    var CreateTestController = function($state, testService) {

        this.test = {groupId : "perfrepouser"};

        this.save = function(test) {
            testService.save(test)
                .then(function () {
                    $state.go('app.test');
                    console.log("TEST CREATED");
                });
        };
    };

    angular.module('org.perfrepo.test.create',
        [
            'org.perfrepo.test.form'
        ])
        .controller('CreateTestController', CreateTestController);

})();