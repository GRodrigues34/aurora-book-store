import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';

const REVEAL_SELECTOR = [
  'main section:not([data-no-reveal])',
  'main article',
  'main form',
  'main [data-reveal]',
  'footer > div',
].join(', ');

/**
 * Smoothly reveals sections, cards, forms, and footer blocks as they enter the viewport.
 * Automatically observes dynamically rendered items (e.g. async book/author grids)
 * across route transitions while respecting reduced-motion preferences.
 */
export function useScrollReveal() {
  const { pathname } = useLocation();

  useEffect(() => {
    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
      return undefined;
    }

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (!entry.isIntersecting) return;
          const el = entry.target;
          el.style.opacity = '1';
          el.style.transform = 'translate3d(0, 0, 0)';
          observer.unobserve(el);
        });
      },
      {
        threshold: 0.06,
        rootMargin: '0px 0px -32px 0px',
      }
    );

    const bindElements = () => {
      const candidates = document.querySelectorAll(REVEAL_SELECTOR);
      candidates.forEach((el, index) => {
        if (el.dataset.revealBound === 'true') return;

        // Skip sticky containers (e.g., HomePage hero) so they are immediately visible.
        const computedPosition = window.getComputedStyle(el).position;
        if (computedPosition === 'sticky') {
          el.dataset.revealBound = 'true';
          return;
        }

        el.dataset.revealBound = 'true';

        // If already in the initial viewport, reveal immediately without hiding first.
        const rect = el.getBoundingClientRect();
        if (rect.top < window.innerHeight * 0.92) {
          el.style.opacity = '1';
          el.style.transform = 'translate3d(0, 0, 0)';
          return;
        }

        const staggerMs = (index % 5) * 45;
        el.style.opacity = '0';
        el.style.transform = 'translate3d(0, 22px, 0)';
        el.style.transition = `opacity 680ms cubic-bezier(0.22, 1, 0.36, 1) ${staggerMs}ms, transform 680ms cubic-bezier(0.22, 1, 0.36, 1) ${staggerMs}ms`;
        observer.observe(el);
      });
    };

    // Initial bind after route render
    const rafId = window.requestAnimationFrame(bindElements);

    // Watch for async-loaded content (books, authors, orders)
    const mutationObserver = new MutationObserver(() => {
      bindElements();
    });
    const mainEl = document.querySelector('main');
    if (mainEl) {
      mutationObserver.observe(mainEl, { childList: true, subtree: true });
    }

    return () => {
      window.cancelAnimationFrame(rafId);
      mutationObserver.disconnect();
      observer.disconnect();
    };
  }, [pathname]);
}
