import { useState, useRef } from 'react';
import { Upload, FileText, X, CheckCircle2 } from 'lucide-react';
import { useLanguage } from '../i18n/LanguageContext';

export default function PublishPage() {
  const { t } = useLanguage();
  const [file, setFile] = useState(null);
  const [isDragging, setIsDragging] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const fileInputRef = useRef(null);

  const handleDragOver = (e) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleDragLeave = () => {
    setIsDragging(false);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setIsDragging(false);
    const droppedFile = e.dataTransfer.files[0];
    if (droppedFile?.type === 'application/pdf') {
      setFile(droppedFile);
    }
  };

  const handleFileSelect = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      setFile(selectedFile);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setSubmitted(true);
  };

  if (submitted) {
    return (
      <div className="page-enter">
        <div className="section-container py-24 sm:py-32">
          <div className="max-w-lg mx-auto text-center">
            <div className="w-16 h-16 rounded-full bg-emerald-50 flex items-center justify-center mx-auto mb-6">
              <CheckCircle2 className="text-emerald-500" size={32} />
            </div>
            <h2 className="text-2xl font-bold text-slate-900 mb-4">
              {t('publish.submitBtn')}
            </h2>
            <p className="text-slate-500 leading-relaxed">
              {t('publish.successMessage')}
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="page-enter">
      <div className="section-container py-12 sm:py-16">
        <div className="max-w-2xl mx-auto">

          {/* Page Header */}
          <div className="mb-10">
            <h1 className="text-3xl sm:text-4xl font-bold text-slate-900 mb-3">
              {t('publish.title')}
            </h1>
            <p className="text-lg text-slate-500 font-light leading-relaxed">
              {t('publish.subtitle')}
            </p>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit} className="space-y-6">

            {/* Name & Email Row */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  {t('publish.nameLabel')}
                </label>
                <input
                  type="text"
                  placeholder={t('publish.namePlaceholder')}
                  className="input-field"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  {t('publish.emailLabel')}
                </label>
                <input
                  type="email"
                  placeholder={t('publish.emailPlaceholder')}
                  className="input-field"
                  required
                />
              </div>
            </div>

            {/* Book Title */}
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-2">
                {t('publish.bookTitleLabel')}
              </label>
              <input
                type="text"
                placeholder={t('publish.bookTitlePlaceholder')}
                className="input-field"
                required
              />
            </div>

            {/* Plot */}
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-2">
                {t('publish.plotLabel')}
              </label>
              <textarea
                placeholder={t('publish.plotPlaceholder')}
                rows={5}
                className="input-field resize-none"
                required
              />
            </div>

            {/* PDF Upload Zone */}
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-2">
                {t('publish.uploadLabel')}
              </label>
              <div
                onDragOver={handleDragOver}
                onDragLeave={handleDragLeave}
                onDrop={handleDrop}
                onClick={() => fileInputRef.current?.click()}
                className={`relative border-2 border-dashed rounded-2xl p-8 text-center cursor-pointer transition-all duration-200 ${
                  isDragging
                    ? 'border-aurora-500 bg-aurora-50/50'
                    : file
                    ? 'border-emerald-300 bg-emerald-50/30'
                    : 'border-slate-200 hover:border-aurora-300 hover:bg-slate-50'
                }`}
              >
                <input
                  ref={fileInputRef}
                  type="file"
                  accept=".pdf"
                  onChange={handleFileSelect}
                  className="hidden"
                />

                {file ? (
                  <div className="flex items-center justify-center gap-3">
                    <FileText className="text-emerald-500" size={24} />
                    <div className="text-left">
                      <p className="text-sm font-medium text-slate-800">{file.name}</p>
                      <p className="text-xs text-slate-400">
                        {(file.size / 1024 / 1024).toFixed(2)} MB
                      </p>
                    </div>
                    <button
                      type="button"
                      onClick={(e) => { e.stopPropagation(); setFile(null); }}
                      className="ml-4 p-1 text-slate-400 hover:text-red-500 transition-colors"
                    >
                      <X size={16} />
                    </button>
                  </div>
                ) : (
                  <>
                    <Upload className="mx-auto text-slate-400 mb-3" size={32} />
                    <p className="text-sm text-slate-500">
                      {t('publish.uploadDrag')}{' '}
                      <span className="text-aurora-600 font-medium">{t('publish.uploadBrowse')}</span>
                    </p>
                    <p className="text-xs text-slate-400 mt-1">{t('publish.uploadHint')}</p>
                  </>
                )}
              </div>
            </div>

            {/* Submit */}
            <button type="submit" className="btn-primary w-full py-4">
              {t('publish.submitBtn')}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
