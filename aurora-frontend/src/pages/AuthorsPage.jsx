import { useState } from 'react';
import { ChevronDown, ChevronUp, BookOpen } from 'lucide-react';
import { useLanguage } from '../i18n/LanguageContext';
import { authors, getBooksByAuthor } from '../data/mockData';

export default function AuthorsPage() {
  const { t } = useLanguage();
  const [expandedAuthor, setExpandedAuthor] = useState(null);

  const toggleAuthor = (authorId) => {
    setExpandedAuthor(prev => (prev === authorId ? null : authorId));
  };

  // Color palette for author avatar backgrounds
  const avatarColors = [
    'from-aurora-500 to-aurora-700',
    'from-emerald-500 to-teal-700',
    'from-indigo-500 to-purple-700',
    'from-amber-500 to-orange-700',
    'from-rose-500 to-pink-700',
  ];

  return (
    <div className="page-enter">
      <div className="section-container py-12 sm:py-16">

        {/* Page Header */}
        <div className="mb-12">
          <h1 className="text-3xl sm:text-4xl font-bold text-slate-900 mb-3">
            {t('authors.title')}
          </h1>
          <p className="text-lg text-slate-500 font-light">
            {t('authors.subtitle')}
          </p>
        </div>

        {/* Author Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {authors.map((author, index) => {
            const authorBooks = getBooksByAuthor(author.id);
            const isExpanded = expandedAuthor === author.id;

            return (
              <div key={author.id} className="card overflow-hidden">
                {/* Author Card */}
                <button
                  onClick={() => toggleAuthor(author.id)}
                  className="w-full p-6 flex items-center gap-4 text-left hover:bg-slate-50/50 transition-colors duration-200"
                >
                  {/* Avatar */}
                  <div className={`w-14 h-14 rounded-2xl bg-gradient-to-br ${avatarColors[index % avatarColors.length]} flex items-center justify-center flex-shrink-0 shadow-sm`}>
                    <span className="text-white text-lg font-bold">
                      {author.name.charAt(0)}
                    </span>
                  </div>

                  {/* Info */}
                  <div className="flex-1 min-w-0">
                    <h3 className="text-base font-semibold text-slate-900 truncate">
                      {author.name}
                    </h3>
                    <p className="text-sm text-slate-400">
                      {authorBooks.length} {t('authors.booksCount')}
                    </p>
                  </div>

                  {/* Expand Icon */}
                  <div className="text-slate-400">
                    {isExpanded ? <ChevronUp size={18} /> : <ChevronDown size={18} />}
                  </div>
                </button>

                {/* Expanded Book List */}
                {isExpanded && (
                  <div className="border-t border-slate-100 bg-slate-50/50 animate-fade-in">
                    {authorBooks.map(book => (
                      <div
                        key={book.id}
                        className="flex items-center gap-3 px-6 py-3 hover:bg-white transition-colors"
                      >
                        <div className="w-10 h-14 rounded-lg overflow-hidden flex-shrink-0 shadow-sm">
                          <img
                            src={book.imageUrl}
                            alt={book.title}
                            className="w-full h-full object-cover"
                          />
                        </div>
                        <div className="flex-1 min-w-0">
                          <p className="text-sm font-medium text-slate-800 truncate">
                            {book.title}
                          </p>
                          <p className="text-xs text-aurora-600 font-semibold">
                            R$ {book.price.toFixed(2).replace('.', ',')}
                          </p>
                        </div>
                        <BookOpen size={14} className="text-slate-300 flex-shrink-0" />
                      </div>
                    ))}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
