(function() {
    'use strict';

    angular
        .module('org.perfrepo.test', [
            'ngResource',
            'ui.router',

            'org.perfrepo.test.search',
            'org.perfrepo.test.detail',
            'org.perfrepo.test.create',
            'org.perfrepo.test.edit',
            'org.perfrepo.test.metric'
        ]);
})();