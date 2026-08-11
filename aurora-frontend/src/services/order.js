import { api } from './api';

export const getOrders = () => api.get('/api/orders');
export const createOrder = (dto) => api.post('/api/orders', dto);
export const checkoutCart = () => api.post('/api/orders/checkout');
export const cancelOrder = (id) => api.delete(`/api/orders/${id}`);
