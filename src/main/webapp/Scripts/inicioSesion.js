// ── 1. BASE DE USUARIOS ──────────────────────────────
// En un proyecto real esto vendría del servidor. Aquí lo guardamos en
// sessionStorage para que el registro funcione entre páginas.

const USUARIOS_CLAVE = 'wyndham_usuarios';

function obtenerUsuarios() {
    const datos = localStorage.getItem(USUARIOS_CLAVE);
    if (datos) return JSON.parse(datos);
    // Usuarios por defecto para probar
    return [
        { usuario: 'admin', password: 'admin123', nombre: 'Administrador' },
        { usuario: 'invitado', password: '1234', nombre: 'Invitado' }
    ];
}

function guardarUsuarios(lista) {
    localStorage.setItem(USUARIOS_CLAVE, JSON.stringify(lista));
}

// ── 2. AUDIO DE AMBIENTE ──────────────────────────────────────────────────────
const audioLogin = document.getElementById('audioLogin');

if (audioLogin) {
    audioLogin.volume = 0.1;
    function iniciarAudio() {
        audioLogin.play().catch(() => { });
        document.removeEventListener('click', iniciarAudio);
    }
    document.addEventListener('click', iniciarAudio);
}

    // ── FUNCIONES UI (toast) ─────────────────────────────────────────────────────
    function showToast(text, duration) {
        duration = duration || 3000;
        const t = document.createElement('div');
        t.className = 'toast';
        t.textContent = text;
        document.body.appendChild(t);
        void t.offsetWidth;
        t.classList.add('show');
        setTimeout(function () {
            t.classList.remove('show');
            setTimeout(function () { t.remove(); }, 250);
        }, duration);
    }

// ── 3. REFERENCIAS AL DOM ────────────────────────────────────────────────────
const loginCard = document.getElementById('login-card');
const registroCard = document.getElementById('registro-card');
const linkRegistro = document.getElementById('link-registro');
const linkLogin = document.getElementById('link-login');

// Campos de login
const campoUsuario = document.getElementById('usuario');
const campoPass = document.getElementById('password');
const btnIngresar = document.getElementById('btn-ingresar');
const msgLogin = document.getElementById('mensaje-login');
const errUsuario = document.getElementById('error-usuario');
const errPass = document.getElementById('error-password');
const btnVerPass = document.getElementById('btn-ver-pass');

// Campos de registro
const campoNombre = document.getElementById('reg-nombre');
const campoRegUser = document.getElementById('reg-usuario');
const campoRegPass = document.getElementById('reg-password');
const btnRegistrar = document.getElementById('btn-registrar');
const msgRegistro = document.getElementById('mensaje-registro');
const errRegNombre = document.getElementById('error-reg-nombre');
const errRegUser = document.getElementById('error-reg-usuario');
const errRegPass = document.getElementById('error-reg-password');

// ── 4. ALTERNAR FORMULARIOS ──────────────────────────────────────────────────
if (linkRegistro) {
    linkRegistro.addEventListener('click', function (e) {
        e.preventDefault();
        loginCard.classList.add('oculto');
        registroCard.classList.remove('oculto');
        limpiarMensajes();
    });
}

if (linkLogin) {
    linkLogin.addEventListener('click', function (e) {
        e.preventDefault();
        registroCard.classList.add('oculto');
        loginCard.classList.remove('oculto');
        limpiarMensajes();
    });
}

// ── 5. MOSTRAR / OCULTAR CONTRASEÑA ──────────────────────────────────────────
if (btnVerPass && campoPass) {
    btnVerPass.addEventListener('click', function () {
        if (campoPass.type === 'password') {
            campoPass.type = 'text';
            btnVerPass.textContent = '🙈';
        } else {
            campoPass.type = 'password';
            btnVerPass.textContent = '👁';
        }
    });
}

// ── 6. VALIDACIÓN Y LOGIN ────────────────────────────────────────────────────
if (btnIngresar) {
    btnIngresar.addEventListener('click', function () {
        limpiarMensajes();
        let valido = true;

        // ── Estructura de control: validación de campos vacíos ──
        if (campoUsuario.value.trim() === '') {
            mostrarError(errUsuario, campoUsuario, 'El usuario es obligatorio.');
            valido = false;
        }

        if (campoPass.value.trim() === '') {
            mostrarError(errPass, campoPass, 'La contraseña es obligatoria.');
            valido = false;
        }

        if (!valido) return; // Estructura de control: salir si hay errores

        // ── Estructura de control: buscar usuario en la lista ──
        const usuarios = obtenerUsuarios();
        let usuarioEncontrado = null;

        for (let i = 0; i < usuarios.length; i++) {
            if (usuarios[i].usuario === campoUsuario.value.trim() &&
                usuarios[i].password === campoPass.value) {
                usuarioEncontrado = usuarios[i];
                break;
            }
        }

        // ── Estructura de control: resultado del login ──
        if (usuarioEncontrado !== null) {
            // Guardar sesión activa
            sessionStorage.setItem('wyndham_sesion', JSON.stringify({
                usuario: usuarioEncontrado.usuario,
                nombre: usuarioEncontrado.nombre,
                activo: true
            }));
            mostrarMensaje(msgLogin, 'exito',
                '✔ Bienvenido, ' + usuarioEncontrado.nombre + '! Redirigiendo...');

            // Redirigir al inicio después de 1.5 s
            setTimeout(function () {
                window.location.href = '../index.html';
            }, 1500);

        } else {
            mostrarMensaje(msgLogin, 'error',
                '✗ Usuario o contraseña incorrectos. Inténtalo de nuevo.');
        }
    });
}

// Permitir login con tecla Enter
[campoUsuario, campoPass].forEach(function (campo) {
    if (campo) {
        campo.addEventListener('keydown', function (e) {
            if (e.key === 'Enter') btnIngresar.click();
        });
    }
});

// ── 7. VALIDACIÓN Y REGISTRO ──────────────────────────────────────────────────
if (btnRegistrar) {
    btnRegistrar.addEventListener('click', function () {
        limpiarMensajes();
        let valido = true;

        // Validar nombre
        if (campoNombre.value.trim().length < 3) {
            mostrarError(errRegNombre, campoNombre, 'Ingresa tu nombre completo (mín. 3 letras).');
            valido = false;
        }

        // Validar nombre de usuario (solo letras/números/guiones)
        const regexUser = /^[a-zA-Z0-9_áéíóúÁÉÍÓÚüÜ]{3,20}$/;
        if (!regexUser.test(campoRegUser.value.trim())) {
            mostrarError(errRegUser, campoRegUser, 'Usuario: 3–20 caracteres, sin espacios ni símbolos.');
            valido = false;
        }

        // Validar contraseña
        if (campoRegPass.value.length < 6) {
            mostrarError(errRegPass, campoRegPass, 'La contraseña debe tener al menos 6 caracteres.');
            valido = false;
        }

        if (!valido) return;

        // Verificar si el usuario ya existe
        const usuarios = obtenerUsuarios();
        const existe = usuarios.some(function (u) {
            return u.usuario === campoRegUser.value.trim();
        });

        if (existe) {
            mostrarError(errRegUser, campoRegUser, 'Ese nombre de usuario ya está en uso. Elige otro.');
            return;
        }

        // Agregar nuevo usuario
        const nuevoUsuario = {
            usuario: campoRegUser.value.trim(),
            password: campoRegPass.value,
            nombre: campoNombre.value.trim()
        };

        usuarios.push(nuevoUsuario);
        guardarUsuarios(usuarios);

        mostrarMensaje(msgRegistro, 'exito',
            '✔ Cuenta creada correctamente. Ahora puedes iniciar sesión.');

        // Auto-login: guardar sesión y notificar brevemente, luego redirigir
        sessionStorage.setItem('wyndham_sesion', JSON.stringify({
            usuario: nuevoUsuario.usuario,
            nombre: nuevoUsuario.nombre,
            activo: true
        }));

        // Mostrar toast de registro y redirigir al inicio
        try {
            showToast('Registrado correctamente', 3000);
        } catch (e) { }

        setTimeout(function () {
            window.location.href = '../index.html';
        }, 1200);
    });
}

// ── 8. FUNCIONES AUXILIARES (DOM) ────────────────────────────────────────────
function mostrarError(spanError, campo, texto) {
    spanError.textContent = texto;
    campo.classList.add('invalido');
}

function mostrarMensaje(div, tipo, texto) {
    div.textContent = texto;
    div.className = 'mensaje ' + tipo;  // 'mensaje exito' o 'mensaje error'
}

function limpiarMensajes() {
    // Limpiar errores por campo
    [errUsuario, errPass, errRegNombre, errRegUser, errRegPass].forEach(function (span) {
        if (span) span.textContent = '';
    });

    // Quitar clase invalido de inputs
    document.querySelectorAll('.form-grupo input').forEach(function (inp) {
        inp.classList.remove('invalido');
    });

    // Ocultar mensajes generales
    [msgLogin, msgRegistro].forEach(function (div) {
        if (div) {
            div.textContent = '';
            div.className = 'mensaje oculto';
        }
    });
}
