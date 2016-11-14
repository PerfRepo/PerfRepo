(function() {
    'use strict';

    var SelectPicker = function($timeout) {
        return {
            restrict: 'A',
            link: function(scope, element) {
                $timeout(function(){
                    $(element).selectpicker();
                });
            }
        }
    };

    var ComboBox = function($timeout) {
        return {
            restrict: 'A',
            link: function(scope, element) {
                $timeout(function(){
                    $(element).combobox();
                });
            }
        }
    };

    angular.module('org.perfrepo.common',
        [

        ])

        .directive('selectPicker', SelectPicker)
        .directive('comboBox' , ComboBox);


})();