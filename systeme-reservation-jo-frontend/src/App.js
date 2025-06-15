// src/App.js
import React, { useEffect } from 'react';
import { BrowserRouter as Router } from 'react-router-dom';
import AppRouter from './router/AppRouter';
import MenuRapide from './components/MenuRapide';
import InactivityHandler from './components/InactivityHandler';
import { UserProvider } from './contexts/UserContext';
import { ThemeProvider } from './contexts/ThemeContext';

function App() {
  useEffect(() => {
    console.log('REACT_APP_API_URL:', process.env.REACT_APP_API_URL);
  }, []);

  return (
    <UserProvider>
      <ThemeProvider>
        <Router>
          {/* On wrappe MenuRapide + AppRouter pour gérer l'inactivité */}
          <InactivityHandler timeout={15 * 60 * 1000}>
            <MenuRapide />
            <AppRouter />
          </InactivityHandler>
        </Router>
      </ThemeProvider>
    </UserProvider>
  );
}

export default App;
