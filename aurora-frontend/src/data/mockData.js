import coverLotr from '../assets/covers/lotr.png';
import cover1984 from '../assets/covers/1984.png';
import coverBraveNewWorld from '../assets/covers/brave-new-world.png';
import coverNeuromancer from '../assets/covers/neuromancer.png';
import coverHamlet from '../assets/covers/hamlet.png';

// Shape mirrors AuthorReadDTO
export const authors = [
  { id: 1, name: 'J.R.R. Tolkien' },
  { id: 2, name: 'George Orwell' },
  { id: 3, name: 'Aldous Huxley' },
  { id: 4, name: 'William Gibson' },
  { id: 5, name: 'William Shakespeare' },
];

// Shape mirrors CategoryReadDTO
export const categories = [
  { id: 1, name: 'Fiction' },
  { id: 2, name: 'Science Fiction' },
  { id: 3, name: 'Fantasy' },
  { id: 4, name: 'Drama' },
  { id: 5, name: 'Biography' },
];

// Shape mirrors GenreReadDTO
export const genres = [
  { id: 1, name: 'High Fantasy' },
  { id: 2, name: 'Cyberpunk' },
  { id: 3, name: 'Dystopian' },
  { id: 4, name: 'Classic Literature' },
  { id: 5, name: 'Romanticism' },
];

// Shape mirrors BookReadDTO exactly
export const books = [
  {
    id: 1,
    title: 'The Lord of the Rings: The Fellowship of the Ring',
    description: 'The first part of the epic fantasy novel. A young hobbit named Frodo is entrusted with an ancient ring and must embark on a perilous journey across Middle-earth to destroy it in the fires of Mount Doom.',
    price: 59.90,
    imageUrl: coverLotr,
    stock: 12,
    authors: [{ id: 1, name: 'J.R.R. Tolkien' }],
    categories: [{ id: 1, name: 'Fiction' }, { id: 3, name: 'Fantasy' }],
    genres: [{ id: 1, name: 'High Fantasy' }],
  },
  {
    id: 2,
    title: '1984',
    description: 'A dystopian social science fiction novel. In a totalitarian society ruled by Big Brother, Winston Smith struggles to maintain his sanity and humanity as he secretly rebels against the oppressive regime.',
    price: 39.90,
    imageUrl: cover1984,
    stock: 25,
    authors: [{ id: 2, name: 'George Orwell' }],
    categories: [{ id: 1, name: 'Fiction' }, { id: 2, name: 'Science Fiction' }],
    genres: [{ id: 3, name: 'Dystopian' }],
  },
  {
    id: 3,
    title: 'Brave New World',
    description: 'A dystopian novel set in a futuristic World State. Citizens are genetically modified and socially conditioned to serve a ruling order. One man dares to question the manufactured happiness.',
    price: 42.50,
    imageUrl: coverBraveNewWorld,
    stock: 8,
    authors: [{ id: 3, name: 'Aldous Huxley' }],
    categories: [{ id: 1, name: 'Fiction' }, { id: 2, name: 'Science Fiction' }],
    genres: [{ id: 3, name: 'Dystopian' }],
  },
  {
    id: 4,
    title: 'Neuromancer',
    description: 'A pioneering cyberpunk science fiction novel. A washed-up computer hacker is hired for one last job: to hack into the most powerful artificial intelligence in existence.',
    price: 49.90,
    imageUrl: coverNeuromancer,
    stock: 15,
    authors: [{ id: 4, name: 'William Gibson' }],
    categories: [{ id: 2, name: 'Science Fiction' }],
    genres: [{ id: 2, name: 'Cyberpunk' }],
  },
  {
    id: 5,
    title: 'Hamlet',
    description: 'A classic tragedy written by William Shakespeare. Prince Hamlet seeks to avenge his father\'s murder by his uncle, who has seized the throne and married Hamlet\'s mother.',
    price: 29.90,
    imageUrl: coverHamlet,
    stock: 30,
    authors: [{ id: 5, name: 'William Shakespeare' }],
    categories: [{ id: 1, name: 'Fiction' }, { id: 4, name: 'Drama' }],
    genres: [{ id: 4, name: 'Classic Literature' }, { id: 5, name: 'Romanticism' }],
  },
];

// Helper: get books by author id
export function getBooksByAuthor(authorId) {
  return books.filter(book =>
    book.authors.some(a => a.id === authorId)
  );
}
