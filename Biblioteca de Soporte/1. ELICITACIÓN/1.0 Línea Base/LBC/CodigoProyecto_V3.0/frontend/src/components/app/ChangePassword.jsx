import { useState } from 'react';
import userService from '../../services/userService';
import './ChangePassword.css';

const ChangePassword = ({ onSuccess, onCancel, isModal = false }) => {
  const [formData, setFormData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    setError('');
  };

  const validateForm = () => {
    if (formData.newPassword !== formData.confirmPassword) {
      setError('Las contraseñas no coinciden');
      return false;
    }

    if (formData.newPassword.length < 8) {
      setError('La contraseña debe tener al menos 8 caracteres');
      return false;
    }

    const passwordRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$/;
    if (!passwordRegex.test(formData.newPassword)) {
      setError('La contraseña debe contener al menos un número, una mayúscula, una minúscula y un carácter especial (@#$%^&+=!)');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!validateForm()) {
      return;
    }

    setLoading(true);

    try {
      const response = await userService.changePassword({
        currentPassword: formData.currentPassword,
        newPassword: formData.newPassword,
        confirmPassword: formData.confirmPassword,
      });

      if (response.success) {
        setSuccess(true);
        setTimeout(() => {
          if (onSuccess) onSuccess();
        }, 2000);
      }
    } catch (err) {
      setError(err.message || 'Error al cambiar la contraseña');
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className={isModal ? 'change-password-modal' : 'change-password-container'}>
        <div className="success-message">
          <div className="success-icon">✓</div>
          <h3>¡Contraseña cambiada exitosamente!</h3>
          <p>Redirigiendo...</p>
        </div>
      </div>
    );
  }

  return (
    <div className={isModal ? 'change-password-modal' : 'change-password-container'}>
      <div className="change-password-card">
        <div className="change-password-header">
          <h2>Cambiar Contraseña</h2>
          <p>Por seguridad, te recomendamos cambiar tu contraseña</p>
        </div>

        <form onSubmit={handleSubmit} className="change-password-form">
          <div className="form-group">
            <label htmlFor="currentPassword">Contraseña Actual:</label>
            <input
              type="password"
              id="currentPassword"
              name="currentPassword"
              value={formData.currentPassword}
              onChange={handleChange}
              placeholder="Ingrese su contraseña actual"
              required
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="newPassword">Nueva Contraseña:</label>
            <input
              type="password"
              id="newPassword"
              name="newPassword"
              value={formData.newPassword}
              onChange={handleChange}
              placeholder="Ingrese su nueva contraseña"
              required
              disabled={loading}
            />
            <small className="help-text">
              Mínimo 8 caracteres, debe incluir mayúsculas, minúsculas, números y caracteres especiales
            </small>
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirmar Contraseña:</label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              placeholder="Confirme su nueva contraseña"
              required
              disabled={loading}
            />
          </div>

          {error && <div className="error-message">{error}</div>}

          <div className="button-group">
            <button 
              type="submit" 
              className="submit-button"
              disabled={loading}
            >
              {loading ? 'Cambiando...' : 'Cambiar Contraseña'}
            </button>
            {onCancel && (
              <button 
                type="button" 
                className="cancel-button"
                onClick={onCancel}
                disabled={loading}
              >
                Continuar sin cambiar
              </button>
            )}
          </div>
        </form>
      </div>
    </div>
  );
};

export default ChangePassword;
