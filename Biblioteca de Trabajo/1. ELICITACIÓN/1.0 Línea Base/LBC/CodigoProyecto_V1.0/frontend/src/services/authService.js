import apiClient from './api/apiClient';
import { API_ENDPOINTS, STORAGE_KEYS } from '../config/constants';

/**
 * Servicio de autenticación
 * Principio de Responsabilidad Única: Solo maneja operaciones de autenticación
 */
class AuthService {
  /**
   * Iniciar sesión
   * @param {string} username - Identificación del usuario
   * @param {string} password - Contraseña del usuario
   * @returns {Promise} Respuesta de la API
   */
  async login(username, password) {
    try {
      const response = await apiClient.post(API_ENDPOINTS.AUTH.LOGIN, {
        username,
        password,
      });
      
      if (response.data.success) {
        const { accessToken, userInfo } = response.data.data;
        this.saveAuthData(accessToken, userInfo);
      }
      
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  /**
   * Guardar datos de autenticación en localStorage
   * @param {string} token - Token de acceso
   * @param {object} user - Información del usuario
   */
  saveAuthData(token, user) {
    localStorage.setItem(STORAGE_KEYS.TOKEN, token);
    localStorage.setItem(STORAGE_KEYS.USER, JSON.stringify(user));
  }

  /**
   * Cerrar sesión
   */
  logout() {
    localStorage.removeItem(STORAGE_KEYS.TOKEN);
    localStorage.removeItem(STORAGE_KEYS.USER);
  }

  /**
   * Verificar si el usuario está autenticado
   * @returns {boolean}
   */
  isAuthenticated() {
    return !!localStorage.getItem(STORAGE_KEYS.TOKEN);
  }

  /**
   * Obtener información del usuario actual
   * @returns {object|null}
   */
  getCurrentUser() {
    const userStr = localStorage.getItem(STORAGE_KEYS.USER);
    return userStr ? JSON.parse(userStr) : null;
  }

  /**
   * Manejar errores de la API
   * @param {object} error - Error de axios
   * @returns {Error}
   */
  handleError(error) {
    if (error.response) {
      const message = error.response.data?.error || error.response.data?.message || 'Error en la autenticación';
      return new Error(message);
    }
    return new Error('Error de conexión con el servidor');
  }
}

export default new AuthService();
