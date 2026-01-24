import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'sonner';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Login from './components/app/Login';
import Dashboard from './components/app/Dashboard';
import AddProduct from './components/app/AddProduct';
import SearchProduct from './components/app/SearchProduct';
import EditProduct from './components/app/EditProduct';
import DeletedProducts from './components/app/DeletedProducts';
import ManageOffers from './components/app/ManageOffers';
import PublicCatalog from './components/app/PublicCatalog';
import GenerateProforma from './components/app/GenerateProforma';
import ConsultProforma from './components/app/ConsultProforma';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <Toaster position="top-right" richColors expand={false} />
      <Router>
        <Routes>
          {/* REQ009: Ruta pública del catálogo */}
          <Route path="/catalog" element={<PublicCatalog />} />
          
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
          <Route 
            path="/edit-product/:id" 
            element={
              <ProtectedRoute>
                <EditProduct />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/deleted-products" 
            element={
              <ProtectedRoute>
                <DeletedProducts />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/manage-offers" 
            element={
              <ProtectedRoute>
                <ManageOffers />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/generate-proforma" 
            element={
              <ProtectedRoute>
                <GenerateProforma />
              </ProtectedRoute>
            } 
          />
          <Route 
            path="/consult-proforma" 
            element={
              <ProtectedRoute>
                <ConsultProforma />
              </ProtectedRoute>
            } 
          />
          <Route path="/" element={<Navigate to="/catalog" replace />} />
          <Route path="*" element={<Navigate to="/catalog" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
