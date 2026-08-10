import { api } from './api';

export const getBooks = () => api.get('/api/books');
export const getAuthors = () => api.get('/api/authors');
export const getCategories = () => api.get('/api/categories');
export const getGenres = () => api.get('/api/genres');
