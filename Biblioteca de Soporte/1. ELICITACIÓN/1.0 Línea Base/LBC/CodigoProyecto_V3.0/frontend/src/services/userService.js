import apiClient from './api/apiClient';
import { API_ENDPOINTS } from '../config/constants';

/**
 * Servicio de usuarios
 * Principio de Responsabilidad Única: Solo maneja operaciones de usuarios
 */
class UserService {
  /**
   * Cambiar contraseña del usuario actual
   * @param {object} passwordData - Datos de cambio de contraseña
   * @returns {Promise} Respuesta de la API
   */
  async changePassword(passwordData) {
    try {
      const response = await apiClient.put(
        API_ENDPOINTS.USERS.CHANGE_PASSWORD,
        passwordData
      );
      return response.data;
    } catch (error) {
      throw this.handleError(error);
    }
  }

  /**
   * Manejar errores de la API
   * @param {object} error - Error de axios
   * @returns {Error}
   */
  handleError(error) {
    if (error.response) {
      const message = error.response.data?.error || error.response.data?.message || 'Error en la operación';
      return new Error(message);
    }
    return new Error('Error de conexión con el servidor');
  }
}

export default new UserService();
