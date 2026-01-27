import apiClient from './api/apiClient';
import { API_ENDPOINTS } from '../config/constants';

export const offerService = {
  // Obtener todas las ofertas
  getAllOffers: async () => {
    const response = await apiClient.get(API_ENDPOINTS.OFFERS.BASE);
    return response.data;
  },

  // Obtener oferta por ID
  getOfferById: async (id) => {
    const response = await apiClient.get(API_ENDPOINTS.OFFERS.BY_ID(id));
    return response.data;
  },

  // Obtener ofertas por producto
  getOffersByProductId: async (productId) => {
    const response = await apiClient.get(API_ENDPOINTS.OFFERS.BY_PRODUCT(productId));
    return response.data;
  },

  // Crear oferta
  createOffer: async (offerData) => {
    const response = await apiClient.post(API_ENDPOINTS.OFFERS.BASE, offerData);
    return response.data;
  },

  // Actualizar oferta
  updateOffer: async (id, offerData) => {
    const response = await apiClient.put(API_ENDPOINTS.OFFERS.BY_ID(id), offerData);
    return response.data;
  },

  // Eliminar oferta
  deleteOffer: async (id) => {
    const response = await apiClient.delete(API_ENDPOINTS.OFFERS.BY_ID(id));
    return response.data;
  },
};
