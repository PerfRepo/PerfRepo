(function() {
    'use strict';

    angular
        .module('org.perfrepo.report')
        .service('reportService', ReportService);

    function ReportService(wizardService, $http, $resource, API_REPORT_URL) {
        var reportResource = $resource(API_REPORT_URL + '/:id',
            {
                id: '@id'
            },
            {
                'update': {
                    method: 'PUT',
                        isArray: false,
                        url: API_REPORT_URL,
                        params: {}
                },
                'save': {
                    method: 'POST',
                    interceptor: {
                        response: function(response) {
                            return response.headers("Location").split("/").pop();
                        }
                    }
                }
            });

        return {
            search: search,
            getById: getById,
            getDefaultPermissions: getDefaultPermissions,
            getSearchCriteria: getSearchCriteria,
            create: create,
            remove: remove,
            update: update
        };

        function search(searchParams) {
            return $http.post(API_REPORT_URL + '/search', searchParams).then(function(response) {
                return {
                    data : response.data,
                    totalCount : parseInt(response.headers('X-Pagination-Total-Count')),
                    pageCount : parseInt(response.headers('X-Pagination-Page-Count')),
                    currentPage : parseInt(response.headers('X-Pagination-Current-Page'))
                };
            });
        }

        function getSearchCriteria() {
            return $http.get(API_REPORT_URL + '/search-criteria').then(function(response) {
                return response.data;
            });
        }

        function getDefaultPermissions() {
            return $http.get(API_REPORT_URL + '/wizard/default-permissions').then(function(response) {
                return response.data;
            });
        }

        function getById(id) {
            return reportResource.get({id: id}).$promise;
        }

        function create(report) {
            return reportResource.save(wizardService.getPreparedReportRequestData(report)).$promise;
        }

        function remove(id) {
            return reportResource.remove({id: id}).$promise;
        }

        function update(report) {
            return reportResource.update(wizardService.getPreparedReportRequestData(report)).$promise;
        }
    }
})();