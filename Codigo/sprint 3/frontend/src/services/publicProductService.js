import axios from 'axios';
import { API_BASE_URL } from '../config/constants';

// Cliente público sin autenticación
const publicApiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const publicProductService = {
  // REQ009-1: Obtener todos los productos públicos
  getAllProducts: async () => {
    const response = await publicApiClient.get('/public/products');
    return response.data;
  },

  // REQ009-1: Obtener producto por ID (público)
  getProductById: async (id) => {
    const response = await publicApiClient.get(`/public/products/${id}`);
    return response.data;
  },

  // REQ009-2: Filtrar productos con filtros básicos
  filterProducts: async (filters) => {
    const params = new URLSearchParams();
    
    if (filters.name) params.append('name', filters.name);
    if (filters.categoryId) params.append('categoryId', filters.categoryId);
    if (filters.materialId) params.append('materialId', filters.materialId);
    if (filters.colorId) params.append('colorId', filters.colorId);
    if (filters.minPrice) params.append('minPrice', filters.minPrice);
    if (filters.maxPrice) params.append('maxPrice', filters.maxPrice);

    const response = await publicApiClient.get(`/public/products/filter?${params.toString()}`);
    return response.data;
  },

  // REQ009-2: Obtener productos por categoría (público)
  getProductsByCategory: async (categoryId) => {
    const response = await publicApiClient.get(`/public/products/category/${categoryId}`);
    return response.data;
  },
};

export const publicCatalogService = {
  // Obtener categorías públicas
  getAllCategories: async () => {
    const response = await publicApiClient.get('/public/catalog/categories');
    return response.data;
  },

  // Obtener materiales públicos
  getAllMaterials: async () => {
    const response = await publicApiClient.get('/public/catalog/materials');
    return response.data;
  },

  // Obtener colores públicos
  getAllColors: async () => {
    const response = await publicApiClient.get('/public/catalog/colors');
    return response.data;
  },
};
