// src/__mocks__/react-router-dom.js
const React = require('react');
module.exports = {
  BrowserRouter: ({ children }) => <>{children}</>,
  Routes: ({ children }) => <>{children}</>,
  Route: () => null,
  Link: ({ children }) => <>{children}</>,
  useNavigate: () => () => {},      // stub de navigate()
  useParams: () => ({}),            // si tu l’utilises
};
