// Menú móvil
const menuBtn = document.getElementById('menuBtn');
const menuMobile = document.getElementById('menuMobile');

menuBtn.addEventListener('click', () => {
    menuMobile.classList.toggle('hidden');
});

// Animación fade-in al hacer scroll
document.addEventListener("DOMContentLoaded", () => {
    const fadeEls = document.querySelectorAll(".fade-in");

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add("visible");
            }
        });
    }, { threshold: 0.1 });

    fadeEls.forEach(el => observer.observe(el));
});
