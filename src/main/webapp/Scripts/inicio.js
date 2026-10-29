// ── 1. AUDIO AMBIENTE (volumen bajo, se activa al interactuar) ──────────────
const audioAmbiente = document.getElementById('audioAmbiente');

if (audioAmbiente) {
    audioAmbiente.volume = 0.15;   // volumen bajo (0.0 → 1.0)

    // Los navegadores bloquean autoplay con sonido: esperamos primer clic del usuario
    function iniciarAudio() {
        audioAmbiente.play().catch(() => { }); // silenciar error si sigue bloqueado
        document.removeEventListener('click', iniciarAudio);
        document.removeEventListener('keydown', iniciarAudio);
    }

    document.addEventListener('click', iniciarAudio);
    document.addEventListener('keydown', iniciarAudio);
}

// ── 2. HEADER STICKY CON EFECTO SCROLL ──────────────────────────────────────
const headerNav = document.getElementById('header-nav');

window.addEventListener('scroll', function () {
    if (window.scrollY > 50) {
        headerNav.classList.add('scrolled');
    } else {
        headerNav.classList.remove('scrolled');
    }
});

// ── 3. MENÚ RESPONSIVO (hamburguesa) ────────────────────────────────────────
const btnMenu = document.getElementById('btn-menu');
const navPrincipal = document.getElementById('nav-principal');

if (btnMenu && navPrincipal) {
    btnMenu.addEventListener('click', function () {
        const estaAbierto = navPrincipal.classList.toggle('nav-abierto');
        btnMenu.classList.toggle('abierto', estaAbierto);
        btnMenu.setAttribute('aria-expanded', estaAbierto);
    });

    // Cerrar menú al hacer clic en un enlace
    navPrincipal.querySelectorAll('a').forEach(function (enlace) {
        enlace.addEventListener('click', function () {
            navPrincipal.classList.remove('nav-abierto');
            btnMenu.classList.remove('abierto');
            btnMenu.setAttribute('aria-expanded', false);
        });
    });
}

// ── 4. SCROLL SUAVE HACIA CONTENIDO (botón "Explorar") ──────────────────────
const btnExplorar = document.getElementById('btn-explorar');
if (btnExplorar) {
    btnExplorar.addEventListener('click', function (e) {
        e.preventDefault();
        const destino = document.getElementById('contenido-principal');
        if (destino) {
            destino.scrollIntoView({ behavior: 'smooth' });
        }
    });
}

// ── 5. SCROLL REVEAL (aparición de secciones al bajar) ──────────────────────
const elementosReveal = document.querySelectorAll(
    '.seccion-servicios, .seccion-horarios, .seccion-conocenos, .seccion-redes, .card'
);

elementosReveal.forEach(function (el) {
    el.classList.add('reveal');
});

const observador = new IntersectionObserver(function (entradas) {
    entradas.forEach(function (entrada) {
        if (entrada.isIntersecting) {
            entrada.target.classList.add('visible');
            observador.unobserve(entrada.target); // dejar de observar una vez visible
        }
    });
}, { threshold: 0.12 });

elementosReveal.forEach(function (el) {
    observador.observe(el);
});

// ── 6. AÑO DINÁMICO EN FOOTER ────────────────────────────────────────────────
const footerAnio = document.querySelector('footer p:last-child');
if (footerAnio) {
    const anioActual = new Date().getFullYear();
    footerAnio.textContent =
        '© ' + anioActual + ' Hotel Wyndham Costa del Sol Chiclayo';
}

// ── 7. OCULTAR ENLACE DE LOGIN SI HAY SESIÓN ACTIVA ─────────────────────────
(function () {
    const sesionData = sessionStorage.getItem('wyndham_sesion');
    const enlaceLogin = document.querySelector('.btn-login');
    if (sesionData && enlaceLogin) {
        try {
            const sesion = JSON.parse(sesionData);
            if (sesion && sesion.activo) {
                // Ocultar el enlace para que no vuelva a mostrarse "Iniciar Sesión"
                enlaceLogin.style.display = 'none';

                // Agregar saludo y botón de Cerrar Sesión al menú si no existen
                var ulNav = (navPrincipal && navPrincipal.querySelector('ul')) || document.querySelector('#nav-principal ul');
                if (ulNav) {
                    // Botón logout
                    if (!document.getElementById('li-logout')) {
                        const li = document.createElement('li');
                        li.id = 'li-logout';
                        const btn = document.createElement('button');
                        btn.id = 'btn-logout';
                        btn.className = 'btn-logout pulse';
                        btn.textContent = 'Cerrar Sesión';
                        btn.addEventListener('click', function () {
                            sessionStorage.removeItem('wyndham_sesion');
                            window.location.href = 'index.html';
                        });
                        li.appendChild(btn);
                        ulNav.appendChild(li);
                    }
                }
            }
        } catch (e) {
            // no bloquear si hay error en parsing
            console.warn('Error leyendo sesión:', e);
        }
    }
})();
