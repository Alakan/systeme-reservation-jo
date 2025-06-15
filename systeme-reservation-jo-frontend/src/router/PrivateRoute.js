import React, { useContext } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { UserContext } from '../contexts/UserContext';

/**
 * children      : composant à afficher si OK
 * allowedRoles  : tableau de rôles autorisés (ex. ['UTILISATEUR'])
 */
export default function PrivateRoute({ children, allowedRoles = [] }) {
  const { isAuthenticated, roles } = useContext(UserContext);
  const location                   = useLocation();

  if (!isAuthenticated) {
    // pas connecté → login
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (
    allowedRoles.length > 0 &&
    !roles.some(r => allowedRoles.includes(r))
  ) {
    // rôle non autorisé → redirige vers page publique
    return <Navigate to="/evenements" replace />;
  }

  return children;
}
