// src/App.js
import { BrowserRouter } from 'react-router-dom';
import MenuRapide from './components/MenuRapide';
import AppRouter from './router/AppRouter';

function App() {
  return (
    <BrowserRouter>
      <MenuRapide />
      <AppRouter />
    </BrowserRouter>
  );
}

export default App;
