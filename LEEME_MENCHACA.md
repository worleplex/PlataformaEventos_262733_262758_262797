COSAS QUE SE NECESITAN PARA PROBAR MI AVANCE
-Poner sus credenciales del cloudinary en el aplication properties
-poner sus credenciales de sql también en el aplication properties
-ejecutrar este comando en MySQL Workbench (correr todo el bloque junto):
INSERT INTO usuarios (nombre, correo, hash_contrasenia, activo, telefono)
VALUES ('Administrador', 'admin@eventyunks.com',
        '$2a$10$5ohfvjaRiUWeYXO/8z7wt.gPcTVqKW6QyZ8oKGCbmYsnOAbcT0CnK', 1, '6440000000');
INSERT INTO administradores (id) VALUES (LAST_INSERT_ID());

Cosas que faltan: 
-hacer lo mismo con los otros cruds.
-hacer lo del layout base en el nav para más seguridad (y reutilizar código)
-Agregar lo que vimos de las sesiones y las validaciones.
