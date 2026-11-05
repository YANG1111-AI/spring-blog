/**
 * Shared CSRF support for every jQuery request issued from the blog templates.
 *
 * Spring Security's CSRF protection is enabled, so a mutating request without a valid token is
 * rejected with 403 before it ever reaches a controller. The token reaches the page as a pair of
 * meta tags rendered by the layout head fragments:
 *
 *   <meta name="_csrf" content="..."/>
 *   <meta name="_csrf_header" content="X-CSRF-TOKEN"/>
 *
 * This file installs one global hook that echoes the token back as the configured header for
 * every non-GET/HEAD request, so individual pages never have to care about it.
 *
 * Load order matters: jQuery first, then this file (see templates/back/footer.html and
 * templates/client/header.html). Plain HTML forms are unaffected - Thymeleaf appends the hidden
 * _csrf field itself for every th:action form.
 */
(function ($) {
    'use strict';

    if (!$) {
        // Loaded before jQuery: nothing to patch, and the page would fail anyway.
        if (window.console) {
            console.warn('csrf.js requires jQuery to be loaded first.');
        }
        return;
    }

    var SAFE_METHODS = /^(GET|HEAD|OPTIONS)$/i;

    function metaContent(name) {
        var tag = document.querySelector('meta[name="' + name + '"]');
        return tag ? tag.getAttribute('content') : null;
    }

    $.ajaxSetup({
        beforeSend: function (xhr, settings) {
            var method = settings.type || settings.method || 'GET';
            if (SAFE_METHODS.test(method)) {
                return;
            }
            // Read the tags on every call so a token refreshed by a re-render is always used.
            var token = metaContent('_csrf');
            var headerName = metaContent('_csrf_header') || 'X-CSRF-TOKEN';
            if (token) {
                xhr.setRequestHeader(headerName, token);
            } else if (window.console) {
                console.warn('Missing <meta name="_csrf">: the request will most likely be ' +
                    'rejected with 403.');
            }
        }
    });
})(window.jQuery);
