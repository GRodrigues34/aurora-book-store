import { useState, useRef, useEffect } from 'react';
import { Send, User, Bot, Loader2, History } from 'lucide-react';
import { useLanguage } from '../../i18n/LanguageContext';
import { useAuth } from '../../context/AuthContext';
import { getChatHistory, sendChatMessage } from '../../services/chat';
import { Link } from 'react-router-dom';

export default function Chat({ embedded = false }) {
  const { t } = useLanguage();
  const { isAuthenticated } = useAuth();
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isLoadingHistory, setIsLoadingHistory] = useState(false);
  const [historyLoaded, setHistoryLoaded] = useState(false);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleLoadHistory = async () => {
    setIsLoadingHistory(true);
    try {
      const history = await getChatHistory();
      const mapped = history.map(msg => ({
        role: msg.chatRole,
        content: msg.content
      }));
      setMessages(prev => [...mapped, ...prev]);
      setHistoryLoaded(true);
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoadingHistory(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!input.trim() || isLoading) return;

    const userMessage = input;
    setInput('');
    
    setMessages(prev => [...prev, { role: 'user', content: userMessage }]);
    setMessages(prev => [...prev, { role: 'ai', content: '' }]);
    setIsLoading(true);

    try {
      const response = await sendChatMessage(userMessage);

      if (!response.ok) {
        throw new Error('Server connection failed');
      }

      const reader = response.body.getReader();
      const decoder = new TextDecoder('utf-8');

      while (true) {
        const { value, done } = await reader.read();
        if (done) break;

        const chunk = decoder.decode(value, { stream: true });
        const lines = chunk.split('\n');
        for (const line of lines) {
          if (line.startsWith('data:')) {
            const dataText = line.substring(5);
            setMessages(prev => {
              const newMessages = [...prev];
              const lastIndex = newMessages.length - 1;
              newMessages[lastIndex] = {
                ...newMessages[lastIndex],
                content: newMessages[lastIndex].content + dataText
              };
              return newMessages;
            });
          }
        }
      }
    } catch (error) {
      console.error(error);
      setMessages(prev => {
        const newMessages = [...prev];
        newMessages[newMessages.length - 1].content = 'AI Connection Error';
        return newMessages;
      });
    } finally {
      setIsLoading(false);
    }
  };

  if (!isAuthenticated) {
    return (
      <div className={`flex flex-col items-center justify-center p-6 text-center ${embedded ? 'h-[55vh]' : 'h-[60vh]'} bg-slate-50`}>
        <Bot size={48} className="text-slate-300 animate-pulse" />
        <h3 className="text-lg font-semibold text-slate-800 mt-4">Boreal AI</h3>
        <p className="text-sm text-slate-500 font-light mt-2 max-w-xs">
          {t('chat.loginRequired')}
        </p>
        <Link to="/login" className="btn-primary px-6 py-2.5 text-sm mt-4">
          {t('auth.entrar')}
        </Link>
      </div>
    );
  }

  return (
    <div className={`flex flex-col ${embedded ? 'h-[55vh]' : 'h-screen max-w-4xl mx-auto'} bg-slate-50`}>
      <div className="flex-1 overflow-y-auto p-4 space-y-6">
        {isAuthenticated && !historyLoaded && (
          <div className="flex justify-center mb-4">
            <button
              onClick={handleLoadHistory}
              disabled={isLoadingHistory}
              className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium text-slate-500 hover:text-aurora-700 bg-slate-100 hover:bg-aurora-50 border border-slate-200 hover:border-aurora-200 rounded-full transition-all duration-200 disabled:opacity-50"
            >
              <History size={12} />
              {isLoadingHistory ? t('chat.loadingHistory') : t('chat.loadHistory')}
            </button>
          </div>
        )}

        {messages.length === 0 && (
          <div className="h-full flex flex-col items-center justify-center text-slate-400 space-y-3 px-4">
            <Bot size={48} className="opacity-20" />
            <p className="text-sm font-light text-slate-500 text-center">{t('chat.empty')}</p>
          </div>
        )}

        {messages.map((msg, idx) => (
          <div key={idx} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}>
            <div className={`flex max-w-[85%] ${msg.role === 'user' ? 'flex-row-reverse' : 'flex-row'}`}>
              <div className={`flex-shrink-0 w-8 h-8 rounded-full flex items-center justify-center shadow-sm ${msg.role === 'user' ? 'bg-aurora-600 ml-3' : 'bg-emerald-500 mr-3'}`}>
                {msg.role === 'user' ? <User className="text-white" size={14} /> : <Bot className="text-white" size={14} />}
              </div>

              <div className={`px-4 py-3 rounded-2xl text-sm ${msg.role === 'user'
                ? 'bg-aurora-600 text-white rounded-tr-sm shadow-sm'
                : 'bg-white text-slate-800 shadow-sm border border-slate-200/60 rounded-tl-sm'
                }`}>
                <p className="whitespace-pre-wrap leading-relaxed">{msg.content}</p>
                {(msg.role === 'ai' || msg.role === 'assistant') && msg.content === '' && isLoading && (
                  <div className="flex space-x-1 items-center h-4">
                    <div className="w-1.5 h-1.5 bg-slate-300 rounded-full animate-bounce"></div>
                    <div className="w-1.5 h-1.5 bg-slate-300 rounded-full animate-bounce" style={{ animationDelay: '0.2s' }}></div>
                    <div className="w-1.5 h-1.5 bg-slate-300 rounded-full animate-bounce" style={{ animationDelay: '0.4s' }}></div>
                  </div>
                )}
              </div>
            </div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      <div className="bg-white border-t p-3 flex-shrink-0">
        <form onSubmit={handleSubmit} className="flex relative items-center">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            disabled={isLoading}
            placeholder={t('chat.placeholder')}
            className="flex-1 bg-slate-100 border-0 rounded-full px-4 py-3 pr-12 text-sm text-slate-700 outline-none focus:bg-white focus:ring-2 focus:ring-aurora-500 transition-all shadow-inner"
          />
          <button
            type="submit"
            disabled={isLoading || !input.trim()}
            className="absolute right-1.5 bg-aurora-600 text-white rounded-full p-2 w-9 h-9 flex items-center justify-center hover:bg-aurora-700 disabled:bg-slate-300 disabled:cursor-not-allowed transition-colors shadow-sm"
          >
            {isLoading ? <Loader2 className="animate-spin text-white" size={16} /> : <Send className="text-white ml-0.5" size={14} />}
          </button>
        </form>
      </div>
    </div>
  );
}
