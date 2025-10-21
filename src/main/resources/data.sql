-- ============================================================
-- CREACIÓN DEL ESQUEMA (si no existe)
-- ============================================================
CREATE SCHEMA IF NOT EXISTS votacion_senado;

-- ============================================================
-- DATOS DE PARTIDOS POLÍTICOS
-- ============================================================
INSERT INTO votacion_senado.partido_politico (id_partido, nom_partido, logo, tipo_lista, tipo_circunscripcion_p)
VALUES 
(1, 'Partido de la Esperanza', 'logo_esperanza.png', 'CERRADA', 'NACIONAL'),
(2, 'Movimiento Verde', 'logo_verde.png', 'ABIERTA', 'NACIONAL'),
(3, 'Alianza Indígena', 'logo_indigena.png', 'CERRADA', 'INDIGENA');

-- ============================================================
-- DATOS DE CANDIDATOS
-- ============================================================
INSERT INTO votacion_senado.candidato (id_candidato, nombre, num_lista, id_partido)
VALUES
(1, 'Camila Rojas', 101, 1),
(2, 'Andrés Gómez', 102, 1),
(3, 'Laura Martínez', 201, 2),
(4, 'Carlos Ramírez', 202, 2),
(5, 'Ana Yucuna', 301, 3),
(6, 'Juan Ipia', 302, 3);

-- ============================================================
-- DATOS DE VOTANTES
-- ============================================================
INSERT INTO votacion_senado.votante 
(id_votante, nombre, apellido, correo, contraseña, username, tipo_circunscripcion) VALUES
(1001, 'María', 'Pérez', 'maria.perez@example.com', '$2a$10$hCWy./X3fetnAxTNjOQVJ.MSOYHh/C2r9SkmwXHJrlCIr95GanUau', 'mperez001', 'NACIONAL'),
(1002, 'Jorge', 'López', 'jorge.lopez@example.com', '$2a$10$hCWy./X3fetnAxTNjOQVJ.MSOYHh/C2r9SkmwXHJrlCIr95GanUau', 'jlopez002', 'NACIONAL'),
(1003, 'Lucía', 'Gaitán', 'lucia.gaitan@example.com', '$2a$10$hCWy./X3fetnAxTNjOQVJ.MSOYHh/C2r9SkmwXHJrlCIr95GanUau', 'lgaitan003', 'INDIGENA'),
(1004, 'Pedro', 'Mora', 'pedro.mora@example.com', '$2a$10$hCWy./X3fetnAxTNjOQVJ.MSOYHh/C2r9SkmwXHJrlCIr95GanUau', 'pmora004', 'NACIONAL'),
(1005, 'Sonia', 'Yucuna', 'sonia.yucuna@example.com', '$2a$10$hCWy./X3fetnAxTNjOQVJ.MSOYHh/C2r9SkmwXHJrlCIr95GanUau', 'syucuna005', 'INDIGENA');

-- ============================================================