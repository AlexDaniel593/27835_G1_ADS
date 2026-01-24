-- Script para corregir la base de datos eliminando la columna failed_attempts
-- Ejecutar este script en MySQL:
-- mysql -u jairo -p < fix_database.sql

USE mueblerix;

-- Eliminar la columna failed_attempts
ALTER TABLE users DROP COLUMN failed_attempts;

-- Verificar que la columna se eliminó correctamente
DESCRIBE users;
