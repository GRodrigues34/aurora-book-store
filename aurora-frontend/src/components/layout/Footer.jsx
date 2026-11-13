import { Link } from 'react-router-dom';
import { useLanguage } from '../../i18n/LanguageContext';

export default function Footer() {
  const { t } = useLanguage();

  return (
    <footer className="bg-obsidian-deep text-paper border-t border-paper/10 overflow-hidden">
      <div className="section-container pt-14 pb-8">
        <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-6 pb-12 border-b border-paper/10">
          <div>
            <p className="font-sans text-base font-medium text-paper">Aurora Bookstore</p>
            <p className="serif-italic text-lg text-paper/60 mt-1">{t('footer.tagline')}</p>
          </div>

          <nav className="flex flex-wrap items-center gap-6">
            <Link to="/books" className="text-sm text-paper/65 hover:text-gold transition-colors">{t('nav.books')}</Link>
            <Link to="/authors" className="text-sm text-paper/65 hover:text-gold transition-colors">{t('nav.authors')}</Link>
            <Link to="/publish" className="text-sm text-paper/65 hover:text-gold transition-colors">{t('nav.publish')}</Link>
            <Link to="/contact" className="text-sm text-paper/65 hover:text-gold transition-colors">{t('nav.contact')}</Link>
          </nav>

          <p className="font-mono text-xs text-paper/45">
            {t('footer.copyright')}
          </p>
        </div>

        <div className="pt-8 select-none" aria-hidden="true">
          <p className="font-sans font-semibold tracking-[-0.06em] leading-[0.82] text-[clamp(3.75rem,14vw,12rem)] text-paper/90 text-center">
            AURORA
          </p>
        </div>
      </div>
    </footer>
  );
}
