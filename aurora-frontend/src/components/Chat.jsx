import { useState, useRef, useEffect } from 'react';
import { Send, User, Bot, Loader2 } from 'lucide-react';

export default function Chat() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef(null);

  // Scroll to the end of the response
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };
  useEffect(() => scrollToBottom(), [messages]);

  // Load chat history on component mount
  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/chat/history/1');
        if (response.ok) {
          const data = await response.json();
          const mappedMessages = data.map(msg => ({
            role: msg.chatRole,
            content: msg.content
          }));
          setMessages(mappedMessages);
        } else {
          console.error('Erro ao buscar histórico de chat');
        }
      } catch (error) {
        console.error('Falha de conexão ao buscar histórico:', error);
      }
    };
    fetchHistory();
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!input.trim() || isLoading) return;

    const userMessage = input;
    setInput('');
    // Add the user messages
    setMessages(prev => [...prev, { role: 'user', content: userMessage }]);
    // Ai response placeholder
    setMessages(prev => [...prev, { role: 'ai', content: '' }]);
    setIsLoading(true);

    try {
      const response = await fetch('http://localhost:8080/api/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: userMessage, userId: "1" }),
      });

      if (!response.ok) {
        throw new Error('Falha na conexão com o servidor');
      }

      // Stream
      const reader = response.body.getReader();
      const decoder = new TextDecoder('utf-8');

      while (true) {
        const { value, done } = await reader.read();
        if (done) break;

        const chunk = decoder.decode(value, { stream: true });

        // Recieves the response in chunks
        const lines = chunk.split('\n');
        for (const line of lines) {
          if (line.startsWith('data:')) {
            const dataText = line.substring(5);

            // Updates the response state
            setMessages(prev => {
              const newMessages = [...prev];
              const lastIndex = newMessages.length - 1;
              // Creates a new object for the last message. (solves double state update problem)
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
        newMessages[newMessages.length - 1].content = 'AI Error';
        return newMessages;
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex flex-col h-screen max-w-4xl mx-auto bg-gray-50 shadow-2xl border-x border-gray-200">

      {/* Header */}
      <div className="bg-white border-b px-6 py-4 flex items-center shadow-sm z-10">
        <Bot className="text-indigo-600 mr-3" size={32} />
        <div>
          <h1 className="text-xl font-bold text-gray-800 tracking-tight">Aurora AI</h1>
          <p className="text-xs text-gray-500 font-medium">Seu assistente virtual de livraria</p>
        </div>
      </div>

      {/* Message area */}
      <div className="flex-1 overflow-y-auto p-6 space-y-8 bg-slate-50">
        {messages.length === 0 && (
          <div className="h-full flex flex-col items-center justify-center text-gray-400 space-y-4">
            <Bot size={72} className="opacity-20 mb-4" />
            <p className="text-xl font-light text-gray-500">Olá! Qual livro estamos procurando hoje?</p>
          </div>
        )}

        {messages.map((msg, idx) => (
          <div key={idx} className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'} animate-in fade-in slide-in-from-bottom-2 duration-300`}>
            <div className={`flex max-w-[85%] ${msg.role === 'user' ? 'flex-row-reverse' : 'flex-row'}`}>

              <div className={`flex-shrink-0 w-10 h-10 rounded-full flex items-center justify-center shadow-md ${msg.role === 'user' ? 'bg-indigo-600 ml-4' : 'bg-emerald-500 mr-4'}`}>
                {msg.role === 'user' ? <User className="text-white" size={20} /> : <Bot className="text-white" size={20} />}
              </div>

              <div className={`px-6 py-4 rounded-3xl ${msg.role === 'user'
                ? 'bg-indigo-600 text-white rounded-tr-sm shadow-md'
                : 'bg-white text-gray-800 shadow-sm border border-gray-200/60 rounded-tl-sm'
                }`}>
                {/* Ai response formatter */}
                <p className="whitespace-pre-wrap leading-relaxed text-[15px]">{msg.content}</p>
                {msg.role === 'ai' && msg.content === '' && isLoading && (
                  <div className="flex space-x-1 items-center h-5">
                    <div className="w-2 h-2 bg-gray-300 rounded-full animate-bounce"></div>
                    <div className="w-2 h-2 bg-gray-300 rounded-full animate-bounce" style={{ animationDelay: '0.2s' }}></div>
                    <div className="w-2 h-2 bg-gray-300 rounded-full animate-bounce" style={{ animationDelay: '0.4s' }}></div>
                  </div>
                )}
              </div>
            </div>
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      {/* Input Form */}
      <div className="bg-white border-t p-5 shadow-[0_-4px_10px_-4px_rgba(0,0,0,0.05)]">
        <form onSubmit={handleSubmit} className="flex relative items-center">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            disabled={isLoading}
            placeholder="Pergunte sobre autores, livros ou recomendações..."
            className="flex-1 bg-gray-100 border-0 rounded-full px-6 py-4 pr-16 text-gray-700 outline-none focus:bg-white focus:ring-2 focus:ring-indigo-500 transition-all shadow-inner"
          />
          <button
            type="submit"
            disabled={isLoading || !input.trim()}
            className="absolute right-2 bg-indigo-600 text-white rounded-full p-2 w-11 h-11 flex items-center justify-center hover:bg-indigo-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors shadow-md"
          >
            {isLoading ? <Loader2 className="animate-spin text-white" size={20} /> : <Send className="text-white ml-1" size={18} />}
          </button>
        </form>
      </div>
    </div>
  );
}
