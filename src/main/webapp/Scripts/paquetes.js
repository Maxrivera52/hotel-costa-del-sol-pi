// ── 1. AUDIO AMBIENTE ─────────────────────────────────────────────────────────
const audioPaquetes = document.getElementById('audioPaquetes');
if (audioPaquetes) {
    audioPaquetes.volume = 0.12;
    function iniciarAudio() {
        audioPaquetes.play().catch(() => { });
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

// ── 3. SALUDO DE SESIÓN (DOM) ─────────────────────────────────────────────────
const sesionData = sessionStorage.getItem('wyndham_sesion');
const saludoDiv = document.getElementById('saludo-sesion');
const nombreSesion = document.getElementById('nombre-sesion');

if (sesionData && saludoDiv && nombreSesion) {
    const sesion = JSON.parse(sesionData);
    if (sesion.activo) {
        nombreSesion.textContent = sesion.nombre;
        saludoDiv.classList.remove('oculto');
    }
}

// ── 4. DATOS DE PAQUETES ──────────────────────────────────────────────────────
// Estructura de control: objeto que actúa como mapa de paquetes
const datosPaquetes = {
    matrimonial: {
        badge: 'Romántico',
        titulo: 'Paquete Matrimonial',
        precio: 'Desde S/. 850 por pareja',
        img: '../image/PaqueteMatrimonial.jpg',
        incluye: [
            '2 noches en Suite Presidencial',
            'Cena romántica para 2 personas',
            'Botella de champagne de bienvenida',
            'Decoración floral del cuarto',
            'Sesión de spa (60 min por persona)',
            'Desayuno buffet incluido'
        ],
        nota: '* Reserva con mínimo 7 días de anticipación.'
    },
    familiar: {
        badge: 'Familia',
        titulo: 'Paquete Familiar',
        precio: 'Desde S/. 620 por familia',
        img: '../image/PaqueteFamiliar.jpg',
        incluye: [
            '2 noches en habitación familiar (4 personas)',
            'Acceso libre a piscina y áreas recreativas',
            'Desayuno buffet incluido para todos',
            'Tour guiado a sitios arqueológicos',
            'Kit de bienvenida para niños',
            'Check-out extendido hasta las 2:00 pm'
        ],
        nota: '* Válido para grupos de hasta 4 personas. Cargo adicional por persona extra.'
    },
    cumpleaños: {
        badge: 'Celebración',
        titulo: 'Paquete Cumpleaños',
        precio: 'Desde S/. 490 por persona',
        img: '../image/PaqueteCumpleañero.jpg',
        incluye: [
            '1 noche en habitación Deluxe',
            'Torta de cumpleaños personalizada',
            'Decoración temática del cuarto',
            'Cóctel de bienvenida',
            'Desayuno especial en la habitación',
            'Tarjeta de felicitación del hotel'
        ],
        nota: '* Disponible solo en la fecha de cumpleaños (se solicita DNI).'
    },
    eventos: {
        badge: 'Corporativo',
        titulo: 'Paquete Eventos',
        precio: 'Desde S/. 1,200 por evento',
        img: '../image/PaqueteEventos.jpg',
        incluye: [
            'Salón de eventos para hasta 100 personas',
            'Equipo audiovisual (proyector, pantalla, micrófonos)',
            'Coffee break (mañana y tarde)',
            'Almuerzo ejecutivo para todos los asistentes',
            'Estacionamiento gratuito',
            'Coordinador de eventos asignado'
        ],
        nota: '* Precio por evento de 8 horas. Consultar tarifas por día completo.'
    }
};

// ── 5. VENTANAS FLOTANTES (MODAL) ─────────────────────────────────────────────
const modalOverlay = document.getElementById('modal-overlay');
const modalCerrar = document.getElementById('modal-cerrar');
const modalImg = document.getElementById('modal-img');
const modalBadge = document.getElementById('modal-badge');
const modalTitulo = document.getElementById('modal-titulo');
const modalPrecio = document.getElementById('modal-precio');
const modalIncluye = document.getElementById('modal-incluye');
const modalNota = document.getElementById('modal-nota');
const btnReservar = document.getElementById('btn-reservar');

// Abrir modal al hacer clic en una tarjeta
document.querySelectorAll('.paquete-card').forEach(function (card) {
    card.addEventListener('click', function () {
        const clave = card.getAttribute('data-paquete');

        // Estructura de control: verificar que el paquete existe (priorizando datos dinámicos inyectados)
        const paquete = (window.datosPaquetes && window.datosPaquetes[clave]) || datosPaquetes[clave];
        if (!clave || !paquete) return;

        // Manipulación del DOM: inyectar datos al modal
        modalImg.src = paquete.img;
        modalImg.alt = paquete.titulo;
        modalBadge.textContent = paquete.badge;
        modalTitulo.textContent = paquete.titulo;
        modalPrecio.textContent = paquete.precio;
        modalNota.textContent = paquete.nota;

        // Construir lista de incluye con DOM
        modalIncluye.innerHTML = '';
        paquete.incluye.forEach(function (item) {
            const li = document.createElement('li');
            li.textContent = item;
            modalIncluye.appendChild(li);
        });

        // Mostrar modal
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
        window.location.href = 'Contacto.html?ref=paquete:' + encodeURIComponent(claveActual);
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
