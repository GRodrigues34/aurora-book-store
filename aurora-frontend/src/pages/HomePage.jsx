import { Link } from 'react-router-dom';
import { ArrowRight, BookOpen, Users, Sparkles } from 'lucide-react';
import { useLanguage } from '../i18n/LanguageContext';
import { getBooks } from '../services/catalog';
import { useState, useEffect } from 'react';
import BookCard from '../components/ui/BookCard';
import auroraLogo from '../assets/aurora-logo.png';

export default function HomePage() {
  const { t } = useLanguage();
  const [featuredBooks, setFeaturedBooks] = useState([]);

  useEffect(() => {
    getBooks()
      .then(data => {
        if (data) {
          setFeaturedBooks(data.slice(0, 3));
        }
      })
      .catch(error => console.error(error));
  }, []);

  return (
    <div className="page-enter">
      {/* Editorial Hero */}
      <section className="border-b border-paper-border bg-paper">
        <div className="section-container py-16 sm:py-24">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
            <div className="lg:col-span-7 space-y-7">
              <h1 className="font-display text-5xl sm:text-6xl lg:text-[4.25rem] font-normal text-ink leading-[1.02] tracking-[-0.03em]">
                {t('home.heroTitle')}
              </h1>

              <p className="text-base sm:text-lg text-ink-muted leading-relaxed max-w-[62ch]">
                {t('home.heroSubtitle')}
              </p>

              <div className="flex flex-wrap items-center gap-4 pt-2">
                <Link to="/books" className="btn-primary text-sm px-7 py-3.5">
                  {t('home.heroCta')}
                  <ArrowRight size={16} />
                </Link>
                <Link to="/authors" className="btn-secondary text-sm px-7 py-3.5">
                  {t('nav.authors')}
                </Link>
              </div>
            </div>

            <div className="lg:col-span-5 flex justify-center lg:justify-end">
              <div className="w-full max-w-sm rounded-2xl bg-white border border-paper-border shadow-aurora-lg p-8 flex flex-col items-center text-center">
                <img
                  src={auroraLogo}
                  alt="Aurora Bookstore Emblem"
                  className="w-56 h-auto object-contain mb-4"
                />
                <p className="font-display italic text-lg text-ink-muted">
                  {t('footer.tagline')}
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Featured Books */}
      <section className="py-20 sm:py-24 bg-white border-b border-paper-border">
        <div className="section-container">
          <div className="flex items-end justify-between pb-6 mb-10 border-b border-paper-border">
            <h2 className="font-display text-3xl sm:text-4xl font-normal text-ink">
              {t('home.featuredTitle')}
            </h2>
            <Link
              to="/books"
              className="text-sm font-medium text-aurora-700 hover:text-aurora-900 transition-colors flex items-center gap-1.5"
            >
              {t('nav.books')}
              <ArrowRight size={15} />
            </Link>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
            {featuredBooks.map(book => (
              <BookCard key={book.id} book={book} />
            ))}
          </div>
        </div>
      </section>

      {/* Why Aurora — Asymmetric 12-Column Editorial Layout */}
      <section className="py-20 sm:py-24 bg-paper">
        <div className="section-container">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12">
            <div className="lg:col-span-4">
              <h2 className="font-display text-3xl sm:text-4xl font-normal text-ink leading-tight">
                {t('home.whyTitle')}
              </h2>
            </div>

            <div className="lg:col-span-8 divide-y divide-paper-border border-t border-b border-paper-border">
              <div className="py-7 flex items-start gap-5">
                <div className="w-11 h-11 rounded-xl bg-white border border-paper-border flex items-center justify-center flex-shrink-0">
                  <BookOpen className="text-aurora-700" size={20} />
                </div>
                <div>
                  <h3 className="font-display text-xl font-medium text-ink mb-1.5">
                    {t('home.whyCurated')}
                  </h3>
                  <p className="text-sm text-ink-muted leading-relaxed max-w-[60ch]">
                    {t('home.whyCuratedDesc')}
                  </p>
                </div>
              </div>

              <div className="py-7 flex items-start gap-5">
                <div className="w-11 h-11 rounded-xl bg-white border border-paper-border flex items-center justify-center flex-shrink-0">
                  <Users className="text-emerald-700" size={20} />
                </div>
                <div>
                  <h3 className="font-display text-xl font-medium text-ink mb-1.5">
                    {t('home.whyCommunity')}
                  </h3>
                  <p className="text-sm text-ink-muted leading-relaxed max-w-[60ch]">
                    {t('home.whyCommunityDesc')}
                  </p>
                </div>
              </div>

              <div className="py-7 flex items-start gap-5">
                <div className="w-11 h-11 rounded-xl bg-white border border-paper-border flex items-center justify-center flex-shrink-0">
                  <Sparkles className="text-aurora-700" size={20} />
                </div>
                <div>
                  <h3 className="font-display text-xl font-medium text-ink mb-1.5">
                    {t('home.whyAi')}
                  </h3>
                  <p className="text-sm text-ink-muted leading-relaxed max-w-[60ch]">
                    {t('home.whyAiDesc')}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
