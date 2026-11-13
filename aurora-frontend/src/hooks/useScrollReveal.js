import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';

const DIRECTIONAL_SELECTOR = [
  'main [data-reveal]',
  'main article',
  'main form',
  'main section > div > div:first-child',
  'footer > div',
].join(', ');

const WORD_HEADING_SELECTOR = 'main h2:not([data-no-reveal]), main h3:not(article h3):not([data-no-reveal])';

function splitHeadingWords(heading) {
  if (heading.dataset.wordsSplit === 'true') return;
  const text = heading.textContent?.trim();
  if (!text) return;

  // Only split plain-text headings so we never break nested icons/links.
  if (heading.children.length > 0) {
    heading.setAttribute('data-reveal', 'left');
    heading.dataset.wordsSplit = 'true';
    return;
  }

  heading.dataset.wordsSplit = 'true';
  heading.setAttribute('data-reveal-words', '');
  const words = text.split(/\s+/);
  heading.textContent = '';

  words.forEach((word, idx) => {
    const mask = document.createElement('span');
    mask.className = 'word-mask';

    const inner = document.createElement('span');
    inner.className = 'word-inner';
    inner.style.setProperty('--word-delay', `${idx * 48}ms`);
    inner.textContent = word;

    mask.appendChild(inner);
    heading.appendChild(mask);
    if (idx < words.length - 1) {
      heading.appendChild(document.createTextNode(' '));
    }
  });
}

function assignDirection(el, index) {
  if (el.hasAttribute('data-reveal')) return;
  const tag = el.tagName.toLowerCase();
  if (tag === 'article') {
    el.setAttribute('data-reveal', 'scale');
    el.style.setProperty('--reveal-delay', `${(index % 4) * 70}ms`);
  } else if (tag === 'form') {
    el.setAttribute('data-reveal', 'right');
  } else if (el.closest('footer')) {
    el.setAttribute('data-reveal', 'up');
  } else {
    el.setAttribute('data-reveal', index % 2 === 0 ? 'left' : 'right');
  }
}

/**
 * Portfolio-grade directional & word-mask scroll reveal choreography.
 * Uses GPU-accelerated transforms (expo.out) and word-by-word masked builds,
 * automatically observing async-loaded grids across route transitions.
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
          entry.target.classList.add('is-inview');
          observer.unobserve(entry.target);
        });
      },
      {
        threshold: 0.08,
        rootMargin: '0px 0px -36px 0px',
      }
    );

    const bindElements = () => {
      document.querySelectorAll(WORD_HEADING_SELECTOR).forEach((heading) => {
        splitHeadingWords(heading);
      });

      const candidates = document.querySelectorAll(DIRECTIONAL_SELECTOR);
      candidates.forEach((el, index) => {
        if (el.dataset.revealBound === 'true') return;
        if (window.getComputedStyle(el).position === 'sticky' || el.closest('[data-no-reveal]')) {
          el.dataset.revealBound = 'true';
          return;
        }

        assignDirection(el, index);
        el.dataset.revealBound = 'true';

        const rect = el.getBoundingClientRect();
        if (rect.top < window.innerHeight * 0.92) {
          el.classList.add('is-inview');
          return;
        }
        observer.observe(el);
      });

      document.querySelectorAll('[data-reveal-words]').forEach((el) => {
        if (el.dataset.revealWordsBound === 'true') return;
        el.dataset.revealWordsBound = 'true';

        const rect = el.getBoundingClientRect();
        if (rect.top < window.innerHeight * 0.92) {
          el.classList.add('is-inview');
          return;
        }
        observer.observe(el);
      });
    };

    const rafId = window.requestAnimationFrame(bindElements);

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
