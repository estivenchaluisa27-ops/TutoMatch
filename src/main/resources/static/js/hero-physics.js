document.addEventListener('DOMContentLoaded', function () {
    var container = document.querySelector('.hero-icons');
    var icons = container ? container.querySelectorAll('.hero-icon') : [];
    if (!container || icons.length === 0) return;

    if (window.innerWidth < 768) return;

    var rect = container.getBoundingClientRect();
    var w = rect.width;
    var h = rect.height;
    var particles = [];

    icons.forEach(function (el, i) {
        var size = 32 + Math.random() * 24;
        var speed = 0.4 + Math.random() * 0.5;
        var angle = Math.random() * Math.PI * 2;
        particles.push({
            el: el,
            x: Math.random() * (w - size),
            y: Math.random() * (h - size),
            vx: Math.cos(angle) * speed,
            vy: Math.sin(angle) * speed,
            size: size,
            mass: size,
            rot: Math.random() * 360,
            rotSpeed: (Math.random() - 0.5) * 0.5
        });
        el.style.width = size + 'px';
        el.style.height = size + 'px';
        el.style.top = '0';
        el.style.left = '0';
    });

    function updateBounds() {
        rect = container.getBoundingClientRect();
        w = rect.width;
        h = rect.height;
    }

    var resizeTimer;
    window.addEventListener('resize', function () {
        clearTimeout(resizeTimer);
        resizeTimer = setTimeout(updateBounds, 200);
    });

    function collide(p1, p2) {
        var dx = p2.x - p1.x;
        var dy = p2.y - p1.y;
        var dist = Math.sqrt(dx * dx + dy * dy);
        var minDist = (p1.size + p2.size) / 2;
        if (dist >= minDist || dist === 0) return;

        var nx = dx / dist;
        var ny = dy / dist;

        var overlap = minDist - dist;
        p1.x -= nx * overlap / 2;
        p1.y -= ny * overlap / 2;
        p2.x += nx * overlap / 2;
        p2.y += ny * overlap / 2;

        var dvx = p1.vx - p2.vx;
        var dvy = p1.vy - p2.vy;
        var dvn = dvx * nx + dvy * ny;
        if (dvn > 0) return;

        var impulse = 2 * dvn / (p1.mass + p2.mass);
        p1.vx -= impulse * p2.mass * nx;
        p1.vy -= impulse * p2.mass * ny;
        p2.vx += impulse * p1.mass * nx;
        p2.vy += impulse * p1.mass * ny;
    }

    function step() {
        for (var i = 0; i < particles.length; i++) {
            var p = particles[i];
            p.x += p.vx;
            p.y += p.vy;
            p.rot += p.rotSpeed;

            if (p.x < 0) { p.x = 0; p.vx = -p.vx; }
            if (p.y < 0) { p.y = 0; p.vy = -p.vy; }
            if (p.x + p.size > w) { p.x = w - p.size; p.vx = -p.vx; }
            if (p.y + p.size > h) { p.y = h - p.size; p.vy = -p.vy; }

            for (var j = i + 1; j < particles.length; j++) {
                collide(p, particles[j]);
            }

            p.el.style.transform = 'translate(' + p.x + 'px, ' + p.y + 'px) rotate(' + p.rot + 'deg)';
        }
        requestAnimationFrame(step);
    }

    requestAnimationFrame(step);
});
