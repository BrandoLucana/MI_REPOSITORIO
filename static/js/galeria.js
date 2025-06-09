// galeria.js
function toggleInfo(button) {
  const info = button.nextElementSibling;
  info.classList.remove('hidden');
  button.classList.add('hidden');
}
