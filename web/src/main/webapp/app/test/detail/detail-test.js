/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
(function() {
    'use strict';

    var DetailTestController = function(test) {

        this.test = test;

    };

    angular.module('org.perfrepo.test.detail',
        [

        ])
        .controller('DetailTestController', DetailTestController);

})();