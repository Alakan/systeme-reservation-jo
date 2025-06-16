import React, { createContext, useState, useEffect } from 'react'

export const UserContext = createContext({
  user: null,
  setUser: () => {},
  isAuthenticated: false,
  roles: []
})

export function UserProvider({ children }) {
  const [user, setUser] = useState(null)

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (!token) {
      setUser(null)
      return
    }
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      console.log('▶️ JWT payload:', payload)

      // NORMALISATION DES RÔLES
      const rawRoles = payload.roles || []
      const normalizedRoles = rawRoles.map(r =>
        r.toString().toUpperCase().replace(/^ROLE_/, '')
      )

      setUser({
        ...payload,
        roles: normalizedRoles
      })
      console.log('rôles normalisés:', normalizedRoles)
    } catch (err) {
      console.error('impossible de décoder le token', err)
      setUser(null)
    }
  }, [])

  const isAuthenticated = Boolean(user)
  const roles           = user?.roles || []

  return (
    <UserContext.Provider value={{ user, setUser, isAuthenticated, roles }}>
      {children}
    </UserContext.Provider>
  )
}
