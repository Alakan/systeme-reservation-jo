import React, { useEffect } from 'react';
import MenuRapide        from './components/MenuRapide';
import AppRouter         from './router/AppRouter';
import InactivityHandler from './components/InactivityHandler';

function App() {
  useEffect(() => {
    console.log('API_URL:', process.env.REACT_APP_API_URL);
  }, []);

  return (
    <InactivityHandler timeout={15 * 60 * 1000}>
      <MenuRapide />
      <AppRouter />
    </InactivityHandler>
  );
}

export default App;
