import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { productService, categoryService, materialService, colorService } from '../../services/productService';
import { uploadImageToCloudinary } from '../../services/cloudinaryService';
import './AddProduct.css';

const AddProduct = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [uploadingImage, setUploadingImage] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  // Estados para catálogos
  const [categories, setCategories] = useState([]);
  const [materials, setMaterials] = useState([]);
  const [colors, setColors] = useState([]);

  // Estados para nuevos elementos
  const [newCategory, setNewCategory] = useState('');
  const [newMaterial, setNewMaterial] = useState('');
  const [newColor, setNewColor] = useState('');

  // Estado del formulario
  const [formData, setFormData] = useState({
    name: '',
    price: '',
    categoryId: '',
    materialIds: [],
    colorIds: [],
    imageUrls: []
  });

  // Estado para vista previa de imagen
  const [imagePreview, setImagePreview] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);

  // Cargar catálogos al montar el componente
  useEffect(() => {
    loadCatalogs();
  }, []);

  const loadCatalogs = async () => {
    try {
      const [categoriesRes, materialsRes, colorsRes] = await Promise.all([
        categoryService.getAllCategories(),
        materialService.getAllMaterials(),
        colorService.getAllColors()
      ]);

      setCategories(categoriesRes.data || []);
      setMaterials(materialsRes.data || []);
      setColors(colorsRes.data || []);
    } catch (error) {
      console.error('Error loading catalogs:', error);
      setErrorMessage('Error al cargar los catálogos');
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
    const file = e.target.files[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) { // 5MB máximo
        setErrorMessage('La imagen no debe superar los 5MB');
        return;
      }

      setSelectedFile(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleAddCategory = async () => {
    if (!newCategory.trim()) return;

    try {
      const response = await categoryService.createCategory({
        name: newCategory,
        description: ''
      });
      
      setCategories(prev => [...prev, response.data]);
      setNewCategory('');
      setSuccessMessage('Categoría agregada exitosamente');
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (error) {
      console.error('Error creating category:', error);
      setErrorMessage('Error al crear la categoría');
    }
  };

  const handleAddMaterial = async () => {
    if (!newMaterial.trim()) return;

    try {
      const response = await materialService.createMaterial({
        name: newMaterial,
        description: ''
      });
      
      setMaterials(prev => [...prev, response.data]);
      setNewMaterial('');
      setSuccessMessage('Material agregado exitosamente');
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (error) {
      console.error('Error creating material:', error);
      setErrorMessage('Error al crear el material');
    }
  };

  const handleAddColor = async () => {
    if (!newColor.trim()) return;

    try {
      const response = await colorService.createColor({
        name: newColor
      });
      
      setColors(prev => [...prev, response.data]);
      setNewColor('');
      setSuccessMessage('Color agregado exitosamente');
      setTimeout(() => setSuccessMessage(''), 3000);
    } catch (error) {
      console.error('Error creating color:', error);
      setErrorMessage('Error al crear el color');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrorMessage('');
    setSuccessMessage('');

    try {
      // Subir imagen a Cloudinary si existe
      let imageUrls = [];
      if (selectedFile) {
        setUploadingImage(true);
        const imageUrl = await uploadImageToCloudinary(selectedFile);
        imageUrls = [imageUrl];
        setUploadingImage(false);
      }

      // Preparar datos del producto
      const productData = {
        ...formData,
        price: parseFloat(formData.price),
        categoryId: parseInt(formData.categoryId),
        imageUrls
      };

      // Crear producto
      await productService.createProduct(productData);

      setSuccessMessage('Producto creado exitosamente');
      
      // Resetear formulario
      setTimeout(() => {
        navigate('/dashboard');
      }, 2000);

    } catch (error) {
      console.error('Error creating product:', error);
      setErrorMessage(error.response?.data?.message || 'Error al crear el producto');
    } finally {
      setLoading(false);
      setUploadingImage(false);
    }
  };

  const handleCancel = () => {
    navigate('/dashboard');
  };

  return (
    <div>
      <header className="add-product-header">
        <h1>MUEBLERIX</h1>
      </header>

      <div className="add-product-container">
        <h2 className="add-product-title">
          <i className="fas fa-plus-circle"></i> Añadir Producto
        </h2>

        {successMessage && (
          <div className="success-message">{successMessage}</div>
        )}

        {errorMessage && (
          <div className="error-container">{errorMessage}</div>
        )}

        <div className="form-wrapper">
          <form className="add-product-form" onSubmit={handleSubmit}>
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
                  className="add-product-input"
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
                  className="add-product-input"
                  placeholder="Precio del producto"
                  step="0.01"
                  min="0"
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

              <div className="add-new-section">
                <input
                  type="text"
                  className="add-new-input"
                  placeholder="Nueva categoría"
                  value={newCategory}
                  onChange={(e) => setNewCategory(e.target.value)}
                />
                <button
                  type="button"
                  className="add-new-btn"
                  onClick={handleAddCategory}
                  disabled={!newCategory.trim()}
                >
                  Añadir
                </button>
              </div>
            </div>

            {/* Materiales */}
            <div className="input-group">
              <label>
                <i className="fas fa-hammer"></i> Materiales:
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

              <div className="add-new-section">
                <input
                  type="text"
                  className="add-new-input"
                  placeholder="Nuevo material"
                  value={newMaterial}
                  onChange={(e) => setNewMaterial(e.target.value)}
                />
                <button
                  type="button"
                  className="add-new-btn"
                  onClick={handleAddMaterial}
                  disabled={!newMaterial.trim()}
                >
                  Añadir
                </button>
              </div>
            </div>

            {/* Colores */}
            <div className="input-group">
              <label>
                <i className="fas fa-palette"></i> Colores:
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

              <div className="add-new-section">
                <input
                  type="text"
                  className="add-new-input"
                  placeholder="Nuevo color"
                  value={newColor}
                  onChange={(e) => setNewColor(e.target.value)}
                />
                <button
                  type="button"
                  className="add-new-btn"
                  onClick={handleAddColor}
                  disabled={!newColor.trim()}
                >
                  Añadir
                </button>
              </div>
            </div>

            {/* Botones */}
            <div className="button-group">
              <button type="submit" className="confirm-btn" disabled={loading}>
                {loading ? (
                  <>
                    <span className="loading-spinner"></span>
                    Guardando...
                  </>
                ) : (
                  <>
                    <i className="fas fa-check"></i> Confirmar
                  </>
                )}
              </button>
              <button type="button" className="cancel-btn" onClick={handleCancel}>
                <i className="fas fa-times"></i> Cancelar
              </button>
            </div>
          </form>

          {/* Imagen */}
          <div className="image-upload">
            <label htmlFor="imagen">
              <i className="fas fa-image"></i> Seleccionar Imagen:
            </label>
            <input
              type="file"
              id="imagen"
              accept="image/*"
              onChange={handleImageSelect}
            />
            <div className="image-preview">
              {uploadingImage ? (
                <div>
                  <span className="loading-spinner"></span>
                  <p>Subiendo imagen...</p>
                </div>
              ) : imagePreview ? (
                <img src={imagePreview} alt="Preview" />
              ) : (
                <span>
                  <i className="fas fa-eye"></i> Vista previa de la imagen
                </span>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddProduct;
