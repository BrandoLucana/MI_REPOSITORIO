window.addEventListener("load", function () {
    const loader = document.querySelector(".loader");

    // Espera al menos 2 segundos antes de ocultar el loader
    setTimeout(() => {
        loader.classList.add("hidden");
    }, 2000); // 2000 milisegundos = 2 segundos
});