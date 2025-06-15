// src/App.test.js
import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import App from './App';

// on mocke react-router-dom et axios via __mocks__/, rien à changer ici

describe('App', () => {
  it('affiche le bouton de menu et ouvre le menu au clic', () => {
    render(<App />);

    // 1) le bouton ☰ doit exister
    const menuBtn = screen.getByRole('button', { name: '☰' });
    expect(menuBtn).toBeInTheDocument();

    // 2) par défaut on ne voit pas l’item "Accueil"
    expect(screen.queryByText('Accueil')).toBeNull();

    // 3) après un clic, l’item "Accueil" apparaît
    fireEvent.click(menuBtn);
    expect(screen.getByText('Accueil')).toBeInTheDocument();
  });
});
