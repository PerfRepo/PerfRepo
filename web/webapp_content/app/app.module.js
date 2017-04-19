(function() {
    'use strict';

    angular
        .module('org.perfrepo', [
            'ui.router',
            'ngProgress',

            'org.perfrepo.base',
            'org.perfrepo.authentication',
            'org.perfrepo.comparisonSession',
            'org.perfrepo.dashboard',
            'org.perfrepo.group',
            'org.perfrepo.user',
            'org.perfrepo.login',
            'org.perfrepo.test',
            'org.perfrepo.testExecution',
            'org.perfrepo.report'
        ])
        // rest endpoints
        .constant('API_URL', 'rest/json')
        .constant('API_TEST_URL', 'rest/json/tests')
        .constant('API_METRIC_URL', 'rest/json/metrics')
        .constant('API_ALERT_URL', 'rest/json/alerts')
        .constant('API_TEST_EXECUTION_URL', 'rest/json/test-executions')
        .constant('API_REPORT_URL', 'rest/json/reports')
        .constant('API_USER_URL', 'rest/json/users')
        .constant('API_GROUP_URL', 'rest/json/groups')
        .constant('API_COMPARISON_SESSION_URL', 'rest/json/comparison-session')
        .constant('API_DASHBOARD_URL', 'rest/json/dashboard')
        // chart colors
        .constant('CHART_COLORS', ['#0088ce', '#cc0000', '#f0ab00', '#3f9c35',
            '#703fec', '#007a87', '00b9e4', '#ec7a08', '#92d400'])
        // settings
        .constant('SETTINGS', {
            LAST_EXECUTIONS_TEST_SEARCH: 3
        });
})();