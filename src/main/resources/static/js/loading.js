(function () {
    'use strict';

    var overlay = document.getElementById('loadingOverlay');
    if (!overlay) return;

    var capPath = document.getElementById('loadingCapPath');
    var brandText = document.getElementById('loadingBrandText');
    var reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    var _ready = false;

    function showLoader() {
        if (overlay.classList.contains('loading-visible')) return;

        if (capPath) {
            capPath.classList.remove('drawn');
            capPath.style.strokeDashoffset = '200';
        }
        if (brandText) {
            brandText.classList.remove('revealed');
        }

        void overlay.offsetHeight;

        overlay.classList.add('loading-visible');

        if (reduceMotion) return;

        if (capPath) {
            requestAnimationFrame(function () {
                requestAnimationFrame(function () {
                    capPath.classList.add('drawn');
                });
            });
        }

        setTimeout(function () {
            if (brandText) brandText.classList.add('revealed');
        }, 900);
    }

    function hideLoader() {
        overlay.classList.remove('loading-visible');
    }

    function pageReady() {
        if (_ready) return;
        _ready = true;
        hideLoader();
    }

    // Show loader on initial page load only on home page (has floating icons)
    if (document.querySelector('.hero-icons')) {
        showLoader();
        // Safety: hide overlay after 5s even if pageReady is never signaled
        setTimeout(pageReady, 5000);
    }

    // Fallback: ensure loader hides once everything fully loads
    window.addEventListener('load', pageReady);
    if (document.readyState === 'complete') {
        pageReady();
    }

    window.addEventListener('pageshow', function (e) {
        if (e.persisted) pageReady();
    });

    window.loadingOverlay = { show: showLoader, hide: hideLoader, pageReady: pageReady };

    document.addEventListener('click', function (e) {
        var link = e.target.closest('a');
        if (!link) return;

        var href = link.getAttribute('href');
        if (!href || href === '#' || href.startsWith('http') || href.startsWith('//') || href.startsWith('mailto:') || href.startsWith('tel:')) return;
        if (link.getAttribute('target') === '_blank') return;
        if (link.hasAttribute('data-no-loader')) return;

        e.preventDefault();
        showLoader();
        setTimeout(function () {
            window.location.href = href;
        }, reduceMotion ? 50 : 300);
    });
})();
