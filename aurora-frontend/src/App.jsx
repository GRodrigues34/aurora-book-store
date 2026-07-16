import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { LanguageProvider } from './i18n/LanguageContext';
import Header from './components/layout/Header';
import Footer from './components/layout/Footer';
import BorealFAB from './components/ui/BorealFAB';
import HomePage from './pages/HomePage';
import BooksPage from './pages/BooksPage';
import AuthorsPage from './pages/AuthorsPage';
import PublishPage from './pages/PublishPage';
import ContactPage from './pages/ContactPage';

function App() {
  return (
    <BrowserRouter>
      <LanguageProvider>
        <div className="min-h-screen flex flex-col bg-white">
          <Header />

          <main className="flex-1">
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/books" element={<BooksPage />} />
              <Route path="/authors" element={<AuthorsPage />} />
              <Route path="/publish" element={<PublishPage />} />
              <Route path="/contact" element={<ContactPage />} />
            </Routes>
          </main>

          <Footer />
          <BorealFAB />
        </div>
      </LanguageProvider>
    </BrowserRouter>
  );
}

export default App;
