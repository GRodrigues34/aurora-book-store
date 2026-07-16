import { useState, useMemo } from 'react';
import { Filter, X } from 'lucide-react';
import { useLanguage } from '../i18n/LanguageContext';
import { books, categories, genres, authors } from '../data/mockData';
import BookCard from '../components/ui/BookCard';

export default function BooksPage() {
  const { t } = useLanguage();
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [selectedGenre, setSelectedGenre] = useState(null);
  const [selectedAuthor, setSelectedAuthor] = useState(null);

  const hasFilters = selectedCategory || selectedGenre || selectedAuthor;

  const filteredBooks = useMemo(() => {
    return books.filter(book => {
      if (selectedCategory && !book.categories.some(c => c.id === selectedCategory)) return false;
      if (selectedGenre && !book.genres.some(g => g.id === selectedGenre)) return false;
      if (selectedAuthor && !book.authors.some(a => a.id === selectedAuthor)) return false;
      return true;
    });
  }, [selectedCategory, selectedGenre, selectedAuthor]);

  const clearFilters = () => {
    setSelectedCategory(null);
    setSelectedGenre(null);
    setSelectedAuthor(null);
  };

  const FilterPill = ({ label, value, selected, onSelect }) => (
    <button
      onClick={() => onSelect(selected ? null : value)}
      className={`inline-flex items-center gap-1 px-3.5 py-1.5 rounded-full text-sm font-medium transition-all duration-200 ${
        selected
          ? 'bg-aurora-700 text-white shadow-sm'
          : 'bg-white text-slate-600 border border-slate-200 hover:border-aurora-300 hover:text-aurora-700'
      }`}
    >
      {label}
      {selected && <X size={12} />}
    </button>
  );

  return (
    <div className="page-enter">
      <div className="section-container py-12 sm:py-16">

        {/* Page Header */}
        <div className="mb-10">
          <h1 className="text-3xl sm:text-4xl font-bold text-slate-900 mb-3">
            {t('books.title')}
          </h1>
          <p className="text-lg text-slate-500 font-light">
            {t('books.subtitle')}
          </p>
        </div>

        {/* Filter Bar */}
        <div className="mb-10 space-y-4">
          {/* Categories */}
          <div className="flex items-center gap-3 flex-wrap">
            <div className="flex items-center gap-1.5 text-xs font-semibold text-slate-400 uppercase tracking-wider min-w-[90px]">
              <Filter size={14} />
              Category
            </div>
            <FilterPill
              label={t('books.allCategories')}
              value={null}
              selected={selectedCategory === null}
              onSelect={() => setSelectedCategory(null)}
            />
            {categories.map(cat => (
              <FilterPill
                key={cat.id}
                label={cat.name}
                value={cat.id}
                selected={selectedCategory === cat.id}
                onSelect={setSelectedCategory}
              />
            ))}
          </div>

          {/* Genres */}
          <div className="flex items-center gap-3 flex-wrap">
            <div className="flex items-center gap-1.5 text-xs font-semibold text-slate-400 uppercase tracking-wider min-w-[90px]">
              <Filter size={14} />
              Genre
            </div>
            <FilterPill
              label={t('books.allGenres')}
              value={null}
              selected={selectedGenre === null}
              onSelect={() => setSelectedGenre(null)}
            />
            {genres.map(genre => (
              <FilterPill
                key={genre.id}
                label={genre.name}
                value={genre.id}
                selected={selectedGenre === genre.id}
                onSelect={setSelectedGenre}
              />
            ))}
          </div>

          {/* Authors */}
          <div className="flex items-center gap-3 flex-wrap">
            <div className="flex items-center gap-1.5 text-xs font-semibold text-slate-400 uppercase tracking-wider min-w-[90px]">
              <Filter size={14} />
              Author
            </div>
            <FilterPill
              label={t('books.allAuthors')}
              value={null}
              selected={selectedAuthor === null}
              onSelect={() => setSelectedAuthor(null)}
            />
            {authors.map(author => (
              <FilterPill
                key={author.id}
                label={author.name}
                value={author.id}
                selected={selectedAuthor === author.id}
                onSelect={setSelectedAuthor}
              />
            ))}
          </div>

          {/* Clear All */}
          {hasFilters && (
            <button
              onClick={clearFilters}
              className="text-sm text-aurora-600 hover:text-aurora-800 font-medium transition-colors"
            >
              {t('books.clearFilters')}
            </button>
          )}
        </div>

        {/* Book Grid */}
        {filteredBooks.length > 0 ? (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
            {filteredBooks.map(book => (
              <BookCard key={book.id} book={book} />
            ))}
          </div>
        ) : (
          <div className="text-center py-20">
            <p className="text-lg text-slate-400 mb-4">{t('books.noResults')}</p>
            <button onClick={clearFilters} className="btn-secondary">
              {t('books.clearFilters')}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
