
CREDENCIALES DE ADMINISTRADOR
Correo: admin@eventyunks.com
Contraseña: Prueba2026!

CREDENCIALES DE ORGANIZADOR
Correo: produccionescentro@eventyunks.com
Contraseña: Prueba2026!

application.properties
spring.application.name=EventYunks

spring.datasource.url=jdbc:mysql://localhost:3306/eventyunks?createDatabaseIfNotExist=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password= xxxx
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

cloudinary.cloud_name=xxxxxx
cloudinary.api_key=xxxxxx
cloudinary.api_secret=xxxxx



USE eventyunks;
SET NAMES utf8mb4;
-- Contraseña de prueba para TODOS los usuarios sembrados (admin, organizadores y asistentes): Prueba2026!
SET @hash := '$2b$12$Ds6Ybcdztwd5c40NWmTYgeSUfEaWnYGmnjgqmEc510jDkyVfJmdGO';

INSERT INTO usuarios (nombre, correo, hash_contrasenia, activo, telefono)
SELECT d.nombre, d.correo, @hash, d.activo, d.telefono
FROM (
SELECT 'Administrador' AS nombre, 'admin@eventyunks.com' AS correo, 1 AS activo, '6440000000' AS telefono UNION ALL
SELECT 'Producciones Centro', 'produccionescentro@eventyunks.com', 1, '55 1234 5678' UNION ALL
SELECT 'Eventos del Valle', 'eventosdelvalle@eventyunks.com', 1, '644 123 4567' UNION ALL
SELECT 'Escena Norte', 'escenanorte@eventyunks.com', 0, '662 987 6543' UNION ALL
SELECT 'Ana López', 'ana.lopez@correo.com', 1, '644 555 0101' UNION ALL
SELECT 'Carlos Ramírez', 'carlos.ramirez@correo.com', 1, '644 555 0102' UNION ALL
SELECT 'Daniela Torres', 'daniela.torres@correo.com', 1, '644 555 0103' UNION ALL
SELECT 'Luis Hernández', 'luis.hernandez@correo.com', 1, '644 555 0104' UNION ALL
SELECT 'Mariana Pérez', 'mariana.perez@correo.com', 1, '644 555 0105'
) d
WHERE NOT EXISTS (SELECT 1 FROM usuarios u WHERE u.correo = d.correo);

INSERT INTO administradores (id)
SELECT u.id
FROM usuarios u
WHERE u.correo = 'admin@eventyunks.com'
AND NOT EXISTS (SELECT 1 FROM administradores a WHERE a.id = u.id);

INSERT INTO organizadores (id, administrador_id)
SELECT u.id, adm.id
FROM usuarios u
JOIN usuarios adm ON adm.correo = 'admin@eventyunks.com'
WHERE u.correo IN ('produccionescentro@eventyunks.com', 'eventosdelvalle@eventyunks.com', 'escenanorte@eventyunks.com')
AND NOT EXISTS (SELECT 1 FROM organizadores o WHERE o.id = u.id);

INSERT INTO asistentes (id)
SELECT u.id
FROM usuarios u
WHERE u.correo IN ('ana.lopez@correo.com', 'carlos.ramirez@correo.com', 'daniela.torres@correo.com', 'luis.hernandez@correo.com', 'mariana.perez@correo.com')
AND NOT EXISTS (SELECT 1 FROM asistentes s WHERE s.id = u.id);

INSERT INTO artistas (nombre, genero_musical, biografia, url_imagen, administrador_id)
SELECT d.nombre, d.genero, d.biografia, d.imagen, adm.id
FROM (
SELECT 'Luna Eléctrica' AS nombre, 'Electrónica' AS genero, 'Dúo de música electrónica con sintetizadores analógicos y visuales en vivo.' AS biografia, '/img/avatar-artista.png' AS imagen UNION ALL
SELECT 'Los Cuervos del Norte', 'Rock', 'Banda de rock alternativo formada en Sonora, con más de diez años de trayectoria.', '/img/avatar-artista.png' UNION ALL
SELECT 'Sofía Marea', 'Indie pop', 'Cantautora de indie pop con letras íntimas y arreglos minimalistas.', NULL UNION ALL
SELECT 'Valeria Costa', 'Pop', 'Cantante de pop con presentaciones en teatros y festivales de todo el país.', '/img/avatar-artista.png' UNION ALL
SELECT 'Marcos Vidal', 'R&B', 'Vocalista de R&B con influencias de soul y música urbana.', NULL UNION ALL
SELECT 'Neón Desierto', 'Electrónica', 'Proyecto de house y techno melódico creado por dos productores del noroeste.', '/img/avatar-artista.png' UNION ALL
SELECT 'Bruma Roja', 'Rock', 'Cuarteto de rock psicodélico conocido por sus presentaciones en vivo.', NULL UNION ALL
SELECT 'Dúo Aurora', 'Pop', 'Dúo vocal de pop acústico con armonías a dos voces.', '/img/avatar-artista.png'
) d
JOIN usuarios adm ON adm.correo = 'admin@eventyunks.com'
WHERE NOT EXISTS (SELECT 1 FROM artistas a WHERE a.nombre = d.nombre);

INSERT INTO croquis (nombre, capacidad)
SELECT d.nombre, d.capacidad
FROM (
SELECT 'Foro Centro'    AS nombre, 60  AS capacidad UNION ALL
SELECT 'Teatro Mediano' AS nombre, 140 AS capacidad UNION ALL
SELECT 'Sala Pequeña'   AS nombre, 40  AS capacidad UNION ALL
SELECT 'Arena Grande'   AS nombre, 340 AS capacidad
) d
WHERE NOT EXISTS (SELECT 1 FROM croquis c WHERE c.nombre = d.nombre);

INSERT INTO zonas (nombre, croquis_id)
SELECT d.zona, c.id
FROM (
SELECT 'Foro Centro'    AS croquis, 'Sección 1' AS zona UNION ALL
SELECT 'Foro Centro'    AS croquis, 'Sección 2' AS zona UNION ALL
SELECT 'Teatro Mediano' AS croquis, 'Sección 1' AS zona UNION ALL
SELECT 'Teatro Mediano' AS croquis, 'Sección 2' AS zona UNION ALL
SELECT 'Teatro Mediano' AS croquis, 'Sección 3' AS zona UNION ALL
SELECT 'Sala Pequeña'   AS croquis, 'Sección 1' AS zona UNION ALL
SELECT 'Arena Grande'   AS croquis, 'Sección 1' AS zona UNION ALL
SELECT 'Arena Grande'   AS croquis, 'Sección 2' AS zona UNION ALL
SELECT 'Arena Grande'   AS croquis, 'Sección 3' AS zona UNION ALL
SELECT 'Arena Grande'   AS croquis, 'Sección 4' AS zona
) d
JOIN croquis c ON c.nombre = d.croquis
WHERE NOT EXISTS (SELECT 1 FROM zonas z WHERE z.croquis_id = c.id AND z.nombre = d.zona);

INSERT INTO asientos (fila, numero, posicion_x, posicion_y, estado, zona_id)
SELECT SUBSTRING('ABCDEFGHIJ', f.n, 1), a.n, a.n, f.n, 'DISPONIBLE', z.id
FROM (
SELECT 'Foro Centro'    AS croquis, 'Sección 1' AS zona, 2  AS filas, 5  AS por_fila UNION ALL
SELECT 'Foro Centro'    AS croquis, 'Sección 2' AS zona, 5  AS filas, 10 AS por_fila UNION ALL
SELECT 'Teatro Mediano' AS croquis, 'Sección 1' AS zona, 4  AS filas, 10 AS por_fila UNION ALL
SELECT 'Teatro Mediano' AS croquis, 'Sección 2' AS zona, 6  AS filas, 10 AS por_fila UNION ALL
SELECT 'Teatro Mediano' AS croquis, 'Sección 3' AS zona, 4  AS filas, 10 AS por_fila UNION ALL
SELECT 'Sala Pequeña'   AS croquis, 'Sección 1' AS zona, 5  AS filas, 8  AS por_fila UNION ALL
SELECT 'Arena Grande'   AS croquis, 'Sección 1' AS zona, 6  AS filas, 10 AS por_fila UNION ALL
SELECT 'Arena Grande'   AS croquis, 'Sección 2' AS zona, 8  AS filas, 10 AS por_fila UNION ALL
SELECT 'Arena Grande'   AS croquis, 'Sección 3' AS zona, 10 AS filas, 10 AS por_fila UNION ALL
SELECT 'Arena Grande'   AS croquis, 'Sección 4' AS zona, 10 AS filas, 10 AS por_fila
) d
JOIN croquis c ON c.nombre = d.croquis
JOIN zonas z   ON z.croquis_id = c.id AND z.nombre = d.zona
JOIN (SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10) f
ON f.n <= d.filas
JOIN (SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10) a
ON a.n <= d.por_fila
WHERE NOT EXISTS (SELECT 1 FROM asientos x WHERE x.zona_id = z.id);

INSERT INTO direcciones (calle, colonia, numero, codigo_postal, ciudad, nombre_lugar)
SELECT d.calle, d.colonia, d.numero, d.cp, d.ciudad, d.lugar
FROM (
SELECT 'Calle Reforma' AS calle, 'Centro' AS colonia, '123' AS numero, '06000' AS cp, 'Ciudad de México' AS ciudad, 'Foro Centro' AS lugar UNION ALL
SELECT 'Av. Juárez', 'Centro', '50', '06050', 'Ciudad de México', 'Parque Alameda' UNION ALL
SELECT 'Calle Hidalgo', 'Centro', '210', '85000', 'Ciudad Obregón', 'Teatro Sonora' UNION ALL
SELECT 'Blvd. Las Palmas', 'Valle Verde', '800', '85800', 'Navojoa', 'Auditorio del Valle' UNION ALL
SELECT 'Av. del Deporte', 'Zona Norte', '1200', '83000', 'Hermosillo', 'Arena Norte'
) d
WHERE NOT EXISTS (SELECT 1 FROM direcciones x WHERE x.calle = d.calle AND x.numero = d.numero AND x.codigo_postal = d.cp);

INSERT INTO recintos (nombre, direccion_id, croquis_id, administrador_id)
SELECT d.nombre, di.id, c.id, adm.id
FROM (
SELECT 'Foro Centro' AS nombre, 'Calle Reforma' AS calle, '123' AS numero, '06000' AS cp, 'Foro Centro' AS croquis UNION ALL
SELECT 'Parque Alameda', 'Av. Juárez', '50', '06050', 'Sala Pequeña' UNION ALL
SELECT 'Teatro Sonora', 'Calle Hidalgo', '210', '85000', 'Teatro Mediano' UNION ALL
SELECT 'Auditorio del Valle', 'Blvd. Las Palmas', '800', '85800', 'Teatro Mediano' UNION ALL
SELECT 'Arena Norte', 'Av. del Deporte', '1200', '83000', 'Arena Grande'
) d
JOIN direcciones di ON di.calle = d.calle AND di.numero = d.numero AND di.codigo_postal = d.cp
JOIN croquis c ON c.nombre = d.croquis
JOIN usuarios adm ON adm.correo = 'admin@eventyunks.com'
WHERE NOT EXISTS (SELECT 1 FROM recintos r WHERE r.nombre = d.nombre);

INSERT INTO eventos (nombre, descripcion, fecha, recinto_id, categoria, url_imagen, estado, organizador_id)
SELECT d.nombre, d.descripcion, d.fecha, r.id, d.categoria, d.imagen, d.estado, u.id
FROM (
SELECT 'Encuentro de música' AS nombre, 'Encuentro de música en vivo en el Foro Centro con tres artistas invitados.' AS descripcion, TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 20 DAY), '19:00:00') AS fecha, 'Música' AS categoria, '/img/evento-musica.jpg' AS imagen, 'PUBLICADO' AS estado, 'produccionescentro@eventyunks.com' AS organizador, 'Foro Centro' AS recinto UNION ALL
SELECT 'Noche de Rock en el Valle', 'Una noche de rock con bandas del noroeste del país.', TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 3 DAY), '21:00:00'), 'Música', '/img/evento-bailey.jpg', 'PUBLICADO', 'eventosdelvalle@eventyunks.com', 'Auditorio del Valle' UNION ALL
SELECT 'Festival Electrónico Norte', 'Festival de música electrónica con escenario principal y zona VIP.', TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 35 DAY), '20:00:00'), 'Música', '/img/evento-musica.jpg', 'PAUSADO', 'escenanorte@eventyunks.com', 'Arena Norte' UNION ALL
SELECT 'La casa de los espejos', 'Obra de teatro de suspenso en dos actos.', TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 10 DAY), '20:00:00'), 'Teatro', '/img/evento-menchaca.jpg', 'PUBLICADO', 'eventosdelvalle@eventyunks.com', 'Teatro Sonora' UNION ALL
SELECT 'Clásico Regional de Fútbol', 'Partido de fútbol entre los equipos regionales más tradicionales.', TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 40 DAY), '17:00:00'), 'Deportes', '/img/evento-bailey.jpg', 'CANCELADO', 'escenanorte@eventyunks.com', 'Arena Norte' UNION ALL
SELECT 'Concierto Acústico de Otoño', 'Presentación acústica en un ambiente íntimo.', TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 25 DAY), '20:00:00'), 'Música', '/img/evento-musica-oscuro.jpg', 'FINALIZADO', 'produccionescentro@eventyunks.com', 'Parque Alameda' UNION ALL
SELECT 'Festival Indie de Verano', 'Jornada de música indie con artistas emergentes.', TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 60 DAY), '18:00:00'), 'Música', '/img/evento-menchaca.jpg', 'FINALIZADO', 'produccionescentro@eventyunks.com', 'Foro Centro' UNION ALL
SELECT 'Taller de Stand-up', 'Taller práctico de comedia en vivo.', TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 60 DAY), '19:30:00'), 'Teatro', NULL, 'BORRADOR', 'produccionescentro@eventyunks.com', 'Parque Alameda' UNION ALL
SELECT 'Gran Final de Pop', 'Gran final del certamen regional de música pop.', TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 10 DAY), '21:00:00'), 'Música', '/img/evento-musica.jpg', 'FINALIZADO', 'eventosdelvalle@eventyunks.com', 'Teatro Sonora' UNION ALL
SELECT 'Copa Valle de Baloncesto', 'Torneo de baloncesto con equipos de la región.', TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 14 DAY), '18:00:00'), 'Deportes', '/img/evento-bailey.jpg', 'PUBLICADO', 'eventosdelvalle@eventyunks.com', 'Auditorio del Valle'
) d
JOIN recintos r ON r.nombre = d.recinto
JOIN usuarios u ON u.correo = d.organizador
WHERE NOT EXISTS (SELECT 1 FROM eventos e WHERE e.nombre = d.nombre);

INSERT INTO boletos (tipo, precio, cantidad_disponibles, descripcion, cantidad_total, evento_id)
SELECT d.tipo, d.precio, d.disponibles, d.descripcion, d.total, e.id
FROM (
SELECT 'Encuentro de música' AS evento, 'VIP' AS tipo, 500.00 AS precio, 9 AS disponibles, 10 AS total, 'Primeras filas con acceso preferente.' AS descripcion UNION ALL
SELECT 'Encuentro de música', 'General', 200.00, 48, 50, 'Entrada general al evento.' UNION ALL
SELECT 'Noche de Rock en el Valle', 'General', 350.00, 97, 100, 'Entrada general al evento.' UNION ALL
SELECT 'Noche de Rock en el Valle', 'Preferente', 650.00, 40, 40, 'Zona preferente cerca del escenario.' UNION ALL
SELECT 'Festival Electrónico Norte', 'General', 1200.00, 200, 200, 'Acceso general al festival.' UNION ALL
SELECT 'Festival Electrónico Norte', 'VIP', 2500.00, 60, 60, 'Zona VIP con barra libre y vista privilegiada.' UNION ALL
SELECT 'La casa de los espejos', 'General', 250.00, 100, 100, 'Entrada general.' UNION ALL
SELECT 'La casa de los espejos', 'Platea', 450.00, 40, 40, 'Asientos de platea.' UNION ALL
SELECT 'Clásico Regional de Fútbol', 'Gradas', 600.00, 200, 200, 'Acceso a gradas.' UNION ALL
SELECT 'Clásico Regional de Fútbol', 'Preferente', 900.00, 100, 100, 'Zona preferente.' UNION ALL
SELECT 'Concierto Acústico de Otoño', 'General', 180.00, 37, 40, 'Entrada general.' UNION ALL
SELECT 'Festival Indie de Verano', 'General', 220.00, 58, 60, 'Entrada general.' UNION ALL
SELECT 'Taller de Stand-up', 'General', 150.00, 40, 40, 'Entrada general.' UNION ALL
SELECT 'Gran Final de Pop', 'General', 750.00, 96, 100, 'Entrada general.' UNION ALL
SELECT 'Gran Final de Pop', 'Preferente', 1100.00, 40, 40, 'Zona preferente.' UNION ALL
SELECT 'Copa Valle de Baloncesto', 'General', 120.00, 99, 100, 'Entrada general.' UNION ALL
SELECT 'Copa Valle de Baloncesto', 'Preferente', 200.00, 40, 40, 'Zona preferente junto a la cancha.'
) d
JOIN eventos e ON e.nombre = d.evento
WHERE NOT EXISTS (SELECT 1 FROM boletos b WHERE b.evento_id = e.id AND b.tipo = d.tipo);

INSERT INTO evento_artista (evento_id, artista_id)
SELECT e.id, a.id
FROM (
SELECT 'Encuentro de música' AS evento, 'Luna Eléctrica' AS artista UNION ALL
SELECT 'Encuentro de música', 'Los Cuervos del Norte' UNION ALL
SELECT 'Encuentro de música', 'Sofía Marea' UNION ALL
SELECT 'Noche de Rock en el Valle', 'Los Cuervos del Norte' UNION ALL
SELECT 'Noche de Rock en el Valle', 'Bruma Roja' UNION ALL
SELECT 'Festival Electrónico Norte', 'Luna Eléctrica' UNION ALL
SELECT 'Festival Electrónico Norte', 'Neón Desierto' UNION ALL
SELECT 'Concierto Acústico de Otoño', 'Sofía Marea' UNION ALL
SELECT 'Concierto Acústico de Otoño', 'Dúo Aurora' UNION ALL
SELECT 'Festival Indie de Verano', 'Sofía Marea' UNION ALL
SELECT 'Festival Indie de Verano', 'Valeria Costa' UNION ALL
SELECT 'Gran Final de Pop', 'Valeria Costa' UNION ALL
SELECT 'Gran Final de Pop', 'Marcos Vidal' UNION ALL
SELECT 'Gran Final de Pop', 'Dúo Aurora'
) d
JOIN eventos e ON e.nombre = d.evento
JOIN artistas a ON a.nombre = d.artista
WHERE NOT EXISTS (SELECT 1 FROM evento_artista ea WHERE ea.evento_id = e.id AND ea.artista_id = a.id);

SET @sembrar := (SELECT COUNT(*) FROM compras) = 0 AND (SELECT COUNT(*) FROM cambios_evento) = 0;

INSERT INTO pagos (monto, metodo, estado) SELECT 400.00, 'Tarjeta', 'APROBADO' FROM DUAL WHERE @sembrar = 1;
SET @pago := LAST_INSERT_ID();
INSERT INTO compras (fecha_compra, total, asistente_id, pago_id)
SELECT DATE_SUB(NOW(), INTERVAL 5 DAY), 400.00, u.id, @pago FROM usuarios u WHERE u.correo = 'ana.lopez@correo.com' AND @sembrar = 1;
SET @compra := LAST_INSERT_ID();
INSERT INTO tickets (codigo_qr, estado, compra_id, boleto_id, asiento_id)
SELECT UUID(), 'VIGENTE', @compra, b.id, NULL FROM boletos b JOIN eventos e ON e.id = b.evento_id JOIN (SELECT 1 AS n UNION ALL SELECT 2) k
WHERE e.nombre = 'Encuentro de música' AND b.tipo = 'General' AND k.n <= 2 AND @sembrar = 1;

INSERT INTO pagos (monto, metodo, estado) SELECT 500.00, 'Transferencia', 'APROBADO' FROM DUAL WHERE @sembrar = 1;
SET @pago := LAST_INSERT_ID();
INSERT INTO compras (fecha_compra, total, asistente_id, pago_id)
SELECT DATE_SUB(NOW(), INTERVAL 4 DAY), 500.00, u.id, @pago FROM usuarios u WHERE u.correo = 'carlos.ramirez@correo.com' AND @sembrar = 1;
SET @compra := LAST_INSERT_ID();
INSERT INTO tickets (codigo_qr, estado, compra_id, boleto_id, asiento_id)
SELECT UUID(), 'VIGENTE', @compra, b.id, NULL FROM boletos b JOIN eventos e ON e.id = b.evento_id
WHERE e.nombre = 'Encuentro de música' AND b.tipo = 'VIP' AND @sembrar = 1;

INSERT INTO pagos (monto, metodo, estado) SELECT 1050.00, 'Tarjeta', 'APROBADO' FROM DUAL WHERE @sembrar = 1;
SET @pago := LAST_INSERT_ID();
INSERT INTO compras (fecha_compra, total, asistente_id, pago_id)
SELECT DATE_SUB(NOW(), INTERVAL 1 DAY), 1050.00, u.id, @pago FROM usuarios u WHERE u.correo = 'daniela.torres@correo.com' AND @sembrar = 1;
SET @compra := LAST_INSERT_ID();
INSERT INTO tickets (codigo_qr, estado, compra_id, boleto_id, asiento_id)
SELECT UUID(), 'VIGENTE', @compra, b.id, NULL FROM boletos b JOIN eventos e ON e.id = b.evento_id JOIN (SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3) k
WHERE e.nombre = 'Noche de Rock en el Valle' AND b.tipo = 'General' AND k.n <= 3 AND @sembrar = 1;

INSERT INTO pagos (monto, metodo, estado) SELECT 500.00, 'Tarjeta', 'RECHAZADO' FROM DUAL WHERE @sembrar = 1;
SET @pago := LAST_INSERT_ID();
INSERT INTO compras (fecha_compra, total, asistente_id, pago_id)
SELECT DATE_SUB(NOW(), INTERVAL 2 DAY), 500.00, u.id, @pago FROM usuarios u WHERE u.correo = 'luis.hernandez@correo.com' AND @sembrar = 1;
SET @compra := LAST_INSERT_ID();
INSERT INTO tickets (codigo_qr, estado, compra_id, boleto_id, asiento_id)
SELECT UUID(), 'CANCELADO', @compra, b.id, NULL FROM boletos b JOIN eventos e ON e.id = b.evento_id JOIN (SELECT 1 AS n UNION ALL SELECT 2) k
WHERE e.nombre = 'La casa de los espejos' AND b.tipo = 'General' AND k.n <= 2 AND @sembrar = 1;

INSERT INTO pagos (monto, metodo, estado) SELECT 360.00, 'Tarjeta', 'APROBADO' FROM DUAL WHERE @sembrar = 1;
SET @pago := LAST_INSERT_ID();
INSERT INTO compras (fecha_compra, total, asistente_id, pago_id)
SELECT DATE_SUB(NOW(), INTERVAL 30 DAY), 360.00, u.id, @pago FROM usuarios u WHERE u.correo = 'mariana.perez@correo.com' AND @sembrar = 1;
SET @compra := LAST_INSERT_ID();
INSERT INTO tickets (codigo_qr, estado, compra_id, boleto_id, asiento_id)
SELECT UUID(), 'USADO', @compra, b.id, NULL FROM boletos b JOIN eventos e ON e.id = b.evento_id JOIN (SELECT 1 AS n UNION ALL SELECT 2) k
WHERE e.nombre = 'Concierto Acústico de Otoño' AND b.tipo = 'General' AND k.n <= 2 AND @sembrar = 1;

INSERT INTO pagos (monto, metodo, estado) SELECT 180.00, 'Efectivo', 'APROBADO' FROM DUAL WHERE @sembrar = 1;
SET @pago := LAST_INSERT_ID();
INSERT INTO compras (fecha_compra, total, asistente_id, pago_id)
SELECT DATE_SUB(NOW(), INTERVAL 28 DAY), 180.00, u.id, @pago FROM usuarios u WHERE u.correo = 'ana.lopez@correo.com' AND @sembrar = 1;
SET @compra := LAST_INSERT_ID();
INSERT INTO tickets (codigo_qr, estado, compra_id, boleto_id, asiento_id)
SELECT UUID(), 'USADO', @compra, b.id, NULL FROM boletos b JOIN eventos e ON e.id = b.evento_id
WHERE e.nombre = 'Concierto Acústico de Otoño' AND b.tipo = 'General' AND @sembrar = 1;

INSERT INTO pagos (monto, metodo, estado) SELECT 3000.00, 'Tarjeta', 'APROBADO' FROM DUAL WHERE @sembrar = 1;
SET @pago := LAST_INSERT_ID();
INSERT INTO compras (fecha_compra, total, asistente_id, pago_id)
SELECT DATE_SUB(NOW(), INTERVAL 15 DAY), 3000.00, u.id, @pago FROM usuarios u WHERE u.correo = 'carlos.ramirez@correo.com' AND @sembrar = 1;
SET @compra := LAST_INSERT_ID();
INSERT INTO tickets (codigo_qr, estado, compra_id, boleto_id, asiento_id)
SELECT UUID(), 'USADO', @compra, b.id, NULL FROM boletos b JOIN eventos e ON e.id = b.evento_id JOIN (SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) k
WHERE e.nombre = 'Gran Final de Pop' AND b.tipo = 'General' AND k.n <= 4 AND @sembrar = 1;

INSERT INTO pagos (monto, metodo, estado) SELECT 440.00, 'Transferencia', 'APROBADO' FROM DUAL WHERE @sembrar = 1;
SET @pago := LAST_INSERT_ID();
INSERT INTO compras (fecha_compra, total, asistente_id, pago_id)
SELECT DATE_SUB(NOW(), INTERVAL 65 DAY), 440.00, u.id, @pago FROM usuarios u WHERE u.correo = 'daniela.torres@correo.com' AND @sembrar = 1;
SET @compra := LAST_INSERT_ID();
INSERT INTO tickets (codigo_qr, estado, compra_id, boleto_id, asiento_id)
SELECT UUID(), 'USADO', @compra, b.id, NULL FROM boletos b JOIN eventos e ON e.id = b.evento_id JOIN (SELECT 1 AS n UNION ALL SELECT 2) k
WHERE e.nombre = 'Festival Indie de Verano' AND b.tipo = 'General' AND k.n <= 2 AND @sembrar = 1;

INSERT INTO pagos (monto, metodo, estado) SELECT 1200.00, 'Tarjeta', 'APROBADO' FROM DUAL WHERE @sembrar = 1;
SET @pago := LAST_INSERT_ID();
INSERT INTO compras (fecha_compra, total, asistente_id, pago_id)
SELECT DATE_SUB(NOW(), INTERVAL 12 DAY), 1200.00, u.id, @pago FROM usuarios u WHERE u.correo = 'mariana.perez@correo.com' AND @sembrar = 1;
SET @compra := LAST_INSERT_ID();
INSERT INTO tickets (codigo_qr, estado, compra_id, boleto_id, asiento_id)
SELECT UUID(), 'CANCELADO', @compra, b.id, NULL FROM boletos b JOIN eventos e ON e.id = b.evento_id JOIN (SELECT 1 AS n UNION ALL SELECT 2) k
WHERE e.nombre = 'Clásico Regional de Fútbol' AND b.tipo = 'Gradas' AND k.n <= 2 AND @sembrar = 1;

INSERT INTO pagos (monto, metodo, estado) SELECT 120.00, 'OXXO', 'PENDIENTE' FROM DUAL WHERE @sembrar = 1;
SET @pago := LAST_INSERT_ID();
INSERT INTO compras (fecha_compra, total, asistente_id, pago_id)
SELECT NOW(), 120.00, u.id, @pago FROM usuarios u WHERE u.correo = 'luis.hernandez@correo.com' AND @sembrar = 1;
SET @compra := LAST_INSERT_ID();
INSERT INTO tickets (codigo_qr, estado, compra_id, boleto_id, asiento_id)
SELECT UUID(), 'VIGENTE', @compra, b.id, NULL FROM boletos b JOIN eventos e ON e.id = b.evento_id
WHERE e.nombre = 'Copa Valle de Baloncesto' AND b.tipo = 'General' AND @sembrar = 1;

INSERT INTO cambios_evento (fecha_cambio, evento_id, organizador_id)
SELECT DATE_SUB(NOW(), INTERVAL 3 DAY), e.id, e.organizador_id FROM eventos e WHERE e.nombre = 'Encuentro de música' AND @sembrar = 1;
INSERT INTO cambios_fecha (id, valor_anterior, valor_nuevo)
SELECT LAST_INSERT_ID(), DATE_SUB(e.fecha, INTERVAL 7 DAY), e.fecha FROM eventos e WHERE e.nombre = 'Encuentro de música' AND @sembrar = 1;

INSERT INTO cambios_evento (fecha_cambio, evento_id, organizador_id)
SELECT DATE_SUB(NOW(), INTERVAL 1 DAY), e.id, e.organizador_id FROM eventos e WHERE e.nombre = 'Noche de Rock en el Valle' AND @sembrar = 1;
INSERT INTO cambios_lugar (id, recinto_anterior_id, recinto_nuevo_id)
SELECT LAST_INSERT_ID(), ra.id, e.recinto_id FROM eventos e JOIN recintos ra ON ra.nombre = 'Teatro Sonora' WHERE e.nombre = 'Noche de Rock en el Valle' AND @sembrar = 1;

INSERT INTO cambios_evento (fecha_cambio, evento_id, organizador_id)
SELECT DATE_SUB(NOW(), INTERVAL 6 DAY), e.id, e.organizador_id FROM eventos e WHERE e.nombre = 'La casa de los espejos' AND @sembrar = 1;
INSERT INTO cambios_descripcion (id, valor_anterior, valor_nuevo)
SELECT LAST_INSERT_ID(), 'Obra de teatro de suspenso.', e.descripcion FROM eventos e WHERE e.nombre = 'La casa de los espejos' AND @sembrar = 1;

SELECT 'usuarios' AS tabla, COUNT(*) AS registros FROM usuarios UNION ALL
SELECT 'artistas', COUNT(*) FROM artistas UNION ALL
SELECT 'croquis', COUNT(*) FROM croquis UNION ALL
SELECT 'zonas', COUNT(*) FROM zonas UNION ALL
SELECT 'asientos', COUNT(*) FROM asientos UNION ALL
SELECT 'recintos', COUNT(*) FROM recintos UNION ALL
SELECT 'eventos', COUNT(*) FROM eventos UNION ALL
SELECT 'boletos', COUNT(*) FROM boletos UNION ALL
SELECT 'evento_artista', COUNT(*) FROM evento_artista UNION ALL
SELECT 'pagos', COUNT(*) FROM pagos UNION ALL
SELECT 'compras', COUNT(*) FROM compras UNION ALL
SELECT 'tickets', COUNT(*) FROM tickets UNION ALL
SELECT 'cambios_evento', COUNT(*) FROM cambios_evento;
