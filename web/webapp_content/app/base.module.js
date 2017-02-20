(function() {
    'use strict';

    angular
        .module('org.perfrepo.base', [
            'ui.bootstrap',
            'ngSanitize',
            'patternfly',
            'angularTrix',
            'ngResource',
            'ngTagsInput',
            'ui.select',
            'nvd3',

            'org.perfrepo.utils'
        ])
})();