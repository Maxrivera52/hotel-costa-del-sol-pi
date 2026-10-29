// ── 1. AUDIO ────────────────────────────────────────────────────────────────
const audioRest = document.getElementById('audioRestaurant');
if (audioRest) {
    audioRest.volume = 0.12;
    function iniciarAudio() {
        audioRest.play().catch(() => { });
        document.removeEventListener('click', iniciarAudio);
    }
    document.addEventListener('click', iniciarAudio);
}

// ── 2. MENÚ RESPONSIVO ────────────────────────────────────────────────────────
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

// ── 3. FILTROS DE MENÚ (DOM + Estructuras de control) ────────────────────────
const botonesFilro = document.querySelectorAll('.filtro-btn');
const platosCards = document.querySelectorAll('.plato-card');
const sinResultados = document.getElementById('sin-resultados');

botonesFilro.forEach(function (btn) {
    btn.addEventListener('click', function () {
        // Actualizar botón activo (DOM)
        botonesFilro.forEach(function (b) { b.classList.remove('activo'); });
        btn.classList.add('activo');

        const filtro = btn.getAttribute('data-filtro');
        let visibles = 0;

        // Estructura de control: mostrar/ocultar según categoría
        platosCards.forEach(function (card) {
            const categoria = card.getAttribute('data-categoria');

            if (filtro === 'todos' || categoria === filtro) {
                card.classList.remove('oculto');
                visibles++;
            } else {
                card.classList.add('oculto');
            }
        });

        // Mostrar mensaje si no hay resultados
        if (sinResultados) {
            if (visibles === 0) {
                sinResultados.classList.remove('oculto');
            } else {
                sinResultados.classList.add('oculto');
            }
        }
    });
});

// ── 4. DATOS DE PLATOS ────────────────────────────────────────────────────────
// Estructura de control: objeto que actúa como mapa de platos
const datosPlatos = {
    'desayuno-americano': {
        badge: 'Desayuno',
        titulo: 'Desayuno Americano',
        precio: 'S/. 38.00 por persona',
        img: '../image/DesayunoAmericano.jpg',
        descripcion: 'Huevos preparados al gusto (fritos, revueltos o en tortilla), tocino crocante, tostadas con mantequilla y mermelada, jugo de naranja recién exprimido y café o té a elección.',
        nota: '* Servido de 6:30 am a 10:00 am. Consultar opción sin gluten.'
    },
    'buffet-continental': {
        badge: 'Desayuno',
        titulo: 'Buffet Continental',
        precio: 'S/. 55.00 por persona',
        img: '../image/Inicio.jpg',
        descripcion: 'Estación libre de panes artesanales, frutas de temporada, yogures, cereales, embutidos, quesos y una selección de bebidas calientes y jugos naturales.',
        nota: '* Precio incluye acceso ilimitado a la estación de buffet.'
    },
    'almuerzo-ejecutivo': {
        badge: 'Almuerzo',
        titulo: 'Almuerzo Ejecutivo',
        precio: 'S/. 45.00 por persona',
        img: '../image/AlmuerzoEjecutivo.jpg',
        descripcion: 'Menú de tres tiempos que varía cada día: entrada del día, plato de fondo a elección entre carne, pollo o pescado, postre casero y bebida incluida.',
        nota: '* Disponible de lunes a viernes de 12:00 pm a 3:00 pm.'
    },
    'causa-nortena': {
        badge: 'Almuerzo',
        titulo: 'Causa a la Norteña',
        precio: 'S/. 32.00 por porción',
        img: '../image/piscina.jpg',
        descripcion: 'Causa de papa amarilla sazonada con ají amarillo, rellena de una generosa porción de mariscos frescos y coronada con salsa criolla.',
        nota: '* Plato bandera de la cocina norteña, preparado el mismo día.'
    },
    'cena-carta': {
        badge: 'Cena',
        titulo: 'Cena a la Carta',
        precio: 'Desde S/. 65.00 por persona',
        img: '../image/CenaAlaCarta.jpg',
        descripcion: 'Selección premium de carnes, pescados y mariscos preparados al momento por nuestro chef, acompañados de guarniciones de temporada.',
        nota: '* Precio varía según el corte o pescado elegido. Consultar carta completa en sala.'
    },
    'cena-romantica': {
        badge: 'Cena',
        titulo: 'Cena Romántica',
        precio: 'S/. 180.00 (para 2 personas)',
        img: '../image/suitepresidencial.jpg',
        descripcion: 'Menú especial para parejas que incluye entrada compartida, plato principal a elección, postre para compartir y una copa de vino por persona, en un ambiente íntimo con velas.',
        nota: '* Reserva con al menos 24 horas de anticipación.'
    }
};

// ── 5. VENTANA FLOTANTE (MODAL) DE PLATOS ─────────────────────────────────────
const modalOverlay = document.getElementById('modal-overlay');
const modalCerrar = document.getElementById('modal-cerrar');
const modalImg = document.getElementById('modal-img');
const modalBadge = document.getElementById('modal-badge');
const modalTitulo = document.getElementById('modal-titulo');
const modalPrecio = document.getElementById('modal-precio');
const modalDescripcion = document.getElementById('modal-descripcion');
const modalNota = document.getElementById('modal-nota');
const btnReservar = document.getElementById('btn-reservar');

// Abrir modal al hacer clic en una tarjeta o en su botón "Ver detalles"
platosCards.forEach(function (card) {
    card.addEventListener('click', function () {
        const clave = card.getAttribute('data-plato');

        // Estructura de control: verificar que el plato existe en el mapa (priorizando datos dinámicos inyectados)
        const plato = (window.datosPlatos && window.datosPlatos[clave]) || datosPlatos[clave];
        if (!clave || !plato) return;

        // Manipulación del DOM: inyectar datos del plato en el modal
        modalImg.src = plato.img;
        modalImg.alt = plato.titulo;
        modalBadge.textContent = plato.badge;
        modalTitulo.textContent = plato.titulo;
        modalPrecio.textContent = plato.precio;
        modalDescripcion.textContent = plato.descripcion;
        modalNota.textContent = plato.nota;

        modalOverlay.setAttribute('data-clave-actual', clave);
        modalOverlay.classList.remove('oculto');
        document.body.style.overflow = 'hidden'; // evitar scroll del fondo
    });
});

// Cerrar modal con botón
if (modalCerrar) {
    modalCerrar.addEventListener('click', cerrarModal);
}

// Cerrar modal haciendo clic fuera de la tarjeta
if (modalOverlay) {
    modalOverlay.addEventListener('click', function (e) {
        if (e.target === modalOverlay) cerrarModal();
    });
}

// Cerrar modal con tecla Escape
document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') cerrarModal();
});

function cerrarModal() {
    modalOverlay.classList.add('oculto');
    document.body.style.overflow = '';
}

// Botón Reservar: redirige a Contacto
if (btnReservar) {
    btnReservar.addEventListener('click', function () {
        const claveActual = modalOverlay.getAttribute('data-clave-actual');
        cerrarModal();
        window.location.href = 'Contacto.html?ref=plato:' + encodeURIComponent(claveActual);
    });
}

// ── 6. AÑO DINÁMICO EN FOOTER ─────────────────────────────────────────────────
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
