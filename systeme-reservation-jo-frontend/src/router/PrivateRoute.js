import React, { useContext }      from 'react';
import { Navigate, useLocation }  from 'react-router-dom';
import { UserContext }            from '../contexts/UserContext';

/**
 * children     : composant à afficher si OK
 * allowedRoles : tableau de rôles autorisés (vide = tout user connecté)
 */
export default function PrivateRoute({ children, allowedRoles = [] }) {
  const { isAuthenticated, roles } = useContext(UserContext);
  const location                   = useLocation();

  if (!isAuthenticated) {
    // non connecté → page de login
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (
    allowedRoles.length > 0 &&
    !roles.some(r => allowedRoles.includes(r))
  ) {
    // connecté mais pas dans les rôles autorisés
    return <Navigate to="/evenements" replace />;
  }

  return children;
}
