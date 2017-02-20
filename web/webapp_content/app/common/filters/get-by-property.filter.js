(function() {
    'use strict';

    angular
        .module('org.perfrepo.base')
        .filter('getByProperty', GetByProperty);

    function GetByProperty() {
        return function(propertyName, propertyValue, array) {
            if (!Array.isArray(array)) {
                return null;
            }

            var i = 0, len = array.length;
            for (; i < len; i++) {
                if (array[i][propertyName] == propertyValue) {
                    return array[i];
                }
            }
            return null;
        }
    }
})();
