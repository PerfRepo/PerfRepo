(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.remove')
        .controller('RemoveTestController', RemoveTestController);

    function RemoveTestController(_test, testService, $uibModalInstance) {
        var vm = this;
        vm.test = _test;
        vm.remove = remove;
        vm.cancel = cancel;

        function remove() {
            testService.remove(vm.test.id).then(function () {
                $uibModalInstance.close(vm.test);
            });
        }

        function cancel() {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();