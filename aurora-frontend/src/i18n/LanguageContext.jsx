import { createContext, useContext, useState, useCallback } from 'react';
import { translations } from './translations';

const LanguageContext = createContext();

export function LanguageProvider({ children }) {
  const [language, setLanguage] = useState('en');

  const toggleLanguage = useCallback(() => {
    setLanguage(prev => (prev === 'en' ? 'pt' : 'en'));
  }, []);

  const t = useCallback(
    (path) => {
      const keys = path.split('.');
      let value = translations[language];
      for (const key of keys) {
        value = value?.[key];
      }
      return value || path;
    },
    [language]
  );

  return (
    <LanguageContext.Provider value={{ language, toggleLanguage, t }}>
      {children}
    </LanguageContext.Provider>
  );
}

export function useLanguage() {
  const context = useContext(LanguageContext);
  if (!context) {
    throw new Error('useLanguage must be used within a LanguageProvider');
  }
  return context;
}
