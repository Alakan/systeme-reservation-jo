import React, { createContext, useState, useEffect } from 'react';

// On expose user + setUser pour pouvoir déconnecter “en dur”
export const UserContext = createContext({
  user: null,
  setUser: () => {}
})

export const UserProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const token          = localStorage.getItem('token')

  useEffect(() => {
    if (!token) {
      setUser(null)
      return
    }
    try {
      const payload = JSON.parse(atob(token.split('.')[1]))
      setUser(payload)
    } catch (err) {
      console.error('Décodage JWT impossible:', err)
      setUser(null)
    }
  }, [token])

  return (
    <UserContext.Provider value={{ user, setUser }}>
      {children}
    </UserContext.Provider>
  )
}
