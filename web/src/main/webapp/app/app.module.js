(function() {
    'use strict';

    angular
        .module('org.perfrepo', [
            'patternfly.navigation',
            'ui.router',

            'org.perfrepo.login',
            'org.perfrepo.group',
            'org.perfrepo.common',
            'org.perfrepo.dashboard',
            'org.perfrepo.test',
            'org.perfrepo.testExecution',
            'org.perfrepo.report'
        ])
        .constant('API_URL', 'rest/json');
})();