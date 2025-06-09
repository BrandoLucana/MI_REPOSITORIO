  const links = document.querySelectorAll('nav a');
  const path = window.location.pathname;

  links.forEach(link => {
    if (link.getAttribute('href') === path) {
      link.classList.add('border-b-2', 'border-white', 'text-white');
    }
  });
