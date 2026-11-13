# Design Brief: Aurora Bookstore

> Locked specification generated via `frontend-grill`.

## 1. Audience & Experience Strategy
- **Primary Audience:** Curious literary readers, gift buyers, and book enthusiasts (20–45).
- **Audience Goal (<10s):** Discover curated books immediately on scroll, search/filter by title/author/genre, or ask **Boreal AI** for tailored recommendations.
- **Emotional & Visual Takeaway:** Quiet literary atelier meets kinetic editorial luxury — warm ivory paper calm punctuated by deep plum/obsidian contrast and silky scroll-driven storytelling.
- **Awwwards References (Mechanics Borrowed):** `unoveglobal.com` (cinematic editorial scroll pacing, high-contrast light/dark section rhythm, pinned storytelling).
- **Anti-References (Never Do):** Generic SaaS 3-card templates, cluttered Amazon-style grids, neon AI gradients.

## 2. Technical Stack & Scroll Engine
- **Framework / Styling / Routing:** React 19 + Vite 8 + Tailwind CSS 3.4 + `react-router-dom` 7 + `lucide-react`.
- **Scroll & Animation Engine:** `gsap` + `ScrollTrigger` + `lenis` (with `gsap.context()` cleanup).
- **Media & Asset Strategy:** Existing book covers + `aurora-logo.png` + crisp editorial framing (`1px` hairlines, no heavy WebGL/3D).

## 3. Visual Identity & Color Architecture
- **Palette Tokens:**
  - Light Surface (Warm Ivory): `#F7F4EF` (`#FFFFFF` cards)
  - Dark Surface (Obsidian): `#111111` (Footer Curtain: `#070707`)
  - Colored/Brand Surface (Deep Purple): `#2A1B3D`
  - Primary Accent / CTA (Aurora Plum): `#6B21A8` (hover `#581C87`)
  - Secondary Accent (Gold): `#D4AF37` (sparingly for `1px` hairlines/progress/badges)
  - Ink / Muted Ink / Hairline Border: `#18141F` / `#5E5768` / `#E6DFD5` (dark hairlines: `rgba(247, 244, 239, 0.14)`)
- **Typography:**
  - Display: `'General Sans'` (Fontshare) + `'Instrument Serif'` (italic, `font-weight: 400` for evocative phrasing)
  - Body: `'General Sans'`
  - Mono / Tabular: `'JetBrains Mono'` (`tabular-nums` for prices, stock, and `01/02/03` indices)
- **Browser Surfaces & Contrast:** WCAG AA (`4.5:1`), themed `::selection`, `:focus-visible`, custom scrollbar.

## 4. Header, Navigation & Site Flow
- **Header Style & Scroll Behavior:** Always-visible sticky architectural bar with `1px` bottom hairline, backdrop blur, wordmark, nav links, search trigger, cart bag, `EN/PT` toggle, and `Sign In` pill CTA.
- **Mobile Navigation Pattern:** Fullscreen editorial overlay menu.
- **Site Flow & Routes:** `/`, `/books`, `/authors`, `/publish`, `/contact`, `/login`, `/register`, `/verify-email`, `/orders`. Users can browse and search books immediately on `/` as soon as they scroll.

## 5. Section-by-Section Blueprint (`/`)
| # | Section Name | Objective | Surface | Layout & Elements | Primary CTA |
|---|---|---|---|---|---|
| 1 | Hero Stage | Set literary tone & offer instant search | Obsidian (`#111111`) | Monumental `General Sans` + `Instrument Serif` headline, subtle plum glow, live search input | `Explore Collection` |
| 2 | Immediate Books Catalog | Let users browse & filter books right away | Warm Ivory (`#F7F4EF`) | Category/genre filter bar + searchable `BookCard` grid | `Add to Cart` / `Buy Now` |
| 3 | Boreal AI Showcase | Highlight local Llama 3.1 + ChromaDB concierge | Deep Purple (`#2A1B3D`) | Pinned sticky left narrative + scrubbed right prompt/recommendation cards | `Ask Boreal AI` |
| 4 | Editorial Pillars | Communicate Aurora's value propositions | Warm Ivory (`#F7F4EF`) | Asymmetric 12-col grid with `01/02/03` markers & `1px` dividers | `Meet Our Authors` |
| 5 | Footer Curtain | Ground the page with brand & navigation | Obsidian (`#070707`) | Oversized `Aurora` wordmark, nav links, copyright | Nav links |

## 6. Scroll Choreography & Animation Spec
- **Signature Interactions (Max 2):**
  1. `ScrollTrigger` pinned sticky split in Section 3 (Boreal AI Showcase).
  2. Scrubbed Hero-to-Catalog transition + `1px` top scroll progress indicator.
- **Supporting Motion:** Split-text hero reveal, staggered `BookCard` & pillar entrances (`transform` + `opacity` only), subtle desktop magnetic CTAs, `Lenis` smooth scroll.
- **Mobile (`<768px`) & Reduced Motion:** Pinned/sticky sections degrade to clean vertical stacks on `<768px`; `prefers-reduced-motion: reduce` disables `Lenis` inertia and `ScrollTrigger` pins.

## 7. UX States, Accessibility & Performance
- Skeleton loading cards, empty-filter state with `Clear Filters`, graceful offline/API-down message.
- `npm run lint` (0 errors) + `npm run build` + visual check at `http://localhost:5173/`.
