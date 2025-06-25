document.addEventListener('DOMContentLoaded', function() {
    // Verificar si ya existe el botón
    let botonSubir = document.getElementById('boton-subir');
    
    if (!botonSubir) {
        // Crear el botón si no existe
        botonSubir = document.createElement('div');
        botonSubir.id = 'boton-subir';
        botonSubir.className = 'boton-subir';
        document.body.appendChild(botonSubir);
    }

    // Controlar visibilidad
    window.addEventListener('scroll', function() {
        if (window.pageYOffset > 300) {
            botonSubir.classList.add('mostrar');
        } else {
            botonSubir.classList.remove('mostrar');
        }
    });

    // Función para subir
    botonSubir.addEventListener('click', function(e) {
        e.preventDefault();
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    });

    // Verificar si hay errores
    console.log('Botón de subir inicializado correctamente');
});