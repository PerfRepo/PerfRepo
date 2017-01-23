(function() {
    'use strict';

    angular
        .module('org.perfrepo.alert.form')
        .component('alertForm', {
            bindings: {
                alert: '=alertParam',
                metrics: '=',
                onSave: '&',
                onCancel: '&'
            },
            controller: FormAlertController,
            controllerAs: 'vm',
            templateUrl: 'app/alert/form/components/form-alert.view.html'
        });

    function FormAlertController($scope) {
        var vm = this;
        vm.save = save;

        if (vm.alert.tags != undefined) {
            vm.tagsObject = [];
            vm.alert.tags.forEach(function(value) {
                vm.tagsObject.push({text: value});
            });
        }

        if (vm.alert.links != undefined) {
            vm.linksObject = [];
            vm.alert.links.forEach(function(value) {
                vm.linksObject.push({text: value});
            });
        }

        $scope.$watch('vm.tagsObject.length', function() {
            if (vm.tagsObject != undefined) {
                vm.alert.tags = vm.tagsObject.map(function (item) {
                    return item.text;
                });
            }
        });

        $scope.$watch('vm.linksObject.length', function() {
            if (vm.linksObject != undefined) {
                vm.alert.links = vm.linksObject.map(function (item) {
                    return item.text;
                });
            }
        });

        function save(form) {
            if (form.$invalid) {
                return;
            }

            this.onSave({alert: vm.alert, form: form});
        }
    }
})();