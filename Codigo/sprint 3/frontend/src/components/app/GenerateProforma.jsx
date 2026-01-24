import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { productService } from '../../services/productService';
import { proformaService } from '../../services/proformaService';
import { offerService } from '../../services/offerService';
import './GenerateProforma.css';

const GenerateProforma = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
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

  const IVA_PERCENTAGE = 0.15;

  useEffect(() => {
    loadProducts();
    loadOffers();
  }, []);

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

  const handleProductToAddChange = (e) => {
    const { name, value } = e.target;
    setProductToAdd(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const getActiveOfferForProduct = (productId) => {
    const today = new Date();
    return offers.find(offer => 
      offer.productId === productId &&
      offer.isActive &&
      new Date(offer.startDate) <= today &&
      new Date(offer.endDate) >= today
    );
  };

  const calculateProductPrice = (product, quantity) => {
    const offer = getActiveOfferForProduct(product.id);
    let unitPrice = parseFloat(product.price);
    let unitDiscount = 0;
    let finalUnitPrice = unitPrice;

    if (offer) {
      if (offer.type === 'PERCENTAGE_DISCOUNT') {
        unitDiscount = unitPrice * (parseFloat(offer.discountValue) / 100);
        finalUnitPrice = unitPrice - unitDiscount;
      } else if (offer.type === 'PROMOTIONAL_PRICE') {
        finalUnitPrice = parseFloat(offer.promotionalPrice);
        unitDiscount = unitPrice - finalUnitPrice;
      }
    }

    return {
      unitPrice,
      unitDiscount,
      finalUnitPrice,
      subtotal: finalUnitPrice * quantity,
      totalDiscount: unitDiscount * quantity,
      offer
    };
  };

  const handleAddProduct = () => {
    if (!productToAdd.productId) {
      toast.error('Debe seleccionar un producto');
      return;
    }

    if (productToAdd.quantity <= 0) {
      toast.error('Cantidad inválida. Debe ingresar una cantidad mayor a 0.');
      return;
    }

    const product = products.find(p => p.id === parseInt(productToAdd.productId));
    
    if (!product) {
      toast.error('Producto no encontrado');
      return;
    }

    // Verificar si el producto ya está agregado
    const existingIndex = selectedProducts.findIndex(p => p.id === product.id);
    
    if (existingIndex >= 0) {
      // Actualizar cantidad
      const updated = [...selectedProducts];
      updated[existingIndex].quantity += productToAdd.quantity;
      setSelectedProducts(updated);
      toast.success('Cantidad actualizada');
    } else {
      // Agregar nuevo producto
      setSelectedProducts(prev => [...prev, {
        ...product,
        quantity: productToAdd.quantity
      }]);
      toast.success('Producto agregado');
    }

    // Resetear selección
    setProductToAdd({ productId: '', quantity: 1 });
  };

  const handleRemoveProduct = (productId) => {
    setSelectedProducts(prev => prev.filter(p => p.id !== productId));
    toast.info('Producto eliminado');
  };

  const handleUpdateQuantity = (productId, newQuantity) => {
    if (newQuantity <= 0) {
      handleRemoveProduct(productId);
      return;
    }

    setSelectedProducts(prev => prev.map(p => 
      p.id === productId ? { ...p, quantity: newQuantity } : p
    ));
  };

  const calculateTotals = () => {
    let subtotalBeforeDiscount = 0;
    let totalDiscount = 0;

    selectedProducts.forEach(product => {
      const calc = calculateProductPrice(product, product.quantity);
      subtotalBeforeDiscount += calc.unitPrice * product.quantity;
      totalDiscount += calc.totalDiscount;
    });

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

  const validateForm = () => {
    if (!customerData.name.trim()) {
      toast.error('El nombre del cliente es obligatorio');
      return false;
    }

    if (!customerData.identification.trim()) {
      toast.error('La cédula es obligatoria');
      return false;
    }

    if (!/^\d{10}$/.test(customerData.identification)) {
      toast.error('La cédula debe tener 10 dígitos');
      return false;
    }

    if (!customerData.phone.trim()) {
      toast.error('El teléfono es obligatorio');
      return false;
    }

    if (!/^\d{10}$/.test(customerData.phone)) {
      toast.error('El teléfono debe tener 10 dígitos');
      return false;
    }

    if (customerData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(customerData.email)) {
      toast.error('El email no es válido');
      return false;
    }

    if (selectedProducts.length === 0) {
      toast.error('Debe seleccionar al menos un producto.');
      return false;
    }

    return true;
  };

  const handlePreviewProforma = () => {
    if (!validateForm()) {
      return;
    }

    const totals = calculateTotals();
    const detailsWithPrices = selectedProducts.map(product => {
      const calc = calculateProductPrice(product, product.quantity);
      return {
        ...product,
        ...calc
      };
    });

    setProformaPreview({
      customer: customerData,
      details: detailsWithPrices,
      ...totals
    });

    setShowConfirmModal(true);
  };

  const handleConfirmProforma = async () => {
    setLoading(true);

    try {
      const proformaData = {
        customer: customerData,
        details: selectedProducts.map(p => ({
          productId: p.id,
          quantity: p.quantity
        }))
      };

      await proformaService.createProforma(proformaData);
      
      toast.success('Proforma generada exitosamente.');
      
      // Resetear formulario
      setCustomerData({
        name: '',
        identification: '',
        address: '',
        phone: '',
        email: ''
      });
      setSelectedProducts([]);
      setShowConfirmModal(false);
      
      // Opcional: redirigir al dashboard después de un momento
      setTimeout(() => {
        navigate('/dashboard');
      }, 2000);

    } catch (error) {
      console.error('Error creating proforma:', error);
      toast.error(error.response?.data?.message || 'Error al generar la proforma');
    } finally {
      setLoading(false);
    }
  };

  const totals = calculateTotals();

  return (
    <div>
      <header className="proforma-header">
        <button className="back-button" onClick={() => navigate('/dashboard')}>
          <i className="fas fa-arrow-left"></i>
        </button>
        <h1>MUEBLERIX</h1>
      </header>

      <div className="proforma-container">
        <h2 className="proforma-title">
          <i className="fas fa-file-invoice"></i> Generar Proforma
        </h2>

        {/* Sección 1: Datos del Cliente */}
        <section className="proforma-section">
          <h3 className="section-title">
            <i className="fas fa-user"></i> Datos del Cliente
          </h3>

          <div className="form-grid">
            <div className="form-group">
              <label htmlFor="name">Nombre Completo *</label>
              <input
                type="text"
                id="name"
                name="name"
                className="form-input"
                placeholder="Ingrese el nombre del cliente"
                value={customerData.name}
                onChange={handleCustomerChange}
                maxLength="150"
              />
            </div>

            <div className="form-group">
              <label htmlFor="identification">Cédula *</label>
              <input
                type="text"
                id="identification"
                name="identification"
                className="form-input"
                placeholder="10 dígitos"
                value={customerData.identification}
                onChange={handleCustomerChange}
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
                className="form-input"
                placeholder="10 dígitos"
                value={customerData.phone}
                onChange={handleCustomerChange}
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
                className="form-input"
                placeholder="cliente@ejemplo.com"
                value={customerData.email}
                onChange={handleCustomerChange}
                maxLength="100"
              />
            </div>

            <div className="form-group full-width">
              <label htmlFor="address">Dirección</label>
              <input
                type="text"
                id="address"
                name="address"
                className="form-input"
                placeholder="Dirección del cliente"
                value={customerData.address}
                onChange={handleCustomerChange}
                maxLength="300"
              />
            </div>
          </div>
        </section>

        {/* Sección 2: Agregar Productos */}
        <section className="proforma-section">
          <h3 className="section-title">
            <i className="fas fa-shopping-cart"></i> Agregar Productos
          </h3>

          <div className="add-product-row">
            <div className="form-group flex-1">
              <label htmlFor="productId">Producto</label>
              <select
                id="productId"
                name="productId"
                className="form-input"
                value={productToAdd.productId}
                onChange={handleProductToAddChange}
              >
                <option value="">Seleccione un producto</option>
                {products.map(product => (
                  <option key={product.id} value={product.id}>
                    {product.name} - ${parseFloat(product.price).toFixed(2)}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group quantity-group">
              <label htmlFor="quantity">Cantidad</label>
              <input
                type="number"
                id="quantity"
                name="quantity"
                className="form-input"
                min="1"
                value={productToAdd.quantity}
                onChange={handleProductToAddChange}
              />
            </div>

            <button
              type="button"
              className="btn-add-product"
              onClick={handleAddProduct}
            >
              <i className="fas fa-plus"></i> Agregar
            </button>
          </div>
        </section>

        {/* Sección 3: Productos Seleccionados */}
        {selectedProducts.length > 0 && (
          <section className="proforma-section">
            <h3 className="section-title">
              <i className="fas fa-list"></i> Productos Seleccionados
            </h3>

            <div className="products-table">
              <table>
                <thead>
                  <tr>
                    <th>Producto</th>
                    <th>Precio Unitario</th>
                    <th>Descuento</th>
                    <th>Precio Final</th>
                    <th>Cantidad</th>
                    <th>Subtotal</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {selectedProducts.map(product => {
                    const calc = calculateProductPrice(product, product.quantity);
                    return (
                      <tr key={product.id}>
                        <td>
                          <div className="product-name-cell">
                            {product.name}
                            {calc.offer && (
                              <span className="offer-badge">
                                <i className="fas fa-tag"></i> Con oferta
                              </span>
                            )}
                          </div>
                        </td>
                        <td>${calc.unitPrice.toFixed(2)}</td>
                        <td className="discount-cell">
                          {calc.unitDiscount > 0 ? `-$${calc.unitDiscount.toFixed(2)}` : '-'}
                        </td>
                        <td className="final-price-cell">${calc.finalUnitPrice.toFixed(2)}</td>
                        <td>
                          <input
                            type="number"
                            className="quantity-input"
                            min="1"
                            value={product.quantity}
                            onChange={(e) => handleUpdateQuantity(product.id, parseInt(e.target.value))}
                          />
                        </td>
                        <td className="subtotal-cell">${calc.subtotal.toFixed(2)}</td>
                        <td>
                          <button
                            className="btn-remove"
                            onClick={() => handleRemoveProduct(product.id)}
                            title="Eliminar producto"
                          >
                            <i className="fas fa-trash"></i>
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </section>
        )}

        {/* Sección 4: Resumen de Totales */}
        {selectedProducts.length > 0 && (
          <section className="proforma-section">
            <h3 className="section-title">
              <i className="fas fa-calculator"></i> Resumen
            </h3>

            <div className="totals-summary">
              <div className="total-row">
                <span>Subtotal (antes de descuentos):</span>
                <span>${totals.subtotalBeforeDiscount.toFixed(2)}</span>
              </div>
              {totals.totalDiscount > 0 && (
                <div className="total-row discount-row">
                  <span>Descuentos aplicados:</span>
                  <span>-${totals.totalDiscount.toFixed(2)}</span>
                </div>
              )}
              <div className="total-row">
                <span>Subtotal:</span>
                <span>${totals.subtotal.toFixed(2)}</span>
              </div>
              <div className="total-row">
                <span>IVA (15%):</span>
                <span>${totals.tax.toFixed(2)}</span>
              </div>
              <div className="total-row final-total">
                <span>TOTAL A PAGAR:</span>
                <span>${totals.total.toFixed(2)}</span>
              </div>
            </div>
          </section>
        )}

        {/* Botones de Acción */}
        <div className="action-buttons">
          <button
            className="btn-cancel"
            onClick={() => navigate('/dashboard')}
          >
            <i className="fas fa-times"></i> Cancelar
          </button>
          <button
            className="btn-confirm"
            onClick={handlePreviewProforma}
            disabled={selectedProducts.length === 0}
          >
            <i className="fas fa-check"></i> Generar Proforma
          </button>
        </div>
      </div>

      {/* Modal de Confirmación */}
      {showConfirmModal && proformaPreview && (
        <div className="modal-overlay" onClick={() => setShowConfirmModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <button className="modal-close" onClick={() => setShowConfirmModal(false)}>
              <i className="fas fa-times"></i>
            </button>

            <h2 className="modal-title">
              <i className="fas fa-eye"></i> Confirmar Proforma
            </h2>

            <div className="modal-body">
              <div className="preview-section">
                <h4>Datos del Cliente</h4>
                <p><strong>Nombre:</strong> {proformaPreview.customer.name}</p>
                <p><strong>Cédula:</strong> {proformaPreview.customer.identification}</p>
                <p><strong>Teléfono:</strong> {proformaPreview.customer.phone}</p>
                {proformaPreview.customer.email && <p><strong>Email:</strong> {proformaPreview.customer.email}</p>}
                {proformaPreview.customer.address && <p><strong>Dirección:</strong> {proformaPreview.customer.address}</p>}
              </div>

              <div className="preview-section">
                <h4>Productos</h4>
                {proformaPreview.details.map((detail, index) => (
                  <div key={index} className="preview-product">
                    <p><strong>{detail.name}</strong></p>
                    <p>Cantidad: {detail.quantity} × ${detail.finalUnitPrice.toFixed(2)} = ${detail.subtotal.toFixed(2)}</p>
                    {detail.offer && <p className="offer-info">✓ Oferta aplicada</p>}
                  </div>
                ))}
              </div>

              <div className="preview-section">
                <h4>Totales</h4>
                <p><strong>Subtotal:</strong> ${proformaPreview.subtotal.toFixed(2)}</p>
                <p><strong>IVA (15%):</strong> ${proformaPreview.tax.toFixed(2)}</p>
                <p className="preview-total"><strong>TOTAL:</strong> ${proformaPreview.total.toFixed(2)}</p>
              </div>
            </div>

            <div className="modal-actions">
              <button
                className="btn-modal-cancel"
                onClick={() => setShowConfirmModal(false)}
              >
                Cancelar
              </button>
              <button
                className="btn-modal-confirm"
                onClick={handleConfirmProforma}
                disabled={loading}
              >
                {loading ? 'Generando...' : 'Confirmar Proforma'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default GenerateProforma;
