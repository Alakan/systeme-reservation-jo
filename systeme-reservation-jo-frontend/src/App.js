// src/App.js
import { BrowserRouter as Router } from 'react-router-dom';
import { useEffect } from 'react';
import AppRouter from './router/AppRouter';
import MenuRapide from './components/MenuRapide';
import { UserProvider } from './contexts/UserContext';
import { ThemeProvider } from './contexts/ThemeContext';

function App() {
  // Ajout d'un useEffect pour loguer la valeur de REACT_APP_API_URL
  useEffect(() => {
    console.log("REACT_APP_API_URL:", process.env.REACT_APP_API_URL);
  }, []);

  return (
    <UserProvider>
      <ThemeProvider>
        <Router>
          <MenuRapide />
          <AppRouter />
        </Router>
      </ThemeProvider>
    </UserProvider>
  );
}

export default App;
