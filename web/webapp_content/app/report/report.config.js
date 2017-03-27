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

            .state('app.editReportWizard', {
                url: 'report/wizard/:id',
                templateUrl: 'app/report/wizard/wizard.view.html',
                controller: 'WizardReportController',
                controllerAs: 'vm',
                resolve: {
                    _data: function(reportService, $stateParams) {
                        return reportService.getById($stateParams.id);
                    },
                    _state: function () {
                        return {
                            changeTypeEnabled: false
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
                    _data: function() {
                        return {};
                    },
                    _state: function () {
                        return {
                            changeTypeEnabled: true
                        }
                    },
                    _users: function(userService) {
                        return userService.getAll();
                    },
                    _groups: function(groupService) {
                        return groupService.getUserGroups();
                    },
                    _permissions: function (reportService) {
                        return reportService.getDefaultPermissions();
                    }
                }
            })

            .state('app.newPreparedReportWizard', {
                url: 'report/wizard',
                templateUrl: 'app/report/wizard/wizard.view.html',
                controller: 'WizardReportController',
                controllerAs: 'vm',
                resolve: {
                    _data: function($stateParams) {
                        return $stateParams.data;
                    },
                    _state: function () {
                        return {
                            changeTypeEnabled: false
                        }
                    },
                    _users: function(userService) {
                        return userService.getAll();
                    },
                    _groups: function(groupService) {
                        return groupService.getUserGroups();
                    },
                    _permissions: function (reportService) {
                        return reportService.getDefaultPermissions();
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