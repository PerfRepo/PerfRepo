(function() {
    'use strict';

    angular
        .module('org.perfrepo.test', [
            'org.perfrepo.test.overview',
            'org.perfrepo.test.detail',
            'org.perfrepo.test.create',
            'org.perfrepo.test.edit',
            'org.perfrepo.test.remove',

            'org.perfrepo.metric',
            'org.perfrepo.alert'
        ]);
})();