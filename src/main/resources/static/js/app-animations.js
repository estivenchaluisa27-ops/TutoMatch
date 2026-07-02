document.body.classList.remove('gsap-loading');

gsap.registerPlugin(ScrollTrigger);

let mm = gsap.matchMedia();

mm.add("(prefers-reduced-motion: no-preference)", () => {

    // ====================================================================
    // FASE 1 — ALTO IMPACTO
    // ====================================================================

    // --- 1. Dashboard stat counters (admin-dashboard.html) ---
    let dashCards = document.querySelectorAll('.row.g-3.mb-4 .card');
    if (dashCards.length) {
        gsap.from(dashCards, {
            autoAlpha: 0,
            y: 30,
            stagger: 0.08,
            duration: 0.5,
            ease: "power2.out"
        });
        dashCards.forEach(card => {
            let h2 = card.querySelector('h2.fw-bold');
            if (h2) {
                let finalVal = parseFloat(h2.textContent) || 0;
                let obj = { val: 0 };
                h2.textContent = '0';
                gsap.to(obj, {
                    val: finalVal,
                    duration: 1.5,
                    ease: "power2.out",
                    snap: { val: 1 },
                    onUpdate: () => { h2.textContent = obj.val; }
                });
            }
        });
    }

    // --- 2. Category cards scroll reveal (home.html) ---
    let catCards = document.querySelectorAll('.container.mb-5 .card-fade-in');
    if (catCards.length) {
        ScrollTrigger.batch(catCards, {
            onEnter: batch => gsap.from(batch, {
                autoAlpha: 0,
                y: 40,
                duration: 0.5,
                stagger: 0.06,
                ease: "back.out(1.2)",
                overwrite: true
            }),
            start: "top 88%"
        });
    }

    // --- 3. Tutor recommendation cards scroll reveal (home.html) ---
    let tutorCards = document.querySelectorAll('.py-5[style*="background"] .card-fade-in');
    if (tutorCards.length) {
        ScrollTrigger.batch(tutorCards, {
            onEnter: batch => gsap.from(batch, {
                autoAlpha: 0,
                y: 40,
                duration: 0.6,
                stagger: 0.08,
                ease: "back.out(1.2)",
                overwrite: true
            }),
            start: "top 88%"
        });
    }

    // --- 4. Table row entrance (admin tables, mis-tutorias) ---
    let tables = document.querySelectorAll('table.table-hover');
    tables.forEach(table => {
        let rows = table.querySelectorAll('tbody tr');
        if (rows.length) {
            ScrollTrigger.batch(rows, {
                onEnter: batch => gsap.from(batch, {
                    autoAlpha: 0,
                    x: -15,
                    duration: 0.3,
                    stagger: 0.04,
                    ease: "power2.out",
                    overwrite: true
                }),
                start: "top 85%"
            });
        }
    });

    // --- 5. Notification items slide-in ---
    let notifList = document.getElementById('notifList');
    if (notifList) {
        let origAppendChild = notifList.appendChild.bind(notifList);
        notifList.appendChild = function (el) {
            origAppendChild(el);
            gsap.from(el, { autoAlpha: 0, y: -10, duration: 0.3, ease: "power2.out" });
        };
        let notifItems = notifList.querySelectorAll('.notif-item');
        if (notifItems.length) {
            gsap.from(notifItems, {
                autoAlpha: 0,
                y: -10,
                stagger: 0.05,
                duration: 0.3,
                ease: "power2.out"
            });
        }
    }

    // --- 6. Flash messages slide-in ---
    let flashContainer = document.querySelector('.flash-messages-container');
    if (flashContainer) {
        let alerts = flashContainer.querySelectorAll('.alert');
        alerts.forEach(alert => {
            gsap.from(alert, {
                y: -40,
                autoAlpha: 0,
                duration: 0.5,
                ease: "back.out(1)",
                onComplete: () => {
                    gsap.to(alert, {
                        autoAlpha: 0,
                        y: -20,
                        duration: 0.4,
                        delay: 5,
                        ease: "power2.in",
                        onComplete: () => {
                            let bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
                            bsAlert.close();
                        }
                    });
                }
            });
        });
    }

    // ====================================================================
    // FASE 2 — MEDIO IMPACTO
    // ====================================================================

    // --- 7. Search results cards scroll reveal (resultados.html) ---
    let resultCards = document.querySelectorAll('.col-lg-9 .card-fade-in');
    if (resultCards.length) {
        ScrollTrigger.batch(resultCards, {
            onEnter: batch => gsap.from(batch, {
                autoAlpha: 0,
                y: 30,
                duration: 0.5,
                stagger: 0.06,
                ease: "back.out(1.1)",
                overwrite: true
            }),
            start: "top 88%"
        });
    }

    // --- 8. Profile page cards scroll reveal ---
    let profileCards = document.querySelectorAll(
        '[class*="col-lg-4"] > .card, [class*="col-lg-8"] > .card'
    );
    if (profileCards.length) {
        ScrollTrigger.batch(profileCards, {
            onEnter: batch => gsap.from(batch, {
                autoAlpha: 0,
                y: 30,
                duration: 0.5,
                stagger: 0.1,
                ease: "power2.out",
                overwrite: true
            }),
            start: "top 85%"
        });
    }

    // --- 9. Modal GSAP enhancement (admin-materias.html) ---
    let modals = document.querySelectorAll('.modal');
    modals.forEach(modal => {
        modal.addEventListener('show.bs.modal', function () {
            let dialog = this.querySelector('.modal-dialog');
            if (dialog) {
                gsap.fromTo(dialog,
                    { autoAlpha: 0, scale: 0.92, y: 20 },
                    { autoAlpha: 1, scale: 1, y: 0, duration: 0.3, ease: "power2.out", clearProps: "scale" }
                );
            }
        });
    });

    // --- 10. Star rating interaction (formulario-resena.html) ---
    let starRating = document.getElementById('starRating');
    if (starRating) {
        let stars = starRating.querySelectorAll('i');
        stars.forEach(star => {
            star.addEventListener('mouseenter', () => {
                gsap.to(star, { scale: 1.25, duration: 0.2, ease: "back.out(2)", overwrite: "auto" });
            });
            star.addEventListener('mouseleave', () => {
                gsap.to(star, { scale: 1, duration: 0.15, ease: "power2.out", overwrite: "auto" });
            });
            star.addEventListener('click', () => {
                gsap.timeline()
                    .to(star, { scale: 1.35, duration: 0.1, ease: "power2.out" })
                    .to(star, { scale: 1, duration: 0.2, ease: "back.out(3)" });
                stars.forEach(s => {
                    let val = parseInt(s.getAttribute('data-value'));
                    let clickedVal = parseInt(star.getAttribute('data-value'));
                    if (val <= clickedVal) {
                        gsap.to(s, { scale: 1, duration: 0.15, overwrite: "auto" });
                    }
                });
            });
        });
    }

    // --- 11. Avatar bounce entrance ---
    let avatars = document.querySelectorAll('.avatar-circle, .avatar-circle-lg');
    if (avatars.length) {
        ScrollTrigger.batch(avatars, {
            onEnter: batch => gsap.from(batch, {
                scale: 0,
                rotation: -8,
                duration: 0.5,
                stagger: 0.08,
                ease: "back.out(2)",
                overwrite: true
            }),
            start: "top 85%"
        });
    }

    // ====================================================================
    // FASE 3 — BAJO IMPACTO
    // ====================================================================

    // --- 12. Pagination fade (admin-tutores.html) ---
    let pagination = document.querySelector('.pagination');
    if (pagination) {
        gsap.from(pagination, {
            autoAlpha: 0,
            y: 10,
            duration: 0.4,
            delay: 0.3,
            ease: "power2.out"
        });
    }

    // --- 13. Empty states fade-in ---
    let emptyStates = document.querySelectorAll('.text-center.py-5');
    if (emptyStates.length) {
        gsap.from(emptyStates, {
            autoAlpha: 0,
            y: 20,
            duration: 0.5,
            stagger: 0.1,
            ease: "power2.out"
        });
    }

    // --- 14. Tab content slide transition (mis-tutorias.html) ---
    let tabPanes = document.querySelectorAll('.tab-pane');
    tabPanes.forEach(pane => {
        pane.addEventListener('shown.bs.tab', function () {
            let content = this.querySelector('.table-responsive, .text-center.py-5');
            if (content) {
                gsap.from(content, { autoAlpha: 0, x: 20, duration: 0.3, ease: "power2.out" });
            }
        });
    });

});

// --- 15. Hero floating icons (physics-based, outside matchMedia to prevent context revert) ---
(function () {
    let container = document.querySelector('.hero-icons');
    if (!container) return;
    let icons = container.querySelectorAll('.hero-icon');
    if (!icons.length || window.innerWidth < 768) return;

    function startFloating() {
        let rect = container.getBoundingClientRect();
        if (rect.width === 0 || rect.height === 0) {
            requestAnimationFrame(startFloating);
            return;
        }

        let padding = 15;
        let items = [];
        let velocityScale = 1;

        icons.forEach(function (icon) {
            let size = 22 + Math.random() * 32;
            icon.style.width = size + 'px';
            icon.style.height = size + 'px';
            icon.style.top = '0';
            icon.style.left = '0';
            icon.style.opacity = '0';

            let x = padding + Math.random() * (rect.width - size - padding * 2);
            let y = padding + Math.random() * (rect.height - size - padding * 2);

            items.push({
                el: icon,
                size: size,
                x: x,
                y: y,
                vx: (Math.random() - 0.5) * velocityScale * 1.2,
                vy: (Math.random() - 0.5) * velocityScale * 1.2,
                rotation: Math.random() * 360,
                vr: (Math.random() - 0.5) * 3
            });
        });

        // Fade in after positioning — wait for completion before revealing page
        var tl = gsap.timeline({
            onComplete: function () {
                if (window.loadingOverlay && window.loadingOverlay.pageReady) {
                    window.loadingOverlay.pageReady();
                }
            }
        });
        icons.forEach(function (icon) {
            tl.to(icon, { opacity: 0.12, duration: 0.6, ease: "power2.out" }, 0);
        });

        // Physics ticker
        function physicsTick() {
            let w = rect.width;
            let h = rect.height;
            let len = items.length;

            for (let i = 0; i < len; i++) {
                let a = items[i];
                a.x += a.vx;
                a.y += a.vy;
                a.rotation += a.vr;

                // Bounce off container walls
                if (a.x < 0) { a.x = 0; a.vx *= -1; }
                if (a.x > w - a.size) { a.x = w - a.size; a.vx *= -1; }
                if (a.y < 0) { a.y = 0; a.vy *= -1; }
                if (a.y > h - a.size) { a.y = h - a.size; a.vy *= -1; }

                gsap.set(a.el, { x: a.x, y: a.y, rotation: a.rotation });
            }

            // Collision detection between pairs
            for (let i = 0; i < len; i++) {
                for (let j = i + 1; j < len; j++) {
                    let a = items[i];
                    let b = items[j];
                    let dx = (a.x + a.size / 2) - (b.x + b.size / 2);
                    let dy = (a.y + a.size / 2) - (b.y + b.size / 2);
                    let dist = Math.sqrt(dx * dx + dy * dy);
                    let minDist = (a.size + b.size) / 2 + 8;

                    if (dist < minDist && dist > 0) {
                        let nx = dx / dist;
                        let ny = dy / dist;
                        let overlap = minDist - dist;

                        a.x += nx * overlap / 2;
                        a.y += ny * overlap / 2;
                        b.x -= nx * overlap / 2;
                        b.y -= ny * overlap / 2;

                        // Elastic collision response
                        let dvx = a.vx - b.vx;
                        let dvy = a.vy - b.vy;
                        let dot = dvx * nx + dvy * ny;
                        if (dot < 0) {
                            a.vx -= dot * nx;
                            a.vy -= dot * ny;
                            b.vx += dot * nx;
                            b.vy += dot * ny;
                        }
                    }
                }
            }
        }

        gsap.ticker.add(physicsTick);
    }

    if (document.readyState === 'complete') {
        startFloating();
    } else {
        window.addEventListener('load', startFloating);
    }
})();
