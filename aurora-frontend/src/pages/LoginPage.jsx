import { useState } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../i18n/LanguageContext';
import { Mail, Lock, Loader2, RefreshCw } from 'lucide-react';
import toast from 'react-hot-toast';
import GoogleSignInButton from '../components/auth/GoogleSignInButton';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [needsVerification, setNeedsVerification] = useState(false);
  const [resending, setResending] = useState(false);

  const { login, resendVerification } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();
  const location = useLocation();

  const from = location.state?.from?.pathname || '/';

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email.trim() || !password.trim() || isLoading) return;

    setIsLoading(true);
    setNeedsVerification(false);
    try {
      const success = await login(email, password);
      if (success) {
        toast.success(t('auth.loginTitle'));
        navigate(from, { replace: true });
      }
    } catch (error) {
      const msg = error.message || 'Invalid credentials';
      if (msg.toLowerCase().includes('verify')) {
        setNeedsVerification(true);
      }
      toast.error(msg);
    } finally {
      setIsLoading(false);
    }
  };

  const handleResend = async () => {
    if (!email.trim() || resending) return;
    setResending(true);
    try {
      await resendVerification(email.trim());
      toast.success(t('auth.resendSuccess'));
    } catch (error) {
      toast.error(error.message || 'Failed to resend verification email');
    } finally {
      setResending(false);
    }
  };

  return (
    <div className="min-h-[82vh] flex items-center justify-center py-16 px-6 bg-paper">
      <div className="bg-white border border-paper-border rounded-2xl shadow-aurora-md max-w-md w-full p-8 sm:p-10 animate-fade-in">
        <div className="mb-8">
          <h1 className="font-display text-3xl font-medium text-ink mb-2">
            {t('auth.loginTitle')}
          </h1>
          <p className="text-sm text-ink-muted">
            {t('auth.loginSubtitle')}
          </p>
        </div>

        {needsVerification && (
          <div className="mb-6 p-4 rounded-xl bg-amber-50 border border-amber-200 text-sm text-amber-900">
            <p className="font-medium mb-2">{t('auth.unverifiedBanner')}</p>
            <button
              type="button"
              onClick={handleResend}
              disabled={resending}
              className="inline-flex items-center gap-1.5 text-xs font-semibold text-aurora-700 hover:text-aurora-900 underline"
            >
              <RefreshCw size={13} className={resending ? 'animate-spin' : ''} />
              {t('auth.resendEmailBtn')}
            </button>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-ink-muted mb-2">
              {t('auth.emailLabel')}
            </label>
            <div className="relative">
              <Mail className="absolute left-3.5 top-1/2 -translate-y-1/2 text-ink-subtle" size={17} />
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder={t('auth.emailPlaceholder')}
                className="input-field pl-10"
                required
                disabled={isLoading}
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-ink-muted mb-2">
              {t('auth.passwordLabel')}
            </label>
            <div className="relative">
              <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 text-ink-subtle" size={17} />
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder={t('auth.passwordPlaceholder')}
                className="input-field pl-10"
                required
                disabled={isLoading}
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={isLoading || !email.trim() || !password.trim()}
            className="btn-primary w-full py-3"
          >
            {isLoading ? (
              <Loader2 className="animate-spin text-white" size={18} />
            ) : (
              t('auth.loginBtn')
            )}
          </button>
        </form>

        <div className="relative my-6">
          <div className="absolute inset-0 flex items-center">
            <div className="w-full border-t border-paper-border" />
          </div>
          <div className="relative flex justify-center text-xs">
            <span className="bg-white px-3 text-ink-subtle">{t('auth.orContinueWith')}</span>
          </div>
        </div>

        <GoogleSignInButton onSuccessRedirect={() => navigate(from, { replace: true })} />

        <div className="text-center mt-7 pt-6 border-t border-paper-border">
          <p className="text-sm text-ink-muted">
            {t('auth.noAccount')}{' '}
            <Link to="/register" className="text-aurora-700 hover:text-aurora-900 font-semibold transition-colors">
              {t('auth.cadastrar')}
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
