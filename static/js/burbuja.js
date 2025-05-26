const btnAbrir = document.getElementById("btnAbrirModal");
const modal = document.getElementById("modalLogin");
const btnCerrar = document.getElementById("btnCerrarModal");

btnAbrir.addEventListener("click", () => {
  modal.classList.remove("hidden");
});

btnCerrar.addEventListener("click", () => {
  modal.classList.add("hidden");
});

// También cerrar al hacer clic fuera del formulario
modal.addEventListener("click", (e) => {
  if (e.target === modal) {
    modal.classList.add("hidden");
  }
});
