// src/contexts/ThemeContext.js
import React, { createContext, useState, useEffect, useContext } from 'react';
import { UserContext } from './UserContext';  // ajuste le chemin si besoin

export const ThemeContext = createContext({
  theme: 'light',
  setTheme: () => {}
});

export function ThemeProvider({ children }) {
  const { roles } = useContext(UserContext);

  // 1) Récupère le choix utilisateur en cache, sinon on déduit du rôle
  const savedTheme = localStorage.getItem('theme');
  const initialTheme = savedTheme
    ? savedTheme
    : roles.includes('ADMINISTRATEUR')
      ? 'dark'
      : 'light';

  const [theme, setTheme] = useState(initialTheme);

  // 2) À chaque changement de thème, on :
  //    - Sauvegarde en localStorage
  //    - Met à jour data-theme sur <html>
  useEffect(() => {
    localStorage.setItem('theme', theme);
    document.documentElement.setAttribute('data-theme', theme);
  }, [theme]);

  // 3) Si les rôles changent (login/logout), et que l’utilisateur
  //    n’a pas déjà forcé son choix (savedTheme), on réinitialise
  useEffect(() => {
    if (!savedTheme) {
      const roleBased = roles.includes('ADMINISTRATEUR') ? 'dark' : 'light';
      setTheme(roleBased);
    }
  }, [roles, savedTheme]);

  return (
    <ThemeContext.Provider value={{ theme, setTheme }}>
      {children}
    </ThemeContext.Provider>
  );
}

// Hook pour l’utiliser dans tes composants
export const useTheme = () => useContext(ThemeContext);
