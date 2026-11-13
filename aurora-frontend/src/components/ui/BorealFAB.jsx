import { useState } from 'react';
import { MessageCircle, X } from 'lucide-react';
import Chat from '../chat/Chat';

export default function BorealFAB() {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <>
      {/* Chat Panel */}
      <div
        aria-hidden={!isOpen}
        className={`fixed bottom-24 right-6 z-50 w-[380px] max-w-[calc(100vw-3rem)] max-h-[70vh] rounded-2xl shadow-[0_24px_60px_rgba(12,10,15,0.28)] border border-paper-border bg-paper overflow-hidden flex flex-col origin-bottom-right transition-all duration-300 ease-[cubic-bezier(0.16,1,0.3,1)] ${
          isOpen
            ? 'opacity-100 translate-y-0 scale-100 pointer-events-auto'
            : 'opacity-0 translate-y-4 scale-95 pointer-events-none'
        }`}
      >
        {/* Panel Header */}
        <div className="flex items-center justify-between px-5 py-3.5 bg-obsidian text-paper border-b border-paper/10 flex-shrink-0">
          <div>
            <h3 className="text-sm font-semibold text-paper">Boreal AI</h3>
            <p className="text-[11px] text-paper/65">Aurora Bookstore Assistant</p>
          </div>
          <button
            onClick={() => setIsOpen(false)}
            className="p-1.5 rounded-full text-paper/75 hover:text-paper hover:bg-white/10 transition-colors"
            aria-label="Close chat"
          >
            <X size={18} />
          </button>
        </div>

        {/* Chat Component */}
        <div className="flex-1 overflow-hidden bg-paper">
          <Chat embedded />
        </div>
      </div>

      {/* FAB Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className={`fixed bottom-6 right-6 z-50 w-14 h-14 rounded-full flex items-center justify-center shadow-lg border border-paper/15 transition-all duration-300 ease-[cubic-bezier(0.16,1,0.3,1)] ${
          isOpen
            ? 'bg-aurora-700 hover:bg-aurora-600 rotate-90 scale-95'
            : 'bg-obsidian hover:bg-aurora-800 hover:scale-105'
        }`}
        aria-label="Toggle Boreal AI Assistant"
      >
        {isOpen ? (
          <X className="text-paper" size={22} />
        ) : (
          <MessageCircle className="text-paper" size={22} />
        )}
      </button>
    </>
  );
}
