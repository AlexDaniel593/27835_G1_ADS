// Configuración de constantes de la aplicación
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

export const CLOUDINARY_CONFIG = {
  CLOUD_NAME: import.meta.env.VITE_CLOUDINARY_CLOUD_NAME,
  UPLOAD_PRESET: import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET,
};

export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth/login',
  },
  USERS: {
    CHANGE_PASSWORD: '/users/change-password',
  },
  PRODUCTS: {
    BASE: '/products',
    BY_ID: (id) => `/products/${id}`,
    BY_CATEGORY: (categoryId) => `/products/category/${categoryId}`,
    SEARCH: '/products/search',
  },
  CATEGORIES: {
    BASE: '/categories',
    BY_ID: (id) => `/categories/${id}`,
  },
  MATERIALS: {
    BASE: '/materials',
    BY_ID: (id) => `/materials/${id}`,
  },
  COLORS: {
    BASE: '/colors',
    BY_ID: (id) => `/colors/${id}`,
  },
};

export const STORAGE_KEYS = {
  TOKEN: 'access_token',
  USER: 'user_info',
};
