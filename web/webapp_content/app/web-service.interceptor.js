(function() {
    'use strict';

    angular
        .module('org.perfrepo')
        .config(config);

    function config($httpProvider) {
        $httpProvider.interceptors.push(webServiceInterceptor);
    }

    function webServiceInterceptor($injector, $q, Notifications) {
        return {
            responseError: function (response) {
                return $injector.invoke(function () {
                    if (response.status === 404) {
                        Notifications.error(response.data.message);
                    }

                    if (response.status === 500) {
                        Notifications.error("Internal Server Error");
                    }

                    if (response.status === 400) {
                        Notifications.error("Bad Request");
                    }

                    return $q.reject(response);
                });
            }
        }
    }
})();