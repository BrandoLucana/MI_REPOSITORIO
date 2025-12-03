-- Crear la base de datos
CREATE DATABASE NoticiasBD;
GO

-- Usar la base de datos
USE NoticiasBD;
GO


CREATE TABLE corresponsal (
    id INT IDENTITY(1,1) PRIMARY KEY,  
    full_name VARCHAR(100) NOT NULL,   
    numero_documento INT NOT NULL UNIQUE,  
    ubigeo VARCHAR(6) NOT NULL CHECK (ubigeo LIKE '15%'),  -- Cadena con expresión lógica (solo Perú)
    centro_poblado VARCHAR(100) NOT NULL,  -- Cadena: Centro poblado/anexo/caserío
    is_active BIT NOT NULL DEFAULT 1,  -- Booleano: Activo (1)/Inactivo (0), default activo
    fecha_registro DATE NOT NULL DEFAULT GETDATE(),  -- Fecha: Registro, default actual
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2 NULL,
    deleted_at DATETIME2 NULL  
);
GO


INSERT INTO corresponsal (full_name, numero_documento, ubigeo, centro_poblado, is_active)
VALUES
('Brando Flores Lucana', 75780872, '150102', 'Centro Poblado Ejemplo Lima', 1),
('Jeferson Soto Fernandez', 12345678, '150201', 'Anexo Miraflores', 1),
('Pedro Juan Perez', 87654321, '150102', 'Caserio Surco', 1),
('Martin Quispe', 11223344, '150301', 'Centro Poblado Cusco', 0),  
('Sara Quispe', 55667788, '150102', 'Anexo La Molina', 1),
('Lozano Ramirez', 99887766, '150401', 'Caserio Arequipa', 0), 
('Juan Perez', 44556677, '150102', 'Centro Poblado Callao', 1),
('Maria Lara', 33445566, '150501', 'Anexo Ica', 1);

-- Ejemplo de consulta para listar solo activos
SELECT * FROM corresponsal WHERE is_active = 1 AND deleted_at IS NULL;

-- Ejemplo de actualización (cambiar estado a inactivo)
UPDATE corresponsal SET is_active = 0, updated_at = GETDATE() WHERE id = 1;

-- Ejemplo de eliminación lógica
UPDATE corresponsal SET deleted_at = GETDATE(), is_active = 0 WHERE id = 1;

-- Ejemplo de restauración lógica
UPDATE corresponsal SET deleted_at = NULL, is_active = 1, updated_at = GETDATE() WHERE id = 1;