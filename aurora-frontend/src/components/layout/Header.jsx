import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Search, Menu, X, Globe, ShoppingBag } from 'lucide-react';
import { useLanguage } from '../../i18n/LanguageContext';
import { useAuth } from '../../context/AuthContext';
import { useCart } from '../../context/CartContext';
import auroraLogo from '../../assets/aurora-logo.png';

export default function Header() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const { language, toggleLanguage, t } = useLanguage();
  const { user, isAuthenticated, logout } = useAuth();
  const { totalItems, toggleCart } = useCart();
  const location = useLocation();

  const navLinks = [
    { to: '/', label: t('nav.home') },
    { to: '/books', label: t('nav.books') },
    { to: '/authors', label: t('nav.authors') },
    { to: '/publish', label: t('nav.publish') },
    { to: '/contact', label: t('nav.contact') },
  ];

  const isActive = (path) => location.pathname === path;

  return (
    <header className="sticky top-0 z-50 bg-white/95 backdrop-blur-md border-b border-slate-100">
      <div className="section-container">
        <div className="flex items-center justify-between h-16 lg:h-20">

          <Link to="/" className="flex items-center gap-3 flex-shrink-0">
            <img src={auroraLogo} alt="Aurora Bookstore" className="h-9 w-auto" />
          </Link>

          <div className="hidden md:flex flex-1 max-w-md mx-8">
            <div className="relative w-full">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
              <input
                type="text"
                placeholder={t('nav.searchPlaceholder')}
                className="w-full pl-10 pr-4 py-2.5 rounded-full bg-slate-50 border border-slate-200 text-sm text-slate-700 placeholder-slate-400 transition-all duration-200 focus:outline-none focus:bg-white focus:border-aurora-400 focus:ring-2 focus:ring-aurora-500/20"
              />
            </div>
          </div>

          <nav className="hidden lg:flex items-center gap-1">
            {navLinks.map(link => (
              <Link
                key={link.to}
                to={link.to}
                className={`px-3 py-2 text-sm font-medium rounded-lg transition-all duration-200 ${
                  isActive(link.to)
                    ? 'text-aurora-700 bg-aurora-50'
                    : 'text-slate-600 hover:text-aurora-700 hover:bg-slate-50'
                }`}
              >
                {link.label}
              </Link>
            ))}

            {isAuthenticated ? (
              <>
                <Link
                  to="/orders"
                  className={`px-3 py-2 text-sm font-medium rounded-lg transition-all duration-200 ${
                    isActive('/orders')
                      ? 'text-aurora-700 bg-aurora-50'
                      : 'text-slate-600 hover:text-aurora-700 hover:bg-slate-50'
                  }`}
                >
                  {t('orders.title')}
                </Link>
                <span className="px-3 py-2 text-sm font-semibold text-slate-700 bg-slate-100 rounded-lg max-w-[150px] truncate">
                  {user?.email?.split('@')[0]}
                </span>
                <button
                  onClick={logout}
                  className="px-3 py-2 text-sm font-medium text-red-600 hover:text-red-800 hover:bg-red-50 rounded-lg transition-all duration-200"
                >
                  {t('auth.logoutBtn')}
                </button>
              </>
            ) : (
              <Link
                to="/login"
                className="px-4 py-2 text-sm font-medium text-white bg-aurora-700 hover:bg-aurora-800 rounded-full transition-all duration-200 shadow-sm ml-2"
              >
                {t('auth.entrar')}
              </Link>
            )}

            <button
              onClick={toggleCart}
              className="relative flex items-center justify-center p-2 text-slate-600 hover:text-aurora-700 hover:bg-slate-50 rounded-lg transition-all duration-200 ml-2"
              aria-label="Open shopping cart"
            >
              <ShoppingBag size={20} />
              {totalItems > 0 && (
                <span className="absolute -top-1.5 -right-1.5 w-5 h-5 rounded-full bg-aurora-600 text-white text-[10px] font-bold flex items-center justify-center border border-white">
                  {totalItems}
                </span>
              )}
            </button>

            <button
              onClick={toggleLanguage}
              className="ml-2 flex items-center gap-1.5 px-3 py-2 text-sm font-medium text-slate-500 hover:text-aurora-700 rounded-lg hover:bg-slate-50 transition-all duration-200"
              aria-label="Toggle language"
            >
              <Globe size={16} />
              <span className="uppercase text-xs font-semibold tracking-wider">{language === 'en' ? 'PT' : 'EN'}</span>
            </button>
          </nav>

          <div className="flex items-center gap-2 lg:hidden">
            <button
              onClick={toggleCart}
              className="relative flex items-center justify-center p-2 text-slate-600 hover:text-aurora-700 rounded-lg transition-colors mr-1"
              aria-label="Open shopping cart"
            >
              <ShoppingBag size={20} />
              {totalItems > 0 && (
                <span className="absolute -top-1.5 -right-1.5 w-5 h-5 rounded-full bg-aurora-600 text-white text-[9px] font-bold flex items-center justify-center border border-white">
                  {totalItems}
                </span>
              )}
            </button>

            <button
              onClick={toggleLanguage}
              className="flex items-center gap-1 px-2 py-2 text-slate-500 hover:text-aurora-700 rounded-lg transition-colors"
              aria-label="Toggle language"
            >
              <Globe size={18} />
              <span className="uppercase text-xs font-semibold">{language === 'en' ? 'PT' : 'EN'}</span>
            </button>
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="p-2 text-slate-600 hover:text-aurora-700 rounded-lg transition-colors"
              aria-label="Toggle menu"
            >
              {mobileMenuOpen ? <X size={22} /> : <Menu size={22} />}
            </button>
          </div>
        </div>
      </div>

      {mobileMenuOpen && (
        <div className="lg:hidden bg-white border-t border-slate-100 animate-fade-in">
          <div className="section-container py-4 space-y-1">
            <div className="relative mb-3 md:hidden">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
              <input
                type="text"
                placeholder={t('nav.searchPlaceholder')}
                className="w-full pl-10 pr-4 py-2.5 rounded-full bg-slate-50 border border-slate-200 text-sm text-slate-700 placeholder-slate-400 transition-all duration-200 focus:outline-none focus:bg-white focus:border-aurora-400 focus:ring-2 focus:ring-aurora-500/20"
              />
            </div>
            {navLinks.map(link => (
              <Link
                key={link.to}
                to={link.to}
                onClick={() => setMobileMenuOpen(false)}
                className={`block px-4 py-2.5 text-sm font-medium rounded-lg transition-all duration-200 ${
                  isActive(link.to)
                    ? 'text-aurora-700 bg-aurora-50'
                    : 'text-slate-600 hover:text-aurora-700 hover:bg-slate-50'
                }`}
              >
                {link.label}
              </Link>
            ))}

            {isAuthenticated ? (
              <>
                <Link
                  to="/orders"
                  onClick={() => setMobileMenuOpen(false)}
                  className={`block px-4 py-2.5 text-sm font-medium rounded-lg transition-all duration-200 ${
                    isActive('/orders')
                      ? 'text-aurora-700 bg-aurora-50'
                      : 'text-slate-600 hover:text-aurora-700 hover:bg-slate-50'
                  }`}
                >
                  {t('orders.title')}
                </Link>
                <button
                  onClick={() => {
                    logout();
                    setMobileMenuOpen(false);
                  }}
                  className="block w-full text-left px-4 py-2.5 text-sm font-medium text-red-600 hover:bg-red-50 rounded-lg transition-all duration-200"
                >
                  {t('auth.logoutBtn')} ({user?.email?.split('@')[0]})
                </button>
              </>
            ) : (
              <Link
                to="/login"
                onClick={() => setMobileMenuOpen(false)}
                className="block px-4 py-2.5 text-sm font-medium text-aurora-700 hover:bg-aurora-50 rounded-lg transition-all duration-200"
              >
                {t('auth.entrar')}
              </Link>
            )}
          </div>
        </div>
      )}
    </header>
  );
}
