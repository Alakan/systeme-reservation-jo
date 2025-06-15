// src/contexts/ThemeContext.js
import { createContext, useState, useContext } from 'react';

export const ThemeContext = createContext({
  theme: 'light',
  setTheme: () => {}
});

export const ThemeProvider = ({ children }) => {
  const token = localStorage.getItem('token');
  let defaultTheme = 'light';

  try {
    if (token) {
      const payload = JSON.parse(atob(token.split('.')[1]));
      if (payload.roles && payload.roles.includes('ADMIN')) {
        defaultTheme = 'dark';
      }
    }
  } catch (error) {
    console.error("Erreur lors du décodage du token :", error);
  }

  const [theme, setTheme] = useState(defaultTheme);

  return (
    <ThemeContext.Provider value={{ theme, setTheme }}>
      {children}
    </ThemeContext.Provider>
  );
};

// Hook personnalisé pour utiliser le ThemeContext
export const useTheme = () => useContext(ThemeContext);
