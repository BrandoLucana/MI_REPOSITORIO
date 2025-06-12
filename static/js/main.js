// Script para togglear el menú móvil
const menuBtn = document.getElementById('menuBtn');
const menuMobile = document.getElementById('menuMobile');

menuBtn.addEventListener('click', () => {
  menuMobile.classList.toggle('hidden');
});

// Opcional: cerrar menú al hacer click en algún enlace móvil
menuMobile.querySelectorAll('a').forEach(link => {
  link.addEventListener('click', () => {
    menuMobile.classList.add('hidden');
  });
});

// Script para abrir/cerrar el modal
const btnAbrir = document.getElementById('abrirModal');
const btnCerrar = document.getElementById('cerrarModal');
const modal = document.getElementById('modalLogin');

btnAbrir.addEventListener('click', () => {
  modal.classList.remove('hidden');
});

btnCerrar.addEventListener('click', () => {
  modal.classList.add('hidden');
});

// También cerrar modal si se hace clic fuera del contenido
modal.addEventListener('click', (e) => {
  if (e.target === modal) {
    modal.classList.add('hidden');
  }
});