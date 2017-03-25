(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .controller('WizardReportController', WizardReportController);

    function WizardReportController(_data, _state, _groups, _users, _permissions, $scope, Page) {
        var vm = this;
        vm.newReport = (_data.id == undefined);
        vm.backCallback = backCallback;
        vm.nextCallback = nextCallback;
        vm.wizardReady = true;
        vm.wizardDone = false;

        vm.data = _data;
        vm.state = _state;
        vm.users = _users;
        vm.groups = _groups;
        vm.defaultPermissions = _permissions;

        initialize();

        function initialize() {
            if (vm.newReport) {
                vm.pageTitle = 'New report wizard';
                vm.data = {
                    type: 'TABLE_COMPARISON',
                    permissions: vm.defaultPermissions
                };
                Page.setTitle('New report');
            } else {
                vm.pageTitle = 'Edit report wizard';
                Page.setTitle(vm.data.name + ' | Edit report wizard');
            }

            $scope.$on("wizard:stepChanged", function (e, parameters) {
                if (parameters.step.stepId === 'permissions') {
                    vm.nextButtonTitle = "Save";
                } else {
                    vm.nextButtonTitle = "Next >";
                }
            });
        }

        function backCallback() {
            return true;
        }

        function nextCallback(step) {
            $scope.$broadcast('next-step', step);
            return false;
        }
    }
})();