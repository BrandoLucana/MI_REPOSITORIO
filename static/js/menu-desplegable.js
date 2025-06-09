document.addEventListener('DOMContentLoaded', function () {
    const dropdownButton = document.getElementById('dropdown-button');
    const dropdownMenu = document.getElementById('dropdown-menu');
    const menuBtn = document.getElementById('menuBtn');
    const menuMobile = document.getElementById('menuMobile');
    const closeMenuBtn = document.getElementById('closeMenuBtn');
    const submenuBtn = document.getElementById('submenuBtn');
    const submenuMobile = document.getElementById('submenuMobile');

    let timeoutId; // Para manejar el retraso en el cierre del submenú

    function closeAllMenus() {
        if (dropdownMenu) dropdownMenu.classList.remove('show');
        if (menuMobile) menuMobile.classList.add('hidden');
        if (submenuMobile) submenuMobile.classList.remove('open');
        document.body.style.overflow = 'auto';
        clearTimeout(timeoutId); // Cancelar cualquier retraso pendiente
    }

    // Manejo del menú desplegable de escritorio
    if (dropdownButton && dropdownMenu) {
        // Clic para dispositivos táctiles o accesibilidad
        dropdownButton.addEventListener('click', function (e) {
            e.stopPropagation();
            dropdownMenu.classList.toggle('show');
        });

        // Hover para escritorio
        dropdownButton.addEventListener('mouseenter', function () {
            clearTimeout(timeoutId); // Cancelar cualquier cierre pendiente
            dropdownMenu.classList.add('show');
        });

        dropdownMenu.addEventListener('mouseenter', function () {
            clearTimeout(timeoutId); // Mantener abierto si el cursor está en el submenú
            dropdownMenu.classList.add('show');
        });

        dropdownButton.addEventListener('mouseleave', function () {
            timeoutId = setTimeout(() => {
                if (!dropdownMenu.matches(':hover')) {
                    dropdownMenu.classList.remove('show');
                }
            }, 300); // Retraso de 300ms para permitir mover el cursor al submenú
        });

        dropdownMenu.addEventListener('mouseleave', function () {
            timeoutId = setTimeout(() => {
                dropdownMenu.classList.remove('show');
            }, 300); // Retraso de 300ms para una transición suave
        });

        // Cerrar al hacer clic fuera
        document.addEventListener('click', function (e) {
            const isClickInsideButton = dropdownButton.contains(e.target);
            const isClickInsideMenu = dropdownMenu.contains(e.target);
            if (!isClickInsideButton && !isClickInsideMenu) {
                dropdownMenu.classList.remove('show');
            }
        });
    }

    // Menú móvil
    if (menuBtn && menuMobile) {
        menuBtn.addEventListener('click', function () {
            menuMobile.classList.remove('hidden');
            document.body.style.overflow = 'hidden';
        });

        if (closeMenuBtn) {
            closeMenuBtn.addEventListener('click', closeAllMenus);
        }
    }

    // Submenú móvil
    if (submenuBtn && submenuMobile) {
        submenuBtn.addEventListener('click', function (e) {
            e.stopPropagation();
            submenuMobile.classList.toggle('open');
        });
    }

    // Cerrar menús al hacer clic en enlaces
    document.querySelectorAll('#menuMobile a, #dropdown-menu a').forEach(link => {
        link.addEventListener('click', closeAllMenus);
    });

    // Escape
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') closeAllMenus();
    });
});




document.addEventListener('DOMContentLoaded', function () {
    const dropdownButton = document.getElementById('dropdown-button');
    const dropdownMenu = document.getElementById('dropdown-menu');
    const menuBtn = document.getElementById('menuBtn');
    const menuMobile = document.getElementById('menuMobile');
    const closeMenuBtn = document.getElementById('closeMenuBtn');
    const submenuBtn = document.getElementById('submenuBtn');
    const submenuMobile = document.getElementById('submenuMobile');
    const loginBtn = document.getElementById('loginBtn');
    const loginBtnMobile = document.getElementById('loginBtnMobile');
    const profileBtn = document.getElementById('profileBtn');
    const profileBtnMobile = document.getElementById('profileBtnMobile');
    const loginModal = document.getElementById('loginModal');
    const profileModal = document.getElementById('profileModal');
    const cancelLogin = document.getElementById('cancelLogin');

    let timeoutId;

    function closeAllMenus() {
        if (dropdownMenu) dropdownMenu.classList.remove('show');
        if (menuMobile) menuMobile.classList.add('hidden');
        if (submenuMobile) submenuMobile.classList.remove('open');
        if (loginModal) loginModal.classList.remove('show');
        if (profileModal) profileModal.classList.remove('show');
        document.body.style.overflow = 'auto';
        clearTimeout(timeoutId);
    }

    // Manejo del menú desplegable de escritorio
    if (dropdownButton && dropdownMenu) {
        dropdownButton.addEventListener('click', function (e) {
            e.stopPropagation();
            dropdownMenu.classList.toggle('show');
        });

        dropdownButton.addEventListener('mouseenter', function () {
            clearTimeout(timeoutId);
            dropdownMenu.classList.add('show');
        });

        dropdownMenu.addEventListener('mouseenter', function () {
            clearTimeout(timeoutId);
            dropdownMenu.classList.add('show');
        });

        dropdownButton.addEventListener('mouseleave', function () {
            timeoutId = setTimeout(() => {
                if (!dropdownMenu.matches(':hover')) {
                    dropdownMenu.classList.remove('show');
                }
            }, 300);
        });

        dropdownMenu.addEventListener('mouseleave', function () {
            timeoutId = setTimeout(() => {
                dropdownMenu.classList.remove('show');
            }, 300);
        });

        document.addEventListener('click', function (e) {
            const isClickInsideButton = dropdownButton.contains(e.target);
            const isClickInsideMenu = dropdownMenu.contains(e.target);
            if (!isClickInsideButton && !isClickInsideMenu) {
                dropdownMenu.classList.remove('show');
            }
        });
    }

    // Menú móvil
    if (menuBtn && menuMobile) {
        menuBtn.addEventListener('click', function () {
            menuMobile.classList.remove('hidden');
            document.body.style.overflow = 'hidden';
        });

        if (closeMenuBtn) {
            closeMenuBtn.addEventListener('click', closeAllMenus);
        }
    }

    // Submenú móvil
    if (submenuBtn && submenuMobile) {
        submenuBtn.addEventListener('click', function (e) {
            e.stopPropagation();
            submenuMobile.classList.toggle('open');
        });
    }

    // Modal de Iniciar Sesión
    if (loginBtn && loginModal) {
        loginBtn.addEventListener('click', function () {
            loginModal.classList.add('show');
            document.body.style.overflow = 'hidden';
        });
    }

    if (loginBtnMobile && loginModal) {
        loginBtnMobile.addEventListener('click', function () {
            loginModal.classList.add('show');
            menuMobile.classList.add('hidden');
            document.body.style.overflow = 'hidden';
        });
    }

    if (cancelLogin && loginModal) {
        cancelLogin.addEventListener('click', closeAllMenus);
    }

    // Modal de Perfil
    if (profileBtn && profileModal) {
        profileBtn.addEventListener('click', function () {
            profileModal.classList.add('show');
            document.body.style.overflow = 'hidden';
        });
    }

    if (profileBtnMobile && profileModal) {
        profileBtnMobile.addEventListener('click', function () {
            profileModal.classList.add('show');
            menuMobile.classList.add('hidden');
            document.body.style.overflow = 'hidden';
        });
    }

    // Cerrar menús al hacer clic en enlaces
    document.querySelectorAll('#menuMobile a, #dropdown-menu a').forEach(link => {
        link.addEventListener('click', closeAllMenus);
    });

    // Escape
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') closeAllMenus();
    });

    // Manejar el envío del formulario (simulación para mostrar el perfil)
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function (e) {
            // Esto se manejará principalmente en el backend, pero simulamos el perfil aquí
            const email = document.getElementById('email').value;
            const name = document.getElementById('name').value;
            if (profileModal) {
                document.getElementById('profileName').textContent = name;
                document.getElementById('profileEmail').textContent = email;
            }
        });
    }
});