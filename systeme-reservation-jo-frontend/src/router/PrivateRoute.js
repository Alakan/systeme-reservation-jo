import React, { useContext } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { UserContext } from '../contexts/UserContext';

/**
 * children     : composant à afficher si OK
 * allowedRoles : rôles autorisés (vide = tout utilisateur connecté)
 */
export default function PrivateRoute({ children, allowedRoles = [] }) {
  const { isAuthenticated, roles } = useContext(UserContext);
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  if (
    allowedRoles.length > 0 &&
    !roles.some((r) => allowedRoles.includes(r))
  ) {
    return <Navigate to="/evenements" replace />;
  }
  return children;
}
