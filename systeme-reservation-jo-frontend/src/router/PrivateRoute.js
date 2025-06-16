import React, { useContext } from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { UserContext } from '../contexts/UserContext'

export default function PrivateRoute({ children, allowedRoles = [] }) {
  const { isAuthenticated, roles } = useContext(UserContext)
  const location                   = useLocation()

  if (!isAuthenticated) {
    // pas loggé → login
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  if (
    allowedRoles.length > 0 &&
    !roles.some(r => allowedRoles.includes(r))
  ) {
    // loggé, pas le bon rôle → page publique
    return <Navigate to="/evenements" replace />
  }

  return children
}
