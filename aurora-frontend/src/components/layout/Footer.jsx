import { Link } from 'react-router-dom';
import { BookOpen } from 'lucide-react';
import { useLanguage } from '../../i18n/LanguageContext';

export default function Footer() {
  const { t } = useLanguage();

  return (
    <footer className="bg-slate-50 border-t border-slate-100">
      <div className="section-container py-12">
        <div className="flex flex-col md:flex-row items-center justify-between gap-6">

          {/* Brand */}
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-aurora-700 flex items-center justify-center">
              <BookOpen className="text-white" size={18} />
            </div>
            <div>
              <p className="text-sm font-semibold text-slate-800">Aurora Bookstore</p>
              <p className="text-xs text-slate-400">{t('footer.tagline')}</p>
            </div>
          </div>

          {/* Links */}
          <nav className="flex items-center gap-6">
            <Link to="/books" className="text-sm text-slate-500 hover:text-aurora-700 transition-colors">{t('nav.books')}</Link>
            <Link to="/authors" className="text-sm text-slate-500 hover:text-aurora-700 transition-colors">{t('nav.authors')}</Link>
            <Link to="/publish" className="text-sm text-slate-500 hover:text-aurora-700 transition-colors">{t('nav.publish')}</Link>
            <Link to="/contact" className="text-sm text-slate-500 hover:text-aurora-700 transition-colors">{t('nav.contact')}</Link>
          </nav>

          {/* Copyright */}
          <p className="text-xs text-slate-400">
            {t('footer.copyright')}
          </p>
        </div>
      </div>
    </footer>
  );
}
