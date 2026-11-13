import { useLanguage } from '../../i18n/LanguageContext';
import { useCart } from '../../context/CartContext';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { ShoppingBag } from 'lucide-react';
import { createOrder } from '../../services/order';
import toast from 'react-hot-toast';

export default function BookCard({ book }) {
  const { t } = useLanguage();
  const { addItem, toggleCart } = useCart();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const authorNames = book.authors.map(a => a.name).join(', ');

  const handleAddToCart = async (e) => {
    e.stopPropagation();
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    try {
      await addItem(book.id, 1);
      toast.success(t('cart.addBtn'));
      toggleCart();
    } catch (error) {
      toast.error('Failed to add item to cart');
    }
  };

  const handleBuyNow = async (e) => {
    e.stopPropagation();
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    try {
      const dto = {
        orderItems: [
          {
            bookId: book.id,
            quantity: 1
          }
        ]
      };
      await createOrder(dto);
      toast.success('Purchase completed successfully!');
      navigate('/orders');
    } catch (error) {
      toast.error('Failed to complete purchase');
    }
  };

  return (
    <article className="card group overflow-hidden flex flex-col">
      <div className="relative aspect-[3/4] overflow-hidden bg-paper-subtle border-b border-paper-border">
        <img
          src={book.imageUrl}
          alt={book.title}
          className="w-full h-full object-cover transition-transform duration-500 ease-out group-hover:scale-[1.03]"
          loading="lazy"
          onError={(e) => {
            e.target.src = 'https://images.unsplash.com/photo-1543002588-bfa74002ed7e?q=80&w=400';
          }}
        />
        <div className="absolute top-3 right-3">
          <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-[11px] font-medium ${
            book.stock > 0
              ? 'bg-white/95 text-emerald-800 border border-emerald-200'
              : 'bg-white/95 text-red-700 border border-red-200'
          }`}>
            {book.stock > 0 ? t('books.inStock') : t('books.outOfStock')}
          </span>
        </div>
      </div>

      <div className="p-5 flex-1 flex flex-col justify-between">
        <div>
          <div className="flex flex-wrap gap-1.5 mb-2.5">
            {book.genres.map(genre => (
              <span
                key={genre.id}
                className="inline-block px-2.5 py-0.5 text-[11px] font-medium text-aurora-800 bg-aurora-50 border border-aurora-100 rounded-full"
              >
                {genre.name}
              </span>
            ))}
          </div>

          <h3 className="font-display text-xl font-medium text-ink leading-snug mb-1 line-clamp-2 group-hover:text-aurora-700 transition-colors duration-200">
            {book.title}
          </h3>

          <p className="text-sm text-ink-muted mb-4">{authorNames}</p>
        </div>

        <div className="flex items-center justify-between pt-3 border-t border-paper-border gap-2">
          <p className="font-mono text-base font-medium text-ink tabular-nums">
            R$ {book.price.toFixed(2).replace('.', ',')}
          </p>
          <div className="flex items-center gap-1.5">
            <button
              onClick={handleAddToCart}
              disabled={book.stock <= 0}
              className="p-2 bg-paper-subtle hover:bg-aurora-50 text-aurora-700 border border-paper-border hover:border-aurora-200 disabled:opacity-40 disabled:cursor-not-allowed rounded-full transition-colors duration-200"
              title={t('cart.addBtn')}
              aria-label={t('cart.addBtn')}
            >
              <ShoppingBag size={16} />
            </button>
            <button
              onClick={handleBuyNow}
              disabled={book.stock <= 0}
              className="px-4 py-2 text-xs font-semibold text-white bg-aurora-700 hover:bg-aurora-800 disabled:bg-paper-subtle disabled:text-ink-subtle rounded-full transition-colors duration-200"
            >
              {t('cart.buyNow')}
            </button>
          </div>
        </div>
      </div>
    </article>
  );
}
