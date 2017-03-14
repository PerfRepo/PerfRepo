(function() {
    'use strict';

    angular
        .module('org.perfrepo.base')
        .directive('prServerValidation', ServerValidation);

    function ServerValidation() {
        return {
            restrict: 'A',
            require: 'ngModel',

            link: function(scope, element, attrs, ctrl){
                var messageElement = angular.element('<span class="help-block"></span>');
                var parentElement = element.parent();
                messageElement.addClass("hide");

                element.after(messageElement);

                element.on('change keyup', function(){
                    ctrl.$setValidity('server', true);
                    setFieldValid();
                });

                scope.$watch(ctrl.$$parentForm.$name + '["' + ctrl.$name + '"].$valid', function(valid) {
                    if (valid === false) {
                        setFieldInvalid();
                    } else {
                        setFieldValid();
                    }
                });

                function setFieldValid() {
                    messageElement.addClass("hide");
                    parentElement.removeClass('has-error');
                }

                function setFieldInvalid() {
                    messageElement.html(ctrl.$serverValidationMessage);
                    messageElement.removeClass("hide");
                    parentElement.addClass('has-error');
                }
            }
        }
    }
})();