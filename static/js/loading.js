document.addEventListener('DOMContentLoaded', function() {
    const loadingScreen = document.getElementById('loading-screen');
    const mainContent = document.getElementById('main-content');
    let pageLoaded = false;

    // Mostrar contenido principal cuando todo esté listo
    function showContent() {
        loadingScreen.style.opacity = '0';
        
        setTimeout(() => {
            loadingScreen.style.display = 'none';
            mainContent.classList.remove('hidden');
        }, 500); // Tiempo para la transición de opacidad
    }

    // Evento cuando toda la página ha cargado
    window.addEventListener('load', function() {
        pageLoaded = true;
        showContent();
    });

    // Timeout por si algo falla en la carga
    setTimeout(function() {
        if (!pageLoaded) {
            console.warn('La página tardó demasiado en cargar. Se ocultó la pantalla de carga por tiempo de espera.');
            showContent();
        }
    }, 5000); // 5 segundos de timeout máximo
});