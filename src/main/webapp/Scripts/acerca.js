// ── AUDIO AMBIENTE ────────────────────────────────────────────────────────────
const audioAcerca = document.getElementById('audioAcerca');
if (audioAcerca) {
    audioAcerca.volume = 0.12;
    function iniciarAudio() {
        audioAcerca.play().catch(() => { });
        document.removeEventListener('click', iniciarAudio);
    }
    document.addEventListener('click', iniciarAudio);
}

// ── MENÚ RESPONSIVO ───────────────────────────────────────────────────────────
const btnMenu = document.getElementById('btn-menu');
const navPrincipal = document.getElementById('nav-principal');

if (btnMenu && navPrincipal) {
    btnMenu.addEventListener('click', function () {
        const abierto = navPrincipal.classList.toggle('nav-abierto');
        btnMenu.classList.toggle('abierto', abierto);
        btnMenu.setAttribute('aria-expanded', abierto);
    });

    navPrincipal.querySelectorAll('a').forEach(function (a) {
        a.addEventListener('click', function () {
            navPrincipal.classList.remove('nav-abierto');
            btnMenu.classList.remove('abierto');
            btnMenu.setAttribute('aria-expanded', false);
        });
    });
}

// ── AÑO DINÁMICO ─────────────────────────────────────────────────────────────
const footerAnio = document.querySelector('footer p:last-child');
if (footerAnio) {
    footerAnio.textContent =
        '© ' + new Date().getFullYear() + ' Hotel Wyndham Costa del Sol Chiclayo';
}

// Ocultar enlace "Iniciar Sesión" y añadir botón Cerrar Sesión si hay sesión activa
(function () {
    const sesionData = sessionStorage.getItem('wyndham_sesion');
    const enlaceLogin = document.querySelector('.btn-login');
    if (sesionData && enlaceLogin) {
        try {
            const sesion = JSON.parse(sesionData);
            if (sesion && sesion.activo) {
                enlaceLogin.style.display = 'none';
                const ulNav = (navPrincipal && navPrincipal.querySelector('ul')) || document.querySelector('#nav-principal ul');
                if (ulNav) {
                    if (!document.getElementById('li-logout')) {
                        const li = document.createElement('li');
                        li.id = 'li-logout';
                        const btn = document.createElement('button');
                        btn.id = 'btn-logout';
                        btn.className = 'btn-logout pulse';
                        btn.textContent = 'Cerrar Sesión';
                        btn.addEventListener('click', function () {
                            sessionStorage.removeItem('wyndham_sesion');
                            window.location.href = '../index.html';
                        });
                        li.appendChild(btn);
                        ulNav.appendChild(li);
                    }
                }
            }
        } catch (e) { }
    }
})();
