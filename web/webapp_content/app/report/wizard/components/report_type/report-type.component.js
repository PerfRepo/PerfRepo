(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.wizard')
        .component('prReportTypeStep', {
            bindings: {
                data: '=',
                currentStep: '=',
                changeTypeEnabled: '<'
            },
            controller: ReportTypeWizardStep,
            controllerAs: 'vm',
            templateUrl: 'app/report/wizard/components/report_type/report-type.view.html'
        });

    function ReportTypeWizardStep($scope, wizardService) {
        var vm = this;
        vm.reportTypes = wizardService.getReportTypes();

        $scope.$on('next-step', function(event, step) {
            if (step.stepId === 'type') {
                validate();
            }
        });

        function validate() {
            vm.currentStep = 'Info';
        }

    }
})();