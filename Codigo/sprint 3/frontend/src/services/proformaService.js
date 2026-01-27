import apiClient from './api/apiClient';

export const proformaService = {
  // REQ010-2: Crear proforma
  createProforma: async (proformaData) => {
    const response = await apiClient.post('/proformas', proformaData);
    return response.data;
  },

  // Obtener todas las proformas
  getAllProformas: async () => {
    const response = await apiClient.get('/proformas');
    return response.data;
  },

  // Obtener proforma por ID
  getProformaById: async (id) => {
    const response = await apiClient.get(`/proformas/${id}`);
    return response.data;
  },

  // REQ012-1: Buscar proformas con filtros
  searchProformas: async (filters) => {
    const response = await apiClient.get('/proformas/search', { params: filters });
    return response.data;
  },

  // REQ011-1: Actualizar proforma
  updateProforma: async (id, proformaData) => {
    const response = await apiClient.put(`/proformas/${id}`, proformaData);
    return response.data;
  },
};
