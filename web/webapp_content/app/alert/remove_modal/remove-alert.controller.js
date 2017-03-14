(function() {
    'use strict';

    angular
        .module('org.perfrepo.alert.remove')
        .controller('RemoveAlertController', RemoveAlertController);

    function RemoveAlertController(_alert, alertService, $uibModalInstance) {
        var vm = this;
        vm.alert = _alert;
        vm.remove = remove;
        vm.cancel = cancel;

        function remove() {
            alertService.remove(vm.alert.id).then(function () {
                $uibModalInstance.close(vm.alert);
            });
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();