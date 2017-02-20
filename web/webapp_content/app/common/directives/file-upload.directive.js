(function() {
    'use strict';

    angular
        .module('org.perfrepo.base')
        .directive('prFileUpload', FileUpload);

    function FileUpload() {
        return {
            restrict: 'A',
            scope: true,
            link: function (scope, element, attr) {
                element.bind('change', function () {
                    scope.formData = new FormData();
                    scope.formData.append('file', element[0].files[0]);

                    console.log(scope.formData);
                    console.log(element[0].files[0]);
                });
            }
        }
    }
})();
