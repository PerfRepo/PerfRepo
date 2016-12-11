(function() {
    'use strict';

    angular
        .module('org.perfrepo.authentication')
        .config(config);

    function config($httpProvider) {
        $httpProvider.interceptors.push(unauthorizedInterceptor);
    }

    function unauthorizedInterceptor($injector, $q) {
        return {
            responseError: function (response) {
                return $injector.invoke(function ($state) {
                    if (response.status === 401) {
                        $state.go('login');
                    }
                    return $q.reject(response);
                });
            }
        }
    }
})();