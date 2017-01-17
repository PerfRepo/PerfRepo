(function() {
    'use strict';

    angular
        .module('org.perfrepo.test.search')
        .component('metricListView', {
            bindings: {
                testId: '<',
                items: '<',
                onUpdateList: '&'
            },
            controller: MetricListViewController,
            controllerAs: 'vm',
            templateUrl: 'app/test/components/metric_list_view/metric-list-view.view.html'
        });

    function MetricListViewController($templateCache, metricModalService) {
        var vm = this;
        vm.testId = this.testId;
        $templateCache.put('metric-edit-button-template', '<span class="pficon pficon-edit"></span> {{actionButton.name}}');
        $templateCache.put('metric-delete-button-template', '<span class="pficon-delete"></span> {{actionButton.name}}');

        vm.actionButtons = getActionButtons();
        vm.config = getListConfig();

        function getListConfig() {
            return {
                showSelectBox: false,
                selectItems: true,
                selectionMatchProp: 'id'
            };
        }

        function getActionButtons() {
            return [
                {
                    name: 'Edit',
                    include: 'metric-edit-button-template',
                    title: 'Edit metric',
                    actionFn: editMetricAction
                },
                {
                    name: 'Delete',
                    include: 'metric-delete-button-template',
                    title: 'Delete metric',
                    actionFn: deleteMetricAction
                }
            ];
        }

        function editMetricAction(action, item) {
            var modalInstance = metricModalService.editMetric(item.id);

            modalInstance.result.then(function () {
                vm.onUpdateList();
            });
        }

        function deleteMetricAction(action, item) {
            var modalInstance = metricModalService.removeMetric(item, vm.testId);

            modalInstance.result.then(function () {
                vm.onUpdateList();
            });
        }
    }
})();