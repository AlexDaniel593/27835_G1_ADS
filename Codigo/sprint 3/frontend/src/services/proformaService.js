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
};
