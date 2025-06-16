import React, { createContext, useState, useEffect } from 'react';

export const UserContext = createContext({
  user: null,
  setUser: () => {},
  isAuthenticated: false,
  roles: []
});

export function UserProvider({ children }) {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      setUser(null);
      return;
    }
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      // Normalisation des rôles : enlever le préfixe ROLE_ et passer en majuscules
      const rawRoles = payload.roles || [];
      const normalizedRoles = rawRoles.map(r =>
        r.toString().toUpperCase().replace(/^ROLE_/, '')
      );
      setUser({ ...payload, roles: normalizedRoles });
    } catch {
      setUser(null);
    }
  }, []);

  const isAuthenticated = Boolean(user);
  const roles           = user?.roles || [];

  return (
    <UserContext.Provider value={{ user, setUser, isAuthenticated, roles }}>
      {children}
    </UserContext.Provider>
  );
}
