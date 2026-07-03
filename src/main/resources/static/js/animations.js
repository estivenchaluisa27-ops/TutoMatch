(function() {
gsap.registerPlugin(ScrollTrigger);

const mm = gsap.matchMedia();

mm.add("(prefers-reduced-motion: no-preference)", () => {
    gsap.from(".login-wrapper, .registro-wrapper", {
        opacity: 0,
        y: 30,
        duration: 0.8,
        ease: "power2.out"
    });

    gsap.from(".login-card, .registro-card", {
        opacity: 0,
        y: 20,
        duration: 0.6,
        delay: 0.3,
        ease: "power2.out"
    });

    gsap.from(".branding-section", {
        opacity: 0,
        x: -20,
        duration: 0.6,
        delay: 0.1,
        ease: "power2.out"
    });

    gsap.utils.toArray(".form-input").forEach(input => {
        input.addEventListener("focus", () => {
            gsap.to(input, {
                borderColor: "#a78bfa",
                scale: 1.01,
                duration: 0.2,
                overwrite: "auto"
            });
        });
        input.addEventListener("blur", () => {
            gsap.to(input, {
                borderColor: "rgba(255, 255, 255, 0.18)",
                scale: 1,
                duration: 0.2,
                overwrite: "auto"
            });
        });
    });

    gsap.utils.toArray(".btn-login, .btn-registro").forEach(btn => {
        btn.addEventListener("mousedown", () => {
            gsap.to(btn, { scale: 0.98, duration: 0.1 });
        });
        btn.addEventListener("mouseup", () => {
            gsap.to(btn, { scale: 1, duration: 0.2, ease: "power2.out" });
        });
        btn.addEventListener("mouseleave", () => {
            gsap.to(btn, { scale: 1, duration: 0.2 });
        });
    });
});
})();


