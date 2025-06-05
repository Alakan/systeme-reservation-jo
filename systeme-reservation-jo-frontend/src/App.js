// src/App.js
import { BrowserRouter as Router } from 'react-router-dom';
import AppRouter from './router/AppRouter';
import MenuRapide from './components/MenuRapide';
import { UserProvider } from './contexts/UserContext';
import { ThemeProvider } from './contexts/ThemeContext';

function App() {
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
