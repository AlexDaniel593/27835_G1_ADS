import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { proformaService } from '../../services/proformaService';
import './ConsultProforma.css';

const ConsultProforma = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [proformas, setProformas] = useState([]);
  const [selectedProforma, setSelectedProforma] = useState(null);
  const [showDetailModal, setShowDetailModal] = useState(false);

  // Filtros de búsqueda
  const [filters, setFilters] = useState({
    code: '',
    customerName: '',
    customerIdentification: '',
    startDate: '',
    endDate: ''
  });

  useEffect(() => {
    loadAllProformas();
  }, []);

  const loadAllProformas = async () => {
    setLoading(true);
    try {
      const response = await proformaService.getAllProformas();
      setProformas(response.data || []);
    } catch (error) {
      console.error('Error loading proformas:', error);
      toast.error('Error al cargar las proformas');
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      // Construir parámetros solo con valores no vacíos
      const params = {};
      if (filters.code) params.code = filters.code;
      if (filters.customerName) params.customerName = filters.customerName;
      if (filters.customerIdentification) params.customerIdentification = filters.customerIdentification;
      if (filters.startDate) params.startDate = new Date(filters.startDate).toISOString();
      if (filters.endDate) {
        const endDateTime = new Date(filters.endDate);
        endDateTime.setHours(23, 59, 59, 999);
        params.endDate = endDateTime.toISOString();
      }

      const response = await proformaService.searchProformas(params);
      setProformas(response.data || []);
      
      if (!response.data || response.data.length === 0) {
        toast.info('No se encontraron proformas con los criterios ingresados');
      } else {
        toast.success(`Se encontraron ${response.data.length} proforma(s)`);
      }
    } catch (error) {
      console.error('Error searching proformas:', error);
      toast.error('Error al buscar las proformas');
    } finally {
      setLoading(false);
    }
  };

  const handleClearFilters = () => {
    setFilters({
      code: '',
      customerName: '',
      customerIdentification: '',
      startDate: '',
      endDate: ''
    });
    loadAllProformas();
  };

  const handleViewDetails = (proforma) => {
    setSelectedProforma(proforma);
    setShowDetailModal(true);
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-EC', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatCurrency = (value) => {
    return `$${parseFloat(value).toFixed(2)}`;
  };

  return (
    <div className="consult-proforma-container">
      <div className="consult-header">
        <h1>Consultar Proformas</h1>
        <button onClick={() => navigate('/dashboard')} className="btn-back">
          <i className="fas fa-arrow-left"></i> Volver
        </button>
      </div>

      {/* Formulario de Filtros */}
      <div className="filters-section">
        <h2>Filtros de Búsqueda</h2>
        <form onSubmit={handleSearch} className="filters-form">
          <div className="filter-row">
            <div className="form-group">
              <label htmlFor="code">Código de Proforma</label>
              <input
                type="text"
                id="code"
                name="code"
                value={filters.code}
                onChange={handleFilterChange}
                placeholder="PRO-202601-0001"
              />
            </div>

            <div className="form-group">
              <label htmlFor="customerName">Nombre del Cliente</label>
              <input
                type="text"
                id="customerName"
                name="customerName"
                value={filters.customerName}
                onChange={handleFilterChange}
                placeholder="Juan Pérez"
              />
            </div>

            <div className="form-group">
              <label htmlFor="customerIdentification">Cédula del Cliente</label>
              <input
                type="text"
                id="customerIdentification"
                name="customerIdentification"
                value={filters.customerIdentification}
                onChange={handleFilterChange}
                placeholder="1234567890"
                maxLength="10"
              />
            </div>
          </div>

          <div className="filter-row">
            <div className="form-group">
              <label htmlFor="startDate">Fecha Desde</label>
              <input
                type="date"
                id="startDate"
                name="startDate"
                value={filters.startDate}
                onChange={handleFilterChange}
              />
            </div>

            <div className="form-group">
              <label htmlFor="endDate">Fecha Hasta</label>
              <input
                type="date"
                id="endDate"
                name="endDate"
                value={filters.endDate}
                onChange={handleFilterChange}
              />
            </div>
          </div>

          <div className="filter-actions">
            <button type="submit" className="btn-search" disabled={loading}>
              <i className="fas fa-search"></i> {loading ? 'Buscando...' : 'Buscar'}
            </button>
            <button type="button" onClick={handleClearFilters} className="btn-clear">
              <i className="fas fa-eraser"></i> Limpiar Filtros
            </button>
          </div>
        </form>
      </div>

      {/* Tabla de Resultados */}
      <div className="results-section">
        <h2>Resultados ({proformas.length})</h2>
        
        {loading ? (
          <div className="loading">
            <i className="fas fa-spinner fa-spin"></i> Cargando...
          </div>
        ) : proformas.length === 0 ? (
          <div className="no-results">
            <i className="fas fa-inbox"></i>
            <p>No se encontraron proformas</p>
          </div>
        ) : (
          <div className="table-container">
            <table className="proformas-table">
              <thead>
                <tr>
                  <th>Código</th>
                  <th>Cliente</th>
                  <th>Cédula</th>
                  <th>Fecha Emisión</th>
                  <th>Total</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                {proformas.map((proforma) => (
                  <tr key={proforma.id}>
                    <td>{proforma.code}</td>
                    <td>{proforma.customer.name}</td>
                    <td>{proforma.customer.identification}</td>
                    <td>{formatDate(proforma.emissionDate)}</td>
                    <td className="price">{formatCurrency(proforma.total)}</td>
                    <td>
                      <button
                        onClick={() => handleViewDetails(proforma)}
                        className="btn-view"
                        title="Ver detalles"
                      >
                        <i className="fas fa-eye"></i>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal de Detalles */}
      {showDetailModal && selectedProforma && (
        <div className="modal-overlay" onClick={() => setShowDetailModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Detalle de Proforma</h2>
              <button onClick={() => setShowDetailModal(false)} className="btn-close">
                <i className="fas fa-times"></i>
              </button>
            </div>

            <div className="modal-body">
              {/* Información de la Proforma */}
              <div className="detail-section">
                <h3>Información General</h3>
                <div className="detail-grid">
                  <div className="detail-item">
                    <span className="label">Código:</span>
                    <span className="value">{selectedProforma.code}</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">Fecha de Emisión:</span>
                    <span className="value">{formatDate(selectedProforma.emissionDate)}</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">Creado por:</span>
                    <span className="value">{selectedProforma.createdByUser}</span>
                  </div>
                </div>
              </div>

              {/* Información del Cliente */}
              <div className="detail-section">
                <h3>Datos del Cliente</h3>
                <div className="detail-grid">
                  <div className="detail-item">
                    <span className="label">Nombre:</span>
                    <span className="value">{selectedProforma.customer.name}</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">Cédula:</span>
                    <span className="value">{selectedProforma.customer.identification}</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">Teléfono:</span>
                    <span className="value">{selectedProforma.customer.phone}</span>
                  </div>
                  <div className="detail-item">
                    <span className="label">Email:</span>
                    <span className="value">{selectedProforma.customer.email || 'N/A'}</span>
                  </div>
                  <div className="detail-item full-width">
                    <span className="label">Dirección:</span>
                    <span className="value">{selectedProforma.customer.address}</span>
                  </div>
                </div>
              </div>

              {/* Detalles de Productos */}
              <div className="detail-section">
                <h3>Productos</h3>
                <table className="details-table">
                  <thead>
                    <tr>
                      <th>Producto</th>
                      <th>Cantidad</th>
                      <th>Precio Unit.</th>
                      <th>Descuento</th>
                      <th>Subtotal</th>
                    </tr>
                  </thead>
                  <tbody>
                    {selectedProforma.details.map((detail) => (
                      <tr key={detail.id}>
                        <td>
                          {detail.productName}
                          {detail.appliedOffer && (
                            <span className="offer-badge">
                              <i className="fas fa-tag"></i> Oferta
                            </span>
                          )}
                        </td>
                        <td>{detail.quantity}</td>
                        <td>{formatCurrency(detail.unitPrice)}</td>
                        <td className="discount">{formatCurrency(detail.unitDiscount)}</td>
                        <td>{formatCurrency(detail.subtotal)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Resumen de Totales */}
              <div className="detail-section">
                <h3>Resumen</h3>
                <div className="totals-summary">
                  <div className="total-row">
                    <span className="label">Subtotal:</span>
                    <span className="value">{formatCurrency(selectedProforma.subtotal)}</span>
                  </div>
                  <div className="total-row">
                    <span className="label">Descuento Total:</span>
                    <span className="value discount">{formatCurrency(selectedProforma.totalDiscount)}</span>
                  </div>
                  <div className="total-row">
                    <span className="label">IVA (15%):</span>
                    <span className="value">{formatCurrency(selectedProforma.tax)}</span>
                  </div>
                  <div className="total-row final">
                    <span className="label">Total:</span>
                    <span className="value">{formatCurrency(selectedProforma.total)}</span>
                  </div>
                </div>
              </div>
            </div>

            <div className="modal-footer">
              <button onClick={() => setShowDetailModal(false)} className="btn-secondary">
                Cerrar
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ConsultProforma;
