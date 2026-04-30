UPDATE senado.usuarios SET ha_votado = FALSE;
UPDATE senado.partidospoliticos SET total_votos_p = 0;
UPDATE senado.candidatos SET total_votos_c = 0;

INSERT INTO senado.partidospoliticos (nom_partido, logo, tipo_lista, tipo_circunscripcion_p)
VALUES
-- NACIONAL - CERRADA (3)
('Partido de la Esperanza', 'Partido_de_la_Esperanza_Logo.jpg', 'CERRADA', 'NACIONAL'),
('Partido Popular', 'Partido_Popular_Logo.jpg', 'CERRADA', 'NACIONAL'),
('Frente Liberal', 'Frente_Liberal_Logo.jpg', 'CERRADA', 'NACIONAL'),

-- NACIONAL - ABIERTA (7)
('Movimiento Verde', 'Movimiento_Verde_Logo.jpg', 'ABIERTA', 'NACIONAL'),
('Coalición Ciudadana', 'Coalicion_Ciudadana_Logo.jpg', 'ABIERTA', 'NACIONAL'),
('Partido Futuro', 'Partido_Futuro_Logo.jpg', 'ABIERTA', 'NACIONAL'),
('Alianza Democrática', 'Alianza_Democratica_Logo.jpg', 'ABIERTA', 'NACIONAL'),
('Movimiento Solidario', 'Movimiento_Solidario_Logo.jpg', 'ABIERTA', 'NACIONAL'),
('Unión Nacional', 'Union_Nacional_Logo.jpg', 'ABIERTA', 'NACIONAL'),
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
