import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { publicProductService, publicCatalogService } from '../../services/publicProductService';
import './PublicCatalog.css';

const PublicCatalog = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [products, setProducts] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [materials, setMaterials] = useState([]);
  const [colors, setColors] = useState([]);
  const [errorMessage, setErrorMessage] = useState('');

  // Estados para filtros
  const [filters, setFilters] = useState({
    name: '',
    categoryId: '',
    materialId: '',
    colorId: '',
    minPrice: '',
    maxPrice: ''
  });

  // Producto seleccionado para modal de detalles
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [showModal, setShowModal] = useState(false);

  // Cargar datos iniciales
  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    setLoading(true);
    setErrorMessage('');

    try {
      const [productsRes, categoriesRes, materialsRes, colorsRes] = await Promise.all([
        publicProductService.getAllProducts(),
        publicCatalogService.getAllCategories(),
        publicCatalogService.getAllMaterials(),
        publicCatalogService.getAllColors()
      ]);

      setProducts(productsRes.data || []);
      setFilteredProducts(productsRes.data || []);
      setCategories(categoriesRes.data || []);
      setMaterials(materialsRes.data || []);
      setColors(colorsRes.data || []);
    } catch (error) {
      console.error('Error loading catalog:', error);
      setErrorMessage('Error al cargar el catálogo de productos');
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

  const handleApplyFilters = async () => {
    setLoading(true);
    setErrorMessage('');

    try {
      // Preparar filtros (solo enviar valores no vacíos)
      const activeFilters = {};
      Object.keys(filters).forEach(key => {
        if (filters[key]) {
          activeFilters[key] = filters[key];
        }
      });

      // Si no hay filtros, obtener todos los productos
      if (Object.keys(activeFilters).length === 0) {
        const response = await publicProductService.getAllProducts();
        setFilteredProducts(response.data || []);
      } else {
        const response = await publicProductService.filterProducts(activeFilters);
        setFilteredProducts(response.data || []);
      }
    } catch (error) {
      console.error('Error filtering products:', error);
      setErrorMessage('Error al filtrar productos');
    } finally {
      setLoading(false);
    }
  };

  const handleClearFilters = async () => {
    setFilters({
      name: '',
      categoryId: '',
      materialId: '',
      colorId: '',
      minPrice: '',
      maxPrice: ''
    });
    
    setLoading(true);
    try {
      const response = await publicProductService.getAllProducts();
      setFilteredProducts(response.data || []);
    } catch (error) {
      console.error('Error reloading products:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleViewDetails = (product) => {
    setSelectedProduct(product);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setSelectedProduct(null);
  };

  return (
    <div>
      <header className="catalog-header">
        <div className="catalog-header-content">
          <h1>MUEBLERIX</h1>
          <p className="catalog-subtitle">Catálogo de Productos</p>
        </div>
        <button className="btn-login" onClick={() => navigate('/login')}>
          <i className="fas fa-sign-in-alt"></i> Ingresar
        </button>
      </header>

      <div className="catalog-container">
        {/* Panel de filtros */}
        <aside className="filters-panel">
          <h2 className="filters-title">
            <i className="fas fa-filter"></i> Filtros
          </h2>

          <div className="filter-group">
            <label htmlFor="name">
              <i className="fas fa-search"></i> Buscar por nombre:
            </label>
            <input
              type="text"
              id="name"
              name="name"
              className="filter-input"
              placeholder="Nombre del producto"
              value={filters.name}
              onChange={handleFilterChange}
            />
          </div>

          <div className="filter-group">
            <label htmlFor="categoryId">
              <i className="fas fa-list"></i> Categoría:
            </label>
            <select
              id="categoryId"
              name="categoryId"
              className="filter-select"
              value={filters.categoryId}
              onChange={handleFilterChange}
            >
              <option value="">Todas las categorías</option>
              {categories.map(category => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </div>

          <div className="filter-group">
            <label htmlFor="materialId">
              <i className="fas fa-cube"></i> Material:
            </label>
            <select
              id="materialId"
              name="materialId"
              className="filter-select"
              value={filters.materialId}
              onChange={handleFilterChange}
            >
              <option value="">Todos los materiales</option>
              {materials.map(material => (
                <option key={material.id} value={material.id}>
                  {material.name}
                </option>
              ))}
            </select>
          </div>

          <div className="filter-group">
            <label htmlFor="colorId">
              <i className="fas fa-palette"></i> Color:
            </label>
            <select
              id="colorId"
              name="colorId"
              className="filter-select"
              value={filters.colorId}
              onChange={handleFilterChange}
            >
              <option value="">Todos los colores</option>
              {colors.map(color => (
                <option key={color.id} value={color.id}>
                  {color.name}
                </option>
              ))}
            </select>
          </div>

          <div className="filter-group">
            <label>
              <i className="fas fa-dollar-sign"></i> Rango de precio:
            </label>
            <div className="price-range">
              <input
                type="number"
                name="minPrice"
                className="filter-input price-input"
                placeholder="Mín"
                min="0"
                step="0.01"
                value={filters.minPrice}
                onChange={handleFilterChange}
              />
              <span>-</span>
              <input
                type="number"
                name="maxPrice"
                className="filter-input price-input"
                placeholder="Máx"
                min="0"
                step="0.01"
                value={filters.maxPrice}
                onChange={handleFilterChange}
              />
            </div>
          </div>

          <div className="filter-buttons">
            <button
              className="btn-apply-filters"
              onClick={handleApplyFilters}
              disabled={loading}
            >
              <i className="fas fa-check"></i> Aplicar Filtros
            </button>
            <button
              className="btn-clear-filters"
              onClick={handleClearFilters}
              disabled={loading}
            >
              <i className="fas fa-times"></i> Limpiar
            </button>
          </div>
        </aside>

        {/* Área de productos */}
        <main className="products-area">
          {errorMessage && (
            <div className="error-message">{errorMessage}</div>
          )}

          {loading ? (
            <div className="loading-spinner">
              <i className="fas fa-spinner fa-spin"></i> Cargando productos...
            </div>
          ) : (
            <>
              <div className="products-header">
                <h2>Productos Disponibles</h2>
                <span className="product-count">
                  {filteredProducts.length} producto(s) encontrado(s)
                </span>
              </div>

              {filteredProducts.length === 0 ? (
                <div className="no-products">
                  <i className="fas fa-box-open"></i>
                  <p>No se encontraron productos con los filtros seleccionados</p>
                </div>
              ) : (
                <div className="products-grid">
                  {filteredProducts.map(product => (
                    <div key={product.id} className="product-card">
                      <div className="product-image">
                        {product.images && product.images.length > 0 ? (
                          <img src={product.images[0].url} alt={product.name} />
                        ) : (
                          <div className="no-image">
                            <i className="fas fa-image"></i>
                          </div>
                        )}
                      </div>
                      <div className="product-info">
                        <h3 className="product-name">{product.name}</h3>
                        <p className="product-category">
                          <i className="fas fa-tag"></i> {product.category?.name || 'Sin categoría'}
                        </p>
                        <p className="product-price">
                          ${parseFloat(product.price).toFixed(2)}
                        </p>
                        <button
                          className="btn-view-details"
                          onClick={() => handleViewDetails(product)}
                        >
                          <i className="fas fa-eye"></i> Ver Detalles
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </>
          )}
        </main>
      </div>

      {/* Modal de detalles del producto */}
      {showModal && selectedProduct && (
        <div className="modal-overlay" onClick={handleCloseModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <button className="modal-close" onClick={handleCloseModal}>
              <i className="fas fa-times"></i>
            </button>

            <h2 className="modal-title">{selectedProduct.name}</h2>

            <div className="modal-body">
              {selectedProduct.images && selectedProduct.images.length > 0 && (
                <div className="modal-image">
                  <img src={selectedProduct.images[0].url} alt={selectedProduct.name} />
                </div>
              )}

              <div className="modal-details">
                <p className="detail-item">
                  <strong><i className="fas fa-dollar-sign"></i> Precio:</strong> 
                  ${parseFloat(selectedProduct.price).toFixed(2)}
                </p>

                <p className="detail-item">
                  <strong><i className="fas fa-list"></i> Categoría:</strong> 
                  {selectedProduct.category?.name || 'Sin categoría'}
                </p>

                {selectedProduct.materials && selectedProduct.materials.length > 0 && (
                  <p className="detail-item">
                    <strong><i className="fas fa-cube"></i> Materiales:</strong> 
                    {selectedProduct.materials.map(m => m.name).join(', ')}
                  </p>
                )}

                {selectedProduct.colors && selectedProduct.colors.length > 0 && (
                  <p className="detail-item">
                    <strong><i className="fas fa-palette"></i> Colores:</strong> 
                    {selectedProduct.colors.map(c => c.name).join(', ')}
                  </p>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PublicCatalog;
