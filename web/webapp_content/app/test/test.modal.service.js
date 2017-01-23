(function() {
    'use strict';

    angular
        .module('org.perfrepo.test')
        .service('testModalService', TestModalService);

    function TestModalService($modal) {

        return {
            createTest: createTest,
            editTest: editTest,
            removeTest: removeTest
        };

        function createTest() {
            return $modal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test/create/create-test.view.html',
                controller: 'CreateTestController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _groups: function(groupService) {
                        return groupService.getUserGroups();
                    }
                }
            });
        }

        function editTest(id) {
            return $modal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test/edit/edit-test.view.html',
                controller: 'EditTestController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _groups: function(groupService) {
                        return groupService.getUserGroups();
                    },
                    _test: function (testService) {
                        return testService.getById(id);
                    }
                }
            });
        }

        function removeTest(test) {
            return $modal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/test/remove/remove-test.view.html',
                controller: 'RemoveTestController',
                controllerAs: 'vm',
                size: 'sm',
                resolve : {
                    _test: function () {
                        return test;
                    }
                }
            });
        }
    }
})();