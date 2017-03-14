(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .component('prReportInformationStep', {
            bindings: {
                data: '=',
                currentStep: '='
            },
            controller: ReportInformationWizardStep,
            controllerAs: 'vm',
            templateUrl: 'app/report/wizard/components/report_information/report-information.view.html'
        });

    function ReportInformationWizardStep($scope, wizardService, validationHelper) {
        var vm = this;

        $scope.$on('next-step', function(event, step) {
            if (step.stepId === 'information') {
                validate();
            }
        });

        function validate() {
            wizardService.validateReportInfoStep(vm.data).then(function() {
                vm.currentStep = 'Configuration';
            }, function(errorResponse) {
                validationHelper.setFormErrors(errorResponse, vm.wizardInfoStep);
                return false;
            });
        }
    }
})();