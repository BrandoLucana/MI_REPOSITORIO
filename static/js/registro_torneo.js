document.addEventListener('DOMContentLoaded', function () {
  const form = document.getElementById('formTorneo');
  const modificarBtn = document.getElementById('modificarBtn');
  const eliminarBtn = document.getElementById('eliminarBtn');
  const limpiarBtn = document.getElementById('limpiarBtn');
  const selectButtons = document.querySelectorAll('.select-torneo');
  let selectedTorneoId = null;

  // Function to populate form with selected tournament data
  function populateForm(torneo) {
    document.getElementById('torneo_id').value = torneo.id;
    document.getElementById('nombre').value = torneo.nombre;
    document.getElementById('fecha').value = torneo.fecha;
    document.getElementById('lugar').value = torneo.lugar;
    document.getElementById('nivel').value = torneo.nivel;
    document.getElementById('descripcion').value = torneo.descripcion;
    document.getElementById('estado').value = torneo.estado;
    selectedTorneoId = torneo.id;
  }

  // Function to clear form
  function clearForm() {
    form.reset();
    document.getElementById('torneo_id').value = '';
    selectedTorneoId = null;
  }

  // Handle row selection
  selectButtons.forEach(button => {
    button.addEventListener('click', function () {
      const torneo = {
        id: this.getAttribute('data-id'),
        nombre: this.getAttribute('data-nombre'),
        fecha: this.getAttribute('data-fecha'),
        lugar: this.getAttribute('data-lugar'),
        nivel: this.getAttribute('data-nivel'),
        descripcion: this.getAttribute('data-descripcion'),
        estado: this.getAttribute('data-estado')
      };
      populateForm(torneo);

      // Highlight selected row
      document.querySelectorAll('.torneo-row').forEach(row => row.classList.remove('bg-gray-100'));
      this.closest('tr').classList.add('bg-gray-100');
    });
  });

  // Handle modify button
  modificarBtn.addEventListener('click', function () {
    if (!selectedTorneoId) {
      alert('Por favor, seleccione un torneo para modificar.');
      return;
    }

    const formData = new FormData(form);
    fetch('/registro-torneo', { // Changed to /registro-torneo
      method: 'POST',
      body: formData
    })
      .then(response => response.json()) // Expect JSON response
      .then(data => {
        if (data.éxito) {
          alert('Torneo actualizado con éxito.');
          window.location.reload(); // Reload to reflect changes
        } else {
          alert('Error al actualizar el torneo: ' + data.mensaje);
        }
      })
      .catch(error => {
        console.error('Error:', error);
        alert('Error al actualizar el torneo.');
      });
  });

  // Handle delete button
  eliminarBtn.addEventListener('click', function () {
    if (!selectedTorneoId) {
      alert('Por favor, seleccione un torneo para eliminar.');
      return;
    }

    if (confirm('¿Está seguro de que desea eliminar este torneo?')) {
      fetch(`/eliminar-torneo/${selectedTorneoId}`, { // Changed to /eliminar-torneo and POST
        method: 'POST'
      })
        .then(response => response.json())
        .then(data => {
          if (data.éxito) {
            alert('Torneo eliminado con éxito.');
            window.location.reload(); // Reload to reflect changes
          } else {
            alert('Error al eliminar el torneo: ' + data.mensaje);
          }
        })
        .catch(error => {
          console.error('Error:', error);
          alert('Error al eliminar el torneo.');
        });
    }
  });

  // Handle clear button
  limpiarBtn.addEventListener('click', function () {
    clearForm();
    document.querySelectorAll('.torneo-row').forEach(row => row.classList.remove('bg-gray-100'));
  });

  // Handle form submission for registration
  form.addEventListener('submit', function (e) {
    if (selectedTorneoId) {
      e.preventDefault();
      alert('Por favor, limpie el formulario antes de registrar un nuevo torneo.');
    }
  });
});