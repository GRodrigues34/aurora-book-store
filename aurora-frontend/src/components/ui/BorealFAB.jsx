import { useState } from 'react';
import { MessageCircle, X } from 'lucide-react';
import Chat from '../chat/Chat';

export default function BorealFAB() {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <>
      {/* Chat Panel */}
      {isOpen && (
        <div className="fixed bottom-24 right-6 z-50 w-[380px] max-h-[70vh] rounded-2xl shadow-2xl border border-slate-200 bg-white overflow-hidden animate-slide-up flex flex-col">
          {/* Panel Header */}
          <div className="flex items-center justify-between px-5 py-3 bg-gradient-to-r from-aurora-700 to-aurora-600 text-white flex-shrink-0">
            <div>
              <h3 className="text-sm font-semibold">Boreal AI</h3>
              <p className="text-[11px] text-aurora-200 opacity-90">Aurora Bookstore Assistant</p>
            </div>
            <button
              onClick={() => setIsOpen(false)}
              className="p-1 rounded-lg hover:bg-white/20 transition-colors"
              aria-label="Close chat"
            >
              <X size={18} />
            </button>
          </div>

          {/* Chat Component */}
          <div className="flex-1 overflow-hidden">
            <Chat embedded />
          </div>
        </div>
      )}

      {/* FAB Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className={`fixed bottom-6 right-6 z-50 w-14 h-14 rounded-full flex items-center justify-center shadow-lg transition-all duration-300 ${
          isOpen
            ? 'bg-slate-700 hover:bg-slate-800 rotate-0'
            : 'bg-gradient-to-br from-aurora-600 to-emerald-500 hover:from-aurora-700 hover:to-emerald-600 animate-pulse-soft'
        }`}
        aria-label="Toggle Boreal AI Assistant"
      >
        {isOpen ? (
          <X className="text-white" size={22} />
        ) : (
          <MessageCircle className="text-white" size={22} />
        )}
      </button>
    </>
  );
}
