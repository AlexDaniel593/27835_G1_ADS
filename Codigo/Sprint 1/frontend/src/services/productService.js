import apiClient from './api/apiClient';
import { API_ENDPOINTS } from '../config/constants';

export const productService = {
  // Crear producto
  createProduct: async (productData) => {
    const response = await apiClient.post(API_ENDPOINTS.PRODUCTS.BASE, productData);
    return response.data;
  },

  // Obtener todos los productos
  getAllProducts: async () => {
    const response = await apiClient.get(API_ENDPOINTS.PRODUCTS.BASE);
    return response.data;
  },

  // Obtener producto por ID
  getProductById: async (id) => {
    const response = await apiClient.get(API_ENDPOINTS.PRODUCTS.BY_ID(id));
    return response.data;
  },

  // Obtener productos por categoría
  getProductsByCategory: async (categoryId) => {
    const response = await apiClient.get(API_ENDPOINTS.PRODUCTS.BY_CATEGORY(categoryId));
    return response.data;
  },

  // Buscar productos por nombre
  searchProducts: async (name) => {
    const response = await apiClient.get(`${API_ENDPOINTS.PRODUCTS.SEARCH}?name=${name}`);
    return response.data;
  },
};

export const categoryService = {
  // Obtener todas las categorías
  getAllCategories: async () => {
    const response = await apiClient.get(API_ENDPOINTS.CATEGORIES.BASE);
    return response.data;
  },

  // Crear categoría
  createCategory: async (categoryData) => {
    const response = await apiClient.post(API_ENDPOINTS.CATEGORIES.BASE, categoryData);
    return response.data;
  },
};

export const materialService = {
  // Obtener todos los materiales
  getAllMaterials: async () => {
    const response = await apiClient.get(API_ENDPOINTS.MATERIALS.BASE);
    return response.data;
  },

  // Crear material
  createMaterial: async (materialData) => {
    const response = await apiClient.post(API_ENDPOINTS.MATERIALS.BASE, materialData);
    return response.data;
  },
};

export const colorService = {
  // Obtener todos los colores
  getAllColors: async () => {
    const response = await apiClient.get(API_ENDPOINTS.COLORS.BASE);
    return response.data;
  },

  // Crear color
  createColor: async (colorData) => {
    const response = await apiClient.post(API_ENDPOINTS.COLORS.BASE, colorData);
    return response.data;
  },
};
