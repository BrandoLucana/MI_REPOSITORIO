document.addEventListener('DOMContentLoaded', function() {
    const tablaUsuarios = document.getElementById('tabla-usuarios');
    const tbody = tablaUsuarios.querySelector('tbody');
    const filtroNombre = document.getElementById('filtro-nombre');
    const filtroNivel = document.getElementById('filtro-nivel');
    const btnBuscar = document.getElementById('btn-buscar');
    const btnReset = document.getElementById('btn-reset');
    const mensajeDiv = document.getElementById('mensaje');

    let usuarios = [];

    cargarUsuariosDesdeBackend();

    async function cargarUsuariosDesdeBackend() {
        try {
            const response = await fetch('/obtener_usuarios');
            if (!response.ok) throw new Error('Error al cargar usuarios');
            
            usuarios = await response.json();
            cargarUsuariosEnTabla();
        } catch (error) {
            console.error('Error:', error);
            mostrarMensaje('Error al cargar los usuarios: ' + error.message, 'error');
        }
    }

    function cargarUsuariosEnTabla(usuariosFiltrados = null) {
        tbody.innerHTML = '';
        const datosAMostrar = usuariosFiltrados || usuarios;
        
        if (datosAMostrar.length === 0) {
            mostrarMensaje('No hay usuarios registrados', 'info');
            return;
        }
        
        datosAMostrar.forEach(usuario => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${usuario.id}</td>
                <td>${usuario.nombre} ${usuario.apellidos}</td>
                <td>${usuario.edad}</td>
                <td>${usuario.email}</td>
                <td>${usuario.nombre_apoderado ? 'Sí' : 'No'}</td>
                <td>${usuario.telefono_apoderado || ''}</td>
                <td>${usuario.nivel_actual}</td>
                <td>${usuario.posicion || ''}</td>
                <td>${new Date(usuario.fecha_registro).toLocaleDateString()}</td>
                <td class="flex gap-2">
                    <button class="delete-btn bg-red-500 text-white px-2 py-1 rounded hover:bg-red-600" data-id="${usuario.id}">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }

    btnBuscar.addEventListener('click', filtrarUsuarios);
    btnReset.addEventListener('click', resetearFiltros);

    async function filtrarUsuarios() {
        const texto = filtroNombre.value.trim();
        const nivel = filtroNivel.value;
        
        try {
            let url = '/obtener_usuarios?';
            if (texto) url += `nombre=${encodeURIComponent(texto)}&`;
            if (nivel) url += `nivel=${encodeURIComponent(nivel)}`;
            
            const response = await fetch(url);
            if (!response.ok) throw new Error('Error al filtrar usuarios');
            
            const resultados = await response.json();
            cargarUsuariosEnTabla(resultados);
            
            if (resultados.length === 0) {
                mostrarMensaje('No se encontraron usuarios con esos criterios', 'info');
            }
        } catch (error) {
            console.error('Error:', error);
            mostrarMensaje('Error al filtrar usuarios: ' + error.message, 'error');
        }
    }

    function resetearFiltros() {
        filtroNombre.value = '';
        filtroNivel.value = '';
        cargarUsuariosDesdeBackend();
        mostrarMensaje('Filtros reseteados correctamente', 'success');
    }

    function mostrarMensaje(mensaje, tipo) {
        mensajeDiv.textContent = mensaje;
        mensajeDiv.className = `system-message ${tipo} p-4 mb-4 rounded`;
        mensajeDiv.style.display = 'block';
        
        setTimeout(() => {
            mensajeDiv.style.display = 'none';
        }, 5000);
    }

    tbody.addEventListener('click', async function(e) {
        if (e.target.closest('.delete-btn')) {
            const id = e.target.closest('.delete-btn').dataset.id;
            const usuario = usuarios.find(u => u.id == id);
            
            if (confirm(`¿Estás seguro de que deseas eliminar a ${usuario.nombre} ${usuario.apellidos}?`)) {
                try {
                    const response = await fetch(`/eliminar_usuario/${id}`, { method: 'POST' });
                    const data = await response.json();
                    
                    if (!data.success) throw new Error(data.error || 'Error al eliminar');
                    
                    mostrarMensaje('Usuario eliminado correctamente', 'success');
                    setTimeout(() => {
                        cargarUsuariosDesdeBackend();
                    }, 1000);
                } catch (error) {
                    console.error('Error:', error);
                    mostrarMensaje('Error al eliminar el usuario: ' + error.message, 'error');
                }
            }
        }
    });
});
document.addEventListener('DOMContentLoaded', function () {
  const sidebarLinks = document.querySelectorAll('aside nav a');
  const sections = document.querySelectorAll('main section');

  // Mostrar sección de usuarios por defecto
  const defaultSection = document.getElementById('seccion-usuarios');
  if (defaultSection) {
    defaultSection.classList.remove('section-hidden');
  }

  sidebarLinks.forEach(link => {
    link.addEventListener('click', function (e) {
      e.preventDefault();
      const targetId = this.getAttribute('href').substring(1); // Obtener el ID de la sección
      sections.forEach(section => {
        section.classList.add('section-hidden');
      });
      document.getElementById(targetId).classList.remove('section-hidden');
    });
  });
});