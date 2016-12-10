(function() {
    'use strict';

    angular
        .module('org.perfrepo.common', [

        ])
        .directive('comboBox' , ComboBox);

    function ComboBox($timeout) {
        return {
            restrict: 'A',
            link: function(scope, element) {
                $timeout(function(){
                    $(element).combobox();
                });
            }
        }
    }
})();