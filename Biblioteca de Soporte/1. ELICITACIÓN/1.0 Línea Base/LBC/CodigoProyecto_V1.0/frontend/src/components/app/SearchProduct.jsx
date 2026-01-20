import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';
import { productService, categoryService, materialService, colorService } from '../../services/productService';
import './SearchProduct.css';

const SearchProduct = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [materials, setMaterials] = useState([]);
  const [colors, setColors] = useState([]);
  
  const [filters, setFilters] = useState({
    name: '',
    categoryId: '',
    materialId: '',
    colorId: '',
    priceRange: ''
  });

  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    try {
      setLoading(true);
      const [productsRes, categoriesRes, materialsRes, colorsRes] = await Promise.all([
        productService.getAllProducts(),
        categoryService.getAllCategories(),
        materialService.getAllMaterials(),
        colorService.getAllColors()
      ]);

      setProducts(productsRes.data || []);
      
      // Filter only active elements from database
      const activeCategories = (categoriesRes.data || []).filter(cat => cat.isActive);
      const activeMaterials = (materialsRes.data || []).filter(mat => mat.isActive);
      const activeColors = (colorsRes.data || []).filter(col => col.isActive);
      
      setCategories(activeCategories);
      setMaterials(activeMaterials);
      setColors(activeColors);
    } catch (error) {
      console.error('Error loading initial data:', error);
      toast.error('Error al cargar los datos. Por favor, intenta de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFilters({
      ...filters,
      [name]: value
    });
  };

  const getPriceRange = (range) => {
    const ranges = {
      'less-50': { min: 0, max: 50 },
      '50-100': { min: 50, max: 100 },
      '150-200': { min: 150, max: 200 },
      '200-250': { min: 200, max: 300 },
      '250-300': { min: 300, max: 350 },
      'more-500': { min: 350, max: 999999 }
    };
    return ranges[range] || null;
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const range = filters.priceRange ? getPriceRange(filters.priceRange) : null;
      
      const params = {
        name: filters.name || null,
        categoryId: filters.categoryId || null,
        materialId: filters.materialId || null,
        colorId: filters.colorId || null,
        minPrice: range ? range.min : null,
        maxPrice: range ? range.max : null
      };

      const response = await productService.searchProductsAdvanced(params);
      setProducts(response.data || []);

      if (response.data.length === 0) {
        toast.info('No se encontraron productos con los filtros seleccionados.');
      } else {
        toast.success(`Se encontraron ${response.data.length} producto(s)`);
      }
    } catch (error) {
      console.error('Error in search:', error);
      toast.error('Error al buscar productos. Por favor, intenta de nuevo.');
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (productId) => {
    // TODO: Functionality to implement
    // navigate(`/edit-product/${productId}`);
  };

  const handleDelete = async (productId, productName) => {
    // TODO: Functionality to implement
  };

  const formatPrice = (price) => {
    return new Intl.NumberFormat('es-EC', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  };

  const handleGoBack = () => {
    navigate('/dashboard');
  };

  return (
    <div className="search-product-page">
      <header>
        <button className="back-to-dashboard" onClick={handleGoBack} title="Volver al Dashboard">
          <i className="fas fa-arrow-left"></i>
        </button>
        <h1>MUEBLERIX</h1>
      </header>

      <div className="container">
        <h1 className="search-title">
          <i className="fas fa-search"></i> Consultar Producto
        </h1>

        {/* Search filter */}
        <div className="search-filter">
          <h2><i className="fas fa-filter"></i> Buscar Producto</h2>
          <form id="search-form" onSubmit={handleSearch}>
            <div className="input-pair">
              <div>
                <label htmlFor="name">Nombre del Producto</label>
                <input
                  type="text"
                  id="name"
                  name="name"
                  placeholder="Ej. Puerta 7 Paneles"
                  value={filters.name}
                  onChange={handleInputChange}
                />
              </div>
              <div>
                <label htmlFor="categoryId">Categoría</label>
                <select
                  id="categoryId"
                  name="categoryId"
                  value={filters.categoryId}
                  onChange={handleInputChange}
                  disabled={categories.length === 0}
                >
                  <option value="">
                    {categories.length === 0 ? 'No hay categorías registradas' : 'Seleccionar Categoría'}
                  </option>
                  {categories.map((cat) => (
                    <option key={cat.id} value={cat.id}>
                      {cat.name}
                    </option>
                  ))}
                </select>
              </div>
            </div>
            
            <div className="input-pair">
              <div>
                <label htmlFor="materialId">Material</label>
                <select
                  id="materialId"
                  name="materialId"
                  value={filters.materialId}
                  onChange={handleInputChange}
                  disabled={materials.length === 0}
                >
                  <option value="">
                    {materials.length === 0 ? 'No hay materiales registrados' : 'Seleccionar Material'}
                  </option>
                  {materials.map((mat) => (
                    <option key={mat.id} value={mat.id}>
                      {mat.name}
                    </option>
                  ))}
                </select>
              </div>
              <div>
                <label htmlFor="colorId">Color</label>
                <select
                  id="colorId"
                  name="colorId"
                  value={filters.colorId}
                  onChange={handleInputChange}
                  disabled={colors.length === 0}
                >
                  <option value="">
                    {colors.length === 0 ? 'No hay colores registrados' : 'Seleccionar Color'}
                  </option>
                  {colors.map((col) => (
                    <option key={col.id} value={col.id}>
                      {col.name}
                    </option>
                  ))}
                </select>
              </div>
            </div>
            
            <div className="input-pair">
              <div>
                <label htmlFor="priceRange">Rango de Precio</label>
                <select
                  id="priceRange"
                  name="priceRange"
                  value={filters.priceRange}
                  onChange={handleInputChange}
                >
                  <option value="">Seleccionar Rango</option>
                  <option value="less-50">Menos de $50</option>
                  <option value="50-100">$50 - $100</option>
                  <option value="150-200">$150 - $200</option>
                  <option value="200-250">$200 - $300</option>
                  <option value="250-300">$300 - $350</option>
                  <option value="more-500">Más de $350</option>
                </select>
              </div>
            </div>
            
            <button type="submit" className="search-btn" disabled={loading}>
              <i className="fas fa-search"></i> {loading ? 'Buscando...' : 'Buscar'}
            </button>
          </form>
        </div>

        {/* Search results table */}
        <div className="search-results">
          <h2><i className="fas fa-table"></i> Resultados de la Consulta</h2>
          {loading ? (
            <p className="loading-message">Cargando productos...</p>
          ) : products.length === 0 ? (
            <p className="no-results">No hay productos para mostrar</p>
          ) : (
            <div className="table-wrapper">
              <table id="results-table">
                <thead>
                  <tr>
                    <th>Nombre</th>
                    <th>Categoría</th>
                    <th>Precio</th>
                    <th>Material</th>
                    <th>Color</th>
                    <th>Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {products.map((product) => (
                    <tr key={product.id}>
                      <td>{product.name}</td>
                      <td>{product.category?.name || 'N/A'}</td>
                      <td>{formatPrice(product.price)}</td>
                      <td>
                        {product.materials && product.materials.length > 0
                          ? product.materials.map(m => m.name).join(', ')
                          : 'N/A'}
                      </td>
                      <td>
                        {product.colors && product.colors.length > 0
                          ? product.colors.map(c => c.name).join(', ')
                          : 'N/A'}
                      </td>
                      <td>
                        <button
                          className="edit-btn"
                          onClick={() => handleEdit(product.id)}
                        >
                          <i className="fas fa-edit"></i> Modificar
                        </button>
                        <button
                          className="delete-btn"
                          onClick={() => handleDelete(product.id, product.name)}
                        >
                          <i className="fas fa-trash-alt"></i> Eliminar
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default SearchProduct;
