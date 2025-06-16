import React from 'react';
import { useTheme } from '../contexts/ThemeContext';

export default function Settings() {
  const { theme, setTheme } = useTheme();
  const toggle = () =>
    setTheme(theme === 'light' ? 'dark' : 'light');

  return (
    <button onClick={toggle} className="theme-toggle-btn">
      {theme === 'light' ? 'Mode Sombre' : 'Mode Clair'}
    </button>
  );
}
