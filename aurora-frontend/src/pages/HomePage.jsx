import { Link } from 'react-router-dom';
import { ArrowRight, BookOpen, Users, Sparkles } from 'lucide-react';
import { useLanguage } from '../i18n/LanguageContext';
import { books } from '../data/mockData';
import BookCard from '../components/ui/BookCard';

export default function HomePage() {
  const { t } = useLanguage();
  const featuredBooks = books.slice(0, 3);

  return (
    <div className="page-enter">

      {/* Hero Section */}
      <section className="relative overflow-hidden">
        {/* Subtle gradient background */}
        <div className="absolute inset-0 bg-gradient-to-br from-aurora-50/50 via-white to-emerald-50/30" />
        <div className="absolute top-0 right-0 w-96 h-96 bg-aurora-200/20 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2" />
        <div className="absolute bottom-0 left-0 w-72 h-72 bg-emerald-200/20 rounded-full blur-3xl translate-y-1/2 -translate-x-1/2" />

        <div className="section-container relative py-24 sm:py-32 lg:py-40">
          <div className="max-w-3xl mx-auto text-center">
            {/* Accent line */}
            <div className="flex justify-center mb-8">
              <div className="h-1 w-16 rounded-full bg-gradient-to-r from-aurora-500 to-emerald-400" />
            </div>

            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-bold text-slate-900 leading-tight mb-6">
              {t('home.heroTitle')}
            </h1>

            <p className="text-lg sm:text-xl text-slate-500 leading-relaxed mb-10 max-w-2xl mx-auto font-light">
              {t('home.heroSubtitle')}
            </p>

            <Link to="/books" className="btn-primary text-base px-8 py-4">
              {t('home.heroCta')}
              <ArrowRight size={18} />
            </Link>
          </div>
        </div>
      </section>

      {/* Featured Books */}
      <section className="py-20 sm:py-24">
        <div className="section-container">
          <div className="flex items-center justify-between mb-12">
            <h2 className="text-2xl sm:text-3xl font-bold text-slate-900">
              {t('home.featuredTitle')}
            </h2>
            <Link to="/books" className="text-sm font-medium text-aurora-700 hover:text-aurora-800 transition-colors flex items-center gap-1">
              {t('nav.books')} <ArrowRight size={14} />
            </Link>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
            {featuredBooks.map(book => (
              <BookCard key={book.id} book={book} />
            ))}
          </div>
        </div>
      </section>

      {/* Why Aurora */}
      <section className="py-20 sm:py-24 bg-slate-50/50">
        <div className="section-container">
          <div className="text-center mb-16">
            <h2 className="text-2xl sm:text-3xl font-bold text-slate-900 mb-4">
              {t('home.whyTitle')}
            </h2>
            <div className="flex justify-center">
              <div className="h-1 w-12 rounded-full bg-gradient-to-r from-aurora-500 to-emerald-400" />
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {/* Curated */}
            <div className="card p-8 text-center group">
              <div className="w-14 h-14 rounded-2xl bg-aurora-50 flex items-center justify-center mx-auto mb-5 group-hover:bg-aurora-100 transition-colors duration-300">
                <BookOpen className="text-aurora-600" size={24} />
              </div>
              <h3 className="text-lg font-semibold text-slate-900 mb-2">
                {t('home.whyCurated')}
              </h3>
              <p className="text-sm text-slate-500 leading-relaxed">
                {t('home.whyCuratedDesc')}
              </p>
            </div>

            {/* Community */}
            <div className="card p-8 text-center group">
              <div className="w-14 h-14 rounded-2xl bg-emerald-50 flex items-center justify-center mx-auto mb-5 group-hover:bg-emerald-100 transition-colors duration-300">
                <Users className="text-emerald-600" size={24} />
              </div>
              <h3 className="text-lg font-semibold text-slate-900 mb-2">
                {t('home.whyCommunity')}
              </h3>
              <p className="text-sm text-slate-500 leading-relaxed">
                {t('home.whyCommunityDesc')}
              </p>
            </div>

            {/* AI */}
            <div className="card p-8 text-center group">
              <div className="w-14 h-14 rounded-2xl bg-purple-50 flex items-center justify-center mx-auto mb-5 group-hover:bg-purple-100 transition-colors duration-300">
                <Sparkles className="text-purple-600" size={24} />
              </div>
              <h3 className="text-lg font-semibold text-slate-900 mb-2">
                {t('home.whyAi')}
              </h3>
              <p className="text-sm text-slate-500 leading-relaxed">
                {t('home.whyAiDesc')}
              </p>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
