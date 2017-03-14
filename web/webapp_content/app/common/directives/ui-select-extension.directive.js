(function() {
    'use strict';

    angular
        .module('org.perfrepo.base')
        .directive('uiSelect' , UiSelectExtension);

    function UiSelectExtension() {
        return {
            link: function(scope, element) {
                scope.$watch('$select.ngModel.$modelValue', function() {
                    element.trigger('change');
                });
            }
        }
    }
})();