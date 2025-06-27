import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';

import 'bootstrap/dist/css/bootstrap.min.css';
import './index.css';

import App from './App';
import reportWebVitals from './reportWebVitals';

import { UserProvider }  from './contexts/UserContext';
import { ThemeProvider } from './contexts/ThemeContext';
import { CartProvider }  from './contexts/CartContext';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <UserProvider>
      <ThemeProvider>
        <CartProvider>
          <BrowserRouter>
            <App />
          </BrowserRouter>
        </CartProvider>
      </ThemeProvider>
    </UserProvider>
  </React.StrictMode>
);

reportWebVitals();
