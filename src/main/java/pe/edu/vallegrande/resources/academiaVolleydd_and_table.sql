
-- ELIMINACION DE LA BASE DE DATOS SI ES QUE EXISTE

DROP DATABASE IF EXISTS appJava;


-- CREACION DE LA BASE DE DATOS
CREATE DATABASE appJava;

-- USO DE LA BASE DE DATOS
USE appJava;


-- CREACION DE LA TABLA TORNEOS
CREATE TABLE torneos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    fecha DATE NOT NULL,
    lugar VARCHAR(100) NOT NULL,
    nivel VARCHAR(50) NOT NULL,
    descripcion TEXT,
    estado VARCHAR(50) NOT NULL
);


-- MOSTRAR DATOS DE LA TABLA TORNEOS
SELECT * FROM torneos;


-- CREACION DE LA TABLA ESTUDIANTE
CREATE TABLE estudiantes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    edad INT NOT NULL,
    dni VARCHAR(20) NOT NULL UNIQUE,
    correo VARCHAR(100) NOT NULL,
    celular VARCHAR(15) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    genero VARCHAR(10) NOT NULL
);



-- MOSTRAR DATOS DE LA TABLA ESTUDIANTE
SELECT * FROM estudiantes;


-- CREACION DE LA TABLA ENTRENADORES
CREATE TABLE entrenadores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    especialidad VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    activo BOOLEAN NOT NULL
);


-- MOSTRAR DATOD DE LA TABLA ENTRENADORES
SELECT * FROM entrenadores;

CREATE TABLE polos (
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       nombre_en_polo VARCHAR(100) NOT NULL,
                       numero_en_polo INT NOT NULL,
                       talla VARCHAR(10) NOT NULL,
                       deporte VARCHAR(50) NOT NULL,
                       incluye_short BOOLEAN NOT NULL,
                       incluye_medias BOOLEAN NOT NULL
);
INSERT INTO polos (nombre_en_polo, numero_en_polo, talla, deporte, incluye_short, incluye_medias) VALUES
                                                                                                      ('Nayeli', 10, 'L', 'Vóley', true, true),
                                                                                                      ('Alejandro', 5, 'L', 'Fútbol', true, false),
                                                                                                      ('Brando', 7, 'L', 'Básquet', false, true),
                                                                                                      ('Lucía', 11, 'M', 'Vóley', true, false),
                                                                                                      ('Diego', 3, 'XL', 'Fútbol', false, false),
                                                                                                      ('Camila', 9, 'S', 'Básquet', true, true),
                                                                                                      ('Andrés', 8, 'L', 'Vóley', true, true),
                                                                                                      ('Valeria', 12, 'M', 'Fútbol', false, true);
SELECT * FROM polos;
CREATE TABLE equipos_voley (
                               id INT AUTO_INCREMENT PRIMARY KEY,
                               nombre_cliente VARCHAR(100) NOT NULL,
                               producto VARCHAR(50) NOT NULL,
                               cantidad INT NOT NULL,
                               precio_unitario DECIMAL(6,2) NOT NULL,
                               total_pago DECIMAL(8,2) NOT NULL
);
INSERT INTO equipos_voley (nombre_cliente, producto, cantidad, precio_unitario, total_pago) VALUES
                                                                                                ('Lucía Rojas', 'Balón de Voleibol', 2, 25.00, 50.00),
                                                                                                ('Carlos Pérez', 'Red de Voleibol', 1, 40.00, 40.00),
                                                                                                ('Ana Quispe', 'Rodilleras (par)', 3, 15.00, 45.00),
                                                                                                ('Jorge Herrera', 'Zapatillas de Voleibol', 1, 60.00, 60.00);

