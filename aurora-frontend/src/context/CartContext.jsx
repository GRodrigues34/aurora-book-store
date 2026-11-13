import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { api } from '../services/api';
import { useAuth } from './AuthContext';

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const { isAuthenticated } = useAuth();
  const [cart, setCart] = useState(null);
  const [isOpen, setIsOpen] = useState(false);

  const fetchCart = useCallback(async () => {
    try {
      const data = await api.get('/api/carts');
      setCart(data);
    } catch (error) {
      console.error(error);
    }
  }, []);

  useEffect(() => {
    if (!isAuthenticated) return;

    let cancelled = false;
    api.get('/api/carts')
      .then((data) => {
        if (!cancelled) setCart(data);
      })
      .catch((error) => console.error(error));

    return () => {
      cancelled = true;
    };
  }, [isAuthenticated]);

  const activeCart = isAuthenticated ? cart : null;

  const addItem = async (bookId, quantity = 1) => {
    try {
      const data = await api.post('/api/carts', { bookId, quantity });
      setCart(data);
    } catch (error) {
      console.error(error);
      throw error;
    }
  };

  const removeItem = async (itemId) => {
    try {
      const data = await api.delete(`/api/carts/${itemId}`);
      setCart(data);
    } catch (error) {
      console.error(error);
      throw error;
    }
  };

  const toggleCart = () => {
    setIsOpen(!isOpen);
  };

  const totalItems = activeCart?.cartItems
    ? Array.from(activeCart.cartItems).reduce((sum, item) => sum + item.quantity, 0)
    : 0;

  const totalPrice = activeCart?.cartItems
    ? Array.from(activeCart.cartItems).reduce((sum, item) => sum + item.book.price * item.quantity, 0)
    : 0;

  return (
    <CartContext.Provider value={{ cart: activeCart, isOpen, totalItems, totalPrice, addItem, removeItem, toggleCart, setIsOpen, fetchCart }}>
      {children}
    </CartContext.Provider>
  );
}

// eslint-disable-next-line react-refresh/only-export-components
export function useCart() {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
}
