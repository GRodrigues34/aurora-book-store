import { api } from './api';

export const getChatHistory = () => api.get('/api/chat');
export const sendChatMessage = (message) => api.postRaw('/api/chat', { message });
