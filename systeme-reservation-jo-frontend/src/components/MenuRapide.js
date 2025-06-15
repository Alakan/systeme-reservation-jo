import React, { useState, useEffect, useContext } from 'react'
import { useNavigate, Link }               from 'react-router-dom'
import { UserContext }                     from '../contexts/UserContext'
import '../styles/MenuRapide.css'

export default function MenuRapide() {
  const [isOpen, setIsOpen] = useState(false)
  const { user, setUser }   = useContext(UserContext)
  const navigate            = useNavigate()

  const isAuthenticated = Boolean(user)

  // Logout : efface token + reset contexte + redirige
  const handleLogout = () => {
    localStorage.removeItem('token')
    setUser(null)
    setIsOpen(false)
    navigate('/login')
  }

  // Protection des routes
  useEffect(() => {
    const protectedPaths = ['/mes-reservations', '/dashboard', '/modifier-profil']
    if (!isAuthenticated && protectedPaths.includes(window.location.pathname)) {
      navigate('/login')
    }
  }, [isAuthenticated, navigate])

  return (
    <div className="menu-rapide-wrapper">
      {/* Barre du haut : menu button à gauche + pseudo à droite */}
      <div className="menu-top-bar">
        <button
          className="menu-button"
          onClick={() => setIsOpen((o) => !o)}
        >
          ☰
        </button>

        {isAuthenticated ? (
          <div className="user-info-top">
            Salut, <strong>{user.username || user.sub}</strong> !
          </div>
        ) : (
          <Link className="login-link" to="/login">
            Connexion
          </Link>
        )}
      </div>

      {/* Le menu déroulant */}
      {isOpen && (
        <div className="menu-items">
          <button onClick={() => { setIsOpen(false); navigate('/') }}>
            Accueil
          </button>
          <button onClick={() => { setIsOpen(false); navigate('/evenements') }}>
            Événements
          </button>

          {isAuthenticated ? (
            <>
              <div className="menu-separator" />
              <button onClick={() => { setIsOpen(false); navigate('/mes-reservations') }}>
                Mes Réservations
              </button>
              <button onClick={() => { setIsOpen(false); navigate('/modifier-profil') }}>
                Modifier mon profil
              </button>
              <button onClick={handleLogout}>
                Déconnexion
              </button>
            </>
          ) : (
            <>
              <div className="menu-separator" />
              <Link to="/register">
                <button onClick={() => setIsOpen(false)}>
                  Créer un compte
                </button>
              </Link>
            </>
          )}
        </div>
      )}
    </div>
  )
}
