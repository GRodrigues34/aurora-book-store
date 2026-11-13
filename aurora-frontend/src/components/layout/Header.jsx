import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
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
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    if (!searchQuery.trim()) return;
    const path = location.pathname.includes('/authors') ? '/authors' : '/books';
    navigate(`${path}?search=${encodeURIComponent(searchQuery.trim())}`);
  };

  const navLinks = [
    { to: '/', label: t('nav.home') },
    { to: '/books', label: t('nav.books') },
    { to: '/authors', label: t('nav.authors') },
    ...(isAuthenticated && user?.role === 'ADMIN' ? [{ to: '/publish', label: t('nav.publish') }] : []),
    { to: '/contact', label: t('nav.contact') },
  ];

  const isActive = (path) => location.pathname === path;

  return (
    <header className="sticky top-0 z-50 bg-paper/95 backdrop-blur-md border-b border-paper-border">
      <div className="section-container">
        <div className="flex items-center justify-between h-16 lg:h-20 gap-4">

          <Link to="/" className="flex items-center gap-2.5 flex-shrink-0">
            <img src={auroraLogo} alt="Aurora Bookstore" className="h-11 lg:h-12 w-auto object-contain" />
            <span className="hidden sm:inline font-display text-xl font-medium tracking-tight text-ink">
              Aurora
            </span>
          </Link>

          <form onSubmit={handleSearchSubmit} className="hidden md:flex flex-1 max-w-md mx-4">
            <div className="relative w-full">
              <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 text-ink-subtle" size={16} />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder={t('nav.searchPlaceholder')}
                className="w-full pl-10 pr-4 py-2 rounded-full bg-white border border-paper-border text-sm text-ink placeholder-ink-subtle transition-all duration-200 focus:outline-none focus:border-aurora-500 focus:ring-2 focus:ring-aurora-500/20"
              />
            </div>
          </form>

          <nav className="hidden lg:flex items-center gap-1">
            {navLinks.map(link => (
              <Link
                key={link.to}
                to={link.to}
                className={`px-3 py-2 text-sm font-medium rounded-lg transition-colors duration-200 ${
                  isActive(link.to)
                    ? 'text-aurora-700 bg-aurora-50'
                    : 'text-ink-muted hover:text-ink hover:bg-paper-subtle'
                }`}
              >
                {link.label}
              </Link>
            ))}

            {isAuthenticated ? (
              <>
                <Link
                  to="/orders"
                  className={`px-3 py-2 text-sm font-medium rounded-lg transition-colors duration-200 ${
                    isActive('/orders')
                      ? 'text-aurora-700 bg-aurora-50'
                      : 'text-ink-muted hover:text-ink hover:bg-paper-subtle'
                  }`}
                >
                  {t('orders.title')}
                </Link>
                <span className="px-3 py-1.5 text-xs font-mono text-ink-muted bg-paper-subtle border border-paper-border rounded-full max-w-[140px] truncate ml-1">
                  {user?.email?.split('@')[0]}
                </span>
                <button
                  onClick={logout}
                  className="px-3 py-2 text-sm font-medium text-red-700 hover:text-red-900 hover:bg-red-50 rounded-lg transition-colors duration-200"
                >
                  {t('auth.logoutBtn')}
                </button>
              </>
            ) : (
              <Link
                to="/login"
                className="px-5 py-2 text-sm font-medium text-white bg-aurora-700 hover:bg-aurora-800 rounded-full transition-colors duration-200 shadow-aurora-sm ml-2"
              >
                {t('auth.entrar')}
              </Link>
            )}

            <button
              onClick={toggleCart}
              className="relative flex items-center justify-center p-2 text-ink-muted hover:text-aurora-700 hover:bg-paper-subtle rounded-lg transition-colors duration-200 ml-1"
              aria-label="Open shopping cart"
            >
              <ShoppingBag size={19} />
              {totalItems > 0 && (
                <span className="absolute -top-1 -right-1 w-5 h-5 rounded-full bg-aurora-700 text-white text-[10px] font-mono font-medium flex items-center justify-center border-2 border-paper tabular-nums">
                  {totalItems}
                </span>
              )}
            </button>

            <button
              onClick={toggleLanguage}
              className="ml-1 flex items-center gap-1.5 px-2.5 py-2 text-sm font-medium text-ink-muted hover:text-ink rounded-lg hover:bg-paper-subtle transition-colors duration-200"
              aria-label="Toggle language"
            >
              <Globe size={15} />
              <span className="uppercase font-mono text-xs">{language === 'en' ? 'EN' : 'PT'}</span>
            </button>
          </nav>

          <div className="flex items-center gap-2 lg:hidden">
            <button
              onClick={toggleCart}
              className="relative flex items-center justify-center p-2 text-ink-muted hover:text-aurora-700 rounded-lg transition-colors mr-1"
              aria-label="Open shopping cart"
            >
              <ShoppingBag size={20} />
              {totalItems > 0 && (
                <span className="absolute -top-1 -right-1 w-5 h-5 rounded-full bg-aurora-700 text-white text-[9px] font-mono font-medium flex items-center justify-center border-2 border-paper tabular-nums">
                  {totalItems}
                </span>
              )}
            </button>

            <button
              onClick={toggleLanguage}
              className="flex items-center gap-1 px-2 py-2 text-ink-muted hover:text-ink rounded-lg transition-colors"
              aria-label="Toggle language"
            >
              <Globe size={17} />
              <span className="uppercase font-mono text-xs">{language === 'en' ? 'EN' : 'PT'}</span>
            </button>
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="p-2 text-ink-muted hover:text-ink rounded-lg transition-colors"
              aria-label="Toggle menu"
            >
              {mobileMenuOpen ? <X size={22} /> : <Menu size={22} />}
            </button>
          </div>
        </div>
      </div>

      {mobileMenuOpen && (
        <div className="lg:hidden bg-white border-t border-paper-border animate-fade-in">
          <div className="section-container py-4 space-y-1">
            <form onSubmit={handleSearchSubmit} className="relative mb-3 md:hidden">
              <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 text-ink-subtle" size={16} />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder={t('nav.searchPlaceholder')}
                className="w-full pl-10 pr-4 py-2.5 rounded-full bg-paper border border-paper-border text-sm text-ink placeholder-ink-subtle focus:outline-none focus:border-aurora-500"
              />
            </form>
            {navLinks.map(link => (
              <Link
                key={link.to}
                to={link.to}
                onClick={() => setMobileMenuOpen(false)}
                className={`block px-4 py-2.5 text-sm font-medium rounded-lg transition-colors ${
                  isActive(link.to)
                    ? 'text-aurora-700 bg-aurora-50'
                    : 'text-ink-muted hover:text-ink hover:bg-paper-subtle'
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
                  className={`block px-4 py-2.5 text-sm font-medium rounded-lg transition-colors ${
                    isActive('/orders')
                      ? 'text-aurora-700 bg-aurora-50'
                      : 'text-ink-muted hover:text-ink hover:bg-paper-subtle'
                  }`}
                >
                  {t('orders.title')}
                </Link>
                <button
                  onClick={() => {
                    logout();
                    setMobileMenuOpen(false);
                  }}
                  className="block w-full text-left px-4 py-2.5 text-sm font-medium text-red-700 hover:bg-red-50 rounded-lg transition-colors"
                >
                  {t('auth.logoutBtn')} ({user?.email?.split('@')[0]})
                </button>
              </>
            ) : (
              <Link
                to="/login"
                onClick={() => setMobileMenuOpen(false)}
                className="block px-4 py-2.5 text-sm font-medium text-aurora-700 hover:bg-aurora-50 rounded-lg transition-colors"
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
