(function() {
    'use strict';

    angular
        .module('org.perfrepo.common')
        .directive('selectPicker', SelectPicker);

    function SelectPicker($timeout) {
        return {
            restrict: 'A',
            link: function(scope, element) {
                $timeout(function(){
                    $(element).selectpicker();
                });
            }
        }
    }
})();