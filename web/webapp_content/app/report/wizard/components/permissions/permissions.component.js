(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .component('prReportPermissionsStep', {
            bindings: {
                data: '=',
                users: '<',
                groups: '<',
                currentStep: '='
            },
            controller: ReportPermissionsWizardStep,
            controllerAs: 'vm',
            templateUrl: 'app/report/wizard/components/permissions/permissions.view.html'
        });

    function ReportPermissionsWizardStep($scope, wizardService, reportService, validationHelper, $filter, $state) {
        var vm = this;
        vm.options = wizardService.getPermissionOptions();
        vm.permissionTypes = permissionTypes;
        vm.addPermission = addPermission;
        vm.removePermission = removePermission;

        $scope.$on('next-step', function(event, step) {
            if (step.stepId === 'permissions') {
                validate();
            }
        });

        function validate() {
            wizardService.validateReportPermissionStep(vm.data).then(function() {
               saveReport();
            }, function(errorResponse) {
                vm.formErrors = validationHelper.prepareGlobalFormErrors(errorResponse);
                validationHelper.setFormErrors(errorResponse, vm.wizardPermissionStep);
                return false;
            });
        }

        function permissionTypes(permissionLevel) {
            var option = $filter('getByProperty')('level', permissionLevel, vm.options);
            if (option == null) {
                return [];
            } else {
                return option.types;
            }
        }

        function addPermission() {
            if (vm.data.permissions == undefined) {
                vm.data.permissions = [];
            }
            vm.data.permissions.push({});
        }

        function removePermission(index) {
            vm.data.permissions.splice(index, 1);
        }

        function saveReport() {
            if (vm.data.id == undefined) {
                createReport();
            } else {
                updateReport();
            }
        }

        function createReport() {
            reportService.create(vm.data).then(function (id) {
                $state.go('app.reportDetail', {id: id});
            }, function(errorResponse) {
                //validationHelper.setFormErrors(errorResponse, form);
            });
        }

        function updateReport() {
            reportService.update(vm.data).then(function (report) {
                $state.go('app.reportDetail', {id: report.id});
            }, function(errorResponse) {
                //validationHelper.setFormErrors(errorResponse, form);
            });
        }
    }
})();