import { CLOUDINARY_CONFIG } from '../config/constants';

/**
 * Comprime y optimiza una imagen antes de subirla
 * @param {File} file - Archivo de imagen original
 * @param {number} maxWidth - Ancho máximo de la imagen (default: 1200)
 * @param {number} quality - Calidad de compresión 0-1 (default: 0.8)
 * @returns {Promise<Blob>} - Imagen comprimida
 */
const compressImage = (file, maxWidth = 1200, quality = 0.8) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    
    reader.onload = (event) => {
      const img = new Image();
      img.src = event.target.result;
      
      img.onload = () => {
        const canvas = document.createElement('canvas');
        let width = img.width;
        let height = img.height;
        
        // Calcular nuevas dimensiones manteniendo aspect ratio
        if (width > maxWidth) {
          height = (height * maxWidth) / width;
          width = maxWidth;
        }
        
        canvas.width = width;
        canvas.height = height;
        
        const ctx = canvas.getContext('2d');
        ctx.drawImage(img, 0, 0, width, height);
        
        canvas.toBlob(
          (blob) => {
            if (blob) {
              resolve(blob);
            } else {
              reject(new Error('Error al comprimir la imagen'));
            }
          },
          'image/jpeg',
          quality
        );
      };
      
      img.onerror = () => reject(new Error('Error al cargar la imagen'));
    };
    
    reader.onerror = () => reject(new Error('Error al leer el archivo'));
  });
};

/**
 * Sube una imagen a Cloudinary con optimización
 * @param {File} file - Archivo de imagen a subir
 * @returns {Promise<string>} - URL de la imagen subida
 */
export const uploadImageToCloudinary = async (file) => {
  try {
    // Comprimir imagen antes de subir
    const compressedBlob = await compressImage(file);
    
    const formData = new FormData();
    formData.append('file', compressedBlob, file.name);
    formData.append('upload_preset', CLOUDINARY_CONFIG.UPLOAD_PRESET);
    // Agregar transformaciones de Cloudinary para optimización adicional
    formData.append('quality', 'auto:good');
    formData.append('fetch_format', 'auto');
    
    const response = await fetch(
      `https://api.cloudinary.com/v1_1/${CLOUDINARY_CONFIG.CLOUD_NAME}/image/upload`,
      {
        method: 'POST',
        body: formData,
      }
    );

    if (!response.ok) {
      throw new Error('Error al subir la imagen a Cloudinary');
    }

    const data = await response.json();
    return data.secure_url;
  } catch (error) {
    console.error('Error uploading to Cloudinary:', error);
    throw error;
  }
};

/**
 * Sube múltiples imágenes a Cloudinary
 * @param {FileList|Array} files - Archivos de imágenes a subir
 * @returns {Promise<string[]>} - Array de URLs de las imágenes subidas
 */
export const uploadMultipleImages = async (files) => {
  const uploadPromises = Array.from(files).map(file => uploadImageToCloudinary(file));
  return Promise.all(uploadPromises);
};
