(function() {
    'use strict';

    angular
        .module('org.perfrepo')
        .factory('Page', PageFactory);

    function PageFactory($rootScope) {
        return {
            setTitle: function(newTitle) {
                $rootScope.title = newTitle;
            }
        };
    }
})();