(function() {
    'use strict';

    angular
        .module('org.perfrepo.testExecution.detail.value')
        .controller('ShowMultiValueDataController', ShowMultiValueDataController);

    function ShowMultiValueDataController(_valuesGroup, _metric, _testExecutionId,
                                          _onUpdateTable, $uibModalInstance) {
        var vm = this;
        vm.valuesGroup = _valuesGroup;
        vm.metric = _metric;
        vm.testExecutionId = _testExecutionId;
        vm.onUpdateTable = _onUpdateTable;
        vm.cancel = cancel;

        function cancel() {
            vm.onUpdateTable().then(function () {
                $uibModalInstance.dismiss('cancel');
            });
        }
    }
})();