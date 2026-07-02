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

    // ── Hat coordinates — 22 pts (viewBox 24×24) ──
    const HAT = [
        { x: 12, y: 3 },     { x: 6.5, y: 6 },    { x: 1, y: 9 },
        { x: 4.7, y: 11 },   { x: 8.3, y: 13 },   { x: 12, y: 15 },
        { x: 16.5, y: 12.5 },{ x: 21, y: 10.09 }, { x: 21, y: 13.5 },
        { x: 21, y: 17 },    { x: 22, y: 17 },    { x: 23, y: 17 },
        { x: 23, y: 13 },    { x: 23, y: 9 },     { x: 5, y: 13.18 },
        { x: 5, y: 17.18 },  { x: 8.5, y: 19.1 }, { x: 12, y: 21 },
        { x: 15.5, y: 19.1 },{ x: 19, y: 17.18 }, { x: 19, y: 13.18 },
        { x: 12, y: 17 },
    ];

    function mapHat(p, r) {
        const s = Math.min(r.width, r.height) / 28;
        return { x: r.width / 2 + (p.x - 12) * s, y: r.height * 0.45 + (p.y - 12) * s };
    }

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
            const sz = 22 + Math.random() * 32;
            el.style.cssText += `width:${sz}px;height:${sz}px;top:0;left:0;opacity:0;`;
            S.particles.push({
                el, size: sz,
                x: pad + Math.random() * (w - sz - pad * 2),
                y: pad + Math.random() * (h - sz - pad * 2),
                vx: (Math.random() - 0.5) * 1.2,
                vy: (Math.random() - 0.5) * 1.2,
                rotation: Math.random() * 360,
                vr: (Math.random() - 0.5) * 3,
                glow: 0, isClone: false,
            });
        });

        // 9 clones — smaller, for density in B & C
        [0, 2, 4, 5, 7, 8, 9, 10, 11].forEach(si => {
            const src = iconEls[si]; if (!src) return;
            const clone = src.cloneNode(true);
            const sz = 16 + Math.random() * 12;
            clone.style.cssText = `width:${sz}px;height:${sz}px;top:0;left:0;opacity:0;`;
            container.appendChild(clone);
            S.particles.push({
                el: clone, size: sz,
                x: pad + Math.random() * (w - sz - pad * 2),
                y: pad + Math.random() * (h - sz - pad * 2),
                vx: (Math.random() - 0.5) * 1.2,
                vy: (Math.random() - 0.5) * 1.2,
                rotation: Math.random() * 360,
                vr: (Math.random() - 0.5) * 3,
                glow: 0, isClone: true,
            });
        });

        // Fade in + pageReady
        const tl = gsap.timeline({ onComplete() {
            if (window.loadingOverlay && window.loadingOverlay.pageReady) window.loadingOverlay.pageReady();
        }});
        S.particles.forEach(p => tl.to(p.el, { opacity: p.isClone ? 0.06 : 0.12, duration: 0.6, ease: "power2.out" }, 0));

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
            const sz = p.isClone ? 16 + Math.random() * 12 : 22 + Math.random() * 32;
            p.size = sz;
            p.el.style.cssText += `width:${sz}px;height:${sz}px;opacity:${p.isClone ? 0.06 : 0.12};filter:none;`;
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
            { rx: r.width * 0.12, ry: r.height * 0.08, speed: 0.008, count: 5 },
            { rx: r.width * 0.22, ry: r.height * 0.15, speed: 0.006, count: 6 },
            { rx: r.width * 0.32, ry: r.height * 0.22, speed: 0.0045, count: 6 },
            { rx: r.width * 0.42, ry: r.height * 0.30, speed: 0.003, count: 5 },
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
            p.el.style.opacity = '0.18';
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
    // Scheduler — A → B → C → A (full cycle)
    // ════════════════════════════════════════════════════════════
    function scheduleTransitions() {
        function loop() {
            S.timer = setTimeout(() => {
                enterPhaseB();
                S.timer = setTimeout(() => {
                    enterPhaseC();
                    S.timer = setTimeout(() => {
                        resetToPhaseA();
                        loop();
                    }, 8000);
                }, 10000);
            }, 12000);
        }
        loop();
    }

    // ════════════════════════════════════════════════════════════
    // FASE C — Formación del sombrero + contorno
    // ════════════════════════════════════════════════════════════
    function enterPhaseC() {
        S.phase = 'C';
        S.transitioning = true;
        gsap.ticker.remove(orbitTick);

        S.particles.forEach((p, i) => {
            const pos = mapHat(HAT[i], S.rect);
            const tSize = i < 14 ? 16 + Math.random() * 6 : 12 + Math.random() * 4;

            gsap.to(p, {
                x: pos.x, y: pos.y,
                duration: 1.5, ease: "power2.inOut",
                onUpdate() { gsap.set(p.el, { x: p.x, y: p.y }); },
                onComplete() {
                    p.el.style.filter = 'drop-shadow(0 0 6px rgba(167,139,250,0.55))';
                }
            });

            gsap.to(p.el, {
                width: tSize, height: tSize,
                opacity: 0.85,
                duration: 1.2, ease: "power2.inOut",
                delay: 0.3,
            });
            p.size = tSize;
        });

        setTimeout(drawHatContour, 1800);
    }

    function drawHatContour() {
        const ctx = S.ctx, w = S.rect.width, h = S.rect.height;
        ctx.clearRect(0, 0, w, h);

        const pts = HAT.map(p => mapHat(p, S.rect));
        ctx.strokeStyle = 'rgba(167,139,250,0.18)';
        ctx.lineWidth = 1.5;
        ctx.setLineDash([3, 5]);

        ctx.beginPath();
        pts.forEach((pt, i) => {
            const cx = pt.x + S.particles[i].size / 2;
            const cy = pt.y + S.particles[i].size / 2;
            i === 0 ? ctx.moveTo(cx, cy) : ctx.lineTo(cx, cy);
        });
        ctx.stroke();

        ctx.setLineDash([]);
    }
    function resetToPhaseA() {
        gsap.ticker.remove(orbitTick);
        S.particles.forEach(p => {
            const pad = 15, w = S.rect.width, h = S.rect.height;
            p.x = pad + Math.random() * (w - p.size - pad * 2);
            p.y = pad + Math.random() * (h - p.size - pad * 2);
            p.vx = (Math.random() - 0.5) * 1.2;
            p.vy = (Math.random() - 0.5) * 1.2;
            p.el.style.cssText += `opacity:${p.isClone ? 0.06 : 0.12};filter:none;`;
            const sz = p.isClone ? 16 + Math.random() * 12 : 22 + Math.random() * 32;
            p.size = sz;
            p.el.style.cssText += `width:${sz}px;height:${sz}px;`;
        });
        enterPhaseA();
    }

    // ── Bootstrap ──
    if (document.readyState === 'complete') init();
    else window.addEventListener('load', init);
})();
