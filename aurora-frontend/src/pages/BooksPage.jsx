import { useState, useMemo, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { X } from 'lucide-react';
import { useLanguage } from '../i18n/LanguageContext';
import { getBooks, getCategories, getGenres, getAuthors } from '../services/catalog';
import BookCard from '../components/ui/BookCard';

export default function BooksPage() {
  const { t } = useLanguage();
  const [books, setBooks] = useState([]);
  const [categories, setCategories] = useState([]);
  const [genres, setGenres] = useState([]);
  const [authors, setAuthors] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [selectedGenre, setSelectedGenre] = useState(null);
  const [selectedAuthor, setSelectedAuthor] = useState(null);
  const [searchParams, setSearchParams] = useSearchParams();
  const searchQuery = searchParams.get('search') || '';

  useEffect(() => {
    Promise.all([getBooks(), getCategories(), getGenres(), getAuthors()])
      .then(([booksData, categoriesData, genresData, authorsData]) => {
        if (booksData) setBooks(booksData);
        if (categoriesData) setCategories(categoriesData);
        if (genresData) setGenres(genresData);
        if (authorsData) setAuthors(authorsData);
      })
      .catch(error => console.error(error));
  }, []);

  const hasFilters = selectedCategory || selectedGenre || selectedAuthor || searchQuery;

  const filteredBooks = useMemo(() => {
    return books.filter(book => {
      if (selectedCategory && !book.categories.some(c => c.id === selectedCategory)) return false;
      if (selectedGenre && !book.genres.some(g => g.id === selectedGenre)) return false;
      if (selectedAuthor && !book.authors.some(a => a.id === selectedAuthor)) return false;

      if (searchQuery) {
        const query = searchQuery.toLowerCase();
        const matchesTitle = book.title.toLowerCase().includes(query);
        const matchesDesc = book.description?.toLowerCase().includes(query);
        const matchesAuthor = book.authors.some(a => a.name.toLowerCase().includes(query));
        const matchesCategory = book.categories.some(c => c.name.toLowerCase().includes(query));
        const matchesGenre = book.genres.some(g => g.name.toLowerCase().includes(query));

        if (!matchesTitle && !matchesDesc && !matchesAuthor && !matchesCategory && !matchesGenre) return false;
      }
      return true;
    });
  }, [books, selectedCategory, selectedGenre, selectedAuthor, searchQuery]);

  const clearFilters = () => {
    setSelectedCategory(null);
    setSelectedGenre(null);
    setSelectedAuthor(null);
    setSearchParams({});
  };

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
        <div className="flex flex-col sm:flex-row items-center gap-4 flex-wrap bg-slate-50 p-6 rounded-2xl border border-slate-200/60 mb-10">
          <div className="flex flex-col w-full sm:w-auto min-w-[180px]">
            <label className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">Category</label>
            <select
              value={selectedCategory || ''}
              onChange={(e) => setSelectedCategory(e.target.value ? parseInt(e.target.value) : null)}
              className="w-full px-4 py-2.5 rounded-xl bg-white border border-slate-200 text-sm text-slate-700 focus:outline-none focus:border-aurora-500 focus:ring-2 focus:ring-aurora-500/20"
            >
              <option value="">{t('books.allCategories')}</option>
              {categories.map(cat => (
                <option key={cat.id} value={cat.id}>{cat.name}</option>
              ))}
            </select>
          </div>

          <div className="flex flex-col w-full sm:w-auto min-w-[180px]">
            <label className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">Genre</label>
            <select
              value={selectedGenre || ''}
              onChange={(e) => setSelectedGenre(e.target.value ? parseInt(e.target.value) : null)}
              className="w-full px-4 py-2.5 rounded-xl bg-white border border-slate-200 text-sm text-slate-700 focus:outline-none focus:border-aurora-500 focus:ring-2 focus:ring-aurora-500/20"
            >
              <option value="">{t('books.allGenres')}</option>
              {genres.map(genre => (
                <option key={genre.id} value={genre.id}>{genre.name}</option>
              ))}
            </select>
          </div>

          <div className="flex flex-col w-full sm:w-auto min-w-[180px]">
            <label className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2">Author</label>
            <select
              value={selectedAuthor || ''}
              onChange={(e) => setSelectedAuthor(e.target.value ? parseInt(e.target.value) : null)}
              className="w-full px-4 py-2.5 rounded-xl bg-white border border-slate-200 text-sm text-slate-700 focus:outline-none focus:border-aurora-500 focus:ring-2 focus:ring-aurora-500/20"
            >
              <option value="">{t('books.allAuthors')}</option>
              {authors.map(author => (
                <option key={author.id} value={author.id}>{author.name}</option>
              ))}
            </select>
          </div>

          {hasFilters && (
            <div className="flex items-end h-full sm:mt-6">
              <button
                onClick={clearFilters}
                className="flex items-center gap-1.5 px-4 py-2.5 text-sm font-medium text-red-600 hover:text-red-700 bg-red-50 hover:bg-red-100 rounded-xl transition-all duration-200"
              >
                <X size={16} />
                {t('books.clearFilters')}
              </button>
            </div>
          )}
        </div>

        {searchQuery && (
          <div className="mb-6 flex items-center justify-between bg-slate-50 border border-slate-200/60 rounded-2xl px-5 py-3">
            <span className="text-sm text-slate-600">
              Showing results for: <span className="font-semibold text-slate-800">"{searchQuery}"</span>
            </span>
            <button
              onClick={() => setSearchParams({})}
              className="text-xs text-slate-400 hover:text-red-500 flex items-center gap-1 transition-colors"
            >
              <X size={14} /> Clear search
            </button>
          </div>
        )}

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
