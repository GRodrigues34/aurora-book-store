import { createContext, useContext, useState, useEffect } from 'react';
import { api } from '../services/api';

const AuthContext = createContext(null);

const parseJwt = (token) => {
  try {
    return JSON.parse(atob(token.split('.')[1]));
  } catch (e) {
    return null;
  }
};

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  const applySessionToken = (jwtToken) => {
    localStorage.setItem('token', jwtToken);
    const decoded = parseJwt(jwtToken);
    setUser({ email: decoded.sub, role: decoded.role });
    setToken(jwtToken);
    setIsAuthenticated(true);
  };

  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    if (storedToken) {
      const decoded = parseJwt(storedToken);
      if (decoded && decoded.exp * 1000 > Date.now()) {
        setUser({ email: decoded.sub, role: decoded.role });
        setToken(storedToken);
        setIsAuthenticated(true);
      } else {
        localStorage.removeItem('token');
      }
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    const response = await api.post('/auth/login', { login: email, password });
    if (response && response.token) {
      applySessionToken(response.token);
      return true;
    }
    return false;
  };

  const loginWithGoogle = async (credential) => {
    const response = await api.post('/auth/google', { credential });
    if (response && response.token) {
      applySessionToken(response.token);
      return true;
    }
    return false;
  };

  const register = async (username, email, password) => {
    const createdUser = await api.post('/auth/register', { username, email, password, role: 'USER' });
    return { requiresVerification: true, email: createdUser?.email || email };
  };

  const verifyEmail = async (verificationToken) => {
    return api.get(`/auth/verify-email?token=${encodeURIComponent(verificationToken)}`);
  };

  const resendVerification = async (email) => {
    return api.post('/auth/resend-verification', { email });
  };

  const logout = () => {
    localStorage.removeItem('token');
    setUser(null);
    setToken(null);
    setIsAuthenticated(false);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated,
        loading,
        login,
        loginWithGoogle,
        register,
        verifyEmail,
        resendVerification,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
