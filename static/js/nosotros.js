document.addEventListener('DOMContentLoaded', () => {
  const btn = document.getElementById('menu-btn');
  const menu = document.getElementById('dropdown-menu');

  btn.addEventListener('click', (e) => {
    e.preventDefault();
    e.stopPropagation();
    menu.classList.toggle('hidden');
  });

  // Cierra el menú al hacer clic fuera de él
  document.addEventListener('click', () => {
    if (!menu.classList.contains('hidden')) {
      menu.classList.add('hidden');
    }
  });

  // Evita cerrar el menú al hacer clic dentro del dropdown
  menu.addEventListener('click', (e) => {
    e.stopPropagation();
  });
});
