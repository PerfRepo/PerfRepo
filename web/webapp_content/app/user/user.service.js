(function() {
    'use strict';

    angular
        .module('org.perfrepo.user')
        .service('userService', UserService);

    function UserService($resource, API_USER_URL) {
        var UserResource = $resource(API_USER_URL + '/:id',
            {
                id: '@id'
            },
            {
                'update': {
                    method: 'PUT',
                    isArray: false,
                    url: API_USER_URL,
                    params: {}
                }
            });

        return {
            getById: getById,
            getAll: getAll,
            update: update
        };

        function getById(id) {
            return UserResource.get({id: id}).$promise;
        }

        function getAll() {
            return UserResource.query().$promise;
        }

        function update(user) {
            return UserResource.update(user).$promise;
        }
    }
})();