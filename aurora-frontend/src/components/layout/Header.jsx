import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Search, Menu, X, Globe } from 'lucide-react';
import { useLanguage } from '../../i18n/LanguageContext';
import auroraLogo from '../../assets/aurora-logo.png';

export default function Header() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const { language, toggleLanguage, t } = useLanguage();
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

          {/* Logo */}
          <Link to="/" className="flex items-center gap-3 flex-shrink-0">
            <img src={auroraLogo} alt="Aurora Bookstore" className="h-9 w-auto" />
          </Link>

          {/* Center: Search Bar (hidden on mobile) */}
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

          {/* Right: Nav Links (desktop) */}
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

            {/* Language Toggle */}
            <button
              onClick={toggleLanguage}
              className="ml-2 flex items-center gap-1.5 px-3 py-2 text-sm font-medium text-slate-500 hover:text-aurora-700 rounded-lg hover:bg-slate-50 transition-all duration-200"
              aria-label="Toggle language"
            >
              <Globe size={16} />
              <span className="uppercase text-xs font-semibold tracking-wider">{language === 'en' ? 'PT' : 'EN'}</span>
            </button>
          </nav>

          {/* Mobile: Language + Hamburger */}
          <div className="flex items-center gap-2 lg:hidden">
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

      {/* Mobile Menu */}
      {mobileMenuOpen && (
        <div className="lg:hidden bg-white border-t border-slate-100 animate-fade-in">
          <div className="section-container py-4 space-y-1">
            {/* Mobile Search */}
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
          </div>
        </div>
      )}
    </header>
  );
}
