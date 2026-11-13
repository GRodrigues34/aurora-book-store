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
  const [isDarkSurface, setIsDarkSurface] = useState(true);
  const [catalogSearchActive, setCatalogSearchActive] = useState(false);
  const { language, toggleLanguage, t } = useLanguage();
  const { user, isAuthenticated, logout } = useAuth();
  const { totalItems, toggleCart } = useCart();
  const location = useLocation();
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');

  useEffect(() => {
    const onCatalogToggle = (e) => {
      setCatalogSearchActive(Boolean(e.detail?.active));
    };
    window.addEventListener('aurora:catalog-search', onCatalogToggle);
    return () => window.removeEventListener('aurora:catalog-search', onCatalogToggle);
  }, []);

  useEffect(() => {
    const updateHeaderState = () => {
      const docHeight = document.documentElement.scrollHeight - window.innerHeight;
      const progress = docHeight > 0 ? Math.min(1, Math.max(0, window.scrollY / docHeight)) : 0;
      setScrollProgress(progress);

      const sections = document.querySelectorAll('[data-surface]');
      let surface = location.pathname === '/' ? 'dark' : 'light';
      const probeY = 48;
      for (const sec of sections) {
        const rect = sec.getBoundingClientRect();
        if (rect.top <= probeY && rect.bottom > probeY) {
          surface = sec.getAttribute('data-surface') || surface;
        }
      }
      setIsDarkSurface(surface === 'dark');
    };

    window.addEventListener('scroll', updateHeaderState, { passive: true });
    window.addEventListener('resize', updateHeaderState, { passive: true });
    updateHeaderState();
    return () => {
      window.removeEventListener('scroll', updateHeaderState);
      window.removeEventListener('resize', updateHeaderState);
    };
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

  const headerBg = isDarkSurface
    ? 'bg-transparent border-paper/10 text-paper'
    : 'bg-transparent border-paper-border text-ink';

  const navLinkClass = (active) =>
    isDarkSurface
      ? active
        ? 'text-paper bg-white/10'
        : 'text-paper/70 hover:text-paper hover:bg-white/5'
      : active
        ? 'text-aurora-800 bg-aurora-50'
        : 'text-ink-muted hover:text-ink hover:bg-paper-subtle';

  return (
    <>
      <header className={`fixed top-0 left-0 right-0 z-50 backdrop-blur-md border-b transition-colors duration-300 ${headerBg}`}>
        <div className="section-container">
          <div className="flex items-center justify-between h-16 lg:h-20 gap-4">
            <Link to="/" className="flex items-center gap-2.5 flex-shrink-0">
              <img src={auroraLogo} alt="Aurora Bookstore" className="h-12 lg:h-14 w-auto object-contain" />
              <span className="font-sans text-lg font-semibold tracking-tight">
                Aurora <span className={`serif-italic ${isDarkSurface ? 'text-aurora-300' : 'text-aurora-700'}`}>Bookstore</span>
              </span>
            </Link>

            <form
              onSubmit={handleSearchSubmit}
              className={`hidden md:flex flex-1 max-w-sm mx-4 transition-all duration-300 ease-out ${
                catalogSearchActive
                  ? 'opacity-0 translate-y-3 scale-95 pointer-events-none'
                  : 'opacity-100 translate-y-0 scale-100'
              }`}
            >
              <div className="relative w-full">
                <Search
                  className={`absolute left-3.5 top-1/2 -translate-y-1/2 ${
                    isDarkSurface ? 'text-paper/45' : 'text-ink-subtle'
                  }`}
                  size={15}
                />
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder={t('nav.searchPlaceholder')}
                  className={`w-full pl-9 pr-4 py-2 rounded-full border text-xs transition-colors focus:outline-none ${
                    isDarkSurface
                      ? 'bg-white/5 border-paper/15 text-paper placeholder-paper/45 focus:bg-white/10 focus:border-gold/60'
                      : 'bg-white border-paper-border text-ink placeholder-ink-subtle focus:border-aurora-500'
                  }`}
                />
              </div>
            </form>

            <nav className="hidden lg:flex items-center gap-1">
              {navLinks.map(link => (
                <Link
                  key={link.to}
                  to={link.to}
                  className={`px-3 py-2 text-xs font-medium tracking-wide rounded-full transition-colors duration-200 ${navLinkClass(isActive(link.to))}`}
                >
                  {link.label}
                </Link>
              ))}

              {isAuthenticated ? (
                <>
                  <Link
                    to="/orders"
                    className={`px-3 py-2 text-xs font-medium tracking-wide rounded-full transition-colors duration-200 ${navLinkClass(isActive('/orders'))}`}
                  >
                    {t('orders.title')}
                  </Link>
                  <span
                    className={`px-3 py-1.5 text-[11px] font-mono rounded-full max-w-[130px] truncate ml-1 border ${
                      isDarkSurface
                        ? 'text-paper/80 bg-white/5 border-paper/15'
                        : 'text-ink-muted bg-paper-subtle border-paper-border'
                    }`}
                  >
                    {user?.email?.split('@')[0]}
                  </span>
                  <button
                    onClick={logout}
                    className={`px-3 py-2 text-xs font-medium rounded-full transition-colors ${
                      isDarkSurface
                        ? 'text-red-300 hover:text-red-200 hover:bg-red-500/10'
                        : 'text-red-700 hover:text-red-900 hover:bg-red-50'
                    }`}
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
                className={`relative flex items-center justify-center p-2 rounded-full transition-colors ml-1 ${
                  isDarkSurface
                    ? 'text-paper/80 hover:text-paper hover:bg-white/5'
                    : 'text-ink-muted hover:text-ink hover:bg-paper-subtle'
                }`}
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
                className={`ml-1 flex items-center gap-1 px-2.5 py-2 text-xs font-medium rounded-full transition-colors ${
                  isDarkSurface
                    ? 'text-paper/70 hover:text-paper hover:bg-white/5'
                    : 'text-ink-muted hover:text-ink hover:bg-paper-subtle'
                }`}
                aria-label="Toggle language"
              >
                <Globe size={14} />
                <span className="uppercase font-mono text-[11px]">{language === 'en' ? 'EN' : 'PT'}</span>
              </button>
            </nav>

            <div className="flex items-center gap-2 lg:hidden">
              <button
                onClick={toggleCart}
                className={`relative flex items-center justify-center p-2 rounded-full transition-colors ${
                  isDarkSurface ? 'text-paper/80 hover:text-paper' : 'text-ink-muted hover:text-ink'
                }`}
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
                className={`flex items-center gap-1 px-2 py-2 rounded-full transition-colors ${
                  isDarkSurface ? 'text-paper/80 hover:text-paper' : 'text-ink-muted hover:text-ink'
                }`}
                aria-label="Toggle language"
              >
                <Globe size={16} />
                <span className="uppercase font-mono text-[11px]">{language === 'en' ? 'EN' : 'PT'}</span>
              </button>

              <button
                onClick={() => setMobileMenuOpen(true)}
                className={`p-2 rounded-full transition-colors ${
                  isDarkSurface ? 'text-paper/80 hover:text-paper' : 'text-ink-muted hover:text-ink'
                }`}
                aria-label="Open menu"
              >
                <Menu size={22} />
              </button>
            </div>
          </div>
        </div>

        {/* 2px Scroll Progress Indicator */}
        <div
          className="h-[2px] bg-gradient-to-r from-aurora-500 via-gold to-aurora-400 origin-left transition-transform duration-75"
          style={{ transform: `scaleX(${scrollProgress})` }}
          aria-hidden="true"
        />
      </header>

      {/* Fullscreen Editorial Mobile Overlay */}
      {mobileMenuOpen && (
        <div className="fixed inset-0 z-50 bg-obsidian text-paper flex flex-col justify-between p-6 sm:p-10 lg:hidden animate-fade-in">
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
