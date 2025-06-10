const menuBtn = document.getElementById('menuBtn');
const menuMobile = document.getElementById('menuMobile');
    menuBtn.addEventListener('click', () => {
    menuMobile.classList.toggle('hidden');
});