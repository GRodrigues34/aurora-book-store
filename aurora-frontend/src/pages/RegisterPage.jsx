import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useLanguage } from '../i18n/LanguageContext';
import { Mail, Lock, User, Loader2, MailCheck, RefreshCw } from 'lucide-react';
import toast from 'react-hot-toast';
import GoogleSignInButton from '../components/auth/GoogleSignInButton';

export default function RegisterPage() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [registeredEmail, setRegisteredEmail] = useState('');
  const [resending, setResending] = useState(false);

  const { register, resendVerification } = useAuth();
  const { t } = useLanguage();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!username.trim() || !email.trim() || !password.trim() || !confirmPassword.trim() || isLoading) return;

    if (password.length < 8) {
      toast.error('Password must be at least 8 characters long');
      return;
    }

    if (password !== confirmPassword) {
      toast.error('Passwords do not match');
      return;
    }

    setIsLoading(true);
    try {
      const result = await register(username, email, password);
      if (result?.requiresVerification) {
        setRegisteredEmail(result.email || email);
        toast.success(t('auth.checkEmailTitle'));
      }
    } catch (error) {
      toast.error(error.message || 'Registration failed');
    } finally {
      setIsLoading(false);
    }
  };

  const handleResend = async () => {
    if (!registeredEmail || resending) return;
    setResending(true);
    try {
      await resendVerification(registeredEmail);
      toast.success(t('auth.resendSuccess'));
    } catch (error) {
      toast.error(error.message || 'Failed to resend email');
    } finally {
      setResending(false);
    }
  };

  if (registeredEmail) {
    return (
      <div className="min-h-[82vh] flex items-center justify-center py-16 px-6 bg-paper">
        <div className="bg-white border border-paper-border rounded-2xl shadow-aurora-md max-w-md w-full p-10 text-center animate-fade-in">
          <div className="w-14 h-14 rounded-2xl bg-aurora-50 border border-aurora-100 flex items-center justify-center mx-auto mb-6">
            <MailCheck className="text-aurora-700" size={26} />
          </div>
          <h1 className="font-display text-3xl font-medium text-ink mb-3">
            {t('auth.checkEmailTitle')}
          </h1>
          <p className="text-sm text-ink-muted leading-relaxed mb-2">
            {t('auth.checkEmailSubtitle')}{' '}
            <span className="font-semibold text-ink">{registeredEmail}</span>.
          </p>
          <p className="text-xs text-ink-subtle leading-relaxed mb-8">
            {t('auth.checkEmailHint')}
          </p>

          <div className="space-y-3">
            <Link to="/login" className="btn-primary w-full">
              {t('auth.backToLogin')}
            </Link>
            <button
              type="button"
              onClick={handleResend}
              disabled={resending}
              className="btn-secondary w-full"
            >
              <RefreshCw size={15} className={resending ? 'animate-spin' : ''} />
              {t('auth.resendEmailBtn')}
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-[82vh] flex items-center justify-center py-16 px-6 bg-paper">
      <div className="bg-white border border-paper-border rounded-2xl shadow-aurora-md max-w-md w-full p-8 sm:p-10 animate-fade-in">
        <div className="mb-8">
          <h1 className="font-display text-3xl font-medium text-ink mb-2">
            {t('auth.registerTitle')}
          </h1>
          <p className="text-sm text-ink-muted">
            {t('auth.registerSubtitle')}
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-ink-muted mb-2">
              {t('auth.usernameLabel')}
            </label>
            <div className="relative">
              <User className="absolute left-3.5 top-1/2 -translate-y-1/2 text-ink-subtle" size={17} />
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder={t('auth.usernamePlaceholder')}
                className="input-field pl-10"
                required
                disabled={isLoading}
              />
            </div>
          </div>

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

          <div>
            <label className="block text-xs font-semibold uppercase tracking-wider text-ink-muted mb-2">
              {t('auth.confirmPasswordLabel')}
            </label>
            <div className="relative">
              <Lock className="absolute left-3.5 top-1/2 -translate-y-1/2 text-ink-subtle" size={17} />
              <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder={t('auth.confirmPasswordPlaceholder')}
                className="input-field pl-10"
                required
                disabled={isLoading}
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={isLoading || !username.trim() || !email.trim() || !password.trim() || !confirmPassword.trim()}
            className="btn-primary w-full py-3"
          >
            {isLoading ? (
              <Loader2 className="animate-spin text-white" size={18} />
            ) : (
              t('auth.registerBtn')
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

        <GoogleSignInButton onSuccessRedirect={() => navigate('/', { replace: true })} />

        <div className="text-center mt-7 pt-6 border-t border-paper-border">
          <p className="text-sm text-ink-muted">
            {t('auth.hasAccount')}{' '}
            <Link to="/login" className="text-aurora-700 hover:text-aurora-900 font-semibold transition-colors">
              {t('auth.entrar')}
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
