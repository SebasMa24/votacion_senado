-- ============================================================
-- SISTEMA DE VOTACIÓN - SCRIPT COMPLETO PARA POSTGRESQL
-- Autor: Grupo 1
-- Base: sistema_votacion
-- ============================================================

-- ============================================================
-- CREAR ROLES
-- ============================================================

DO $$
BEGIN
    -- Crear rol admin si no existe
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'admin') THEN
        EXECUTE 'CREATE ROLE admin LOGIN PASSWORD ''admin123''';
        RAISE NOTICE 'Rol admin creado.';
    ELSE
        RAISE NOTICE 'Rol admin ya existe, se omite.';
    END IF;

    -- Crear rol app si no existe
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'app') THEN
        EXECUTE 'CREATE ROLE app LOGIN PASSWORD ''app123''';
        RAISE NOTICE 'Rol app creado.';
    ELSE
        RAISE NOTICE 'Rol app ya existe, se omite.';
    END IF;

    -- Crear rol votante si no existe
    IF NOT EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'votante') THEN
        EXECUTE 'CREATE ROLE votante LOGIN PASSWORD ''votante123''';
        RAISE NOTICE 'Rol votante creado.';
    ELSE
        RAISE NOTICE 'Rol votante ya existe, se omite.';
    END IF;
END
$$;

-- ===========================================
-- CREAR BASE DE DATOS
-- ===========================================
CREATE DATABASE sistema_votacion OWNER admin;

-- ============================================================
-- CREAR SCHEMA
-- ============================================================
CREATE SCHEMA IF NOT EXISTS senado AUTHORIZATION admin;

-- ============================================================
-- CREAR TABLAS
-- ============================================================
-- ============================================================
-- TABLA: VOTANTES
-- ============================================================

CREATE TABLE IF NOT EXISTS senado.Usuarios (
    id_usuario INT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    username VARCHAR(30) UNIQUE NOT NULL,
    correo VARCHAR(100) UNIQUE NOT NULL,
    contraseña VARCHAR(255) NOT NULL,
    ha_votado BOOLEAN DEFAULT FALSE,
    tipo_circunscripcion VARCHAR(30)
        CHECK (tipo_circunscripcion IN ('NACIONAL', 'INDIGENA')),
    rol VARCHAR(20)
        CHECK (rol IN ('VOTANTE', 'ADMIN'))
);

-- ============================================================
-- TABLA: PARTIDOS POLÍTICOS
-- ============================================================

CREATE TABLE IF NOT EXISTS senado.PartidosPoliticos (
    id_partido SERIAL PRIMARY KEY,
    nom_partido VARCHAR(100) UNIQUE NOT NULL,
    logo VARCHAR(255),
    tipo_lista VARCHAR(20)
        CHECK (tipo_lista IN ('ABIERTA', 'CERRADA')),
    tipo_circunscripcion_p VARCHAR(30)
        CHECK (tipo_circunscripcion_p IN ('NACIONAL', 'INDIGENA')),
    total_votos_p INT DEFAULT 0
);

-- ============================================================
-- TABLA: CANDIDATOS
-- ============================================================

CREATE TABLE IF NOT EXISTS senado.Candidatos (
    id_candidato SERIAL PRIMARY KEY,
    nombre_c VARCHAR(50) NOT NULL,
    apellido_c VARCHAR(50) NOT NULL,
    numero_lista INT NOT NULL,
    id_partido INT NOT NULL,
    total_votos_c INT DEFAULT 0,
    CONSTRAINT fk_partido
        FOREIGN KEY (id_partido)
        REFERENCES senado.PartidosPoliticos(id_partido)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT unq_num_lista
        UNIQUE (id_partido, numero_lista)
);

-- ===========================================
-- PERMISOS POR ROL
-- ===========================================
-- ADMIN = acceso total al esquema
GRANT USAGE, CREATE ON SCHEMA senado TO admin;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA senado TO admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA senado GRANT ALL PRIVILEGES ON TABLES TO admin;

-- APP = CRUD datos masivos
GRANT CONNECT ON DATABASE sistema_votacion TO app;
GRANT USAGE ON SCHEMA senado TO app;
GRANT SELECT, INSERT ON ALL TABLES IN SCHEMA senado TO app;
ALTER DEFAULT PRIVILEGES IN SCHEMA senado GRANT SELECT, INSERT , UPDATE, DELETE ON TABLES TO app;

-- VOTANTE = solo lectura + actualización del campo "ha_votado" en Votantes.
GRANT CONNECT ON DATABASE sistema_votacion TO votante;
GRANT USAGE ON SCHEMA senado TO votante;
GRANT SELECT ON ALL TABLES IN SCHEMA senado TO votante;
ALTER DEFAULT PRIVILEGES IN SCHEMA senado GRANT SELECT ON TABLES TO votante;
REVOKE UPDATE ON TABLE senado.Usuarios FROM votante; GRANT UPDATE (ha_votado) ON TABLE senado.Usuarios TO votante

-- ============================================================
-- TEST DE INSERCIÓN DE DATOS 
-- ============================================================
INSERT INTO senado.partidospoliticos (nom_partido, logo, tipo_lista, tipo_circunscripcion_p)
VALUES
-- NACIONAL - CERRADA (3)
('Partido de la Esperanza', 'Partido_de_la_Esperanza_Logo.jpg', 'CERRADA', 'NACIONAL'),
('Partido Popular', 'Partido_Popular_Logo.jpg', 'CERRADA', 'NACIONAL'),
('Frente Liberal', 'Frente_Liberal_Logo.jpg', 'CERRADA', 'NACIONAL'),

-- NACIONAL - ABIERTA (7)
('Movimiento Verde', 'Movimiento_Verde_Logo.jpg', 'ABIERTA', 'NACIONAL'),
('Coalición Ciudadana', 'Coalición_Ciudadana_Logo.jpg', 'ABIERTA', 'NACIONAL'),
('Partido Futuro', 'Partido_Futuro_Logo.jpg', 'ABIERTA', 'NACIONAL'),
('Alianza Democrática', 'Alianza_Democrática_Logo.jpg', 'ABIERTA', 'NACIONAL'),
('Movimiento Solidario', 'Movimiento_Solidario_Logo.jpg', 'ABIERTA', 'NACIONAL'),
('Unión Nacional', 'Unión_Nacional_Logo.jpg', 'ABIERTA', 'NACIONAL'),
('Voz Ciudadana', 'Voz_Ciudadana_Logo.jpg', 'ABIERTA', 'NACIONAL'),

-- INDÍGENA (2 abiertas, 1 cerrada)
('Partido Verde Indígena', 'Partido_Verde_Indigena_Logo.jpg', 'ABIERTA', 'INDIGENA'),
('Movimiento Indígena', 'Movimiento_Indigena_Logo.jpg', 'ABIERTA', 'INDIGENA'),
('Alianza Indígena', 'Alianza_Indigena_Logo.jpg', 'CERRADA', 'INDIGENA')
ON CONFLICT (nom_partido) DO NOTHING;

INSERT INTO senado.Usuarios (id_usuario,nombre, apellido, username, correo, contraseña, tipo_circunscripcion, rol)
VALUES 
(1,'Sebastian', 'Martinez', 'smartinez1', 'smartinez@example.com', '$2a$10$hCWy./X3fetnAxTNjOQVJ.MSOYHh/C2r9SkmwXHJrlCIr95GanUau', 'NACIONAL','ADMIN')
ON CONFLICT (username) DO NOTHING;


-- ============================================================
-- CONSULTAS
-- ============================================================

-- tablas creadas
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'senado';

-- Consultar registros
SELECT * FROM senado.PartidosPoliticos;
SELECT * FROM senado.Candidatos;
SELECT * FROM senado.Usuarios;