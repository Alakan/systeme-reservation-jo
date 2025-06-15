import React, { useContext }   from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { UserContext }          from '../contexts/UserContext';

/**
 * children      : le composant à rendre si OK
 * allowedRoles  : tableau de rôles autorisés (ex. ['ADMINISTRATEUR'])
 */
export default function PrivateRoute({
  children,
  allowedRoles = []
}) {
  const { isAuthenticated, roles } = useContext(UserContext);
  const location                   = useLocation();

  if (!isAuthenticated) {
    // pas loggé → on force la connexion
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (
    allowedRoles.length > 0 &&
    !roles.some((r) => allowedRoles.includes(r))
  ) {
    // rôle non autorisé → redirige vers la page événements
    return <Navigate to="/evenements" replace />;
  }

  // OK !
  return children;
}
