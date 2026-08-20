-- ============================================================================
-- Base de Datos: hotel_costa_del_sol (PostgreSQL 15+)
-- Avance de Proyecto Final 2 (APF2 - Semana 10)
-- Integrantes: Miguel Suarez, Neo Ventura, Max Rivera
-- ============================================================================

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

-- 5. Reservas
CREATE TABLE reservas (
    id_reserva SERIAL PRIMARY KEY,
    codigo_reserva VARCHAR(20) UNIQUE NOT NULL,
    id_cliente INT NOT NULL REFERENCES usuarios(id_usuario),
    id_habitacion INT NOT NULL REFERENCES habitaciones(id_habitacion),
    fecha_checkin DATE NOT NULL,
    fecha_checkout DATE NOT NULL,
    total NUMERIC(10,2) NOT NULL,
    estado VARCHAR(20) DEFAULT 'CONFIRMADA',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
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
('simple', 'Habitacion Simple Confort', 'Ideal para viajeros individuales y estadÃ­as de negocios ejecutivas.', 120.00, 1, 'habitacionstandar.png'),
('matrimonial', 'Habitacion Matrimonial Deluxe', 'Cama King Size, aire acondicionado, smart TV y vista panorÃ¡mica.', 180.00, 2, 'habitaciondeluxe.jpg'),
('doble', 'Habitacion Doble Twin', 'Dos camas de plaza y media, escritorio de trabajo y baÃ±o privado.', 220.00, 3, 'habitaciondeluxe.jpg'),
('suite_jr', 'Suite Junior Costa del Sol', 'Sala de estar independiente, frigobar surtido y tina de hidromasaje.', 320.00, 2, 'suitejunior.jpg'),
('suite_pres', 'Suite Presidencial Real', 'MÃ¡ximo confort con terraza privada, jacuzzi, comedor y vistas exclusivas.', 550.00, 4, 'suitepresidencial.jpg');

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