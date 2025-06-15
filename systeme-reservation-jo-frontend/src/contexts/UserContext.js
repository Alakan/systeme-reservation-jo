import React, { createContext, useState, useEffect } from 'react';

export const UserContext = createContext({
  user: null,
  setUser: () => {},
  isAuthenticated: false,
  roles: []
});

export function UserProvider({ children }) {
  const [user, setUser]     = useState(null);
  const token               = localStorage.getItem('token');

  useEffect(() => {
    if (!token) return setUser(null);
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      setUser(payload);
    } catch {
      setUser(null);
    }
  }, [token]);

  const isAuthenticated = Boolean(user);
  const roles           = user?.roles || [];

  return (
    <UserContext.Provider value={{
      user, setUser, isAuthenticated, roles
    }}>
      {children}
    </UserContext.Provider>
  );
}
