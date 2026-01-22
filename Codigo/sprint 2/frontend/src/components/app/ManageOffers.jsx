import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast, Toaster } from 'sonner';
import { offerService } from '../../services/offerService';
import { productService } from '../../services/productService';
import './ManageOffers.css';

const ManageOffers = () => {
  const [offers, setOffers] = useState([]);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const [editingOffer, setEditingOffer] = useState(null);
  const [offerToDelete, setOfferToDelete] = useState(null);
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    productId: '',
    type: 'PERCENTAGE_DISCOUNT',
    discountValue: '',
    promotionalPrice: '',
    startDate: '',
    endDate: '',
    isActive: true,
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      const [offersResponse, productsResponse] = await Promise.all([
        offerService.getAllOffers(),
        productService.getAllProducts()
      ]);
      
      setOffers(offersResponse.data || []);
      setProducts(productsResponse.data || []);
    } catch (err) {
      console.error('Error loading data:', err);
      const errorMsg = err.response?.data?.message || 'Error al cargar los datos';
      setError(errorMsg);
      toast.error(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  // Paso 3: Seleccionar acción - Registrar nueva oferta (Flujo A1)
  const handleNewOffer = () => {
    setEditingOffer(null);
    setFormData({
      productId: '',
      type: 'PERCENTAGE_DISCOUNT',
      discountValue: '',
      promotionalPrice: '',
      startDate: '',
      endDate: '',
      isActive: true,
    });
    setShowForm(true);
  };

  // Flujo B1: Seleccionar oferta para modificar
  const handleEditOffer = (offer) => {
    setEditingOffer(offer);
    setFormData({
      productId: offer.productId,
      type: offer.type,
      discountValue: offer.discountValue || '',
      promotionalPrice: offer.promotionalPrice || '',
      startDate: offer.startDate,
      endDate: offer.endDate,
      isActive: offer.isActive,
    });
    setShowForm(true);
  };

  // Flujo C1: Seleccionar oferta para eliminar
  const handleDeleteClick = (offer) => {
    setOfferToDelete(offer);
    setShowDeleteConfirm(true);
  };

  // A4 / B4: Confirmar operación
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      setSubmitting(true);
      setError(null);

      // Preparar datos
      const offerData = {
        ...formData,
        productId: parseInt(formData.productId),
        discountValue: formData.type === 'PERCENTAGE_DISCOUNT' ? 
                       parseFloat(formData.discountValue) : null,
        promotionalPrice: formData.type === 'PROMOTIONAL_PRICE' ? 
                         parseFloat(formData.promotionalPrice) : null,
      };

      let response;
      if (editingOffer) {
        // B6: Actualizar oferta
        response = await offerService.updateOffer(editingOffer.id, offerData);
      } else {
        // A6: Registrar nueva oferta
        response = await offerService.createOffer(offerData);
      }

      // A7 / B7: Mensaje de éxito
      toast.success(response.message || (editingOffer ? 'Oferta actualizada exitosamente' : 'Oferta creada exitosamente'));
      
      setShowForm(false);
      await loadData();

    } catch (err) {
      console.error('Error saving offer:', err);
      // Excepciones A3, A5, B3, B5
      const errorMsg = err.response?.data?.message || 'Error al guardar la oferta';
      setError(errorMsg);
      toast.error(errorMsg);
    } finally {
      setSubmitting(false);
    }
  };

  // C3: Confirmar eliminación
  const handleConfirmDelete = async () => {
    if (!offerToDelete) return;

    try {
      setSubmitting(true);
      setError(null);

      // C4: Eliminar oferta
      const response = await offerService.deleteOffer(offerToDelete.id);
      
      // C5: Mensaje de éxito
      toast.success(response.message || 'Oferta eliminada exitosamente');
      
      setShowDeleteConfirm(false);
      setOfferToDelete(null);
      await loadData();

    } catch (err) {
      console.error('Error deleting offer:', err);
      const errorMsg = err.response?.data?.message || 'Error al eliminar la oferta';
      setError(errorMsg);
      toast.error(errorMsg);
      setShowDeleteConfirm(false);
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancelForm = () => {
    // Excepción C3: Cancelar
    setShowForm(false);
    setEditingOffer(null);
    setError(null);
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('es-EC');
  };

  const getStatusClass = (status) => {
    if (status === 'VIGENTE') return 'status-vigente';
    if (status === 'PRÓXIMA') return 'status-proxima';
    return 'status-expirada';
  };

  const getTypeClass = (type) => {
    return type === 'PERCENTAGE_DISCOUNT' ? 'type-discount' : 'type-promotional';
  };

  const getTypeLabel = (type) => {
    return type === 'PERCENTAGE_DISCOUNT' ? 'Descuento %' : 'Precio Promocional';
  };

  if (loading) {
    return (
      <div className="manage-offers-container">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Cargando ofertas...</p>
        </div>
      </div>
    );
  }

  return (
    <>
      {/* Encabezado */}
      <div className="manage-offers-header">
        <button className="back-to-dashboard" onClick={() => navigate('/dashboard')}>
          ← Volver
        </button>
        <h1>GESTIONAR OFERTAS</h1>
      </div>

      <div className="manage-offers-container">
        <div className="manage-offers-title">
          <span>🎁</span>
          <h2>Gestión de Ofertas Especiales</h2>
        </div>

        {error && (
          <div className="error-message">
            ⚠️ {error}
          </div>
        )}

        {/* Paso 3: Seleccionar acción */}
        <div className="action-buttons">
          <button className="btn-new-offer" onClick={handleNewOffer}>
            ➕ Registrar Nueva Oferta
          </button>
        </div>

        {/* Paso 2: Lista de ofertas actuales */}
        <div className="offers-list">
          <h2>Ofertas Actuales</h2>
          
          {offers.length === 0 ? (
            <div className="empty-state">
              <div className="empty-state-icon">🎁</div>
              <h3>No hay ofertas registradas</h3>
              <p>Comienza creando tu primera oferta especial</p>
            </div>
          ) : (
            <table className="offers-table">
              <thead>
                <tr>
                  <th>Producto</th>
                  <th>Tipo</th>
                  <th>Descuento/Precio</th>
                  <th>Precio Original</th>
                  <th>Precio Final</th>
                  <th>Vigencia</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
              {offers.map((offer) => (
                <tr key={offer.id}>
                  <td>
                    <strong>{offer.productName}</strong>
                  </td>
                  <td>
                    <span className={`offer-type-badge ${getTypeClass(offer.type)}`}>
                      {getTypeLabel(offer.type)}
                    </span>
                  </td>
                  <td>
                    {offer.type === 'PERCENTAGE_DISCOUNT' ? 
                      `${offer.discountValue}%` : 
                      `$${offer.promotionalPrice?.toFixed(2)}`}
                  </td>
                  <td>
                    <div className="price-info">
                      <span className="original-price">
                        ${offer.originalPrice?.toFixed(2)}
                      </span>
                    </div>
                  </td>
                  <td>
                    <span className="final-price">
                      ${offer.finalPrice?.toFixed(2)}
                    </span>
                  </td>
                  <td>
                    <div>
                      <div>{formatDate(offer.startDate)}</div>
                      <div>{formatDate(offer.endDate)}</div>
                    </div>
                  </td>
                  <td>
                    <span className={`status-badge ${getStatusClass(offer.status)}`}>
                      {offer.status}
                    </span>
                  </td>
                  <td>
                    <div className="table-actions">
                      <button 
                        className="action-btn btn-edit" 
                        onClick={() => handleEditOffer(offer)}
                      >
                        ✏️ Modificar
                      </button>
                      <button 
                        className="action-btn btn-delete" 
                        onClick={() => handleDeleteClick(offer)}
                      >
                        🗑️ Eliminar
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* A2 / B2: Formulario de oferta */}
      {showForm && (
        <div className="offer-modal-overlay" onClick={handleCancelForm}>
          <div className="offer-modal" onClick={(e) => e.stopPropagation()}>
            <div className="offer-modal-header">
              <h2>{editingOffer ? 'Modificar Oferta' : 'Registrar Nueva Oferta'}</h2>
              <button className="close-modal" onClick={handleCancelForm}>×</button>
            </div>
            
            {error && (
              <div className="error-message">
                ⚠️ {error}
              </div>
            )}

            <form className="offer-form" onSubmit={handleSubmit}>
              {/* A3: Ingresar información */}
              <div className="form-group">
                <label>Producto *</label>
                <select
                  name="productId"
                  value={formData.productId}
                  onChange={handleInputChange}
                  required
                >
                  <option value="">Seleccione un producto</option>
                  {products.map((product) => (
                    <option key={product.id} value={product.id}>
                      {product.name} - ${product.price.toFixed(2)}
                    </option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Tipo de Oferta *</label>
                <select
                  name="type"
                  value={formData.type}
                  onChange={handleInputChange}
                  required
                >
                  <option value="PERCENTAGE_DISCOUNT">Descuento Porcentual (%)</option>
                  <option value="PROMOTIONAL_PRICE">Precio Promocional</option>
                </select>
              </div>

              {formData.type === 'PERCENTAGE_DISCOUNT' ? (
                <div className="form-group">
                  <label>Porcentaje de Descuento (%) *</label>
                  <input
                    type="number"
                    name="discountValue"
                    value={formData.discountValue}
                    onChange={handleInputChange}
                    min="0.01"
                    max="100"
                    step="0.01"
                    required
                    placeholder="Ej: 20"
                  />
                </div>
              ) : (
                <div className="form-group">
                  <label>Precio Promocional ($) *</label>
                  <input
                    type="number"
                    name="promotionalPrice"
                    value={formData.promotionalPrice}
                    onChange={handleInputChange}
                    min="0.01"
                    step="0.01"
                    required
                    placeholder="Ej: 45.99"
                  />
                </div>
              )}

              <div className="form-row">
                <div className="form-group">
                  <label>Fecha de Inicio *</label>
                  <input
                    type="date"
                    name="startDate"
                    value={formData.startDate}
                    onChange={handleInputChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Fecha de Fin *</label>
                  <input
                    type="date"
                    name="endDate"
                    value={formData.endDate}
                    onChange={handleInputChange}
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label>
                  <input
                    type="checkbox"
                    name="isActive"
                    checked={formData.isActive}
                    onChange={handleInputChange}
                  />
                  Oferta Activa
                </label>
              </div>

              <div className="form-buttons">
                <button 
                  type="button" 
                  className="btn-cancel" 
                  onClick={handleCancelForm}
                  disabled={submitting}
                >
                  Cancelar
                </button>
                <button 
                  type="submit" 
                  className="btn-submit"
                  disabled={submitting}
                >
                  {submitting ? 'Guardando...' : 
                   editingOffer ? 'Guardar Cambios' : 'Crear Oferta'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* C2: Modal de confirmación de eliminación */}
      {showDeleteConfirm && offerToDelete && (
        <div className="offer-modal-overlay" onClick={() => setShowDeleteConfirm(false)}>
          <div className="confirmation-modal" onClick={(e) => e.stopPropagation()}>
            <h3>⚠️ Confirmar Eliminación</h3>
            <p>
              ¿Está seguro que desea eliminar la oferta del producto{' '}
              <strong>{offerToDelete.productName}</strong>?
            </p>
            <p>Esta acción no se puede deshacer.</p>
            
            <div className="confirmation-buttons">
              <button 
                className="btn-cancel" 
                onClick={() => setShowDeleteConfirm(false)}
                disabled={submitting}
              >
                Cancelar
              </button>
              <button 
                className="btn-confirm" 
                onClick={handleConfirmDelete}
                disabled={submitting}
              >
                {submitting ? 'Eliminando...' : 'Confirmar Eliminación'}
              </button>
            </div>
          </div>
        </div>
      )}

      <Toaster position="top-right" richColors />
    </div>
    </>
  );
};

export default ManageOffers;
