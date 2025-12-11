import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'sonner';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './components/app/Login';
import Dashboard from './components/app/Dashboard';
import AddProduct from './components/app/AddProduct';
import SearchProduct from './components/app/SearchProduct';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <Toaster position="top-right" richColors expand={false} />
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route 
            path="/dashboard" 
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/add-product" 
            element={
              <ProtectedRoute>
                <AddProduct />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/consultar-producto" 
            element={
              <ProtectedRoute>
                <SearchProduct />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/search-product" 
            element={
              <ProtectedRoute>
                <SearchProduct />
              </ProtectedRoute>
            } 
          />
          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
