import { useCart } from '../../context/CartContext';
import { useAuth } from '../../context/AuthContext';
import { useLanguage } from '../../i18n/LanguageContext';
import { useNavigate } from 'react-router-dom';
import { X, Trash2, ShoppingBag, ArrowRight } from 'lucide-react';
import toast from 'react-hot-toast';

export default function CartDrawer() {
  const { cart, isOpen, toggleCart, removeItem, totalPrice } = useCart();
  const { isAuthenticated } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();

  const handleCheckoutClick = () => {
    if (!isAuthenticated) {
      toggleCart();
      navigate('/login');
    } else {
      toast.success('Proceeding to checkout...');
    }
  };

  const handleRemove = async (itemId) => {
    try {
      await removeItem(itemId);
      toast.success('Item removed from cart');
    } catch (error) {
      toast.error('Failed to remove item');
    }
  };

  if (!isOpen) return null;

  const itemsList = cart?.cartItems ? Array.from(cart.cartItems) : [];

  return (
    <div className="fixed inset-0 z-50 overflow-hidden">
      <div 
        className="absolute inset-0 bg-slate-900/40 backdrop-blur-sm transition-opacity duration-300"
        onClick={toggleCart}
      />

      <div className="absolute inset-y-0 right-0 max-w-full flex pl-10">
        <div className="w-screen max-w-md bg-white shadow-2xl flex flex-col h-full animate-slide-up">
          <div className="px-6 py-5 bg-slate-50 border-b border-slate-100 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <ShoppingBag className="text-aurora-700" size={20} />
              <h2 className="text-lg font-semibold text-slate-900">
                {t('cart.title')}
              </h2>
            </div>
            <button 
              onClick={toggleCart} 
              className="p-1.5 rounded-lg text-slate-400 hover:text-slate-600 hover:bg-slate-100 transition-all duration-200"
            >
              <X size={20} />
            </button>
          </div>

          <div className="flex-1 overflow-y-auto px-6 py-4 space-y-6">
            {itemsList.length === 0 ? (
              <div className="h-full flex flex-col items-center justify-center text-slate-400 space-y-3">
                <ShoppingBag size={48} className="opacity-20" />
                <p className="text-sm font-light text-slate-500">
                  {t('cart.empty')}
                </p>
              </div>
            ) : (
              itemsList.map(item => (
                <div key={item.id} className="flex gap-4 border-b border-slate-100 pb-4">
                  <div className="w-16 h-20 rounded-lg overflow-hidden bg-slate-50 border border-slate-100 shadow-sm flex-shrink-0">
                    <img 
                      src={item.book.imageUrl} 
                      alt={item.book.title} 
                      className="w-full h-full object-cover"
                      onError={(e) => {
                        e.target.src = 'https://images.unsplash.com/photo-1543002588-bfa74002ed7e?q=80&w=400';
                      }}
                    />
                  </div>
                  
                  <div className="flex-1 min-w-0">
                    <h4 className="text-sm font-medium text-slate-900 truncate">
                      {item.book.title}
                    </h4>
                    <p className="text-xs text-slate-400 mt-1">
                      Qty: {item.quantity}
                    </p>
                    <p className="text-sm font-semibold text-aurora-700 mt-1">
                      R$ {(item.book.price * item.quantity).toFixed(2).replace('.', ',')}
                    </p>
                  </div>

                  <button 
                    onClick={() => handleRemove(item.id)}
                    className="p-1.5 text-slate-400 hover:text-red-500 hover:bg-red-50 rounded-lg transition-all duration-200 self-start"
                  >
                    <Trash2 size={16} />
                  </button>
                </div>
              ))
            )}
          </div>

          {itemsList.length > 0 && (
            <div className="border-t border-slate-100 px-6 py-5 space-y-4 bg-slate-50">
              <div className="flex justify-between items-center">
                <span className="text-sm font-medium text-slate-500">
                  {t('cart.total')}
                </span>
                <span className="text-xl font-bold text-slate-900">
                  R$ {totalPrice.toFixed(2).replace('.', ',')}
                </span>
              </div>

              <button
                onClick={handleCheckoutClick}
                className="w-full btn-primary py-3.5 shadow-md flex items-center justify-center gap-2"
              >
                {isAuthenticated ? (
                  <>
                    {t('cart.checkoutBtn')}
                    <ArrowRight size={16} />
                  </>
                ) : (
                  t('cart.loginToCheckout')
                )}
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
