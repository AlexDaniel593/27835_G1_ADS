// Configuración de constantes de la aplicación
export const API_BASE_URL = 'http://localhost:8080/api/v1';

export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/auth/login',
  },
  USERS: {
    CHANGE_PASSWORD: '/users/change-password',
  },
};

export const STORAGE_KEYS = {
  TOKEN: 'access_token',
  USER: 'user_info',
};
