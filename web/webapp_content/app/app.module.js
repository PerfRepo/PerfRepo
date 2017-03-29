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
        .constant('API_URL', 'rest/json')
        .constant('API_TEST_URL', 'rest/json/tests')
        .constant('API_METRIC_URL', 'rest/json/metrics')
        .constant('API_ALERT_URL', 'rest/json/alerts')
        .constant('API_TEST_EXECUTION_URL', 'rest/json/test-executions')
        .constant('API_REPORT_URL', 'rest/json/reports')
        .constant('API_USER_URL', 'rest/json/users')
        .constant('API_GROUP_URL', 'rest/json/groups')
        .constant('API_COMPARISON_SESSION_URL', 'rest/json/comparison-session');
})();