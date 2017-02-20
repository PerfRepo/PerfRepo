(function() {
    'use strict';

    angular
        .module('org.perfrepo.base')
        .filter('fileSize', FileSize);

    function FileSize() {
        return function(bytes) {
            var units = ['bytes', 'kB', 'MB', 'GB', 'TB', 'PT'];
            var number = Math.floor(Math.log(bytes) / Math.log(1024));

            var size = Number((bytes / Math.pow(1024, Math.floor(number))).toFixed(1));
            var unit = units[number];

            return size + ' ' + unit;
        }
    }
})();