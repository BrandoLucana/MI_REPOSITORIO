  window.addEventListener('DOMContentLoaded', () => {
    const fadeElements = document.querySelectorAll('.animate-fadeInLeft, .animate-fadeInRight');

    fadeElements.forEach(el => {
      el.style.opacity = 0;
      el.style.transform = el.classList.contains('animate-fadeInLeft') ? 'translateX(-50px)' : 'translateX(50px)';
      el.style.transition = 'all 1s ease-out';
    });

    const observer = new IntersectionObserver(entries => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.style.opacity = 1;
          entry.target.style.transform = 'translateX(0)';
        }
      });
    });

    fadeElements.forEach(el => observer.observe(el));
  });