(function() {
    'use strict';

    var TestController = function() {
        var vm = this;
        vm.hello = "test";
    };

    angular.module('org.perfrepo.test',
        [
            'ui.router'
        ])

        .controller('TestController', TestController)

        .config(function($stateProvider) {
            $stateProvider.state('app.test', {
                url: 'test',
                templateUrl: 'app/test/test.html',
                controller: 'TestController',
                controllerAs: 'test'
            });
        });

})();