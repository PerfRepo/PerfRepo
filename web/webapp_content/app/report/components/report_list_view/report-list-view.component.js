(function() {
    'use strict';

    angular
        .module('org.perfrepo.report.search')
        .component('reportListView', {
            bindings: {
                items: '<',
                onUpdateList: '&'
            },
            controller: ReportListViewController,
            controllerAs: 'vm',
            templateUrl: 'app/report/components/report_list_view/report-list-view.view.html'
        });

    function ReportListViewController($state,  $templateCache, reportModalService) {
        var vm = this;
        $templateCache.put('report-detail-button-template', '<span class="fa fa-th-list"></span> {{actionButton.name}}');
        $templateCache.put('report-edit-button-template', '<span class="pficon pficon-edit"></span> {{actionButton.name}}');
        $templateCache.put('report-delete-button-template', '<span class="pficon-delete"></span> {{actionButton.name}}');

        vm.actionButtons = getActionButtons();
        vm.menuActions = getMenuActions();
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
                    name: 'Detail',
                    include: 'report-detail-button-template',
                    title: 'Report detail',
                    class: 'btn btn-default',
                    actionFn: showReportDetailAction
                },
                {
                    name: 'Edit',
                    include: 'report-edit-button-template',
                    title: 'Edit report',
                    actionFn: editReportAction
                },
                {
                    name: 'Delete',
                    include: 'report-delete-button-template',
                    title: 'Delete report',
                    actionFn: deleteReportAction
                }
            ];
        }

        function getMenuActions() {
            return [
            ];
        }

        function showReportDetailAction(action, item) {
            $state.go('app.reportDetail', {id: item.id});
        }

        function editReportAction(action, item) {
            $state.go('app.editReportWizard', {id: item.id});
        }

        function deleteReportAction(action, item) {
            var modalInstance = reportModalService.removeReport(item);

            modalInstance.result.then(function () {
                vm.onUpdateList();
            });
        }
    }
})();