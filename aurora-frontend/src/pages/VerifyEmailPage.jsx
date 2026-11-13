import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { CheckCircle2, XCircle, Loader2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../i18n/LanguageContext';

export default function VerifyEmailPage() {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');
  const { verifyEmail } = useAuth();
  const { t } = useLanguage();

  const [status, setStatus] = useState(() => (token ? 'loading' : 'error'));
  const [errorMessage, setErrorMessage] = useState(() =>
    token ? '' : 'Verification token is missing.'
  );

  useEffect(() => {
    if (!token) return;

    let cancelled = false;
    verifyEmail(token)
      .then(() => {
        if (!cancelled) setStatus('success');
      })
      .catch((err) => {
        if (!cancelled) {
          setStatus('error');
          setErrorMessage(err.message || 'Invalid or expired verification token.');
        }
      });

    return () => {
      cancelled = true;
    };
  }, [token, verifyEmail]);

  return (
    <div className="min-h-[75vh] flex items-center justify-center py-16 px-6 bg-paper">
      <div className="bg-white border border-paper-border rounded-2xl shadow-aurora-md max-w-md w-full p-10 text-center animate-fade-in">
        {status === 'loading' && (
          <>
            <div className="w-14 h-14 rounded-2xl bg-aurora-50 border border-aurora-100 flex items-center justify-center mx-auto mb-6">
              <Loader2 className="animate-spin text-aurora-700" size={26} />
            </div>
            <h1 className="font-display text-3xl font-medium text-ink mb-3">
              {t('auth.verifyingTitle')}
            </h1>
            <p className="text-sm text-ink-muted leading-relaxed">
              {t('auth.verifyingSubtitle')}
            </p>
          </>
        )}

        {status === 'success' && (
          <>
            <div className="w-14 h-14 rounded-2xl bg-emerald-50 border border-emerald-200 flex items-center justify-center mx-auto mb-6">
              <CheckCircle2 className="text-emerald-700" size={26} />
            </div>
            <h1 className="font-display text-3xl font-medium text-ink mb-3">
              {t('auth.verifiedTitle')}
            </h1>
            <p className="text-sm text-ink-muted leading-relaxed mb-8">
              {t('auth.verifiedSubtitle')}
            </p>
            <Link to="/login" className="btn-primary w-full">
              {t('auth.backToLogin')}
            </Link>
          </>
        )}

        {status === 'error' && (
          <>
            <div className="w-14 h-14 rounded-2xl bg-red-50 border border-red-200 flex items-center justify-center mx-auto mb-6">
              <XCircle className="text-red-600" size={26} />
            </div>
            <h1 className="font-display text-3xl font-medium text-ink mb-3">
              {t('auth.verifyErrorTitle')}
            </h1>
            <p className="text-sm text-ink-muted leading-relaxed mb-8">
              {errorMessage}
            </p>
            <Link to="/login" className="btn-secondary w-full">
              {t('auth.backToLogin')}
            </Link>
          </>
        )}
      </div>
    </div>
  );
}
