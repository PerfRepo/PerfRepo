(function() {
    'use strict';

    angular
        .module('org.perfrepo.alert.create')
        .controller('CreateAlertController', CreateAlertController);

    function CreateAlertController(_testId, _metrics, alertService, validationHelper, $modalInstance) {
        var vm = this;
        vm.metrics = _metrics;
        vm.alert = {testId: _testId};
        vm.save = save;
        vm.cancel = cancel;

        function save(alert, form) {
            alertService.create(alert).then(function () {
                $modalInstance.close(alert);
            }, function(errorResponse) {
                validationHelper.setFormErrors(errorResponse, form);
            });
        }

        function cancel() {
            $modalInstance.dismiss('cancel');
        }
    }
})();