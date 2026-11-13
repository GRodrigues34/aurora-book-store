import { GoogleLogin } from '@react-oauth/google';
import { useAuth } from '../../context/AuthContext';
import { useLanguage } from '../../i18n/LanguageContext';
import toast from 'react-hot-toast';

export default function GoogleSignInButton({ onSuccessRedirect }) {
  const { loginWithGoogle } = useAuth();
  const { t } = useLanguage();
  const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;

  if (!clientId) {
    return (
      <div className="w-full rounded-full border border-paper-border bg-paper px-4 py-2.5 text-center text-xs text-ink-muted">
        {t('auth.googleNotConfigured')}
      </div>
    );
  }

  const handleSuccess = async (credentialResponse) => {
    try {
      const ok = await loginWithGoogle(credentialResponse.credential);
      if (ok) {
        toast.success(t('auth.loginTitle'));
        onSuccessRedirect?.();
      }
    } catch (error) {
      toast.error(error.message || 'Google sign-in failed');
    }
  };

  return (
    <div className="flex justify-center w-full">
      <GoogleLogin
        onSuccess={handleSuccess}
        onError={() => toast.error('Google sign-in failed')}
        shape="pill"
        theme="outline"
        size="large"
        width="360"
      />
    </div>
  );
}
