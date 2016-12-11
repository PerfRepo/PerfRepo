(function() {
    'use strict';

    angular
        .module('org.perfrepo.authentication')
        .config(config);

    function config($httpProvider) {
        $httpProvider.interceptors.push(tokenInterceptor);
    }

    function tokenInterceptor($injector) {
        return {
            request: function(config) {
                var authenticationService = $injector.get('authenticationService');
                if (authenticationService.isAuthenticated()) {
                    config.headers = config.headers || {};
                    config.headers['Authorization'] = "Bearer " + authenticationService.getToken();
                }

                return config;
            }
        }
    }
})();