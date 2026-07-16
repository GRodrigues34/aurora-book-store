import { useState } from 'react';
import { Mail, MapPin, Phone, Clock, CheckCircle2, Send } from 'lucide-react';
import { useLanguage } from '../i18n/LanguageContext';

export default function ContactPage() {
  const { t } = useLanguage();
  const [submitted, setSubmitted] = useState(false);

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
              {t('contact.submitBtn')}
            </h2>
            <p className="text-slate-500 leading-relaxed">
              {t('contact.successMessage')}
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="page-enter">
      <div className="section-container py-12 sm:py-16">

        {/* Page Header */}
        <div className="mb-12">
          <h1 className="text-3xl sm:text-4xl font-bold text-slate-900 mb-3">
            {t('contact.title')}
          </h1>
          <p className="text-lg text-slate-500 font-light leading-relaxed max-w-2xl">
            {t('contact.subtitle')}
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-5 gap-12 lg:gap-16">

          {/* Form (left - 3 cols) */}
          <div className="lg:col-span-3">
            <form onSubmit={handleSubmit} className="space-y-6">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">
                    {t('contact.nameLabel')}
                  </label>
                  <input
                    type="text"
                    placeholder={t('contact.namePlaceholder')}
                    className="input-field"
                    required
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-slate-700 mb-2">
                    {t('contact.emailLabel')}
                  </label>
                  <input
                    type="email"
                    placeholder={t('contact.emailPlaceholder')}
                    className="input-field"
                    required
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  {t('contact.subjectLabel')}
                </label>
                <input
                  type="text"
                  placeholder={t('contact.subjectPlaceholder')}
                  className="input-field"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  {t('contact.messageLabel')}
                </label>
                <textarea
                  placeholder={t('contact.messagePlaceholder')}
                  rows={6}
                  className="input-field resize-none"
                  required
                />
              </div>

              <button type="submit" className="btn-primary px-8 py-4">
                <Send size={16} />
                {t('contact.submitBtn')}
              </button>
            </form>
          </div>

          {/* Contact Info (right - 2 cols) */}
          <div className="lg:col-span-2">
            <div className="card p-8 space-y-8">
              <h3 className="text-lg font-semibold text-slate-900">
                {t('contact.infoTitle')}
              </h3>

              <div className="space-y-6">
                <div className="flex items-start gap-4">
                  <div className="w-10 h-10 rounded-xl bg-aurora-50 flex items-center justify-center flex-shrink-0">
                    <MapPin className="text-aurora-600" size={18} />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-slate-800 mb-0.5">Address</p>
                    <p className="text-sm text-slate-500">{t('contact.address')}</p>
                  </div>
                </div>

                <div className="flex items-start gap-4">
                  <div className="w-10 h-10 rounded-xl bg-aurora-50 flex items-center justify-center flex-shrink-0">
                    <Mail className="text-aurora-600" size={18} />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-slate-800 mb-0.5">Email</p>
                    <p className="text-sm text-slate-500">{t('contact.email')}</p>
                  </div>
                </div>

                <div className="flex items-start gap-4">
                  <div className="w-10 h-10 rounded-xl bg-aurora-50 flex items-center justify-center flex-shrink-0">
                    <Phone className="text-aurora-600" size={18} />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-slate-800 mb-0.5">Phone</p>
                    <p className="text-sm text-slate-500">{t('contact.phone')}</p>
                  </div>
                </div>

                <div className="h-px bg-slate-100" />

                <div className="flex items-start gap-4">
                  <div className="w-10 h-10 rounded-xl bg-emerald-50 flex items-center justify-center flex-shrink-0">
                    <Clock className="text-emerald-600" size={18} />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-slate-800 mb-0.5">{t('contact.hoursTitle')}</p>
                    <p className="text-sm text-slate-500">{t('contact.hours')}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
