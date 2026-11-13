import { useState, useEffect, useMemo, useRef } from 'react';
import { Link } from 'react-router-dom';
import { ArrowRight, Search, Sparkles, BookOpen, Users, X } from 'lucide-react';
import gsap from 'gsap';
import { ScrollTrigger } from 'gsap/ScrollTrigger';
import { useLanguage } from '../i18n/LanguageContext';
import { getBooks, getCategories, getGenres } from '../services/catalog';
import BookCard from '../components/ui/BookCard';
import borealAiBg from '../assets/boreal-ai-bg.jpeg';

gsap.registerPlugin(ScrollTrigger);

const BOREAL_PROMPTS = [
  {
    code: '01',
    prompt: 'Suggest a philosophical dystopia similar to Brave New World.',
    reply: 'Boreal suggests 1984, Fahrenheit 451, and Neuromancer based on shared themes and mood.',
  },
  {
    code: '02',
    prompt: 'I need a thoughtful literary gift under R$ 70.',
    reply: 'Curated in-stock editions matched to your budget, genre, and reading mood.',
  },
  {
    code: '03',
    prompt: 'Which classic pairs best with rainy-evening reading?',
    reply: 'Personalized book pairings chosen from our live bookstore collection.',
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
  const [showAllBooks, setShowAllBooks] = useState(false);

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

    // rAF-gated Hero -> Catalog curtain exit choreography (matches portfolio Layout.astro)
    let scrollTicking = false;
    const clamp = (v, min, max) => Math.max(min, Math.min(max, v));
    const smoothstep = (t) => t * t * (3 - 2 * t);

    const updateHeroCurtain = () => {
      scrollTicking = false;
      const cat = catalogRef.current;
      const root = pageRef.current;
      if (!cat || !root) return;

      const vh = window.innerHeight || 1;
      const riseRaw = clamp(1 - cat.getBoundingClientRect().top / vh, 0, 1);

      const shade = root.querySelector('#hero-exit-shade');
      if (shade) {
        shade.style.opacity = (smoothstep(riseRaw) * 0.82).toFixed(3);
      }

      root.querySelectorAll('[data-hero-el]').forEach((el, idx) => {
        const order = Number(el.getAttribute('data-hero-el') || idx);
        const stagger = order * 0.065;
        const outRaw = clamp((riseRaw - stagger) / Math.max(0.82 - stagger, 0.25), 0, 1);
        const out = smoothstep(outRaw);

        if (out <= 0.001) {
          el.style.opacity = '';
          el.style.transform = '';
        } else {
          const driftY = -out * (36 + order * 10);
          const sc = 1 - out * 0.08;
          const op = clamp(1 - out * 1.08, 0, 1);
          el.style.opacity = op.toFixed(3);
          el.style.transform = `translate3d(0, ${driftY.toFixed(1)}px, 0) scale(${sc.toFixed(4)})`;
        }
      });
    };

    const onScroll = () => {
      if (!scrollTicking) {
        scrollTicking = true;
        window.requestAnimationFrame(updateHeroCurtain);
      }
    };

    window.addEventListener('scroll', onScroll, { passive: true });
    window.addEventListener('resize', onScroll, { passive: true });
    updateHeroCurtain();

    return () => {
      window.removeEventListener('scroll', onScroll);
      window.removeEventListener('resize', onScroll);
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
      {/* Section 1: Editorial Bookstore Hero Stage */}
      <section data-surface="dark" className="sticky top-0 z-10 min-h-[84vh] flex items-center bg-obsidian text-paper border-b border-paper/10 overflow-hidden">
        <img
          src="https://images.unsplash.com/photo-1507842217343-583bb7270b66?auto=format&fit=crop&w=2000&q=80"
          alt="Warm atmospheric bookstore shelves"
          className="pointer-events-none absolute inset-0 w-full h-full object-cover object-center scale-105"
        />
        <div
          className="pointer-events-none absolute inset-0"
          style={{
            background:
              'linear-gradient(90deg, rgba(12, 10, 15, 0.92) 0%, rgba(12, 10, 15, 0.72) 48%, rgba(12, 10, 15, 0.34) 100%)',
          }}
          aria-hidden="true"
        />

        <div
          id="hero-exit-shade"
          className="pointer-events-none absolute inset-0 bg-obsidian opacity-0"
          aria-hidden="true"
        />

        <div className="section-container relative w-full py-24 sm:py-28 lg:py-32">
          <div className="max-w-2xl space-y-6">
            <p data-hero-el="0" className="hero-reveal font-mono text-[11px] uppercase tracking-[0.26em] text-gold/90">
              Curated Literature · AI Discovery
            </p>

            <h1 data-hero-el="1" className="hero-reveal font-serif font-normal text-paper text-[clamp(3rem,6vw,5.25rem)] leading-[1.02] tracking-[-0.02em]">
              {t('home.heroTitle')}
            </h1>

            <p data-hero-el="2" className="hero-reveal font-serif text-lg sm:text-xl text-paper/85 leading-relaxed max-w-[46ch]">
              {t('home.heroSubtitle')}
            </p>

            {/* Instant Search + CTA */}
            <div data-hero-el="3" className="hero-reveal flex flex-col sm:flex-row items-stretch sm:items-center gap-3 pt-3 max-w-xl">
              <div className="relative flex-1">
                <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-paper/55" size={17} />
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter') scrollToCatalog();
                  }}
                  placeholder={t('nav.searchPlaceholder')}
                  className="w-full pl-11 pr-4 py-3.5 rounded-md bg-obsidian/70 backdrop-blur-sm border border-paper/25 text-sm text-paper placeholder-paper/50 focus:outline-none focus:bg-obsidian/90 focus:border-gold"
                />
              </div>
              <button
                type="button"
                onClick={scrollToCatalog}
                className="px-7 py-3.5 rounded-md bg-aurora-700 hover:bg-aurora-600 text-paper text-sm font-semibold inline-flex items-center justify-center gap-2 transition-colors shadow-lg"
              >
                <span>{t('home.heroCta')}</span>
                <ArrowRight size={16} />
              </button>
            </div>
          </div>
        </div>

        <div data-hero-el="4" className="scroll-cue hidden sm:flex" aria-hidden="true">
          <span>Scroll</span>
          <i />
        </div>
      </section>

      {/* Section 2: Immediate Books Catalog (Warm Ivory) */}
      <section ref={catalogRef} id="catalog" data-surface="light" className="relative z-20 rounded-t-[2rem] shadow-[0_-24px_60px_rgba(0,0,0,0.28)] bg-paper text-ink py-16 sm:py-24 border-b border-paper-border">
        <div className="section-container">
          <div className="flex flex-col md:flex-row md:items-end justify-between gap-6 pb-8 mb-10 border-b border-paper-border">
            <div>
              <h2 className="font-serif font-normal text-4xl sm:text-5xl tracking-[-0.02em] text-ink">
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
                  className="pl-10 pr-4 py-3 rounded-full bg-white border border-paper-border text-sm text-ink placeholder-ink-subtle focus:outline-none focus:border-aurora-600 w-full sm:w-[28rem] lg:w-[36rem]"
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
            <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
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
            <>
              <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
                {(showAllBooks ? filteredBooks : filteredBooks.slice(0, 10)).map((book) => (
                  <BookCard key={book.id} book={book} />
                ))}
              </div>

              {filteredBooks.length > 10 && !showAllBooks && (
                <div className="mt-10 flex justify-center">
                  <button
                    type="button"
                    onClick={() => setShowAllBooks(true)}
                    className="px-7 py-3 rounded-full bg-aurora-700 hover:bg-aurora-600 text-paper text-sm font-semibold inline-flex items-center gap-2 transition-colors shadow-sm"
                  >
                    <span>Show More</span>
                    <ArrowRight size={16} />
                  </button>
                </div>
              )}
            </>
          )}
        </div>
      </section>

      {/* Section 3: Boreal AI Showcase (Deep Purple Editorial Stage) */}
      <section ref={borealSectionRef} data-surface="dark" className="relative z-20 bg-deep-purple text-paper py-20 sm:py-28 border-b border-paper/10">
        <div className="pointer-events-none absolute inset-0 overflow-hidden" aria-hidden="true">
          <img
            src={borealAiBg}
            alt="Boreal AI neural light portrait"
            className="w-full h-full object-cover object-center opacity-90"
          />
          <div
            className="absolute inset-0"
            style={{
              background:
                'linear-gradient(90deg, rgba(36, 18, 58, 0.38) 0%, rgba(36, 18, 58, 0.22) 50%, rgba(24, 12, 40, 0.30) 100%)',
            }}
          />
        </div>
        <div className="section-container relative">
          <div className="ml-auto max-w-xl space-y-8">
            <div ref={borealPinRef} className="space-y-4">
              <div className="inline-flex items-center gap-2 text-gold font-mono text-xs uppercase tracking-widest">
                <Sparkles size={14} />
                <span>Boreal AI</span>
              </div>
              <h2 className="font-serif font-normal text-5xl sm:text-6xl lg:text-7xl tracking-[-0.02em] leading-[1.0] text-paper">
                {t('home.whyAi')}
              </h2>
              <p className="text-base text-paper/70 leading-relaxed max-w-[44ch]">
                {t('home.whyAiDesc')}
              </p>
            </div>

            <div className="space-y-3">
              {BOREAL_PROMPTS.map((item, idx) => (
                <div
                  key={item.code}
                  data-reveal="right"
                  style={{ '--reveal-delay': `${idx * 90}ms` }}
                  className="rounded-xl border border-paper/15 bg-white/5 p-4 sm:p-5 space-y-2.5"
                >
                  <span className="font-mono text-[11px] text-gold tabular-nums">{item.code}</span>
                  <p className="serif-italic text-lg sm:text-xl text-paper leading-snug">
                    “{item.prompt}”
                  </p>
                  <p className="text-xs text-paper/65 leading-relaxed pt-2 border-t border-paper/10">
                    {item.reply}
                  </p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Section 4: Editorial Pillars (Warm Ivory) */}
      <section data-surface="light" className="relative z-20 bg-paper text-ink py-20 sm:py-28">
        <div className="section-container">
          <div data-reveal="up" className="text-center max-w-xl mx-auto mb-14 space-y-4">
            <h2 className="font-serif font-normal text-4xl sm:text-5xl tracking-[-0.02em] text-ink leading-tight">
              {t('home.whyTitle')}
            </h2>
            <div>
              <Link to="/authors" className="line-link text-sm font-medium text-ink">
                <span>{t('authors.title')}</span>
                <ArrowRight size={15} />
              </Link>
            </div>
          </div>

          <div className="pillars-list grid grid-cols-1 md:grid-cols-3 gap-8 pt-10 border-t border-paper-border">
            <div data-reveal="scale" style={{ '--reveal-delay': '0.06s' }} className="pillar-row flex flex-col items-start gap-4">
              <div className="flex items-center gap-3">
                <span className="font-mono text-xs text-aurora-700 tabular-nums">01</span>
                <div className="w-10 h-10 rounded-xl bg-white border border-paper-border flex items-center justify-center flex-shrink-0">
                  <BookOpen className="text-aurora-700" size={18} />
                </div>
              </div>
              <div>
                <h3 className="text-xl font-medium text-ink mb-1.5">{t('home.whyCurated')}</h3>
                <p className="text-sm text-ink-muted leading-relaxed">{t('home.whyCuratedDesc')}</p>
              </div>
            </div>

            <div data-reveal="scale" style={{ '--reveal-delay': '0.14s' }} className="pillar-row flex flex-col items-start gap-4">
              <div className="flex items-center gap-3">
                <span className="font-mono text-xs text-aurora-700 tabular-nums">02</span>
                <div className="w-10 h-10 rounded-xl bg-white border border-paper-border flex items-center justify-center flex-shrink-0">
                  <Users className="text-emerald-700" size={18} />
                </div>
              </div>
              <div>
                <h3 className="text-xl font-medium text-ink mb-1.5">{t('home.whyCommunity')}</h3>
                <p className="text-sm text-ink-muted leading-relaxed">{t('home.whyCommunityDesc')}</p>
              </div>
            </div>

            <div data-reveal="scale" style={{ '--reveal-delay': '0.22s' }} className="pillar-row flex flex-col items-start gap-4">
              <div className="flex items-center gap-3">
                <span className="font-mono text-xs text-aurora-700 tabular-nums">03</span>
                <div className="w-10 h-10 rounded-xl bg-white border border-paper-border flex items-center justify-center flex-shrink-0">
                  <Sparkles className="text-aurora-700" size={18} />
                </div>
              </div>
              <div>
                <h3 className="text-xl font-medium text-ink mb-1.5">{t('home.whyAi')}</h3>
                <p className="text-sm text-ink-muted leading-relaxed">{t('home.whyAiDesc')}</p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
