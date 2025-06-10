const tablaBody = document.getElementById('cuerpoTabla');
const form = document.getElementById('formTorneo');

form.addEventListener('submit', function(event) {
  event.preventDefault();

  const nombre = document.getElementById('nombre').value.trim();
  const fecha = document.getElementById('fecha').value;

  if(nombre && fecha) {
    const fila = document.createElement('tr');
    fila.classList.add('fade-in'); // animación al aparecer
    
    fila.innerHTML = `
      <td>${nombre}</td>
      <td>${fecha}</td>
      <td><button class="btnEliminar">Eliminar</button></td>
    `;

    tablaBody.appendChild(fila);

    // Limpiar formulario
    form.reset();
  }
});

// Delegar evento para eliminar filas con animación
tablaBody.addEventListener('click', function(event) {
  if(event.target.classList.contains('btnEliminar')) {
    const fila = event.target.closest('tr');
    fila.classList.add('fade-out'); // animación al desaparecer

    // Esperar a que termine la animación para eliminar la fila
    fila.addEventListener('animationend', () => {
      fila.remove();
    });
  }
});

document.getElementById('menuBtn').addEventListener('click', () => {
  document.getElementById('menuMobile').classList.toggle('hidden');
});