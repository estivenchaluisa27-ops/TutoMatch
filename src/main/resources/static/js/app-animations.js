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

    // --- 15. Desktop split layout: panel scroll reveal (home.html) ---
    let panelLeft = document.querySelector('.col-lg-9.panel-scrollable');
    if (panelLeft) {
        let leftCards = panelLeft.querySelectorAll('.card-fade-in');
        if (leftCards.length) {
            ScrollTrigger.batch(leftCards, {
                onEnter: batch => gsap.to(batch, {
                    autoAlpha: 1, y: 0, duration: 0.5, stagger: 0.06,
                    ease: "back.out(1.2)", overwrite: true
                }),
                start: "top 88%",
                scroller: panelLeft
            });
        }
    }

    let panelRight = document.querySelector('.tutor-sidebar');
    if (panelRight) {
        let rightCards = panelRight.querySelectorAll('.card-fade-in');
        if (rightCards.length) {
            ScrollTrigger.batch(rightCards, {
                onEnter: batch => gsap.to(batch, {
                    autoAlpha: 1, y: 0, duration: 0.5, stagger: 0.08,
                    ease: "power2.out", overwrite: true
                }),
                start: "top 85%",
                scroller: panelRight
            });
        }
    }

    // --- 16. Sidebar entrance animation (home.html desktop) ---
    let sidebar = document.querySelector('.tutor-sidebar');
    if (sidebar) {
        gsap.from(sidebar, {
            autoAlpha: 0,
            x: 30,
            duration: 0.6,
            delay: 0.3,
            ease: "power2.out"
        });
    }

    // --- 17. Grid semanal cells entrance (configurar-disponibilidad) ---
    let sgCells = document.querySelectorAll('.sg-cell:not(.sg-active)');
    let sgActiveCells = document.querySelectorAll('.sg-cell.sg-active');
    if (sgCells.length || sgActiveCells.length) {
        if (sgActiveCells.length) {
            gsap.from(sgActiveCells, {
                autoAlpha: 0,
                scale: 0.6,
                duration: 0.3,
                stagger: 0.008,
                ease: "back.out(1.7)",
                overwrite: true
            });
        }
        if (sgCells.length) {
            gsap.from(sgCells, {
                autoAlpha: 0,
                duration: 0.2,
                stagger: 0.004,
                ease: "power2.out",
                delay: 0.15,
                overwrite: true
            });
        }
    }

});

// --- 15. Hero floating icons — Phase System (outside matchMedia to prevent context revert) ---
(function () {
    let container = document.querySelector('.hero-icons');
    if (!container) return;
    let iconEls = container.querySelectorAll('.hero-icon');
    if (!iconEls.length || window.innerWidth < 768) return;

    // ── State ──
    const S = {
        phase: 'A',
        transitioning: false,
        particles: [],
        mouse: { x: -9999, y: -9999 },
        rect: null,
        timer: null,
        canvas: null,
        ctx: null,
        orbitConfigs: null,
        orbitAngles: [],
    };

    function ensureCanvas(r) {
        if (S.canvas) return;
        const c = document.createElement('canvas');
        c.id = 'hero-orbit-canvas';
        c.style.cssText = 'position:absolute;inset:0;pointer-events:none;opacity:0;z-index:1';
        c.width = r.width; c.height = r.height;
        container.appendChild(c);
        S.canvas = c; S.ctx = c.getContext('2d');
    }

    // ── Mouse ──
    const hero = container.closest('.hero-section');
    if (hero) {
        hero.addEventListener('mousemove', e => {
            const r = container.getBoundingClientRect();
            S.mouse.x = e.clientX - r.left;
            S.mouse.y = e.clientY - r.top;
        });
        hero.addEventListener('mouseleave', () => { S.mouse.x = -9999; S.mouse.y = -9999; });
    }

    // ── Init ──
    function init() {
        const r = container.getBoundingClientRect();
        if (r.width === 0 || r.height === 0) { requestAnimationFrame(init); return; }
        S.rect = r;
        const pad = 15, w = r.width, h = r.height;

        iconEls.forEach(el => {
            const sz = 14 + Math.random() * 30;
            el.style.cssText += `width:${sz}px;height:${sz}px;top:0;left:0;opacity:0;`;
            S.particles.push({
                el, size: sz,
                x: pad + Math.random() * (w - sz - pad * 2),
                y: pad + Math.random() * (h - sz - pad * 2),
                vx: (Math.random() - 0.5) * 1.2,
                vy: (Math.random() - 0.5) * 1.2,
                rotation: Math.random() * 360,
                vr: (Math.random() - 0.5) * 3,
                glow: 0,
            });
        });

        // Fade in + pageReady
        const tl = gsap.timeline({ onComplete() {
            if (window.loadingOverlay && window.loadingOverlay.pageReady) window.loadingOverlay.pageReady();
        }});
        S.particles.forEach(p => tl.to(p.el, { opacity: 0.08, duration: 0.6, ease: "power2.out" }, 0));

        ensureCanvas(r);
        S.orbitConfigs = buildOrbitConfigs(r);
        enterPhaseA();
        scheduleTransitions();
    }

    // ════════════════════════════════════════════════════════════
    // FASE A — Física libre + magnetismo cursor + glow
    // ════════════════════════════════════════════════════════════
    function enterPhaseA() {
        S.phase = 'A';
        S.transitioning = false;
        gsap.ticker.remove(orbitTick);
        gsap.ticker.add(physicsTickA);

        S.particles.forEach(p => {
            p.vx = (Math.random() - 0.5) * 1.2;
            p.vy = (Math.random() - 0.5) * 1.2;
            const sz = 14 + Math.random() * 30;
            p.size = sz;
            p.el.style.cssText += `width:${sz}px;height:${sz}px;opacity:0.08;filter:none;`;
        });

        if (S.canvas) gsap.to(S.canvas, { opacity: 0, duration: 0.5 });
    }

    function physicsTickA() {
        const { rect, mouse, particles } = S;
        const w = rect.width, h = rect.height, len = particles.length;

        for (let i = 0; i < len; i++) {
            const a = particles[i];

            if (mouse.x >= 0 && mouse.y >= 0) {
                const dx = a.x + a.size / 2 - mouse.x;
                const dy = a.y + a.size / 2 - mouse.y;
                const dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < 120 && dist > 0) {
                    const str = (120 - dist) / 120 * 0.5;
                    a.vx += (dx / dist) * str;
                    a.vy += (dy / dist) * str;
                }
            }

            a.x += a.vx; a.y += a.vy; a.rotation += a.vr;
            a.vx *= 0.995; a.vy *= 0.995;
            a.glow = Math.max(0, a.glow - 0.015);

            if (a.x < 0) { a.x = 0; a.vx *= -0.9; a.glow = 0.3; }
            if (a.x > w - a.size) { a.x = w - a.size; a.vx *= -0.9; a.glow = 0.3; }
            if (a.y < 0) { a.y = 0; a.vy *= -0.9; a.glow = 0.3; }
            if (a.y > h - a.size) { a.y = h - a.size; a.vy *= -0.9; a.glow = 0.3; }

            gsap.set(a.el, { x: a.x, y: a.y, rotation: a.rotation });
        }

        for (let i = 0; i < len; i++) {
            for (let j = i + 1; j < len; j++) {
                const a = particles[i], b = particles[j];
                const dx = (a.x + a.size / 2) - (b.x + b.size / 2);
                const dy = (a.y + a.size / 2) - (b.y + b.size / 2);
                const dist = Math.sqrt(dx * dx + dy * dy);
                const min = (a.size + b.size) / 2 + 8;

                if (dist < min && dist > 0) {
                    const nx = dx / dist, ny = dy / dist, ov = min - dist;
                    a.x += nx * ov / 2; a.y += ny * ov / 2;
                    b.x -= nx * ov / 2; b.y -= ny * ov / 2;
                    const dot = (a.vx - b.vx) * nx + (a.vy - b.vy) * ny;
                    if (dot < 0) {
                        a.vx -= dot * nx; a.vy -= dot * ny;
                        b.vx += dot * nx; b.vy += dot * ny;
                        a.glow = 0.4; b.glow = 0.4;
                    }
                }
            }
        }

        for (let i = 0; i < len; i++) {
            const a = particles[i];
            a.el.style.filter = a.glow > 0.01
                ? `drop-shadow(0 0 ${a.glow * 8}px rgba(167,139,250,${a.glow}))`
                : 'none';
        }
    }

    // ════════════════════════════════════════════════════════════
    // FASE B — Órbitas elípticas + anillos en canvas
    // ════════════════════════════════════════════════════════════
    function buildOrbitConfigs(r) {
        const cx = r.width / 2, cy = r.height * 0.45;
        return [
            { rx: r.width * 0.10, ry: r.height * 0.07, speed: 0.009, count: 6 },
            { rx: r.width * 0.17, ry: r.height * 0.12, speed: 0.007, count: 6 },
            { rx: r.width * 0.24, ry: r.height * 0.17, speed: 0.005, count: 6 },
            { rx: r.width * 0.31, ry: r.height * 0.22, speed: 0.0035, count: 6 },
            { rx: r.width * 0.38, ry: r.height * 0.27, speed: 0.0025, count: 6 },
        ];
    }

    function assignOrbits(particles, configs) {
        const angles = [];
        let idx = 0;
        configs.forEach(cfg => {
            for (let i = 0; i < cfg.count && idx < particles.length; i++, idx++) {
                angles.push({
                    angle: (i / cfg.count) * Math.PI * 2,
                    rx: cfg.rx, ry: cfg.ry, speed: cfg.speed,
                    cx: particles[0].x, cy: particles[0].y, // placeholder
                });
            }
        });
        return angles;
    }

    function enterPhaseB() {
        S.phase = 'B';
        S.transitioning = true;
        gsap.ticker.remove(physicsTickA);

        const r = S.rect;
        const cx = r.width / 2, cy = r.height * 0.45;
        S.orbitConfigs = buildOrbitConfigs(r);
        S.orbitAngles = assignOrbits(S.particles, S.orbitConfigs);

        // Fix center coordinates after we know them
        S.orbitAngles.forEach(o => { o.cx = cx; o.cy = cy; });

        // Migrate each particle to its orbital starting position
        S.particles.forEach((p, i) => {
            const orb = S.orbitAngles[i];
            if (!orb) return;
            const tx = orb.cx + Math.cos(orb.angle) * orb.rx - p.size / 2;
            const ty = orb.cy + Math.sin(orb.angle) * orb.ry - p.size / 2;
            gsap.to(p, {
                x: tx, y: ty, duration: 2, ease: "power2.inOut",
                onUpdate() { gsap.set(p.el, { x: p.x, y: p.y }); },
            });
            gsap.to(p.el, { opacity: 0.18, duration: 1.2, ease: "power2.inOut", delay: 0.3 });
        });

        gsap.to(S.canvas, { opacity: 1, duration: 1, delay: 0.5 });

        setTimeout(() => {
            S.transitioning = false;
            gsap.ticker.add(orbitTick);
        }, 2500);
    }

    function orbitTick() {
        if (S.transitioning) return;
        const ctx = S.ctx, w = S.rect.width, h = S.rect.height;
        ctx.clearRect(0, 0, w, h);

        // Draw rings
        const cx = w / 2, cy = h * 0.45;
        ctx.strokeStyle = 'rgba(167,139,250,0.06)';
        ctx.lineWidth = 1;
        if (S.orbitConfigs) {
            S.orbitConfigs.forEach(c => {
                ctx.beginPath();
                ctx.ellipse(cx, cy, c.rx, c.ry, 0, 0, Math.PI * 2);
                ctx.stroke();
            });
        }

        // Move particles along orbits
        S.particles.forEach((p, i) => {
            const orb = S.orbitAngles[i];
            if (!orb) return;
            orb.angle += orb.speed;
            p.x = orb.cx + Math.cos(orb.angle) * orb.rx - p.size / 2;
            p.y = orb.cy + Math.sin(orb.angle) * orb.ry - p.size / 2;
            gsap.set(p.el, { x: p.x, y: p.y });
        });
    }

    // ════════════════════════════════════════════════════════════
    // Scheduler — A → B → Cap Rain → A (full cycle)
    // ════════════════════════════════════════════════════════════
    function scheduleTransitions() {
        function loop() {
            S.timer = setTimeout(() => {
                enterPhaseB();
                S.timer = setTimeout(() => {
                    enterCapRain();
                    S.timer = setTimeout(() => {
                        exitCapRain(() => {
                            resetToPhaseA();
                            loop();
                        });
                    }, 14000);
                }, 14000);
            }, 28000);
        }
        loop();
    }

    // ════════════════════════════════════════════════════════════
    // FASE C — Lluvia de Birretes (4 estilos Iconify)
    // ════════════════════════════════════════════════════════════
    const CAP_PATHS = [
        `M19 12.282V14a7 7 0 0 1-14 0v-1.718l6.359 3.007a1.5 1.5 0 0 0 1.282 0zm-6.359-9.57l9.08 4.294c.734.346.827 1.302.279 1.803V13a1 1 0 0 1-2 0V9.81l-7.359 3.48a1.5 1.5 0 0 1-1.282 0l-9.08-4.295a1.1 1.1 0 0 1 0-1.988l9.08-4.294a1.5 1.5 0 0 1 1.282 0Z`,
        `M12 3L1 9l11 6l9-4.91V17h2V9M5 13.18v4L12 21l7-3.82v-4L12 17z`,
        `M12 3L1 9l4 2.18v6L12 21l7-3.82v-6l2-1.09V17h2V9zm6.82 6L12 12.72L5.18 9L12 5.28zM17 16l-5 2.72L7 16v-3.73L12 15l5-2.73z`,
        `M21 17v-6.9L12 15 1 9l11-6 11 6v8h-2zm-9 4-7-3.8v-5l7 3.8 7-3.8v5L12 21z`,
    ];
    const CAP_COLORS = ['#A78BFA', '#7C3AED', '#8B5CF6', '#C4B5FD', '#FBBF24', '#FFF'];
    let capElements = [];

    function createCapSVG(pathIdx) {
        const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
        svg.setAttribute('viewBox', '0 0 24 24');
        svg.innerHTML = `<path fill="currentColor" d="${CAP_PATHS[pathIdx]}"/>`;
        return svg;
    }

    function enterCapRain() {
        S.phase = 'C';
        gsap.ticker.remove(orbitTick);
        if (S.canvas) {
            S.ctx && S.ctx.clearRect(0, 0, S.rect.width, S.rect.height);
            gsap.to(S.canvas, { opacity: 0, duration: 0.3 });
        }
        S.particles.forEach(p => gsap.to(p.el, { opacity: 0, duration: 0.4, ease: "power2.inOut" }));

        const r = S.rect;
        const count = 120 + Math.floor(Math.random() * 60);
        const perRow = 8;

        for (let i = 0; i < count; i++) {
            const row = Math.floor(i / perRow);
            const sz = 14 + Math.random() * 30;
            const styleIdx = Math.floor(Math.random() * CAP_PATHS.length);
            const color = CAP_COLORS[Math.floor(Math.random() * CAP_COLORS.length)];
            const x = Math.random() * (r.width - sz);
            const startY = -80 - Math.random() * 120;
            const fallDur = 1.2 + Math.random() * 1.2;
            const rotation = (Math.random() - 0.5) * 540;
            const delay = Math.random() * 2.5;
            const driftX = (Math.random() - 0.5) * 25;
            const bounce1 = 5 + Math.random() * 8;
            const bounce2 = bounce1 * 0.3 + Math.random() * 3;

            const el = createCapSVG(styleIdx);
            el.style.cssText = `position:absolute;width:${sz}px;height:${sz}px;left:${x}px;top:${startY}px;opacity:0;pointer-events:none;z-index:2;color:${color};`;
            container.appendChild(el);
            capElements.push(el);

            const landY = Math.max(r.height * 0.12, r.height - sz * 0.3 - row * (sz * 0.32));
            const finalY = landY + (Math.random() - 0.5) * 2;
            const finalX = x + driftX + (Math.random() - 0.5) * 10;

            const tl = gsap.timeline({ delay });
            tl.to(el, { opacity: 0.4 + Math.random() * 0.5, duration: 0.2, ease: "power2.out" })
              .to(el, { top: finalY + 3, left: finalX, rotation, duration: fallDur, ease: "power3.in" })
              .to(el, { top: finalY - bounce1, duration: 0.13, ease: "power2.out" })
              .to(el, { top: finalY + 1, duration: 0.1, ease: "power1.in" })
              .to(el, { top: finalY - bounce2, duration: 0.08, ease: "power2.out" })
              .to(el, {
                  top: finalY,
                  left: parseFloat(el.style.left) + (Math.random() - 0.5) * 8,
                  rotation: rotation + (Math.random() - 0.5) * 15,
                  duration: 0.1, ease: "power1.out",
              });
        }
    }

    function exitCapRain(cb) {
        if (!capElements.length) { cb && cb(); return; }
        const r = S.rect;
        const tl = gsap.timeline({
            onComplete() { capElements.forEach(el => el.remove()); capElements = []; cb && cb(); }
        });
        capElements.forEach((el, i) => {
            const drift = (Math.random() - 0.5) * 60;
            tl.to(el, {
                top: r.height + 40 + Math.random() * 60,
                left: parseFloat(el.style.left) + drift,
                opacity: 0,
                rotation: (Math.random() - 0.5) * 360,
                duration: 0.4 + Math.random() * 0.3,
                ease: "power2.in",
            }, i * 0.012);
        });
    }

    function resetToPhaseA() {
        S.transitioning = true;
        gsap.ticker.remove(orbitTick);

        const pad = 15, w = S.rect.width, h = S.rect.height;

        S.particles.forEach(p => {
            p._orb = null;
            p.vx = (Math.random() - 0.5) * 1.2;
            p.vy = (Math.random() - 0.5) * 1.2;
            p.el.style.filter = 'none';

            const sz = 14 + Math.random() * 30;
            const newX = pad + Math.random() * (w - sz - pad * 2);
            const newY = pad + Math.random() * (h - sz - pad * 2);

            gsap.to(p, {
                x: newX, y: newY, duration: 1.5, ease: "power2.inOut",
                onUpdate() { gsap.set(p.el, { x: p.x, y: p.y }); },
            });
            gsap.to(p.el, { opacity: 0.08, width: sz, height: sz, duration: 1.2, ease: "power2.inOut" });
            p.size = sz;
        });

        gsap.delayedCall(1.8, () => {
            S.phase = 'A';
            S.transitioning = false;
            gsap.ticker.add(physicsTickA);
            if (S.canvas) gsap.to(S.canvas, { opacity: 0, duration: 0.5 });
        });
    }

    // ── Bootstrap ──
    if (document.readyState === 'complete') init();
    else window.addEventListener('load', init);
})();
