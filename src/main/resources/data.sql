-- ============================================================
-- CREACIÓN DEL ESQUEMA (si no existe)
-- ============================================================
CREATE SCHEMA IF NOT EXISTS votacion_senado;

-- ============================================================
-- DATOS DE PARTIDOS POLÍTICOS
-- ============================================================
INSERT INTO votacion_senado.partido_politico (nom_partido, logo, tipo_lista, tipo_circunscripcion_p)
VALUES 
('Partido de la Esperanza', 'logo_esperanza.png', 'CERRADA', 'NACIONAL'),
('Movimiento Verde', 'logo_verde.png', 'ABIERTA', 'NACIONAL'),
('Alianza Indígena', 'logo_indigena.png', 'CERRADA', 'INDIGENA'),
('Coalición Ciudadana', 'logo_ciudadana.png', 'ABIERTA', 'NACIONAL'),
('Partido Futuro', 'logo_futuro.png', 'ABIERTA', 'NACIONAL'),
('Alianza Democrática', 'logo_democratica.png', 'ABIERTA', 'NACIONAL'),
('Partido Popular', 'logo_popular.png', 'CERRADA', 'NACIONAL'),
('Movimiento Solidario', 'logo_solidario.png', 'ABIERTA', 'NACIONAL'),
('Frente Liberal', 'logo_liberal.png', 'CERRADA', 'NACIONAL'),
('Unión Nacional', 'logo_union.png', 'ABIERTA', 'NACIONAL'),
('Voz Ciudadana', 'logo_voz.png', 'ABIERTA', 'NACIONAL'),
('Alianza Social', 'logo_social.png', 'CERRADA', 'NACIONAL'),
('Partido Verde Indígena', 'logo_verde_indigena.png', 'ABIERTA', 'INDIGENA'),
('Movimiento Independiente', 'logo_independiente.png', 'ABIERTA', 'NACIONAL'),
('Partido Progreso', 'logo_progreso.png', 'ABIERTA', 'NACIONAL');

-- ============================================================
-- DATOS DE CANDIDATOS
-- ============================================================
INSERT INTO votacion_senado.candidato (nombre, apellido, num_lista, id_partido) VALUES
-- Movimiento Verde (id=2)
('Juan', 'Pérez', 1, 2),
('Ana', 'Gómez', 2, 2),
('Luis', 'Martínez', 3, 2),
('Carla', 'Ramírez', 4, 2),
('Diego', 'Hernández', 5, 2),

-- Coalición Ciudadana (id=4)
('María', 'López', 1, 4),
('Carlos', 'Fernández', 2, 4),
('Sofía', 'Gutiérrez', 3, 4),
('Pedro', 'Rojas', 4, 4),
('Lucía', 'Castillo', 5, 4),

-- Partido Futuro (id=5)
('Andrés', 'Sánchez', 1, 5),
('Valeria', 'Jiménez', 2, 5),
('Mario', 'Torres', 3, 5),
('Paula', 'Moreno', 4, 5),
('Javier', 'Ortega', 5, 5),

-- Alianza Democrática (id=6)
('Isabella', 'Mendoza', 1, 6),
('Ricardo', 'Alvarez', 2, 6),
('Daniela', 'Campos', 3, 6),
('Fernando', 'Ramírez', 4, 6),
('Laura', 'Hernández', 5, 6),

-- Movimiento Solidario (id=8)
('Andres', 'Cifuentes', 1, 8),
('Mariana', 'Santos', 2, 8),
('Sebastian', 'Diaz', 3, 8),

-- Unión Nacional (id=10)
('Camila', 'Vargas', 1, 10),
('Felipe', 'Morales', 2, 10),

-- Voz Ciudadana (id=11)
('Nathalia', 'Rojas', 1, 11),
('Diego', 'Castro', 2, 11),

-- Partido Verde Indígena (id=13)
('Laura', 'Pacheco', 1, 13),
('Manuel', 'Suarez', 2, 13),

-- Movimiento Independiente (id=14)
('Catalina', 'Cabrera', 1, 14),
('Jhon', 'Cardona', 2, 14),

-- Partido Progreso (id=15)
('Santiago', 'Salazar', 1, 15),
('Carolina', 'Rueda', 2, 15);


-- ============================================================
-- DATOS DE USUARIOS
-- ============================================================
INSERT INTO votacion_senado.usuarios 
(id_usuario, nombre, apellido, correo, contraseña, username, tipo_circunscripcion, rol) VALUES
(1001, 'María', 'Pérez', 'maria.perez@example.com', '$2a$10$hCWy./X3fetnAxTNjOQVJ.MSOYHh/C2r9SkmwXHJrlCIr95GanUau', 'mperez001', 'NACIONAL', 'VOTANTE'),
(1002, 'Jorge', 'López', 'jorge.lopez@example.com', '$2a$10$hCWy./X3fetnAxTNjOQVJ.MSOYHh/C2r9SkmwXHJrlCIr95GanUau', 'jlopez002', 'NACIONAL','VOTANTE'),
(1003, 'Lucía', 'Gaitán', 'lucia.gaitan@example.com', '$2a$10$hCWy./X3fetnAxTNjOQVJ.MSOYHh/C2r9SkmwXHJrlCIr95GanUau', 'lgaitan003', 'INDIGENA','VOTANTE'),
(1004, 'Pedro', 'Mora', 'pedro.mora@example.com', '$2a$10$hCWy./X3fetnAxTNjOQVJ.MSOYHh/C2r9SkmwXHJrlCIr95GanUau', 'pmora004', 'NACIONAL','VOTANTE'),
(1005, 'Sonia', 'Yucuna', 'sonia.yucuna@example.com', '$2a$10$hCWy./X3fetnAxTNjOQVJ.MSOYHh/C2r9SkmwXHJrlCIr95GanUau', 'syucuna005', 'INDIGENA','VOTANTE');

-- ============================================================