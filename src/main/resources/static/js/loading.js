(function () {
    'use strict';

    var overlay = document.getElementById('loadingOverlay');
    if (!overlay) return;

    var capPath = document.getElementById('loadingCapPath');
    var brandText = document.getElementById('loadingBrandText');
    var reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

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

    if (document.readyState === 'complete') {
        hideLoader();
    } else {
        window.addEventListener('load', hideLoader);
    }
    window.addEventListener('pageshow', function (e) {
        if (e.persisted) hideLoader();
    });

    window.loadingOverlay = { show: showLoader, hide: hideLoader };

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
