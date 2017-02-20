(function() {
    'use strict';

    angular
        .module('org.perfrepo.alert.edit')
        .controller('EditAlertController', EditAlertController);

    function EditAlertController(_alert, _metrics, alertService, validationHelper, $uibModalInstance) {
        var vm = this;
        vm.metrics = _metrics;
        vm.alert = _alert;
        vm.save = save;
        vm.cancel = cancel;

        function save(alert, form) {
            alertService.update(alert).then(function () {
                $uibModalInstance.close(alert);
            }, function(errorResponse) {
                validationHelper.setFormErrors(errorResponse, form);
            });
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();