import { useLanguage } from '../../i18n/LanguageContext';

export default function BookCard({ book }) {
  const { t } = useLanguage();
  const authorNames = book.authors.map(a => a.name).join(', ');

  return (
    <div className="card group overflow-hidden cursor-pointer">
      {/* Cover Image */}
      <div className="relative aspect-[3/4] overflow-hidden bg-slate-100">
        <img
          src={book.imageUrl}
          alt={book.title}
          className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
          loading="lazy"
        />
        {/* Stock Badge */}
        <div className="absolute top-3 right-3">
          <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium ${
            book.stock > 0
              ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
              : 'bg-red-50 text-red-600 border border-red-200'
          }`}>
            {book.stock > 0 ? t('books.inStock') : t('books.outOfStock')}
          </span>
        </div>
      </div>

      {/* Info */}
      <div className="p-5">
        {/* Genre Tags */}
        <div className="flex flex-wrap gap-1.5 mb-3">
          {book.genres.map(genre => (
            <span
              key={genre.id}
              className="inline-block px-2 py-0.5 text-[11px] font-medium text-aurora-700 bg-aurora-50 rounded-full"
            >
              {genre.name}
            </span>
          ))}
        </div>

        {/* Title */}
        <h3 className="text-base font-semibold text-slate-900 leading-snug mb-1 line-clamp-2 group-hover:text-aurora-700 transition-colors duration-200">
          {book.title}
        </h3>

        {/* Author */}
        <p className="text-sm text-slate-500 mb-3">{authorNames}</p>

        {/* Price */}
        <p className="text-lg font-bold text-aurora-700">
          R$ {book.price.toFixed(2).replace('.', ',')}
        </p>
      </div>
    </div>
  );
}
