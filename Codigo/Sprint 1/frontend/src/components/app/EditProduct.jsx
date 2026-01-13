import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { toast } from 'sonner';
import { productService, categoryService, materialService, colorService } from '../../services/productService';
import { uploadImageToCloudinary } from '../../services/cloudinaryService';
import './EditProduct.css';

const EditProduct = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [uploadingImage, setUploadingImage] = useState(false);
  const [initialLoad, setInitialLoad] = useState(true);

  // Estados para catálogos
  const [categories, setCategories] = useState([]);
  const [materials, setMaterials] = useState([]);
  const [colors, setColors] = useState([]);

  // Estado del formulario
  const [formData, setFormData] = useState({
    name: '',
    price: '',
    categoryId: '',
    materialIds: [],
    colorIds: [],
    imageUrls: []
  });

  // Estados para manejo de imágenes
  const [imagePreviews, setImagePreviews] = useState([]);
  const [selectedFiles, setSelectedFiles] = useState([]);

  // Cargar producto y catálogos al montar el componente
  useEffect(() => {
    loadProductAndCatalogs();
  }, [id]);

  const loadProductAndCatalogs = async () => {
    try {
      setLoading(true);
      const [productRes, categoriesRes, materialsRes, colorsRes] = await Promise.all([
        productService.getProductById(id),
        categoryService.getAllCategories(),
        materialService.getAllMaterials(),
        colorService.getAllColors()
      ]);

      const product = productRes.data;
      
      // Configurar datos del producto
      setFormData({
        name: product.name,
        price: product.price.toString(),
        categoryId: product.category.id,
        materialIds: product.materials.map(m => m.id),
        colorIds: product.colors.map(c => c.id),
        imageUrls: product.images.map(img => img.url)
      });

      // Configurar previsualizaciones de imágenes
      setImagePreviews(product.images.map(img => img.url));

      // Filtrar solo elementos activos
      const activeCategories = (categoriesRes.data || []).filter(cat => cat.isActive);
      const activeMaterials = (materialsRes.data || []).filter(mat => mat.isActive);
      const activeColors = (colorsRes.data || []).filter(col => col.isActive);
      
      setCategories(activeCategories);
      setMaterials(activeMaterials);
      setColors(activeColors);

      setInitialLoad(false);
    } catch (error) {
      console.error('Error loading product:', error);
      toast.error('Error al cargar el producto');
      navigate('/search-product');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleRadioChange = (e) => {
    const { value } = e.target;
    setFormData(prev => ({
      ...prev,
      categoryId: parseInt(value)
    }));
  };

  const handleCheckboxChange = (e, type) => {
    const { value, checked } = e.target;
    const numValue = parseInt(value);

    setFormData(prev => ({
      ...prev,
      [type]: checked
        ? [...prev[type], numValue]
        : prev[type].filter(id => id !== numValue)
    }));
  };

  const handleImageSelect = (e) => {
    const files = Array.from(e.target.files);
    
    if (files.length === 0) return;

    // Validar tamaño de archivos
    for (let file of files) {
      if (file.size > 5 * 1024 * 1024) { // 5MB máximo
        toast.error('Las imágenes no deben superar los 5MB');
        return;
      }
    }

    setSelectedFiles(files);

    // Crear previsualizaciones
    const newPreviews = [];
    files.forEach(file => {
      const reader = new FileReader();
      reader.onloadend = () => {
        newPreviews.push(reader.result);
        if (newPreviews.length === files.length) {
          setImagePreviews(newPreviews);
        }
      };
      reader.readAsDataURL(file);
    });
  };

  const handleRemoveImage = (index) => {
    const newPreviews = imagePreviews.filter((_, i) => i !== index);
    const newUrls = formData.imageUrls.filter((_, i) => i !== index);
    const newFiles = selectedFiles.filter((_, i) => i !== index);

    setImagePreviews(newPreviews);
    setSelectedFiles(newFiles);
    setFormData(prev => ({
      ...prev,
      imageUrls: newUrls
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Validaciones
    if (!formData.name.trim()) {
      toast.error('El nombre del producto es obligatorio');
      return;
    }

    const price = parseFloat(formData.price);
    if (isNaN(price) || price <= 0) {
      toast.error('El precio debe ser mayor a 0');
      return;
    }

    if (!formData.categoryId) {
      toast.error('Debe seleccionar una categoría');
      return;
    }

    setLoading(true);

    try {
      // Subir nuevas imágenes si existen
      let imageUrls = [...formData.imageUrls];
      
      if (selectedFiles.length > 0) {
        setUploadingImage(true);
        const uploadPromises = selectedFiles.map(file => uploadImageToCloudinary(file));
        const uploadedUrls = await Promise.all(uploadPromises);
        imageUrls = uploadedUrls;
        setUploadingImage(false);
      }

      // Preparar datos del producto
      const productData = {
        name: formData.name.trim(),
        price: price,
        categoryId: parseInt(formData.categoryId),
        materialIds: formData.materialIds,
        colorIds: formData.colorIds,
        imageUrls
      };

      // Actualizar producto
      await productService.updateProduct(id, productData);

      toast.success('Producto actualizado correctamente');
      
      // Navegar de vuelta a la búsqueda
      setTimeout(() => {
        navigate('/search-product');
      }, 1500);

    } catch (error) {
      console.error('Error updating product:', error);
      const errorMessage = error.response?.data?.message || 'Error al actualizar el producto';
      
      if (errorMessage.includes('inválidos') || errorMessage.includes('formato')) {
        toast.error('Datos inválidos. Verifique la información ingresada.');
      } else if (errorMessage.includes('imagen')) {
        toast.error('El producto debe tener al menos una imagen');
      } else {
        toast.error(errorMessage);
      }
    } finally {
      setLoading(false);
      setUploadingImage(false);
    }
  };

  const handleCancel = () => {
    if (window.confirm('¿Está seguro de cancelar? Los cambios no guardados se perderán.')) {
      navigate('/search-product');
    }
  };

  if (initialLoad) {
    return (
      <div className="edit-product-page">
        <header className="edit-product-header">
          <h1>MUEBLERIX</h1>
        </header>
        <div className="edit-product-container">
          <p className="loading-message">Cargando producto...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="edit-product-page">
      <header className="edit-product-header">
        <button className="back-to-search" onClick={handleCancel} title="Volver a Búsqueda">
          <i className="fas fa-arrow-left"></i>
        </button>
        <h1>MUEBLERIX</h1>
      </header>

      <div className="edit-product-container">
        <h2 className="edit-product-title">
          <i className="fas fa-edit"></i> Modificar Producto
        </h2>

        <div className="form-wrapper">
          <form className="edit-product-form" onSubmit={handleSubmit}>
            {/* Nombre y precio en la misma fila */}
            <div className="input-row">
              <div className="input-group">
                <label htmlFor="name">
                  <i className="fas fa-box"></i> Nombre:
                </label>
                <input
                  type="text"
                  id="name"
                  name="name"
                  className="edit-product-input"
                  placeholder="Nombre del producto"
                  value={formData.name}
                  onChange={handleInputChange}
                  required
                />
              </div>

              <div className="input-group">
                <label htmlFor="price">
                  <i className="fas fa-money-bill-wave"></i> Precio:
                </label>
                <input
                  type="number"
                  id="price"
                  name="price"
                  className="edit-product-input"
                  placeholder="Precio del producto"
                  step="0.01"
                  min="0.01"
                  value={formData.price}
                  onChange={handleInputChange}
                  required
                />
              </div>
            </div>

            {/* Categoría */}
            <div className="input-group">
              <label>
                <i className="fas fa-list"></i> Categoría:
              </label>
              <div className="checkbox-options">
                {categories.map(category => (
                  <label key={category.id} className="checkbox-label">
                    <input
                      type="radio"
                      name="categoryRadio"
                      value={category.id}
                      checked={formData.categoryId === category.id}
                      onChange={handleRadioChange}
                      required
                    />
                    {category.name}
                  </label>
                ))}
              </div>
            </div>

            {/* Material */}
            <div className="input-group">
              <label>
                <i className="fas fa-hammer"></i> Material (opcional):
              </label>
              <div className="checkbox-options">
                {materials.map(material => (
                  <label key={material.id} className="checkbox-label">
                    <input
                      type="checkbox"
                      value={material.id}
                      checked={formData.materialIds.includes(material.id)}
                      onChange={(e) => handleCheckboxChange(e, 'materialIds')}
                    />
                    {material.name}
                  </label>
                ))}
              </div>
            </div>

            {/* Color */}
            <div className="input-group">
              <label>
                <i className="fas fa-palette"></i> Color (opcional):
              </label>
              <div className="checkbox-options">
                {colors.map(color => (
                  <label key={color.id} className="checkbox-label">
                    <input
                      type="checkbox"
                      value={color.id}
                      checked={formData.colorIds.includes(color.id)}
                      onChange={(e) => handleCheckboxChange(e, 'colorIds')}
                    />
                    {color.name}
                  </label>
                ))}
              </div>
            </div>

            {/* Imagen */}
            <div className="input-group">
              <label htmlFor="images">
                <i className="fas fa-image"></i> Imágenes del Producto:
              </label>
              <input
                type="file"
                id="images"
                name="images"
                accept="image/*"
                multiple
                className="edit-product-input file-input"
                onChange={handleImageSelect}
              />
              <p className="helper-text">
                Puede subir múltiples imágenes (máx. 5MB cada una)
              </p>

              {imagePreviews.length > 0 && (
                <div className="image-preview-container">
                  {imagePreviews.map((preview, index) => (
                    <div key={index} className="image-preview-wrapper">
                      <img src={preview} alt={`Preview ${index + 1}`} className="image-preview" />
                      <button
                        type="button"
                        className="remove-image-btn"
                        onClick={() => handleRemoveImage(index)}
                        title="Eliminar imagen"
                      >
                        <i className="fas fa-times"></i>
                      </button>
                      {index === 0 && <span className="primary-badge">Principal</span>}
                    </div>
                  ))}
                </div>
              )}
            </div>

            {/* Botones */}
            <div className="button-group">
              <button
                type="button"
                className="cancel-btn"
                onClick={handleCancel}
                disabled={loading || uploadingImage}
              >
                <i className="fas fa-times"></i> Cancelar
              </button>
              <button
                type="submit"
                className="submit-btn"
                disabled={loading || uploadingImage}
              >
                <i className="fas fa-save"></i>{' '}
                {uploadingImage ? 'Subiendo imágenes...' : loading ? 'Guardando...' : 'Guardar Cambios'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default EditProduct;
