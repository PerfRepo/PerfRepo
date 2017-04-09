(function() {
    'use strict';

    angular
        .module('org.perfrepo.user')
        .service('userModalService', UserModalService);

    function UserModalService($uibModal) {

        return {
            editUser: editUser
        };

        function editUser(userId) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/user/edit_modal/edit-user.view.html',
                controller: 'EditUserController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _user: function (userService) {
                        return userService.getById(userId);
                    }
                }
            });
        }
    }
})();