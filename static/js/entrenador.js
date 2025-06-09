// Función para verificar y actualizar el estado del perfil del entrenador
function actualizarEstadoPerfil() {
  const usuario = localStorage.getItem('entrenador');
  const perfilElement = document.getElementById('perfilEntrenador');
  const loginButtonElement = document.getElementById('btnLoginHeader');
  const nombreElement = document.getElementById('nombreEntrenador');

  if (usuario && perfilElement && loginButtonElement && nombreElement) {
    perfilElement.style.display = 'block';
    loginButtonElement.style.display = 'none';
    nombreElement.textContent = usuario;
  } else if (perfilElement && loginButtonElement) {
    perfilElement.style.display = 'none';
    loginButtonElement.style.display = 'block';
  }
}

// Función para configurar los eventos del modal y perfil
function configurarEventosPerfil() {
  // Evento para el formulario de login
  const loginForm = document.getElementById('loginForm');
  if (loginForm) {
    loginForm.addEventListener('submit', function(e) {
      e.preventDefault();
      const usuarioInput = document.getElementById('usuario');
      if (usuarioInput) {
        const usuario = usuarioInput.value.trim();
        if (usuario) {
          localStorage.setItem('entrenador', usuario);
          actualizarEstadoPerfil();
          
          // Cerrar el modal
          const modal = document.getElementById('modalLogin');
          if (modal) {
            modal.style.display = 'none';
          }
        }
      }
    });
  }

  // Evento para cerrar sesión
  const cerrarSesionBtn = document.getElementById('btnCerrarSesion');
  if (cerrarSesionBtn) {
    cerrarSesionBtn.addEventListener('click', function(e) {
      e.preventDefault();
      localStorage.removeItem('entrenador');
      actualizarEstadoPerfil();
    });
  }

  // Eventos para abrir/cerrar el modal
  const abrirModalBtn = document.getElementById('btnAbrirModalHeader');
  if (abrirModalBtn) {
    abrirModalBtn.addEventListener('click', function() {
      const modal = document.getElementById('modalLogin');
      if (modal) {
        modal.style.display = 'flex';
      }
    });
  }

  const cerrarModalBtn = document.getElementById('btnCerrarModal');
  if (cerrarModalBtn) {
    cerrarModalBtn.addEventListener('click', function() {
      const modal = document.getElementById('modalLogin');
      if (modal) {
        modal.style.display = 'none';
      }
    });
  }

  // Cerrar modal al hacer clic fuera del contenido
  const modal = document.getElementById('modalLogin');
  if (modal) {
    modal.addEventListener('click', function(e) {
      if (e.target === this) {
        this.style.display = 'none';
      }
    });
  }
}

// Función para configurar el menú móvil
function configurarMenuMovil() {
  const menuBtn = document.getElementById('menuBtn');
  const menuMobile = document.getElementById('menuMobile');

  if (menuBtn && menuMobile) {
    menuBtn.addEventListener('click', () => {
      menuMobile.classList.toggle('hidden');
    });

    // Cerrar menú al hacer click en enlaces
    menuMobile.querySelectorAll('a').forEach(link => {
      link.addEventListener('click', () => {
        menuMobile.classList.add('hidden');
      });
    });
  }

  // Submenú móvil
  const submenuBtn = document.getElementById('submenuBtn');
  const submenuMobile = document.getElementById('submenuMobile');
  
  if (submenuBtn && submenuMobile) {
    submenuBtn.addEventListener('click', function() {
      submenuMobile.classList.toggle('hidden');
    });
  }
}

// Inicialización cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function() {
  actualizarEstadoPerfil();
  configurarEventosPerfil();
  configurarMenuMovil();
});