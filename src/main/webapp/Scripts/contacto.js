// ── AUDIO AMBIENTE ────────────────────────────────────────────────────────────
const audioContacto = document.getElementById('audioContacto');
if (audioContacto) {
    audioContacto.volume = 0.12;
    function iniciarAudio() {
        audioContacto.play().catch(() => { });
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

// ── FUNCIONES UI (toast) ─────────────────────────────────────────────────────
function showToast(text, duration) {
    duration = duration || 3000;
    const t = document.createElement('div');
    t.className = 'toast';
    t.textContent = text;
    document.body.appendChild(t);
    // force reflow to enable transition
    void t.offsetWidth;
    t.classList.add('show');
    setTimeout(function () {
        t.classList.remove('show');
        setTimeout(function () { t.remove(); }, 250);
    }, duration);
}

// ── VALIDACIÓN DE SESIÓN (simulada) ─────────────────────────────────────────
function verificarSesion() {
    const sesionData = sessionStorage.getItem('wyndham_sesion');
    if (!sesionData) {
        alert('Debes iniciar sesión para reservar. Serás redirigido a la página de inicio de sesión.');
        window.location.href = 'InicioSesion.html';
        return false;
    }

    try {
        const sesion = JSON.parse(sesionData);
        if (!sesion || !sesion.activo) {
            alert('Debes iniciar sesión para reservar. Serás redirigido a la página de inicio de sesión.');
            window.location.href = 'InicioSesion.html';
            return false;
        }
    } catch (e) {
        alert('Debes iniciar sesión para reservar. Serás redirigido a la página de inicio de sesión.');
        window.location.href = 'InicioSesion.html';
        return false;
    }

    return true;
}

// ── PRESELECCIONAR PAQUETE/PLATO DESDE LA URL ────────────────────────────────
(function () {
    const params = new URLSearchParams(window.location.search);
    const ref = params.get('ref');
    const selectRef = document.getElementById('referencia');
    if (ref && selectRef) {
        selectRef.value = ref;
    }
})();

// ── ENVÍO DEL FORMULARIO ────────────────────────────────────────────────────
const formReserva = document.getElementById('formReserva');

if (formReserva) {
    formReserva.addEventListener('submit', function (e) {
        e.preventDefault();

        // Verificar sesión antes de procesar el formulario
        if (!verificarSesion()) return;

        // Recoger datos
        const nombre = document.getElementById('nombre').value.trim();
        const email = document.getElementById('email').value.trim();
        const telefono = document.getElementById('telefono').value.trim();
        const password = document.getElementById('password').value;
        const habitacion = document.getElementById('habitacion').value;
        const referencia = document.getElementById('referencia').value;
        const checkin = document.getElementById('checkin').value;
        const checkout = document.getElementById('checkout').value;
        const huespedes = document.getElementById('huespedes').value;

        // Validaciones básicas
        if (!nombre || !email || !telefono || !password || !habitacion || !checkin || !checkout) {
            alert('⚠️ Por favor, completa todos los campos obligatorios.');
            return;
        }

        if (password.length < 6) {
            alert('⚠️ La contraseña debe tener al menos 6 caracteres.');
            return;
        }

        if (new Date(checkin) >= new Date(checkout)) {
            alert('⚠️ La fecha de check-out debe ser posterior al check-in.');
            return;
        }

        // Servicios adicionales seleccionados
        const adicionales = [];
        document.querySelectorAll('.check-group input[type="checkbox"]:checked').forEach(function (cb) {
            adicionales.push(cb.value);
        });

        const factura = document.querySelector('input[name="factura"]:checked').value;
        const mensaje = document.getElementById('mensaje').value.trim();

        // Guardar la reserva en localStorage (persiste entre sesiones)
        const RESERVAS_CLAVE = 'wyndham_reservas';
        const reservas = JSON.parse(localStorage.getItem(RESERVAS_CLAVE) || '[]');
        reservas.push({
            nombre, email, telefono, habitacion, referencia,
            checkin, checkout, huespedes, adicionales, factura, mensaje,
            fecha: new Date().toISOString()
        });
        localStorage.setItem(RESERVAS_CLAVE, JSON.stringify(reservas));

        // Mostrar notificación breve tipo "toast"
        showToast('Reserva registrada exitosamente', 3000);

        // Limpiar formulario de registro y reserva inmediatamente
        try {
            formReserva.reset();
        } catch (e) { }

        // Aquí podrías enviar los datos a un servidor con fetch()
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
