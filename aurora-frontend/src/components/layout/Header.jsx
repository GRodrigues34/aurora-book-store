import { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Search, Menu, X, Globe, ShoppingBag } from 'lucide-react';
import { useLanguage } from '../../i18n/LanguageContext';
import { useAuth } from '../../context/AuthContext';
import { useCart } from '../../context/CartContext';
import auroraLogo from '../../assets/aurora-logo.png';

export default function Header() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [scrollProgress, setScrollProgress] = useState(0);
  const { language, toggleLanguage, t } = useLanguage();
  const { user, isAuthenticated, logout } = useAuth();
  const { totalItems, toggleCart } = useCart();
  const location = useLocation();
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    const onScroll = () => {
      const docHeight = document.documentElement.scrollHeight - window.innerHeight;
      const progress = docHeight > 0 ? Math.min(1, Math.max(0, window.scrollY / docHeight)) : 0;
      setScrollProgress(progress);
    };
    window.addEventListener('scroll', onScroll, { passive: true });
    onScroll();
    return () => window.removeEventListener('scroll', onScroll);
  }, [location.pathname]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    if (!searchQuery.trim()) return;
    const path = location.pathname.includes('/authors') ? '/authors' : '/books';
    navigate(`${path}?search=${encodeURIComponent(searchQuery.trim())}`);
    setMobileMenuOpen(false);
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
    <>
      <header className="sticky top-0 z-50 bg-obsidian/92 backdrop-blur-md border-b border-paper/10 text-paper">
        <div className="section-container">
          <div className="flex items-center justify-between h-16 lg:h-20 gap-4">
            <Link to="/" className="flex items-center gap-2.5 flex-shrink-0">
              <img src={auroraLogo} alt="Aurora Bookstore" className="h-9 lg:h-10 w-auto object-contain" />
              <span className="font-sans text-lg font-semibold tracking-tight text-paper">
                Aurora <span className="serif-italic text-aurora-300">Bookstore</span>
              </span>
            </Link>

            <form onSubmit={handleSearchSubmit} className="hidden md:flex flex-1 max-w-sm mx-4">
              <div className="relative w-full">
                <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 text-paper/40" size={15} />
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder={t('nav.searchPlaceholder')}
                  className="w-full pl-9 pr-4 py-2 rounded-full bg-white/5 border border-paper/15 text-xs text-paper placeholder-paper/40 transition-colors focus:outline-none focus:bg-white/10 focus:border-gold/60"
                />
              </div>
            </form>

            <nav className="hidden lg:flex items-center gap-1">
              {navLinks.map(link => (
                <Link
                  key={link.to}
                  to={link.to}
                  className={`px-3 py-2 text-xs font-medium tracking-wide rounded-full transition-colors duration-200 ${
                    isActive(link.to)
                      ? 'text-paper bg-white/10'
                      : 'text-paper/65 hover:text-paper hover:bg-white/5'
                  }`}
                >
                  {link.label}
                </Link>
              ))}

              {isAuthenticated ? (
                <>
                  <Link
                    to="/orders"
                    className={`px-3 py-2 text-xs font-medium tracking-wide rounded-full transition-colors duration-200 ${
                      isActive('/orders')
                        ? 'text-paper bg-white/10'
                        : 'text-paper/65 hover:text-paper hover:bg-white/5'
                    }`}
                  >
                    {t('orders.title')}
                  </Link>
                  <span className="px-3 py-1.5 text-[11px] font-mono text-paper/80 bg-white/5 border border-paper/15 rounded-full max-w-[130px] truncate ml-1">
                    {user?.email?.split('@')[0]}
                  </span>
                  <button
                    onClick={logout}
                    className="px-3 py-2 text-xs font-medium text-red-300 hover:text-red-200 hover:bg-red-500/10 rounded-full transition-colors"
                  >
                    {t('auth.logoutBtn')}
                  </button>
                </>
              ) : (
                <Link
                  to="/login"
                  className="px-5 py-2 text-xs font-semibold text-paper bg-aurora-700 hover:bg-aurora-600 rounded-full transition-colors duration-200 ml-2"
                >
                  {t('auth.entrar')}
                </Link>
              )}

              <button
                onClick={toggleCart}
                className="relative flex items-center justify-center p-2 text-paper/75 hover:text-paper hover:bg-white/5 rounded-full transition-colors ml-1"
                aria-label="Open shopping cart"
              >
                <ShoppingBag size={18} />
                {totalItems > 0 && (
                  <span className="absolute -top-0.5 -right-0.5 w-4 h-4 rounded-full bg-gold text-obsidian text-[10px] font-mono font-bold flex items-center justify-center tabular-nums">
                    {totalItems}
                  </span>
                )}
              </button>

              <button
                onClick={toggleLanguage}
                className="ml-1 flex items-center gap-1 px-2.5 py-2 text-xs font-medium text-paper/65 hover:text-paper rounded-full hover:bg-white/5 transition-colors"
                aria-label="Toggle language"
              >
                <Globe size={14} />
                <span className="uppercase font-mono text-[11px]">{language === 'en' ? 'EN' : 'PT'}</span>
              </button>
            </nav>

            <div className="flex items-center gap-2 lg:hidden">
              <button
                onClick={toggleCart}
                className="relative flex items-center justify-center p-2 text-paper/80 hover:text-paper rounded-full transition-colors"
                aria-label="Open shopping cart"
              >
                <ShoppingBag size={19} />
                {totalItems > 0 && (
                  <span className="absolute -top-0.5 -right-0.5 w-4 h-4 rounded-full bg-gold text-obsidian text-[9px] font-mono font-bold flex items-center justify-center tabular-nums">
                    {totalItems}
                  </span>
                )}
              </button>

              <button
                onClick={toggleLanguage}
                className="flex items-center gap-1 px-2 py-2 text-paper/75 hover:text-paper rounded-full transition-colors"
                aria-label="Toggle language"
              >
                <Globe size={16} />
                <span className="uppercase font-mono text-[11px]">{language === 'en' ? 'EN' : 'PT'}</span>
              </button>

              <button
                onClick={() => setMobileMenuOpen(true)}
                className="p-2 text-paper/80 hover:text-paper rounded-full transition-colors"
                aria-label="Open menu"
              >
                <Menu size={22} />
              </button>
            </div>
          </div>
        </div>

        {/* 1px Scroll Progress Indicator */}
        <div
          className="h-[2px] bg-gradient-to-r from-aurora-500 via-gold to-aurora-400 origin-left transition-transform duration-75"
          style={{ transform: `scaleX(${scrollProgress})` }}
          aria-hidden="true"
        />
      </header>

      {/* Fullscreen Editorial Mobile Overlay */}
      {mobileMenuOpen && (
        <div className="fixed inset-0 z-50 bg-obsidian text-paper flex flex-col justify-between p-6 sm:p-10 lg:hidden">
          <div className="flex items-center justify-between border-b border-paper/15 pb-5">
            <span className="font-sans text-lg font-semibold tracking-tight">
              Aurora <span className="serif-italic text-aurora-300">Bookstore</span>
            </span>
            <button
              onClick={() => setMobileMenuOpen(false)}
              className="p-2 rounded-full border border-paper/20 text-paper hover:bg-white/10 transition-colors"
              aria-label="Close menu"
            >
              <X size={20} />
            </button>
          </div>

          <div className="space-y-6 my-auto py-8">
            <form onSubmit={handleSearchSubmit} className="relative">
              <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-paper/40" size={16} />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder={t('nav.searchPlaceholder')}
                className="w-full pl-11 pr-4 py-3 rounded-full bg-white/5 border border-paper/15 text-sm text-paper placeholder-paper/40 focus:outline-none focus:border-gold"
              />
            </form>

            <nav className="flex flex-col space-y-2">
              {navLinks.map((link, idx) => (
                <Link
                  key={link.to}
                  to={link.to}
                  onClick={() => setMobileMenuOpen(false)}
                  className="flex items-baseline gap-4 py-2 border-b border-paper/10 text-3xl font-medium tracking-tight text-paper hover:text-aurora-300 transition-colors"
                >
                  <span className="font-mono text-xs text-gold tabular-nums">
                    0{idx + 1}
                  </span>
                  <span>{link.label}</span>
                </Link>
              ))}
              {isAuthenticated && (
                <Link
                  to="/orders"
                  onClick={() => setMobileMenuOpen(false)}
                  className="flex items-baseline gap-4 py-2 border-b border-paper/10 text-3xl font-medium tracking-tight text-paper hover:text-aurora-300 transition-colors"
                >
                  <span className="font-mono text-xs text-gold tabular-nums">
                    0{navLinks.length + 1}
                  </span>
                  <span>{t('orders.title')}</span>
                </Link>
              )}
            </nav>
          </div>

          <div className="pt-4 border-t border-paper/15 flex items-center justify-between gap-4">
            {isAuthenticated ? (
              <button
                onClick={() => {
                  logout();
                  setMobileMenuOpen(false);
                }}
                className="w-full py-3 rounded-full border border-red-400/40 text-red-300 text-sm font-medium"
              >
                {t('auth.logoutBtn')} ({user?.email?.split('@')[0]})
              </button>
            ) : (
              <Link
                to="/login"
                onClick={() => setMobileMenuOpen(false)}
                className="btn-primary w-full text-center"
              >
                {t('auth.entrar')}
              </Link>
            )}
          </div>
        </div>
      )}
    </>
  );
}
