-- Script de migración para agregar campos de restauración de productos (RF-07)
-- Fecha: 2026-01-22
-- Descripción: Agrega campos restoredAt y restoration_user_id a la tabla products

-- Agregar columna para fecha de restauración
ALTER TABLE products 
ADD COLUMN restored_at DATETIME NULL AFTER deleted_at;

-- Agregar columna para usuario que restauró el producto
ALTER TABLE products 
ADD COLUMN restoration_user_id BIGINT NULL AFTER deletion_user_id;

-- Agregar clave foránea para el usuario que restauró
ALTER TABLE products 
ADD CONSTRAINT fk_products_restoration_user 
FOREIGN KEY (restoration_user_id) REFERENCES users(id);

-- Agregar índice para mejorar el rendimiento de las consultas
CREATE INDEX idx_products_is_deleted ON products(is_deleted);
CREATE INDEX idx_products_deleted_at ON products(deleted_at);

-- Comentarios de las columnas
ALTER TABLE products 
MODIFY COLUMN restored_at DATETIME NULL 
COMMENT 'Fecha y hora en que el producto fue restaurado';

ALTER TABLE products 
MODIFY COLUMN restoration_user_id BIGINT NULL 
COMMENT 'ID del usuario que restauró el producto';

-- Verificar la estructura de la tabla
DESCRIBE products;
