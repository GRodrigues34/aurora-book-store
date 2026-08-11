import { useState, useEffect } from 'react';
import { getOrders, cancelOrder } from '../services/order';
import { useLanguage } from '../i18n/LanguageContext';
import { ShoppingBag, Calendar, DollarSign, Package, Trash2 } from 'lucide-react';
import toast from 'react-hot-toast';
import { Link } from 'react-router-dom';

export default function OrdersPage() {
  const { t } = useLanguage();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchOrders = async () => {
    try {
      const data = await getOrders();
      setOrders(data || []);
    } catch (error) {
      toast.error('Failed to load orders');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const handleCancelOrder = async (orderId) => {
    try {
      await cancelOrder(orderId);
      toast.success('Order cancelled successfully');
      fetchOrders();
    } catch (error) {
      toast.error('Failed to cancel order');
    }
  };

  if (loading) {
    return (
      <div className="min-h-[60vh] flex items-center justify-center">
        <div className="w-10 h-10 border-4 border-aurora-200 border-t-aurora-700 rounded-full animate-spin" />
      </div>
    );
  }

  return (
    <div className="page-enter">
      <div className="section-container py-12 sm:py-16">
        <div className="mb-10">
          <h1 className="text-3xl sm:text-4xl font-bold text-slate-900 mb-3">
            {t('orders.title')}
          </h1>
          <p className="text-lg text-slate-500 font-light">
            {t('orders.subtitle')}
          </p>
        </div>

        {orders.length === 0 ? (
          <div className="card py-16 px-4 text-center max-w-xl mx-auto flex flex-col items-center justify-center space-y-5">
            <ShoppingBag size={48} className="text-slate-300" />
            <div className="space-y-2">
              <h3 className="text-lg font-semibold text-slate-800">
                No orders found
              </h3>
              <p className="text-sm text-slate-500 font-light">
                {t('orders.empty')}
              </p>
            </div>
            <Link to="/books" className="btn-primary px-6 py-2.5 text-sm">
              Browse Books
            </Link>
          </div>
        ) : (
          <div className="space-y-6">
            {orders.map((order) => (
              <div key={order.orderId} className="card overflow-hidden border border-slate-100">
                <div className="bg-slate-50 px-6 py-4 flex flex-wrap items-center justify-between gap-4 border-b border-slate-100 text-sm">
                  <div className="flex flex-wrap items-center gap-6 text-slate-600">
                    <div>
                      <span className="text-xs text-slate-400 block uppercase tracking-wider mb-0.5">
                        Order ID
                      </span>
                      <span className="font-semibold text-slate-800">
                        #{order.orderId}
                      </span>
                    </div>

                    <div>
                      <span className="text-xs text-slate-400 block uppercase tracking-wider mb-0.5">
                        {t('orders.date')}
                      </span>
                      <span className="font-medium text-slate-800 flex items-center gap-1.5">
                        <Calendar size={14} className="text-slate-400" />
                        {new Date(order.date).toLocaleDateString()}
                      </span>
                    </div>

                    <div>
                      <span className="text-xs text-slate-400 block uppercase tracking-wider mb-0.5">
                        {t('orders.total')}
                      </span>
                      <span className="font-semibold text-aurora-700 flex items-center gap-1">
                        <DollarSign size={14} className="text-aurora-600" />
                        R$ {order.totalPrice.toFixed(2).replace('.', ',')}
                      </span>
                    </div>

                    <div>
                      <span className="text-xs text-slate-400 block uppercase tracking-wider mb-0.5">
                        {t('orders.status')}
                      </span>
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                        {t('orders.completed')}
                      </span>
                    </div>
                  </div>

                  <button
                    onClick={() => handleCancelOrder(order.orderId)}
                    className="flex items-center gap-1.5 text-xs font-medium text-red-600 hover:text-red-800 hover:bg-red-50 px-3 py-2 rounded-lg transition-all duration-200 border border-transparent hover:border-red-100"
                  >
                    <Trash2 size={14} />
                    {t('orders.cancelBtn')}
                  </button>
                </div>

                <div className="divide-y divide-slate-100 px-6">
                  {Array.from(order.orderItems).map((item) => (
                    <div key={item.book.id} className="py-4 flex items-center gap-4">
                      <div className="w-12 h-16 rounded overflow-hidden bg-slate-50 border border-slate-100 shadow-sm flex-shrink-0">
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
                        <p className="text-xs text-slate-500 mt-1 flex items-center gap-1">
                          <Package size={12} className="text-slate-400" />
                          Qty: {item.quantity} × R$ {item.price.toFixed(2).replace('.', ',')}
                        </p>
                      </div>

                      <div className="text-sm font-bold text-slate-800">
                        R$ {item.totalPrice.toFixed(2).replace('.', ',')}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
