document.addEventListener('DOMContentLoaded', function() {
    // Selección de elementos del DOM
    const tablaUsuarios = document.getElementById('tabla-usuarios');
    const tbody = tablaUsuarios.querySelector('tbody');
    const filtroNombre = document.getElementById('filtro-nombre');
    const filtroNivel = document.getElementById('filtro-nivel');
    const btnBuscar = document.getElementById('btn-buscar');
    const btnReset = document.getElementById('btn-reset');
    const mensajeDiv = document.getElementById('mensaje');

    // Variable para almacenar los usuarios
    let usuarios = [];

    // Cargar usuarios al iniciar
    cargarUsuariosDesdeBackend();

    // Función para cargar usuarios desde el backend
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

    // Función para cargar usuarios en la tabla
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
                <td class="actions-cell">
                    <button class="action-btn delete-btn" data-id="${usuario.id}">
                        <i class="fas fa-trash-alt"></i>
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }

    // Event listeners
    btnBuscar.addEventListener('click', filtrarUsuarios);
    btnReset.addEventListener('click', resetearFiltros);

    // Funciones de filtrado
    async function filtrarUsuarios() {
        const texto = filtroNombre.value.trim();
        const nivel = filtroNivel.value;
        
        try {
            // Construir URL con parámetros de búsqueda
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

    // Mostrar mensaje
    function mostrarMensaje(mensaje, tipo) {
        mensajeDiv.textContent = mensaje;
        mensajeDiv.className = `system-message ${tipo}`;
        mensajeDiv.style.display = 'block';
        
        setTimeout(() => {
            mensajeDiv.style.display = 'none';
        }, 5000);
    }

    // Event delegation para botones de acciones (solo eliminación)
    tbody.addEventListener('click', async function(e) {
        if (e.target.closest('.delete-btn')) {
            const id = e.target.closest('.delete-btn').dataset.id;
            const usuario = usuarios.find(u => u.id == id);
            
            if (confirm(`¿Estás seguro de que deseas eliminar a ${usuario.nombre} ${usuario.apellidos}?`)) {
                try {
                    const response = await fetch(`/eliminar_usuario/${id}`, {
                        method: 'POST'
                    });
                    
                    const data = await response.json();
                    
                    if (!data.success) throw new Error(data.error || 'Error al eliminar');
                    
                    mostrarMensaje('Usuario eliminado correctamente', 'success');
                    // Recargar los usuarios después de 1 segundo
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