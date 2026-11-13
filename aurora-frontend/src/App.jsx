import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { GoogleOAuthProvider } from '@react-oauth/google';
import { LanguageProvider } from './i18n/LanguageContext';
import { AuthProvider } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import { Toaster } from 'react-hot-toast';
import Header from './components/layout/Header';
import Footer from './components/layout/Footer';
import BorealFAB from './components/ui/BorealFAB';
import CartDrawer from './components/layout/CartDrawer';
import HomePage from './pages/HomePage';
import BooksPage from './pages/BooksPage';
import AuthorsPage from './pages/AuthorsPage';
import PublishPage from './pages/PublishPage';
import ContactPage from './pages/ContactPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import VerifyEmailPage from './pages/VerifyEmailPage';
import OrdersPage from './pages/OrdersPage';
import PrivateRoute from './components/PrivateRoute';
import { useSmoothScroll } from './hooks/useSmoothScroll';

function AppRoutes() {
  useSmoothScroll();
  return (
    <BrowserRouter>
      <LanguageProvider>
        <AuthProvider>
          <CartProvider>
            <Toaster position="top-right" />
            <div className="min-h-screen flex flex-col bg-paper text-ink">
              <Header />
              <CartDrawer />

              <main className="flex-1">
                <Routes>
                  <Route path="/" element={<HomePage />} />
                  <Route path="/books" element={<BooksPage />} />
                  <Route path="/authors" element={<AuthorsPage />} />
                  <Route path="/publish" element={<PrivateRoute adminOnly={true}><PublishPage /></PrivateRoute>} />
                  <Route path="/contact" element={<ContactPage />} />
                  <Route path="/login" element={<LoginPage />} />
                  <Route path="/register" element={<RegisterPage />} />
                  <Route path="/verify-email" element={<VerifyEmailPage />} />
                  <Route path="/orders" element={<PrivateRoute><OrdersPage /></PrivateRoute>} />
                </Routes>
              </main>

              <Footer />
              <BorealFAB />
            </div>
          </CartProvider>
        </AuthProvider>
      </LanguageProvider>
    </BrowserRouter>
  );
}

function App() {
  const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
  if (googleClientId) {
    return (
      <GoogleOAuthProvider clientId={googleClientId}>
        <AppRoutes />
      </GoogleOAuthProvider>
    );
  }
  return <AppRoutes />;
}

export default App;
