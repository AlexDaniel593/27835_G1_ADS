import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { productService } from '../../services/productService';
import './DeletedProducts.css';

const DeletedProducts = () => {
  const [deletedProducts, setDeletedProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [restoring, setRestoring] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    loadDeletedProducts();
  }, []);

  const loadDeletedProducts = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await productService.getDeletedProducts();
      setDeletedProducts(response.data || []);
    } catch (err) {
      console.error('Error loading deleted products:', err);
      setError(err.response?.data?.message || 'Error al cargar los productos eliminados');
    } finally {
      setLoading(false);
    }
  };

  // Paso 4 y 5: Seleccionar producto y mostrar confirmación
  const handleRestoreClick = (product) => {
    setSelectedProduct(product);
    setShowModal(true);
  };

  // Excepción E.6: Cancelar restauración
  const handleCancelRestore = () => {
    setShowModal(false);
    setSelectedProduct(null);
  };

  // Paso 6-11: Confirmar y ejecutar restauración
  const handleConfirmRestore = async () => {
    if (!selectedProduct) return;

    try {
      setRestoring(true);
      setError(null);

      // Llamar al servicio de restauración
      const response = await productService.restoreProduct(selectedProduct.id);

      // Paso 11: Mostrar mensaje de éxito
      alert(response.message || 'Producto restaurado correctamente.');

      // Cerrar modal
      setShowModal(false);
      setSelectedProduct(null);

      // Recargar la lista de productos eliminados
      await loadDeletedProducts();

    } catch (err) {
      console.error('Error restoring product:', err);
      // Mostrar mensaje de error (validación de duplicados, etc.)
      setError(err.response?.data?.message || 'Error al restaurar el producto');
      setShowModal(false);
    } finally {
      setRestoring(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString('es-EC', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  // Paso 1-2: Acceder a la Bandeja de Eliminados
  if (loading) {
    return (
      <div className="deleted-products-container">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Cargando productos eliminados...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="deleted-products-container">
      <button className="back-button" onClick={() => navigate('/dashboard')}>
        ← Volver al Menú Principal
      </button>

      <div className="deleted-products-header">
        <h1>🗑️ Bandeja de Eliminados</h1>
        <p>Productos eliminados que pueden ser restaurados</p>
      </div>

      {error && (
        <div className="error-message">
          ⚠️ {error}
        </div>
      )}

      {/* Paso 3: Mostrar lista de productos eliminados */}
      {/* Excepción E.3: No hay productos en la bandeja */}
      {deletedProducts.length === 0 ? (
        <div className="empty-message">
          <h2>No hay productos en la bandeja</h2>
          <p>Todos los productos están activos en el catálogo</p>
        </div>
      ) : (
        <div className="deleted-products-grid">
          {deletedProducts.map((product) => (
            <div key={product.id} className="deleted-product-card">
              <div className="deleted-badge">ELIMINADO</div>
              
              <div className="product-image-container">
                {product.images && product.images.length > 0 ? (
                  <img
                    src={product.images[0].url}
                    alt={product.name}
                    className="product-image"
                  />
                ) : (
                  <div className="no-image">📦</div>
                )}
              </div>

              <div className="product-info">
                <h3 className="product-name">{product.name}</h3>
                <p className="product-price">${product.price.toFixed(2)}</p>

                <div className="product-details">
                  <div className="detail-row">
                    <span className="detail-label">Categoría:</span>
                    <span className="detail-value">{product.category?.name || 'N/A'}</span>
                  </div>
                  <div className="detail-row">
                    <span className="detail-label">ID:</span>
                    <span className="detail-value">#{product.id}</span>
                  </div>
                </div>

                {(product.materials && product.materials.length > 0) || 
                 (product.colors && product.colors.length > 0) ? (
                  <div className="product-tags">
                    {product.materials?.map((material) => (
                      <span key={material.id} className="tag">
                        🔨 {material.name}
                      </span>
                    ))}
                    {product.colors?.map((color) => (
                      <span key={color.id} className="tag">
                        🎨 {color.name}
                      </span>
                    ))}
                  </div>
                ) : null}

                {/* Paso 10: Mostrar información de eliminación */}
                <div className="deletion-info">
                  <p><strong>Fecha de eliminación:</strong></p>
                  <p>{formatDate(product.deletedAt)}</p>
                </div>

                {/* Paso 5: Botón para restaurar producto */}
                <button
                  className="restore-button"
                  onClick={() => handleRestoreClick(product)}
                  disabled={restoring}
                >
                  {restoring ? 'Restaurando...' : '♻️ Restaurar Producto'}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Paso 6: Modal de confirmación de restauración */}
      {showModal && selectedProduct && (
        <div className="modal-overlay" onClick={handleCancelRestore}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2>Confirmar Restauración</h2>
            <p>
              ¿Está seguro que desea restaurar este producto? El producto volverá
              al catálogo activo y estará disponible nuevamente.
            </p>
            
            <div className="modal-product-info">
              <p><strong>Producto:</strong> {selectedProduct.name}</p>
              <p><strong>Precio:</strong> ${selectedProduct.price.toFixed(2)}</p>
              <p><strong>Categoría:</strong> {selectedProduct.category?.name || 'N/A'}</p>
            </div>

            <div className="modal-actions">
              <button
                className="modal-button cancel-button"
                onClick={handleCancelRestore}
                disabled={restoring}
              >
                Cancelar
              </button>
              <button
                className="modal-button confirm-button"
                onClick={handleConfirmRestore}
                disabled={restoring}
              >
                {restoring ? 'Restaurando...' : 'Confirmar Restauración'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default DeletedProducts;
