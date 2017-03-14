(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.form')
        .component('testExecutionForm', {
            bindings: {
                testExecution: '=',
                onSave: '&',
                onCancel: '&'
            },
            controller: FormTestExecutionController,
            controllerAs: 'vm',
            templateUrl: 'app/test_execution/components/form_test_execution/form-test-execution.view.html'
        });

    function FormTestExecutionController($scope) {
        var vm = this;
        vm.save = save;
        vm.dateOptions = {
            extraFormats: [moment.ISO_8601],
            format: 'MMM DD, YYYY HH:mm',
            sideBySide: true,
            showClose: true,
            allowInputToggle: true
        };

        if (vm.testExecution.tags != undefined) {
            vm.tagsObject = [];
            vm.testExecution.tags.forEach(function(value) {
                vm.tagsObject.push({text: value});
            });
        }

        $scope.$watch('vm.tagsObject.length', function() {
            if (vm.tagsObject != undefined) {
                vm.testExecution.tags = vm.tagsObject.map(function (item) {
                    return item.text;
                });
            }
        });

        function save(form) {
            if (form.$invalid) {
                return;
            }
            this.onSave({testExecution: vm.testExecution, form:form});
        }

    }
})();