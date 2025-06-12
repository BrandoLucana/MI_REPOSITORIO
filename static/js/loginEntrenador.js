document.addEventListener('DOMContentLoaded', () => {
  // Seleccionar elementos del DOM con verificación
  const btnAbrir = document.getElementById("abrirModal");
  const btnCerrar = document.getElementById("cerrarModal");
  const modal = document.getElementById("modalLogin");

  // Verificar que los elementos existan
  if (!btnAbrir || !btnCerrar || !modal) {
    console.error('Uno o más elementos no se encontraron:', { btnAbrir, btnCerrar, modal });
    return;
  }

  // Abrir modal al hacer clic en el botón flotante
  btnAbrir.addEventListener("click", () => {
    modal.classList.remove("hidden");
  });

  // Cerrar modal al hacer clic en la 'X'
  btnCerrar.addEventListener("click", () => {
    modal.classList.add("hidden");
  });

  // Cerrar modal si se hace clic fuera del contenido
  modal.addEventListener("click", (e) => {
    if (e.target === modal) {
      modal.classList.add("hidden");
    }
  });
});