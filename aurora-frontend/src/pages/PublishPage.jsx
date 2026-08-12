import { useState, useEffect } from 'react';
import { Plus, X, Loader2, BookOpen } from 'lucide-react';
import { useLanguage } from '../i18n/LanguageContext';
import { getAuthors, getCategories, getGenres } from '../services/catalog';
import { api } from '../services/api';
import toast from 'react-hot-toast';

export default function PublishPage() {
  const { t } = useLanguage();
  const [loading, setLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [price, setPrice] = useState('');
  const [imageUrl, setImageUrl] = useState('');
  const [stock, setStock] = useState('');

  const [availableAuthors, setAvailableAuthors] = useState([]);
  const [availableCategories, setAvailableCategories] = useState([]);
  const [availableGenres, setAvailableGenres] = useState([]);

  const [selectedAuthors, setSelectedAuthors] = useState([]);
  const [selectedCategories, setSelectedCategories] = useState([]);
  const [selectedGenres, setSelectedGenres] = useState([]);

  const [newAuthorName, setNewAuthorName] = useState('');
  const [newCategoryName, setNewCategoryName] = useState('');
  const [newGenreName, setNewGenreName] = useState('');

  const loadTaxonomies = async () => {
    try {
      const [authors, categories, genres] = await Promise.all([
        getAuthors(),
        getCategories(),
        getGenres()
      ]);
      setAvailableAuthors(authors || []);
      setAvailableCategories(categories || []);
      setAvailableGenres(genres || []);
    } catch (error) {
      toast.error('Failed to load catalog data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTaxonomies();
  }, []);

  const handleSelectAuthor = (e) => {
    const authorId = parseInt(e.target.value);
    if (!authorId) return;
    const author = availableAuthors.find(a => a.id === authorId);
    if (author && !selectedAuthors.some(a => a.id === author.id)) {
      setSelectedAuthors([...selectedAuthors, author]);
    }
    e.target.value = '';
  };

  const handleAddNewAuthor = () => {
    if (!newAuthorName.trim()) return;
    if (!selectedAuthors.some(a => a.name.toLowerCase() === newAuthorName.trim().toLowerCase())) {
      setSelectedAuthors([...selectedAuthors, { name: newAuthorName.trim(), isNew: true }]);
    }
    setNewAuthorName('');
  };

  const handleSelectCategory = (e) => {
    const categoryId = parseInt(e.target.value);
    if (!categoryId) return;
    const category = availableCategories.find(c => c.id === categoryId);
    if (category && !selectedCategories.some(c => c.id === category.id)) {
      setSelectedCategories([...selectedCategories, category]);
    }
    e.target.value = '';
  };

  const handleAddNewCategory = () => {
    if (!newCategoryName.trim()) return;
    if (!selectedCategories.some(c => c.name.toLowerCase() === newCategoryName.trim().toLowerCase())) {
      setSelectedCategories([...selectedCategories, { name: newCategoryName.trim(), isNew: true }]);
    }
    setNewCategoryName('');
  };

  const handleSelectGenre = (e) => {
    const genreId = parseInt(e.target.value);
    if (!genreId) return;
    const genre = availableGenres.find(g => g.id === genreId);
    if (genre && !selectedGenres.some(g => g.id === genre.id)) {
      setSelectedGenres([...selectedGenres, genre]);
    }
    e.target.value = '';
  };

  const handleAddNewGenre = () => {
    if (!newGenreName.trim()) return;
    if (!selectedGenres.some(g => g.name.toLowerCase() === newGenreName.trim().toLowerCase())) {
      setSelectedGenres([...selectedGenres, { name: newGenreName.trim(), isNew: true }]);
    }
    setNewGenreName('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (selectedAuthors.length === 0) {
      toast.error('Please select or create at least one author');
      return;
    }
    if (selectedCategories.length === 0) {
      toast.error('Please select or create at least one category');
      return;
    }
    if (selectedGenres.length === 0) {
      toast.error('Please select or create at least one genre');
      return;
    }

    setIsSubmitting(true);

    try {
      const authorPromises = selectedAuthors.map(async (author) => {
        if (author.isNew) {
          const res = await api.post('/api/authors', { name: author.name });
          return res.id;
        }
        return author.id;
      });

      const categoryPromises = selectedCategories.map(async (category) => {
        if (category.isNew) {
          const res = await api.post('/api/categories', { name: category.name });
          return res.id;
        }
        return category.id;
      });

      const genrePromises = selectedGenres.map(async (genre) => {
        if (genre.isNew) {
          const res = await api.post('/api/genres', { name: genre.name });
          return res.id;
        }
        return genre.id;
      });

      const [authorIds, categoryIds, genreIds] = await Promise.all([
        Promise.all(authorPromises),
        Promise.all(categoryPromises),
        Promise.all(genrePromises)
      ]);

      const payload = {
        title,
        description,
        price: parseFloat(price),
        imageUrl: imageUrl.trim() || 'https://images.unsplash.com/photo-1543002588-bfa74002ed7e?q=80&w=400',
        stock: parseInt(stock),
        authorIds,
        categoryIds,
        genreIds
      };

      await api.post('/api/books', payload);
      toast.success('Book published successfully!');
      
      setTitle('');
      setDescription('');
      setPrice('');
      setImageUrl('');
      setStock('');
      setSelectedAuthors([]);
      setSelectedCategories([]);
      setSelectedGenres([]);
      
      loadTaxonomies();
    } catch (error) {
      toast.error('Failed to publish book');
    } finally {
      setIsSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-[60vh] flex items-center justify-center">
        <Loader2 className="animate-spin text-aurora-600" size={36} />
      </div>
    );
  }

  return (
    <div className="page-enter">
      <div className="section-container py-12 sm:py-16">
        <div className="max-w-2xl mx-auto">
          
          <div className="mb-10">
            <h1 className="text-3xl sm:text-4xl font-bold text-slate-900 mb-3 flex items-center gap-3">
              <BookOpen className="text-aurora-700" size={32} />
              Publish Book
            </h1>
            <p className="text-lg text-slate-500 font-light leading-relaxed">
              Create a new book catalog entry and link or create authors, categories, and genres.
            </p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-2">
                Book Title
              </label>
              <input
                type="text"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="Enter book title"
                className="input-field"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-2">
                Description
              </label>
              <textarea
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="Enter book plot or description"
                rows={4}
                className="input-field resize-none"
                required
              />
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  Price (R$)
                </label>
                <input
                  type="number"
                  step="0.01"
                  min="0"
                  value={price}
                  onChange={(e) => setPrice(e.target.value)}
                  placeholder="0.00"
                  className="input-field"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  Stock Quantity
                </label>
                <input
                  type="number"
                  min="0"
                  value={stock}
                  onChange={(e) => setStock(e.target.value)}
                  placeholder="0"
                  className="input-field"
                  required
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-2">
                Image URL (Optional)
              </label>
              <input
                type="url"
                value={imageUrl}
                onChange={(e) => setImageUrl(e.target.value)}
                placeholder="https://example.com/cover.jpg"
                className="input-field"
              />
            </div>

            <div className="border-t border-slate-100 pt-6 space-y-6">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  Authors
                </label>
                <div className="flex gap-2">
                  <select
                    onChange={handleSelectAuthor}
                    className="input-field flex-1"
                    defaultValue=""
                  >
                    <option value="" disabled>Select existing author</option>
                    {availableAuthors.map(author => (
                      <option key={author.id} value={author.id}>{author.name}</option>
                    ))}
                  </select>
                  
                  <div className="flex-1 flex gap-1.5">
                    <input
                      type="text"
                      value={newAuthorName}
                      onChange={(e) => setNewAuthorName(e.target.value)}
                      placeholder="Or enter new author"
                      className="input-field flex-1"
                    />
                    <button
                      type="button"
                      onClick={handleAddNewAuthor}
                      className="p-3 bg-aurora-50 hover:bg-aurora-100 text-aurora-700 rounded-xl transition-all duration-200"
                    >
                      <Plus size={18} />
                    </button>
                  </div>
                </div>

                <div className="flex flex-wrap gap-1.5 mt-3">
                  {selectedAuthors.map((author, index) => (
                    <span 
                      key={author.id || `new-auth-${index}`} 
                      className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold ${
                        author.isNew 
                          ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' 
                          : 'bg-aurora-50 text-aurora-700 border border-aurora-200'
                      }`}
                    >
                      {author.name} {author.isNew && '(New)'}
                      <button
                        type="button"
                        onClick={() => setSelectedAuthors(selectedAuthors.filter((_, i) => i !== index))}
                        className="text-slate-400 hover:text-red-500 transition-colors"
                      >
                        <X size={12} />
                      </button>
                    </span>
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  Categories
                </label>
                <div className="flex gap-2">
                  <select
                    onChange={handleSelectCategory}
                    className="input-field flex-1"
                    defaultValue=""
                  >
                    <option value="" disabled>Select existing category</option>
                    {availableCategories.map(category => (
                      <option key={category.id} value={category.id}>{category.name}</option>
                    ))}
                  </select>
                  
                  <div className="flex-1 flex gap-1.5">
                    <input
                      type="text"
                      value={newCategoryName}
                      onChange={(e) => setNewCategoryName(e.target.value)}
                      placeholder="Or enter new category"
                      className="input-field flex-1"
                    />
                    <button
                      type="button"
                      onClick={handleAddNewCategory}
                      className="p-3 bg-aurora-50 hover:bg-aurora-100 text-aurora-700 rounded-xl transition-all duration-200"
                    >
                      <Plus size={18} />
                    </button>
                  </div>
                </div>

                <div className="flex flex-wrap gap-1.5 mt-3">
                  {selectedCategories.map((category, index) => (
                    <span 
                      key={category.id || `new-cat-${index}`} 
                      className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold ${
                        category.isNew 
                          ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' 
                          : 'bg-aurora-50 text-aurora-700 border border-aurora-200'
                      }`}
                    >
                      {category.name} {category.isNew && '(New)'}
                      <button
                        type="button"
                        onClick={() => setSelectedCategories(selectedCategories.filter((_, i) => i !== index))}
                        className="text-slate-400 hover:text-red-500 transition-colors"
                      >
                        <X size={12} />
                      </button>
                    </span>
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  Genres
                </label>
                <div className="flex gap-2">
                  <select
                    onChange={handleSelectGenre}
                    className="input-field flex-1"
                    defaultValue=""
                  >
                    <option value="" disabled>Select existing genre</option>
                    {availableGenres.map(genre => (
                      <option key={genre.id} value={genre.id}>{genre.name}</option>
                    ))}
                  </select>
                  
                  <div className="flex-1 flex gap-1.5">
                    <input
                      type="text"
                      value={newGenreName}
                      onChange={(e) => setNewGenreName(e.target.value)}
                      placeholder="Or enter new genre"
                      className="input-field flex-1"
                    />
                    <button
                      type="button"
                      onClick={handleAddNewGenre}
                      className="p-3 bg-aurora-50 hover:bg-aurora-100 text-aurora-700 rounded-xl transition-all duration-200"
                    >
                      <Plus size={18} />
                    </button>
                  </div>
                </div>

                <div className="flex flex-wrap gap-1.5 mt-3">
                  {selectedGenres.map((genre, index) => (
                    <span 
                      key={genre.id || `new-gen-${index}`} 
                      className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold ${
                        genre.isNew 
                          ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' 
                          : 'bg-aurora-50 text-aurora-700 border border-aurora-200'
                      }`}
                    >
                      {genre.name} {genre.isNew && '(New)'}
                      <button
                        type="button"
                        onClick={() => setSelectedGenres(selectedGenres.filter((_, i) => i !== index))}
                        className="text-slate-400 hover:text-red-500 transition-colors"
                      >
                        <X size={12} />
                      </button>
                    </span>
                  ))}
                </div>
              </div>
            </div>

            <button
              type="submit"
              disabled={isSubmitting}
              className="btn-primary w-full py-4 shadow-md flex items-center justify-center gap-2 mt-8"
            >
              {isSubmitting ? (
                <>
                  <Loader2 className="animate-spin text-white" size={20} />
                  Publishing Book...
                </>
              ) : (
                'Publish Book'
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
