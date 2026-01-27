import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { productService } from '../../services/productService';
import { proformaService } from '../../services/proformaService';
import { offerService } from '../../services/offerService';
import './EditProforma.css';

const EditProforma = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [products, setProducts] = useState([]);
  const [offers, setOffers] = useState([]);

  // Datos del cliente
  const [customerData, setCustomerData] = useState({
    name: '',
    identification: '',
    address: '',
    phone: '',
    email: ''
  });

  // Productos seleccionados
  const [selectedProducts, setSelectedProducts] = useState([]);

  // Producto a agregar
  const [productToAdd, setProductToAdd] = useState({
    productId: '',
    quantity: 1
  });

  // Modal de confirmación
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [proformaPreview, setProformaPreview] = useState(null);

  // Razón de modificación
  const [modificationReason, setModificationReason] = useState('');

  const IVA_PERCENTAGE = 0.15;

  useEffect(() => {
    loadProforma();
    loadProducts();
    loadOffers();
  }, [id]);

  const loadProforma = async () => {
    try {
      const response = await proformaService.getProformaById(id);
      const proforma = response.data;

      // Cargar datos del cliente
      setCustomerData({
        name: proforma.customer.name,
        identification: proforma.customer.identification,
        address: proforma.customer.address,
        phone: proforma.customer.phone,
        email: proforma.customer.email || ''
      });

      // Cargar productos seleccionados
      setSelectedProducts(proforma.details.map(detail => ({
        productId: detail.productId,
        productName: detail.productName,
        quantity: detail.quantity,
        unitPrice: detail.unitPrice,
        unitDiscount: detail.unitDiscount,
        subtotal: detail.subtotal,
        appliedOffer: detail.appliedOffer
      })));

      setLoading(false);
    } catch (error) {
      console.error('Error loading proforma:', error);
      toast.error('Error al cargar la proforma');
      navigate('/consult-proforma');
    }
  };

  const loadProducts = async () => {
    try {
      const response = await productService.getAllProducts();
      setProducts(response.data || []);
    } catch (error) {
      console.error('Error loading products:', error);
      toast.error('Error al cargar los productos');
    }
  };

  const loadOffers = async () => {
    try {
      const response = await offerService.getAllOffers();
      setOffers(response.data || []);
    } catch (error) {
      console.error('Error loading offers:', error);
    }
  };

  const handleCustomerChange = (e) => {
    const { name, value } = e.target;
    setCustomerData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleAddProduct = () => {
    if (!productToAdd.productId) {
      toast.error('Seleccione un producto');
      return;
    }

    const product = products.find(p => p.id === parseInt(productToAdd.productId));
    if (!product) return;

    // Verificar si ya está agregado
    if (selectedProducts.find(p => p.productId === product.id)) {
      toast.error('El producto ya está en la lista');
      return;
    }

    // Buscar oferta activa
    const activeOffer = offers.find(o => 
      o.productId === product.id && 
      o.isActive &&
      new Date(o.startDate) <= new Date() &&
      new Date(o.endDate) >= new Date()
    );

    let unitPrice = product.price;
    let unitDiscount = 0;
    let finalUnitPrice = unitPrice;

    if (activeOffer) {
      if (activeOffer.type === 'PERCENTAGE_DISCOUNT') {
        unitDiscount = (unitPrice * activeOffer.discountValue) / 100;
        finalUnitPrice = unitPrice - unitDiscount;
      } else if (activeOffer.type === 'PROMOTIONAL_PRICE') {
        finalUnitPrice = activeOffer.promotionalPrice;
        unitDiscount = unitPrice - finalUnitPrice;
      }
    }

    const newProduct = {
      productId: product.id,
      productName: product.name,
      quantity: productToAdd.quantity,
      unitPrice: unitPrice,
      unitDiscount: unitDiscount,
      subtotal: finalUnitPrice * productToAdd.quantity,
      appliedOffer: activeOffer || null
    };

    setSelectedProducts([...selectedProducts, newProduct]);
    setProductToAdd({ productId: '', quantity: 1 });
    toast.success('Producto agregado');
  };

  const handleRemoveProduct = (productId) => {
    setSelectedProducts(selectedProducts.filter(p => p.productId !== productId));
    toast.info('Producto eliminado');
  };

  const handleQuantityChange = (productId, newQuantity) => {
    if (newQuantity < 1) return;

    setSelectedProducts(selectedProducts.map(p => {
      if (p.productId === productId) {
        const finalUnitPrice = p.unitPrice - p.unitDiscount;
        return {
          ...p,
          quantity: newQuantity,
          subtotal: finalUnitPrice * newQuantity
        };
      }
      return p;
    }));
  };

  const calculateTotals = () => {
    const subtotalBeforeDiscount = selectedProducts.reduce((sum, p) => 
      sum + (p.unitPrice * p.quantity), 0);
    const totalDiscount = selectedProducts.reduce((sum, p) => 
      sum + (p.unitDiscount * p.quantity), 0);
    const subtotal = subtotalBeforeDiscount - totalDiscount;
    const tax = subtotal * IVA_PERCENTAGE;
    const total = subtotal + tax;

    return {
      subtotalBeforeDiscount,
      totalDiscount,
      subtotal,
      tax,
      total
    };
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    // Validaciones
    if (!customerData.name || !customerData.identification || !customerData.phone || !customerData.address) {
      toast.error('Complete todos los datos del cliente');
      return;
    }

    if (selectedProducts.length === 0) {
      toast.error('Debe incluir al menos un producto en la proforma');
      return;
    }

    if (!/^\d{10}$/.test(customerData.identification)) {
      toast.error('La cédula debe tener 10 dígitos');
      return;
    }

    if (!/^\d{10}$/.test(customerData.phone)) {
      toast.error('El teléfono debe tener 10 dígitos');
      return;
    }

    const totals = calculateTotals();
    setProformaPreview({ ...customerData, products: selectedProducts, ...totals });
    setShowConfirmModal(true);
  };

  const handleConfirmUpdate = async () => {
    setSaving(true);

    try {
      const requestData = {
        customer: {
          name: customerData.name,
          identification: customerData.identification,
          address: customerData.address,
          phone: customerData.phone,
          email: customerData.email || null
        },
        details: selectedProducts.map(p => ({
          productId: p.productId,
          quantity: p.quantity
        })),
        modificationReason: modificationReason || 'Actualización de proforma'
      };

      await proformaService.updateProforma(id, requestData);
      
      toast.success('Proforma actualizada correctamente');
      navigate('/consult-proforma');
    } catch (error) {
      console.error('Error updating proforma:', error);
      toast.error(error.response?.data?.message || 'Error al actualizar la proforma');
    } finally {
      setSaving(false);
      setShowConfirmModal(false);
    }
  };

  if (loading) {
    return (
      <div className="edit-proforma-container">
        <div className="loading">
          <i className="fas fa-spinner fa-spin"></i> Cargando proforma...
        </div>
      </div>
    );
  }

  const totals = calculateTotals();

  return (
    <div className="edit-proforma-container">
      <div className="edit-header">
        <h1>Actualizar Proforma</h1>
        <button onClick={() => navigate('/consult-proforma')} className="btn-back">
          <i className="fas fa-arrow-left"></i> Volver
        </button>
      </div>

      <form onSubmit={handleSubmit} className="edit-proforma-form">
        {/* Datos del Cliente */}
        <div className="form-section">
          <h2>Datos del Cliente</h2>
          <div className="form-grid">
            <div className="form-group">
              <label htmlFor="name">Nombre Completo *</label>
              <input
                type="text"
                id="name"
                name="name"
                value={customerData.name}
                onChange={handleCustomerChange}
                required
                maxLength="150"
              />
            </div>

            <div className="form-group">
              <label htmlFor="identification">Cédula *</label>
              <input
                type="text"
                id="identification"
                name="identification"
                value={customerData.identification}
                onChange={handleCustomerChange}
                required
                maxLength="10"
                pattern="\d{10}"
              />
            </div>

            <div className="form-group">
              <label htmlFor="phone">Teléfono *</label>
              <input
                type="text"
                id="phone"
                name="phone"
                value={customerData.phone}
                onChange={handleCustomerChange}
                required
                maxLength="10"
                pattern="\d{10}"
              />
            </div>

            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input
                type="email"
                id="email"
                name="email"
                value={customerData.email}
                onChange={handleCustomerChange}
                maxLength="100"
              />
            </div>

            <div className="form-group full-width">
              <label htmlFor="address">Dirección *</label>
              <textarea
                id="address"
                name="address"
                value={customerData.address}
                onChange={handleCustomerChange}
                required
                maxLength="300"
                rows="2"
              />
            </div>
          </div>
        </div>

        {/* Agregar Productos */}
        <div className="form-section">
          <h2>Productos</h2>
          <div className="add-product-section">
            <div className="form-group">
              <label htmlFor="product">Seleccionar Producto</label>
              <select
                id="product"
                value={productToAdd.productId}
                onChange={(e) => setProductToAdd({ ...productToAdd, productId: e.target.value })}
              >
                <option value="">-- Seleccione un producto --</option>
                {products.filter(p => p.isActive && !p.isDeleted).map(product => (
                  <option key={product.id} value={product.id}>
                    {product.name} - ${product.price.toFixed(2)}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="quantity">Cantidad</label>
              <input
                type="number"
                id="quantity"
                value={productToAdd.quantity}
                onChange={(e) => setProductToAdd({ ...productToAdd, quantity: parseInt(e.target.value) || 1 })}
                min="1"
              />
            </div>

            <button type="button" onClick={handleAddProduct} className="btn-add-product">
              <i className="fas fa-plus"></i> Agregar
            </button>
          </div>

          {/* Tabla de Productos Seleccionados */}
          {selectedProducts.length > 0 && (
            <div className="products-table-container">
              <table className="products-table">
                <thead>
                  <tr>
                    <th>Producto</th>
                    <th>Cantidad</th>
                    <th>Precio Unit.</th>
                    <th>Descuento</th>
                    <th>Subtotal</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {selectedProducts.map((product) => (
                    <tr key={product.productId}>
                      <td>
                        {product.productName}
                        {product.appliedOffer && (
                          <span className="offer-badge">
                            <i className="fas fa-tag"></i> Oferta
                          </span>
                        )}
                      </td>
                      <td>
                        <input
                          type="number"
                          value={product.quantity}
                          onChange={(e) => handleQuantityChange(product.productId, parseInt(e.target.value) || 1)}
                          min="1"
                          className="quantity-input"
                        />
                      </td>
                      <td>${product.unitPrice.toFixed(2)}</td>
                      <td className="discount">${product.unitDiscount.toFixed(2)}</td>
                      <td className="price">${product.subtotal.toFixed(2)}</td>
                      <td>
                        <button
                          type="button"
                          onClick={() => handleRemoveProduct(product.productId)}
                          className="btn-remove"
                          title="Eliminar producto"
                        >
                          <i className="fas fa-trash"></i>
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Resumen de Totales */}
        {selectedProducts.length > 0 && (
          <div className="totals-section">
            <h2>Resumen</h2>
            <div className="totals-grid">
              <div className="total-row">
                <span>Subtotal antes de descuento:</span>
                <span>${totals.subtotalBeforeDiscount.toFixed(2)}</span>
              </div>
              <div className="total-row discount">
                <span>Descuento total:</span>
                <span>-${totals.totalDiscount.toFixed(2)}</span>
              </div>
              <div className="total-row">
                <span>Subtotal:</span>
                <span>${totals.subtotal.toFixed(2)}</span>
              </div>
              <div className="total-row">
                <span>IVA (15%):</span>
                <span>${totals.tax.toFixed(2)}</span>
              </div>
              <div className="total-row final">
                <span>Total a pagar:</span>
                <span>${totals.total.toFixed(2)}</span>
              </div>
            </div>
          </div>
        )}

        {/* Razón de Modificación */}
        <div className="form-section">
          <h2>Razón de Modificación</h2>
          <div className="form-group full-width">
            <label htmlFor="modificationReason">Descripción (opcional)</label>
            <textarea
              id="modificationReason"
              value={modificationReason}
              onChange={(e) => setModificationReason(e.target.value)}
              maxLength="1000"
              rows="3"
              placeholder="Describa brevemente la razón de la actualización..."
            />
          </div>
        </div>

        {/* Botones de Acción */}
        <div className="form-actions">
          <button type="button" onClick={() => navigate('/consult-proforma')} className="btn-cancel">
            Cancelar
          </button>
          <button type="submit" className="btn-submit" disabled={selectedProducts.length === 0}>
            <i className="fas fa-save"></i> Guardar Cambios
          </button>
        </div>
      </form>

      {/* Modal de Confirmación */}
      {showConfirmModal && proformaPreview && (
        <div className="modal-overlay" onClick={() => setShowConfirmModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Confirmar Actualización</h2>
              <button onClick={() => setShowConfirmModal(false)} className="btn-close">
                <i className="fas fa-times"></i>
              </button>
            </div>

            <div className="modal-body">
              <div className="preview-section">
                <h3>Cliente</h3>
                <p><strong>Nombre:</strong> {proformaPreview.name}</p>
                <p><strong>Cédula:</strong> {proformaPreview.identification}</p>
                <p><strong>Teléfono:</strong> {proformaPreview.phone}</p>
              </div>

              <div className="preview-section">
                <h3>Productos ({proformaPreview.products.length})</h3>
                <ul className="products-list">
                  {proformaPreview.products.map((p, index) => (
                    <li key={index}>
                      {p.productName} - Cantidad: {p.quantity} - ${p.subtotal.toFixed(2)}
                    </li>
                  ))}
                </ul>
              </div>

              <div className="preview-section">
                <h3>Total a Pagar</h3>
                <p className="total-preview">${proformaPreview.total.toFixed(2)}</p>
              </div>
            </div>

            <div className="modal-footer">
              <button onClick={() => setShowConfirmModal(false)} className="btn-secondary">
                Cancelar
              </button>
              <button onClick={handleConfirmUpdate} className="btn-primary" disabled={saving}>
                {saving ? 'Guardando...' : 'Confirmar Actualización'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default EditProforma;
