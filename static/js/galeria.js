// galeria.js
function toggleInfo(button) {
  const info = button.nextElementSibling;
  info.classList.remove('hidden');
  button.classList.add('hidden');
}

const imagenes = [
  { src: "../static/img/galeria1.jpg", titulo: "Entrenamiento Juvenil" },
  { src: "../static/img/galeria2.jpg", titulo: "Competencia Interna" },
  { src: "../static/img/galeria3.jpg", titulo: "Entrenamiento Avanzado" },
  { src: "../static/img/galeria4.jpg", titulo: "Competencia en Ica" }
];

function cambiarImagen(index) {
  const img = document.getElementById("carruselImagen");
  const titulo = document.getElementById("tituloImagen");

  img.classList.add("opacity-0");

  setTimeout(() => {
    img.src = imagenes[index].src;
    titulo.textContent = imagenes[index].titulo;
    img.classList.remove("opacity-0");
  }, 300);
}