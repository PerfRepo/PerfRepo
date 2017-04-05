(function() {
    'use strict';

    angular.module('org.perfrepo.report')
        .config(config);

    function config($stateProvider) {
        $stateProvider
            .state('app.reportDetail', {
                url: 'report/detail/:id',
                templateUrl: 'app/report/detail/detail-report.view.html',
                controller: 'DetailReportController',
                controllerAs: 'vm',
                resolve: {
                    _report: function(reportService, $stateParams) {
                        return reportService.getById($stateParams.id);
                    }
                }
            })

            .state('app.reportPreview', {
                url: 'report/preview',
                templateUrl: 'app/report/preview/preview-report.view.html',
                controller: 'PreviewReportController',
                controllerAs: 'vm',
                resolve: {
                    _report: function(reportService) {
                        return reportService.getTableComparisonPreviewReport();
                    }
                }
            })

            .state('app.editReportWizard', {
                url: 'report/wizard/:id',
                templateUrl: 'app/report/wizard/wizard.view.html',
                controller: 'WizardReportController',
                controllerAs: 'vm',
                resolve: {
                    _data: function(reportService, $stateParams) {
                        return reportService.getById($stateParams.id);
                    },
                    _state: function (_data) {
                        return {
                            changeTypeEnabled: false,
                            contentTitle: 'Report edit',
                            pageTitle: _data.name + ' | Report wizard'
                        }
                    },
                    _users: function(userService) {
                        return userService.getAll();
                    },
                    _groups: function(groupService) {
                        return groupService.getUserGroups();
                    }
                }
            })

            .state('app.newReportWizard', {
                url: 'report/wizard',
                templateUrl: 'app/report/wizard/wizard.view.html',
                controller: 'WizardReportController',
                controllerAs: 'vm',
                resolve: {
                    _permissions: function (reportService) {
                        return reportService.getDefaultPermissions();
                    },
                    _data: function(_permissions) {
                        return {
                            type: 'TABLE_COMPARISON',
                            permissions: _permissions
                        }
                    },
                    _state: function () {
                        return {
                            changeTypeEnabled: true,
                            contentTitle: 'New report',
                            pageTitle: 'Report wizard'
                        }
                    },
                    _users: function(userService) {
                        return userService.getAll();
                    },
                    _groups: function(groupService) {
                        return groupService.getUserGroups();
                    }
                }
            })

            .state('app.newPreparedReportWizard', {
                url: 'report/prepared-wizard',
                templateUrl: 'app/report/wizard/wizard.view.html',
                controller: 'WizardReportController',
                controllerAs: 'vm',
                resolve: {
                    _data: function(reportService) {
                        return reportService.getTableComparisonPreviewReport();
                    },
                    _state: function () {
                        return {
                            changeTypeEnabled: false,
                            contentTitle: 'New prepared report',
                            pageTitle: 'Report wizard'
                        }
                    },
                    _users: function(userService) {
                        return userService.getAll();
                    },
                    _groups: function(groupService) {
                        return groupService.getUserGroups();
                    }
                }
            })

            .state('app.reportOverview', {
                url: 'report/search',
                templateUrl: 'app/report/overview/report-overview.view.html',
                controller: 'ReportOverviewController',
                controllerAs: 'vm',
                resolve: {
                    _searchCriteria: function(reportService) {
                        return reportService.getSearchCriteria();
                    },
                    _initialSearchResult: function(reportService, _searchCriteria) {
                        return reportService.search(_searchCriteria);
                    }
                }
            });
    }
})();