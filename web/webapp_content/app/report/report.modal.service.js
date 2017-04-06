(function() {
    'use strict';

    angular
        .module('org.perfrepo.report')
        .service('reportModalService', ReportModalService);

    function ReportModalService($uibModal) {

        return {
            removeReport: removeReport,
            showChartForMultiValueTableComparison: showChartForMultiValueTableComparison,
            showPermissions: showPermissions,
            showLineChartPointDetail: showLineChartPointDetail
        };

        function showPermissions(permissions) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/report/permissions/permissions-modal.view.html',
                controller: 'ShowPermissionsReportController',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _permissions: function () {
                        return permissions;
                    }
                }
            });
        }

        function removeReport(report) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/report/remove/remove-report.view.html',
                controller: 'RemoveReportController',
                controllerAs: 'vm',
                size: 'sm',
                resolve : {
                    _report: function () {
                        return report;
                    }
                }
            });
        }

        function showChartForMultiValueTableComparison(contentCells, headerCells, selectedExecutionIndex, metricName) {
            return $uibModal.open({
                animation: true,
                backdrop: 'static',
                keyboard: false,
                templateUrl: 'app/report/components/table_comparison_report_detail/chart_modal/chart-comparison.view.html',
                controller: 'ChartComparisonController',
                controllerAs: 'vm',
                size: 'lg',
                resolve : {
                    _contentCells: function() {
                        return contentCells;
                    },
                    _headerCells: function () {
                        return headerCells;
                    },
                    _selectedExecutionIndex: function() {
                        return selectedExecutionIndex;
                    },
                    _metricName: function() {
                        return metricName;
                    }

                }
            });
        }

        function showLineChartPointDetail(point, series) {
            return $uibModal.open({
                animation: true,
                templateUrl: 'app/report/components/metric_history_report_detail/point_detail_modal/point-detail-modal.view.html',
                controller: 'LineChartPointDetailController',
                backdropClass: 'line-chart-point-modal',
                windowClass: 'line-chart-point-modal',
                controllerAs: 'vm',
                size: 'md',
                resolve : {
                    _testExecution: function (testExecutionService) {
                        return testExecutionService.getById(point.executionId);
                    },
                    _series: function () {
                        return series;
                    }
                }
            });
        }
    }
})();