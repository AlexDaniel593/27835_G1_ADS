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
              <h3>Productos</h3>
              <p>Gestiona el catálogo de productos</p>
              <button className="card-button">Ver Productos</button>
            </div>

            <div className="dashboard-card">
              <div className="card-icon" style={{background: '#50C878'}}>
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 12H15M9 16H15M17 21H7C5.89543 21 5 20.1046 5 19V5C5 3.89543 5.89543 3 7 3H12.5858C12.851 3 13.1054 3.10536 13.2929 3.29289L18.7071 8.70711C18.8946 8.89464 19 9.149 19 9.41421V19C19 20.1046 18.1046 21 17 21Z" stroke="white" strokeWidth="2"/>
                </svg>
              </div>
              <h3>Proformas</h3>
              <p>Crea y gestiona proformas</p>
              <button className="card-button">Ver Proformas</button>
            </div>

            <div className="dashboard-card">
              <div className="card-icon" style={{background: '#FFB347'}}>
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M17 21V19C17 17.9391 16.5786 16.9217 15.8284 16.1716C15.0783 15.4214 14.0609 15 13 15H5C3.93913 15 2.92172 15.4214 2.17157 16.1716C1.42143 16.9217 1 17.9391 1 19V21" stroke="white" strokeWidth="2"/>
                  <circle cx="9" cy="7" r="4" stroke="white" strokeWidth="2"/>
                  <path d="M23 21V19C23 17.9391 22.5786 16.9217 21.8284 16.1716C21.0783 15.4214 20.0609 15 19 15H18" stroke="white" strokeWidth="2"/>
                </svg>
              </div>
              <h3>Clientes</h3>
              <p>Administra clientes</p>
              <button className="card-button">Ver Clientes</button>
            </div>

            <div className="dashboard-card">
              <div className="card-icon" style={{background: '#9370DB'}}>
                <svg width="40" height="40" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="white" strokeWidth="2"/>
                  <path d="M2 17L12 22L22 17" stroke="white" strokeWidth="2"/>
                  <path d="M2 12L12 17L22 12" stroke="white" strokeWidth="2"/>
                </svg>
              </div>
              <h3>Reportes</h3>
              <p>Visualiza estadísticas</p>
              <button className="card-button">Ver Reportes</button>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default Dashboard;
