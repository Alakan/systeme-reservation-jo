import React from 'react';
import '../styles/Footer.css';

export default function Footer() {
  return (
    <footer className="footer">
      <p>&copy; 2025 Jeux Olympiques</p>
      <div className="rings">
        <img src="/rings.svg" alt="Anneaux Olympiques" />
      </div>
    </footer>
  );
}
