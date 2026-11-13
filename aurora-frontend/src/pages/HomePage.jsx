import { useState, useEffect, useMemo, useRef } from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight, Search, Sparkles, BookOpen, Users, X } from 'lucide-react';
import gsap from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { useLanguage } from '../i18n/LanguageContext';
import { getBooks, getCategories, getGenres } from '../services/catalog';
import BookCard from '../components/ui/BookCard';

gsap.registerPlugin(ScrollTrigger);

const BOREAL_PROMPTS = [
  {
    code: '01',
    prompt: 'Suggest a philosophical dystopia similar to Brave New World.',
    reply: 'Boreal matches vector context from ChromaDB: 1984, Fahrenheit 451, and Neuromancer.',
  },
  {
    code: '02',
    prompt: 'I need a thoughtful literary gift under R$ 70.',
    reply: 'Curated in-stock editions filtered by price, genre, and reader mood.',
  },
  {
    code: '03',
    prompt: 'Which classic pairs best with rainy-evening reading?',
    reply: 'Instant semantic recommendations grounded in our live catalog.',
  },
];

export default function HomePage() {
  const { t } = useLanguage();
  const pageRef = useRef(null);
  const catalogRef = useRef(null);
  const borealSectionRef = useRef(null);
  const borealPinRef = useRef(null);

  const [books, setBooks] = useState([]);
  const [categories, setCategories] = useState([]);
  const [genres, setGenres] = useState([]);
  const [loadingBooks, setLoadingBooks] = useState(true);
  const [apiError, setApiError] = useState(false);

  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [selectedGenre, setSelectedGenre] = useState(null);
  const [catalogInView, setCatalogInView] = useState(false);

  useEffect(() => {
    let cancelled = false;
    Promise.all([getBooks(), getCategories(), getGenres()])
      .then(([booksData, categoriesData, genresData]) => {
        if (cancelled) return;
        setBooks(booksData || []);
        setCategories(categoriesData || []);
        setGenres(genresData || []);
      })
      .catch(() => {
        if (!cancelled) setApiError(true);
      })
      .finally(() => {
        if (!cancelled) setLoadingBooks(false);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (prefersReducedMotion) return;

    const ctx = gsap.context(() => {
      gsap.from('.hero-reveal', {
        y: 28,
        opacity: 0,
        duration: 0.85,
        stagger: 0.1,
        ease: 'power3.out',
      });

      gsap.from('.pillar-row', {
        y: 24,
        opacity: 0,
        duration: 0.65,
        stagger: 0.12,
        ease: 'power2.out',
        scrollTrigger: {
          trigger: '.pillars-list',
          start: 'top 80%',
          once: true,
        },
      });

      if (catalogRef.current) {
        ScrollTrigger.create({
          trigger: catalogRef.current,
          start: 'top top+=120',
          end: 'bottom top+=120',
          onEnter: () => {
            setCatalogInView(true);
            window.dispatchEvent(new CustomEvent('aurora:catalog-search', { detail: { active: true } }));
          },
          onLeave: () => {
            setCatalogInView(false);
            window.dispatchEvent(new CustomEvent('aurora:catalog-search', { detail: { active: false } }));
          },
          onEnterBack: () => {
            setCatalogInView(true);
            window.dispatchEvent(new CustomEvent('aurora:catalog-search', { detail: { active: true } }));
          },
          onLeaveBack: () => {
            setCatalogInView(false);
            window.dispatchEvent(new CustomEvent('aurora:catalog-search', { detail: { active: false } }));
          },
        });
      }
    }, pageRef);

    return () => {
      window.dispatchEvent(new CustomEvent('aurora:catalog-search', { detail: { active: false } }));
      ctx.revert();
    };
  }, []);

  const filteredBooks = useMemo(() => {
    return books.filter((book) => {
      if (selectedCategory && !book.categories?.some((c) => c.id === selectedCategory)) return false;
      if (selectedGenre && !book.genres?.some((g) => g.id === selectedGenre)) return false;
      if (searchQuery.trim()) {
        const q = searchQuery.toLowerCase();
        const inTitle = book.title?.toLowerCase().includes(q);
        const inAuthor = book.authors?.some((a) => a.name.toLowerCase().includes(q));
        const inGenre = book.genres?.some((g) => g.name.toLowerCase().includes(q));
        if (!inTitle && !inAuthor && !inGenre) return false;
      }
      return true;
    });
  }, [books, selectedCategory, selectedGenre, searchQuery]);

  const hasFilters = Boolean(searchQuery.trim() || selectedCategory || selectedGenre);

  const scrollToCatalog = () => {
    catalogRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  return (
    <div ref={pageRef} className="overflow-x-clip">
      {/* Section 1: Obsidian Hero Stage */}
      <section data-surface="dark" className="relative bg-obsidian text-paper border-b border-paper/10">
        <div
          className="pointer-events-none absolute inset-0"
          style={{
            background: 'radial-gradient(55% 50% at 75% 35%, rgba(107, 33, 168, 0.22), transparent 70%)',
          }}
          aria-hidden="true"
        />

        <div className="section-container relative py-20 sm:py-28 lg:py-32">
          <div className="max-w-5xl space-y-8">
            <h1 className="hero-reveal font-sans font-medium text-paper text-[clamp(2.75rem,6.4vw,5.75rem)] leading-[0.94] tracking-[-0.045em]">
              {t('home.heroTitle')}
            </h1>

            <p className="hero-reveal text-base sm:text-lg text-paper/70 leading-relaxed max-w-[60ch]">
              {t('home.heroSubtitle')}
            </p>

            {/* Instant Search + CTA */}
            <div className="hero-reveal flex flex-col sm:flex-row items-stretch sm:items-center gap-3 pt-2 max-w-2xl">
              <div className="relative flex-1">
                <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-paper/45" size={17} />
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter') scrollToCatalog();
                  }}
                  placeholder={t('nav.searchPlaceholder')}
                  className="w-full pl-11 pr-4 py-3.5 rounded-full bg-white/5 border border-paper/20 text-sm text-paper placeholder-paper/45 focus:outline-none focus:bg-white/10 focus:border-gold"
                />
              </div>
              <button
                type="button"
                onClick={scrollToCatalog}
                className="px-7 py-3.5 rounded-full bg-aurora-700 hover:bg-aurora-600 text-paper text-sm font-semibold inline-flex items-center justify-center gap-2 transition-colors"
              >
                <span>{t('home.heroCta')}</span>
                <ArrowRight size={16} />
              </button>
            </div>
          </div>
        </div>
      </section>

      {/* Section 2: Immediate Books Catalog (Warm Ivory) */}
      <section ref={catalogRef} id="catalog" data-surface="light" className="bg-paper text-ink py-16 sm:py-24 border-b border-paper-border">
        <div className="section-container">
          <div className="flex flex-col md:flex-row md:items-end justify-between gap-6 pb-8 mb-10 border-b border-paper-border">
            <div>
              <h2 className="font-sans text-3xl sm:text-5xl font-medium tracking-[-0.035em] text-ink">
                {t('books.title')}
              </h2>
              <p className="serif-italic text-xl text-ink-muted mt-1">
                {t('books.subtitle')}
              </p>
            </div>

            {/* Filters + Jumping Search Bar */}
            <div className="flex flex-wrap items-center gap-3">
              <div
                className={`relative transition-all duration-300 ease-out ${
                  catalogInView
                    ? 'opacity-100 translate-y-0 scale-100'
                    : 'opacity-70 -translate-y-2 scale-95'
                }`}
              >
                <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 text-ink-subtle" size={14} />
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder={t('nav.searchPlaceholder')}
                  className="pl-9 pr-4 py-2.5 rounded-full bg-white border border-paper-border text-xs text-ink placeholder-ink-subtle focus:outline-none focus:border-aurora-600 w-52 sm:w-64"
                />
              </div>

              <select
                value={selectedCategory || ''}
                onChange={(e) => setSelectedCategory(e.target.value ? Number(e.target.value) : null)}
                aria-label={t('books.allCategories')}
                className="px-4 py-2.5 rounded-full bg-white border border-paper-border text-xs font-medium text-ink focus:outline-none focus:border-aurora-600"
              >
                <option value="">{t('books.allCategories')}</option>
                {categories.map((cat) => (
                  <option key={cat.id} value={cat.id}>{cat.name}</option>
                ))}
              </select>

              <select
                value={selectedGenre || ''}
                onChange={(e) => setSelectedGenre(e.target.value ? Number(e.target.value) : null)}
                aria-label={t('books.allGenres')}
                className="px-4 py-2.5 rounded-full bg-white border border-paper-border text-xs font-medium text-ink focus:outline-none focus:border-aurora-600"
              >
                <option value="">{t('books.allGenres')}</option>
                {genres.map((genre) => (
                  <option key={genre.id} value={genre.id}>{genre.name}</option>
                ))}
              </select>

              {hasFilters && (
                <button
                  type="button"
                  onClick={() => {
                    setSearchQuery('');
                    setSelectedCategory(null);
                    setSelectedGenre(null);
                  }}
                  className="inline-flex items-center gap-1.5 px-4 py-2.5 rounded-full border border-paper-border bg-white text-xs font-medium text-ink-muted hover:text-ink transition-colors"
                >
                  <X size={14} />
                  <span>{t('books.clearFilters')}</span>
                </button>
              )}
            </div>
          </div>

          {apiError ? (
            <div className="rounded-2xl border border-paper-border bg-white p-10 text-center">
              <p className="text-sm text-ink-muted">
                Unable to load the catalog right now. Start the backend server to browse live inventory.
              </p>
            </div>
          ) : loadingBooks ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
              {[1, 2, 3].map((n) => (
                <div key={n} className="rounded-2xl border border-paper-border bg-white overflow-hidden animate-pulse">
                  <div className="aspect-[3/4] bg-paper-subtle" />
                  <div className="p-5 space-y-3">
                    <div className="h-4 w-24 bg-paper-subtle rounded" />
                    <div className="h-6 w-3/4 bg-paper-subtle rounded" />
                    <div className="h-4 w-1/2 bg-paper-subtle rounded" />
                  </div>
                </div>
              ))}
            </div>
          ) : filteredBooks.length === 0 ? (
            <div className="rounded-2xl border border-paper-border bg-white p-12 text-center space-y-4">
              <p className="text-base text-ink-muted">{t('books.noResults')}</p>
              {hasFilters && (
                <button
                  type="button"
                  onClick={() => {
                    setSearchQuery('');
                    setSelectedCategory(null);
                    setSelectedGenre(null);
                  }}
                  className="btn-secondary"
                >
                  {t('books.clearFilters')}
                </button>
              )}
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
              {filteredBooks.map((book) => (
                <BookCard key={book.id} book={book} />
              ))}
            </div>
          )}
        </div>
      </section>

      {/* Section 3: Boreal AI Showcase (Deep Purple Pinned Stage) */}
      <section ref={borealSectionRef} data-surface="dark" className="bg-deep-purple text-paper py-20 sm:py-28 border-b border-paper/10">
        <div className="section-container">
          <div className="grid grid-cols-1 md:grid-cols-12 gap-12 items-start">
            <div ref={borealPinRef} className="md:col-span-5 md:sticky md:top-28 space-y-6">
              <div className="inline-flex items-center gap-2 text-gold font-mono text-xs uppercase tracking-widest">
                <Sparkles size={14} />
                <span>Boreal AI</span>
              </div>
              <h2 className="font-sans text-3xl sm:text-5xl font-medium tracking-[-0.035em] leading-[1.02] text-paper">
                {t('home.whyAi')}
              </h2>
              <p className="text-base text-paper/70 leading-relaxed max-w-[44ch]">
                {t('home.whyAiDesc')}
              </p>
            </div>

            <div className="md:col-span-7 space-y-6">
              {BOREAL_PROMPTS.map((item) => (
                <div
                  key={item.code}
                  className="rounded-2xl border border-paper/15 bg-white/5 p-7 sm:p-9 space-y-4"
                >
                  <span className="font-mono text-xs text-gold tabular-nums">{item.code}</span>
                  <p className="serif-italic text-2xl sm:text-3xl text-paper leading-snug">
                    “{item.prompt}”
                  </p>
                  <p className="text-sm text-paper/65 leading-relaxed pt-2 border-t border-paper/10">
                    {item.reply}
                  </p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Section 4: Editorial Pillars (Warm Ivory) */}
      <section data-surface="light" className="bg-paper text-ink py-20 sm:py-28">
        <div className="section-container">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12">
            <div className="lg:col-span-4 space-y-5">
              <h2 className="font-sans text-3xl sm:text-5xl font-medium tracking-[-0.035em] text-ink leading-tight">
                {t('home.whyTitle')}
              </h2>
              <Link to="/authors" className="btn-secondary inline-flex">
                <span>{t('authors.title')}</span>
                <ArrowRight size={15} />
              </Link>
            </div>

            <div className="lg:col-span-8 pillars-list divide-y divide-paper-border border-t border-b border-paper-border">
              <div className="pillar-row py-8 flex items-start gap-6">
                <span className="font-mono text-xs text-aurora-700 pt-1 tabular-nums">01</span>
                <div className="w-10 h-10 rounded-xl bg-white border border-paper-border flex items-center justify-center flex-shrink-0">
                  <BookOpen className="text-aurora-700" size={18} />
                </div>
                <div>
                  <h3 className="text-xl font-medium text-ink mb-1.5">{t('home.whyCurated')}</h3>
                  <p className="text-sm text-ink-muted leading-relaxed max-w-[58ch]">{t('home.whyCuratedDesc')}</p>
                </div>
              </div>

              <div className="pillar-row py-8 flex items-start gap-6">
                <span className="font-mono text-xs text-aurora-700 pt-1 tabular-nums">02</span>
                <div className="w-10 h-10 rounded-xl bg-white border border-paper-border flex items-center justify-center flex-shrink-0">
                  <Users className="text-emerald-700" size={18} />
                </div>
                <div>
                  <h3 className="text-xl font-medium text-ink mb-1.5">{t('home.whyCommunity')}</h3>
                  <p className="text-sm text-ink-muted leading-relaxed max-w-[58ch]">{t('home.whyCommunityDesc')}</p>
                </div>
              </div>

              <div className="pillar-row py-8 flex items-start gap-6">
                <span className="font-mono text-xs text-aurora-700 pt-1 tabular-nums">03</span>
                <div className="w-10 h-10 rounded-xl bg-white border border-paper-border flex items-center justify-center flex-shrink-0">
                  <Sparkles className="text-aurora-700" size={18} />
                </div>
                <div>
                  <h3 className="text-xl font-medium text-ink mb-1.5">{t('home.whyAi')}</h3>
                  <p className="text-sm text-ink-muted leading-relaxed max-w-[58ch]">{t('home.whyAiDesc')}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
