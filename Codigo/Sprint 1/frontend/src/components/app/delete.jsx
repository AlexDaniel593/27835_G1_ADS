import { useState, useEffect } from 'react';
import { productService } from '../services/productService';
import { toast } from 'sonner';
import './ManageProducts.css';

const ManageProducts = () => {
  const [loading, setLoading] = useState(false);
  const [products, setProducts] = useState([]);
  
  const [form, setForm] = useState({
    name: '',
    category: '',
    price: '',
  });

  // Load products on first render
  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      setLoading(true);
      const response = await productService.getAllProducts();
      setProducts(response.data || []);
    } catch (error) {
      console.error(error);
      toast.error("Error al cargar productos");
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    setForm({ 
      ...form, 
      [e.target.name]: e.target.value 
    });
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await productService.createProduct(form);
      toast.success("Producto registrado correctamente");
      setForm({ name: '', category: '', price: '' });
      loadProducts();
    } catch (error) {
      console.error(error);
      toast.error("No se pudo registrar el producto");
    } finally {
      setLoading(false);
    }
  };

  const formatPrice = (value) =>
    new Intl.NumberFormat("es-EC", {
      style: "currency",
      currency: "USD"
    }).format(value);

  return (
    <div className="manage-page">
      <h1>MUEBLERIX - Gestión de Productos</h1>

      {/* Formulario para registrar producto */}
      <div className="form-section">
        <h2>Registrar Nuevo Producto</h2>

        <form onSubmit={handleCreate}>
          <input
            type="text"
            name="name"
            placeholder="Nombre del producto"
            value={form.name}
            onChange={handleInputChange}
            required
          />

          <input
            type="text"
            name="category"
            placeholder="Categoría"
            value={form.category}
            onChange={handleInputChange}
            required
          />

          <input
            type="number"
            name="price"
            placeholder="Precio"
            value={form.price}
            onChange={handleInputChange}
            required
          />

          <button type="submit" disabled={loading}>
            {loading ? "Guardando..." : "Registrar"}
          </button>
        </form>
      </div>

      {/* Tabla de productos */}
      <div className="table-section">
        <h2>Productos Registrados</h2>

        {loading ? (
          <p>Cargando...</p>
        ) : products.length === 0 ? (
          <p>No hay productos registrados.</p>
        ) : (
          <table className="product-table">
            <thead>
              <tr>
                <th>Nombre</th>
                <th>Categoría</th>
                <th>Precio</th>
              </tr>
            </thead>

            <tbody>
              {products.map((p) => (
                <tr key={p.id}>
                  <td>{p.name}</td>
                  <td>{p.category}</td>
                  <td>{formatPrice(p.price)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default ManageProducts;
