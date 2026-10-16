-- ============================================================================
-- Base de Datos: hotel_costa_del_sol (PostgreSQL 15+)
-- Avance de Proyecto Final 3 (APF3 - Semana 12 - Alcance 70%)
-- Integrantes: Miguel Suarez, Neo Ventura, Max Rivera
-- ============================================================================

DROP TABLE IF EXISTS auditoria CASCADE;
DROP TABLE IF EXISTS pagos CASCADE;
DROP TABLE IF EXISTS estancias CASCADE;
DROP TABLE IF EXISTS reservas CASCADE;
DROP TABLE IF EXISTS habitaciones CASCADE;
DROP TABLE IF EXISTS tipos_habitacion CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

-- 1. Roles
CREATE TABLE roles (
    id_rol SERIAL PRIMARY KEY,
    codigo VARCHAR(30) UNIQUE NOT NULL,
    nombre VARCHAR(50) NOT NULL
);

-- 2. Usuarios
CREATE TABLE usuarios (
    id_usuario SERIAL PRIMARY KEY,
    id_rol INT NOT NULL REFERENCES roles(id_rol),
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(150) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(25) NOT NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Tipos de Habitacion
CREATE TABLE tipos_habitacion (
    id_tipo SERIAL PRIMARY KEY,
    codigo VARCHAR(30) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio_base_noche NUMERIC(10,2) NOT NULL,
    capacidad_maxima INT NOT NULL DEFAULT 2,
    imagen_url VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE
);

-- 4. Habitaciones
CREATE TABLE habitaciones (
    id_habitacion SERIAL PRIMARY KEY,
    id_tipo INT NOT NULL REFERENCES tipos_habitacion(id_tipo),
    numero VARCHAR(10) UNIQUE NOT NULL,
    piso INT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
    precio_por_noche NUMERIC(10,2) NOT NULL,
    observaciones VARCHAR(255)
);

-- 5. Reservas (Ampliado APF3 - 70%)
CREATE TABLE reservas (
    id_reserva SERIAL PRIMARY KEY,
    codigo_reserva VARCHAR(25) UNIQUE NOT NULL,
    id_cliente INT NOT NULL REFERENCES usuarios(id_usuario),
    id_tipo_habitacion INT NOT NULL REFERENCES tipos_habitacion(id_tipo),
    id_habitacion INT REFERENCES habitaciones(id_habitacion),
    fecha_checkin DATE NOT NULL,
    fecha_checkout DATE NOT NULL,
    numero_huespedes INT NOT NULL DEFAULT 1,
    solicita_factura BOOLEAN DEFAULT FALSE,
    comentarios VARCHAR(255),
    precio_alojamiento NUMERIC(10,2) NOT NULL,
    precio_servicios_extra NUMERIC(10,2) DEFAULT 0.00,
    monto_total NUMERIC(10,2) NOT NULL,
    estado VARCHAR(25) DEFAULT 'PENDIENTE',
    origen VARCHAR(20) DEFAULT 'WEB',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. Estancias Check-in / Check-out (APF3 - 70%)
CREATE TABLE estancias (
    id_estancia SERIAL PRIMARY KEY,
    id_reserva INT UNIQUE NOT NULL REFERENCES reservas(id_reserva) ON DELETE CASCADE,
    fecha_hora_checkin TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_hora_checkout TIMESTAMP,
    llave_asignada VARCHAR(30) NOT NULL,
    consumos_adicionales NUMERIC(10,2) DEFAULT 0.00,
    observaciones VARCHAR(255),
    id_recepcionista_checkin INT REFERENCES usuarios(id_usuario),
    id_recepcionista_checkout INT REFERENCES usuarios(id_usuario)
);

-- 7. Pagos y Verificacion de Vouchers (APF3 - 70%)
CREATE TABLE pagos (
    id_pago SERIAL PRIMARY KEY,
    id_reserva INT NOT NULL REFERENCES reservas(id_reserva) ON DELETE CASCADE,
    monto NUMERIC(10,2) NOT NULL,
    metodo_pago VARCHAR(40) NOT NULL,
    numero_operacion VARCHAR(50),
    comprobante_voucher VARCHAR(150),
    estado_pago VARCHAR(25) DEFAULT 'VERIFICADO',
    id_recepcionista INT REFERENCES usuarios(id_usuario),
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. Bitacora de Auditoria y Seguridad (APF3 - 70%)
CREATE TABLE auditoria (
    id_auditoria SERIAL PRIMARY KEY,
    id_usuario INT REFERENCES usuarios(id_usuario),
    modulo VARCHAR(40) NOT NULL,
    accion VARCHAR(50) NOT NULL,
    descripcion TEXT NOT NULL,
    ip_address VARCHAR(45) DEFAULT '127.0.0.1',
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- Datos Iniciales (Seed Data)
-- Passwords: 'admin123' / 'recep123' / 'client123' (BCrypt 12 rounds)
-- ============================================================================
INSERT INTO roles (codigo, nombre) VALUES
('ROLE_ADMIN', 'Administrador General'),
('ROLE_RECEPCION', 'Recepcionista'),
('ROLE_CLIENTE', 'Cliente Huesped');

INSERT INTO usuarios (id_rol, username, password_hash, nombre_completo, email, telefono, estado) VALUES
(1, 'admin', '$2a$12$vVp8PZeqoK9N9z3rFkM00.N07H26yT3e7o5y0iH1UuE3kF0QxRjfe', 'Max Rivera Moran', 'maxrivera938@gmail.com', '987654321', 'ACTIVO'),
(2, 'recepcion', '$2a$12$2oE50l7m7Fk2W/jA9zY1U.1QzM77qC9T4e8y1iH2VuF4lG1RySkfe', 'Miguel Angel Suarez', 'Miguelperdom543@gmail.com', '987654322', 'ACTIVO'),
(3, 'cliente', '$2a$12$9aD10k2n3Ej5V/iB8xX0T.0PyL66pB8S3d7x0hG0UtE2jE9QxRjfe', 'Neo Ventura Lorenzo', 'nventtura420@gmail.com', '987654323', 'ACTIVO');

INSERT INTO tipos_habitacion (codigo, nombre, descripcion, precio_base_noche, capacidad_maxima, imagen_url) VALUES
('simple', 'Habitacion Simple Confort', 'Ideal para viajeros individuales y estadias de negocios ejecutivas.', 120.00, 1, 'habitacionstandar.png'),
('matrimonial', 'Habitacion Matrimonial Deluxe', 'Cama King Size, aire acondicionado, smart TV y vista panoramica.', 180.00, 2, 'habitaciondeluxe.jpg'),
('doble', 'Habitacion Doble Twin', 'Dos camas de plaza y media, escritorio de trabajo y bano privado.', 220.00, 3, 'habitaciondeluxe.jpg'),
('suite_jr', 'Suite Junior Costa del Sol', 'Sala de estar independiente, frigobar surtido y tina de hidromasaje.', 320.00, 2, 'suitejunior.jpg'),
('suite_pres', 'Suite Presidencial Real', 'Maximo confort con terraza privada, jacuzzi, comedor y vistas exclusivas.', 550.00, 4, 'suitepresidencial.jpg');

INSERT INTO habitaciones (id_tipo, numero, piso, estado, precio_por_noche, observaciones) VALUES
(1, '101', 1, 'DISPONIBLE', 120.00, 'Frente al jardin interior'),
(1, '102', 1, 'OCUPADA', 120.00, 'Huesped con salida programada'),
(2, '201', 2, 'DISPONIBLE', 180.00, 'Cama King, balcon este'),
(2, '202', 2, 'MANTENIMIENTO', 180.00, 'Revision de aire acondicionado'),
(3, '203', 2, 'DISPONIBLE', 220.00, 'Dos camas Twin, iluminacion natural'),
(3, '301', 3, 'DISPONIBLE', 220.00, 'Piso silencioso para familias'),
(4, '401', 4, 'OCUPADA', 320.00, 'Suite Junior ocupada por reserva ejecutiva'),
(4, '402', 4, 'DISPONIBLE', 320.00, 'Equipada con hidromasaje'),
(5, '501', 5, 'DISPONIBLE', 550.00, 'Suite Presidencial con terraza VIP');

INSERT INTO reservas (codigo_reserva, id_cliente, id_tipo_habitacion, id_habitacion, fecha_checkin, fecha_checkout, numero_huespedes, solicita_factura, comentarios, precio_alojamiento, precio_servicios_extra, monto_total, estado, origen) VALUES
('WYN-2026-A102BC', 3, 1, 2, CURRENT_DATE - 1, CURRENT_DATE + 1, 1, TRUE, 'Viaje corporativo ejecutivo', 240.00, 0.00, 240.00, 'CHECK_IN', 'WEB'),
('WYN-2026-D401EF', 3, 4, 7, CURRENT_DATE, CURRENT_DATE + 3, 2, TRUE, 'Aniversario con vista panoramica', 960.00, 80.00, 1040.00, 'CHECK_IN', 'RECEPCION'),
('WYN-2026-P201GH', 3, 2, 3, CURRENT_DATE + 2, CURRENT_DATE + 5, 2, FALSE, 'Llegada en vuelo nocturno', 540.00, 0.00, 540.00, 'CONFIRMADA', 'WEB'),
('WYN-2026-X501JK', 3, 5, NULL, CURRENT_DATE + 4, CURRENT_DATE + 6, 3, TRUE, 'Requiere recojo aeropuerto', 1100.00, 120.00, 1220.00, 'PENDIENTE', 'WEB');

INSERT INTO estancias (id_reserva, llave_asignada, consumos_adicionales, observaciones, id_recepcionista_checkin) VALUES
(1, 'KEY-RFID-102A', 35.00, 'Huesped instalado sin novedades', 2),
(2, 'KEY-RFID-401B', 80.00, 'Amenidades VIP entregadas en Suite Junior', 2);

INSERT INTO pagos (id_reserva, monto, metodo_pago, numero_operacion, comprobante_voucher, estado_pago, id_recepcionista) VALUES
(1, 240.00, 'TARJETA_CREDITO', 'OP-908123', 'VOUCHER-102.pdf', 'VERIFICADO', 2),
(2, 1040.00, 'TRANSFERENCIA_BANCARIA', 'BCP-445512', 'VOUCHER-401.pdf', 'VERIFICADO', 2),
(3, 540.00, 'YAPE_PLIN', 'YP-778899', 'VOUCHER-201.png', 'VERIFICADO', 2);

INSERT INTO auditoria (id_usuario, modulo, accion, descripcion, ip_address) VALUES
(1, 'SEGURIDAD', 'INIT_SYSTEM', 'Inicializacion de arquitectura MVC, DAO, SOLID y librerias APF3', '127.0.0.1'),
(2, 'RECEPCION', 'CHECK_IN', 'Check-in registrado para reserva WYN-2026-A102BC en habitacion 102', '192.168.1.15'),
(2, 'RECEPCION', 'VERIFICAR_PAGO', 'Pago verificado por S/ 1040.00 para reserva WYN-2026-D401EF', '192.168.1.15');
