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
                //   var authService = $injector.get('authService');
                //   if (authService.isAuthenticated()
                // authService.getToken()
                if (true) {
                    config.headers = config.headers || {};
                    config.headers['Authorization'] = "Bearer CfdAdRgS-security token ;)";
                }

                return config;
            }
        }
    }
})();