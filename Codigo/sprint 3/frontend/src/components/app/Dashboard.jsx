import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import ChangePassword from '../app/ChangePassword';
import './Dashboard.css';

const Dashboard = () => {
  const { user, logout } = useAuth();
  const [showWelcomeModal, setShowWelcomeModal] = useState(false);
  const [showChangePassword, setShowChangePassword] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const hasSeenWelcome = sessionStorage.getItem('welcomeShown');
    if (!hasSeenWelcome) {
      setShowWelcomeModal(true);
      sessionStorage.setItem('welcomeShown', 'true');
    }
  }, []);
  const handleChangePasswordClick = () => {
    setShowWelcomeModal(false);
    setShowChangePassword(true);
  };

  const handleContinueWithoutChange = () => {
    setShowWelcomeModal(false);
  };

  const handlePasswordChangeSuccess = () => {
    setShowChangePassword(false);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const handleAddProduct = () => {
    navigate('/add-product');
  };

  const handleConsultarProducto = () => {
    navigate('/consultar-producto');
  };

  const handleDeletedProducts = () => {
    navigate('/deleted-products');
  };

  const handleManageOffers = () => {
    navigate('/manage-offers');
  };

  const handleGenerateProforma = () => {
    navigate('/generate-proforma');
  };

  return (
    <div className="dashboard-container">
      {showWelcomeModal && (
        <div className="modal-overlay">
          <div className="welcome-modal">
            <div className="modal-icon">
              <svg width="80" height="80" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L2 7L12 12L22 7L12 2Z" fill="#4A90E2"/>
                <path d="M2 17L12 22L22 17" stroke="#4A90E2" strokeWidth="2"/>
                <path d="M2 12L12 17L22 12" stroke="#4A90E2" strokeWidth="2"/>
              </svg>
            </div>
            <h2>¡Bienvenido a Mueblerix!</h2>
            <p>Hola, <strong>{user?.firstName} {user?.lastName}</strong></p>
            <p className="modal-description">
              Por tu seguridad, te recomendamos cambiar tu contraseña en el primer inicio de sesión.
            </p>
            <div className="modal-buttons">
              <button 
                className="btn-primary"
                onClick={handleChangePasswordClick}
              >
                Cambiar Contraseña
              </button>
              <button 
                className="btn-secondary"
                onClick={handleContinueWithoutChange}
              >
                Continuar sin cambiar
              </button>
            </div>
          </div>
        </div>
      )}

      {showChangePassword && (
        <div className="modal-overlay">
          <ChangePassword 
            onSuccess={handlePasswordChangeSuccess}
            onCancel={() => setShowChangePassword(false)}
            isModal={true}
          />
        </div>
      )}

      <header className="dashboard-header">
        <div className="header-content">
          <h1>MUEBLERIX</h1>
          <div className="user-menu">
            <div className="user-info">
              <span className="user-name">{user?.firstName} {user?.lastName}</span>
              <span className="user-role">{user?.roles?.join(', ')}</span>
            </div>
            <button className="btn-settings" onClick={() => setShowChangePassword(true)}>
              <i className="fas fa-cog"></i> Cambiar Contraseña
            </button>
            <button className="btn-logout" onClick={handleLogout}>
              Cerrar Sesión
            </button>
          </div>
        </div>
      </header>

      <main className="dashboard-main">
        <div className="dashboard-content">
          <div className="welcome-section">
            <h2>¡Bienvenido de nuevo, {user?.firstName}!</h2>
            <p>Sistema de gestión de productos Mueblerix</p>
          </div>

          <div className="dashboard-grid">
            <div className="dashboard-card">
              <div className="card-icon" style={{background: '#4A90E2'}}>
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M20 7H4C2.89543 7 2 7.89543 2 9V19C2 20.1046 2.89543 21 4 21H20C21.1046 21 22 20.1046 22 19V9C22 7.89543 21.1046 7 20 7Z" stroke="white" strokeWidth="2"/>
                  <path d="M16 7V5C16 3.89543 15.1046 3 14 3H10C8.89543 3 8 3.89543 8 5V7" stroke="white" strokeWidth="2"/>
                </svg>
              </div>
              <h3>Consultar Productos</h3>
              <p>Busca y gestiona el catálogo de productos</p>
              <button className="card-button" onClick={handleConsultarProducto}>Ver Productos</button>
            </div>


            <div className="dashboard-card">
              <div className="card-icon" style={{background: '#FFB347'}}>
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M12 4V20M20 12H4" stroke="white" strokeWidth="2" strokeLinecap="round"/>
                  <circle cx="12" cy="12" r="9" stroke="white" strokeWidth="2"/>
                </svg>
              </div>
              <h3>Agregar Producto</h3>
              <p>Añade nuevos productos al catálogo</p>
              <button className="card-button" onClick={handleAddProduct}>Agregar Producto</button>
            </div>

            <div className="dashboard-card">
              <div className="card-icon" style={{background: '#E74C3C'}}>
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M3 6H21M8 6V4C8 3.44772 8.44772 3 9 3H15C15.5523 3 16 3.44772 16 4V6M19 6V20C19 20.5523 18.5523 21 18 21H6C5.44772 21 5 20.5523 5 20V6" stroke="white" strokeWidth="2" strokeLinecap="round"/>
                  <path d="M10 11V16M14 11V16" stroke="white" strokeWidth="2" strokeLinecap="round"/>
                </svg>
              </div>
              <h3>Bandeja de Eliminados</h3>
              <p>Gestiona y restaura productos eliminados</p>
              <button className="card-button" onClick={handleDeletedProducts}>Ver Eliminados</button>
            </div>

            <div className="dashboard-card">
              <div className="card-icon" style={{background: '#FF6B6B'}}>
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M21 7L18.5 4.5M18.5 4.5L16 7M18.5 4.5L21 2M7 21L9.5 18.5M9.5 18.5L12 21M9.5 18.5L7 16" stroke="white" strokeWidth="2" strokeLinecap="round"/>
                  <path d="M12 2V6M12 18V22M22 12H18M6 12H2" stroke="white" strokeWidth="2" strokeLinecap="round"/>
                  <circle cx="12" cy="12" r="3" stroke="white" strokeWidth="2"/>
                </svg>
              </div>
              <h3>Gestionar Ofertas</h3>
              <p>Crea, modifica y elimina ofertas especiales</p>
              <button className="card-button" onClick={handleManageOffers}>Administrar Ofertas</button>
            </div>

            <div className="dashboard-card">
              <div className="card-icon" style={{background: '#5BC0DE'}}>
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 12H15M9 16H15M17 21H7C5.89543 21 5 20.1046 5 19V5C5 3.89543 5.89543 3 7 3H12.5858C12.851 3 13.1054 3.10536 13.2929 3.29289L18.7071 8.70711C18.8946 8.89464 19 9.149 19 9.41421V19C19 20.1046 18.1046 21 17 21Z" stroke="white" strokeWidth="2" strokeLinecap="round"/>
                  <path d="M13 3V8C13 8.55228 13.4477 9 14 9H19" stroke="white" strokeWidth="2" strokeLinecap="round"/>
                </svg>
              </div>
              <h3>Generar Proforma</h3>
              <p>Crea proformas para clientes con ofertas</p>
              <button className="card-button" onClick={handleGenerateProforma}>Nueva Proforma</button>
            </div>

            <div className="dashboard-card">
              <div className="card-icon" style={{background: '#9B59B6'}}>
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M21 21L15 15M17 10C17 13.866 13.866 17 10 17C6.13401 17 3 13.866 3 10C3 6.13401 6.13401 3 10 3C13.866 3 17 6.13401 17 10Z" stroke="white" strokeWidth="2" strokeLinecap="round"/>
                  <path d="M8 10H12M10 8V12" stroke="white" strokeWidth="2" strokeLinecap="round"/>
                </svg>
              </div>
              <h3>Consultar Proforma</h3>
              <p>Busca y visualiza proformas generadas</p>
              <button className="card-button" onClick={() => navigate('/consult-proforma')}>Consultar</button>
            </div>

          </div>
        </div>
      </main>
    </div>
  );
};

export default Dashboard;
