(function() {
    'use strict';

    var TestController = function() {
        var vm = this;
        vm.hello = "test";
    };

    angular.module('org.perfrepo.test',
        [
            'ngResource',
            'ui.router',

            'org.perfrepo.test.create'
        ])

        .controller('TestController', TestController)

        .config(function($stateProvider) {
            $stateProvider
                .state('app.test', {
                    url: 'test',
                    templateUrl: 'app/test/test.html',
                    controller: 'TestController',
                    controllerAs: 'test'})

                .state('app.test-create', {
                    url: 'test/create',
                    templateUrl: 'app/test/create/create-test.html',
                    controller: 'CreateTestController',
                    controllerAs: 'vm'
            });
        });

})();