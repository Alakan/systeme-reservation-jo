// src/components/MenuRapide.js
import React, { useState, useContext } from 'react'
import { useNavigate }                 from 'react-router-dom'
import { UserContext }                 from '../contexts/UserContext'
import '../styles/MenuRapide.css'

export default function MenuRapide() {
  const [isOpen, setIsOpen]        = useState(false)
  const { isAuthenticated, roles, setUser } = useContext(UserContext)
  const navigate                   = useNavigate()

  // ferme le menu ET navigue
  const goTo = (path) => {
    setIsOpen(false)
    navigate(path, { replace: true })
  }

  // logout : supprime token, reset user, ferme menu, redirige
  const handleLogout = () => {
    localStorage.removeItem('token')
    setUser(null)
    goTo('/login')
  }

  // choisi le bon dashboard
  const dashboardPath = roles.includes('ADMINISTRATEUR')
    ? '/admin'
    : '/dashboard'

  return (
    <nav className="menu-rapide">
      <button
        className="menu-button"
        onClick={() => setIsOpen(o => !o)}
      >
        ☰
      </button>

      {isAuthenticated && (
        <span className="user-info-top">
          Salut, <strong>
            {roles.includes('ADMINISTRATEUR') ? 'Admin' : 'User'}
          </strong> !
        </span>
      )}

      {isOpen && (
        <div className="menu-items">
          <button onClick={() => goTo('/')}>Accueil</button>
          <button onClick={() => goTo('/evenements')}>Événements</button>

          {isAuthenticated ? (
            <>
              <button onClick={() => goTo(dashboardPath)}>
                Dashboard
              </button>
              {roles.includes('UTILISATEUR') && (
                <button onClick={() => goTo('/mes-reservations')}>
                  Mes réservations
                </button>
              )}
              <button onClick={() => goTo('/modifier-profil')}>
                Mon profil
              </button>
              <button className="logout-btn" onClick={handleLogout}>
                Déconnexion
              </button>
            </>
          ) : (
            <>
              <button onClick={() => goTo('/login')}>
                Connexion
              </button>
              <button onClick={() => goTo('/register')}>
                Créer un compte
              </button>
            </>
          )}
        </div>
      )}
    </nav>
  )
}
