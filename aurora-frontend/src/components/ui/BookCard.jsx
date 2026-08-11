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
    <div className="card group overflow-hidden cursor-pointer">
      <div className="relative aspect-[3/4] overflow-hidden bg-slate-100">
        <img
          src={book.imageUrl}
          alt={book.title}
          className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
          loading="lazy"
          onError={(e) => {
            e.target.src = 'https://images.unsplash.com/photo-1543002588-bfa74002ed7e?q=80&w=400';
          }}
        />
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

      <div className="p-5">
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

        <h3 className="text-base font-semibold text-slate-900 leading-snug mb-1 line-clamp-2 group-hover:text-aurora-700 transition-colors duration-200">
          {book.title}
        </h3>

        <p className="text-sm text-slate-500 mb-3">{authorNames}</p>

        <div className="flex items-center justify-between mt-4 gap-2">
          <p className="text-lg font-bold text-aurora-700">
            R$ {book.price.toFixed(2).replace('.', ',')}
          </p>
          <div className="flex items-center gap-1.5">
            <button
              onClick={handleAddToCart}
              disabled={book.stock <= 0}
              className="p-2 bg-aurora-50 hover:bg-aurora-100 text-aurora-700 disabled:bg-slate-100 disabled:text-slate-400 rounded-full transition-colors duration-200"
              title={t('cart.addBtn')}
            >
              <ShoppingBag size={16} />
            </button>
            <button
              onClick={handleBuyNow}
              disabled={book.stock <= 0}
              className="px-3.5 py-2 text-xs font-semibold text-white bg-aurora-600 hover:bg-aurora-700 disabled:bg-slate-100 disabled:text-slate-400 rounded-full transition-colors duration-200"
            >
              {t('cart.buyNow')}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
