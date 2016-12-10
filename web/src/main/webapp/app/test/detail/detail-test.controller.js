/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.detail', [

        ])
        .controller('DetailTestController', DetailTestController);

    function DetailTestController(test) {
        var vm = this;
        vm.test = test;
    }
})();