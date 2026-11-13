import { Link } from 'react-router-dom';
import { BookOpen } from 'lucide-react';
import { useLanguage } from '../../i18n/LanguageContext';

export default function Footer() {
  const { t } = useLanguage();

  return (
    <footer className="bg-paper-subtle border-t border-paper-border">
      <div className="section-container py-12">
        <div className="flex flex-col md:flex-row items-center justify-between gap-6">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl bg-aurora-700 flex items-center justify-center">
              <BookOpen className="text-white" size={18} />
            </div>
            <div>
              <p className="font-display text-base font-medium text-ink">Aurora Bookstore</p>
              <p className="text-xs text-ink-muted">{t('footer.tagline')}</p>
            </div>
          </div>

          <nav className="flex items-center gap-6">
            <Link to="/books" className="text-sm text-ink-muted hover:text-aurora-700 transition-colors">{t('nav.books')}</Link>
            <Link to="/authors" className="text-sm text-ink-muted hover:text-aurora-700 transition-colors">{t('nav.authors')}</Link>
            <Link to="/publish" className="text-sm text-ink-muted hover:text-aurora-700 transition-colors">{t('nav.publish')}</Link>
            <Link to="/contact" className="text-sm text-ink-muted hover:text-aurora-700 transition-colors">{t('nav.contact')}</Link>
          </nav>

          <p className="text-xs text-ink-subtle">
            {t('footer.copyright')}
          </p>
        </div>
      </div>
    </footer>
  );
}
