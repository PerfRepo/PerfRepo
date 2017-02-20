/**
 *
 * @author Jiri Grunwald (grunwjir@gmail.com)
 */
(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.detail')
        .controller('DetailTestController', DetailTestController);

    function DetailTestController(_test, _isUserAlertsSubscriber, testService, testModalService,
                                  testExecutionModalService, $state) {
        var vm = this;
        vm.test = _test;
        vm.isUserAlertsSubscriber = _isUserAlertsSubscriber;
        vm.alertButtonEnabled = true;
        vm.editTest = editTest;
        vm.removeTest = removeTest;
        vm.createTestExecution = createTestExecution;
        vm.showTestExecutions = showTestExecutions;
        vm.subscribeAlerts = subscribeAlerts;
        vm.unsubscribeAlerts = unsubscribeAlerts;
        vm.updateDetail = updateDetail;

        function createTestExecution() {
            var modalInstance = testExecutionModalService.createTestExecution(vm.test.id);

            modalInstance.result.then(function (id) {
                $state.go('app.testExecutionDetail', {id: id});
            });
        }

        function showTestExecutions() {
            $state.go('app.testExecutionOverview', {initialFilters: [{id: 'uid', value: vm.test.uid, title: 'Test UID'}]});
        }

        function subscribeAlerts() {
            vm.alertButtonEnabled = false;
            testService.subscribeAlerts(vm.test.id).then(function() {
                vm.isUserAlertsSubscriber = true;
                vm.alertButtonEnabled = true;
            });
        }

        function unsubscribeAlerts() {
            vm.alertButtonEnabled = false;
            testService.unsubscribeAlerts(vm.test.id).then(function() {
                vm.isUserAlertsSubscriber = false;
                vm.alertButtonEnabled = true;
            });
        }

        function editTest() {
            var modalInstance = testModalService.editTest(vm.test.id);

            modalInstance.result.then(function () {
                updateDetail();
            });
        }

        function removeTest() {
            var modalInstance = testModalService.removeTest(vm.test);

            modalInstance.result.then(function () {
                $state.go('app.testOverview');
            });
        }

        function updateDetail() {
            testService.getById(vm.test.id).then(function(response) {
                vm.test = response;
            });
        }
    }
})();