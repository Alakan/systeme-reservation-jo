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

  // protège les routes si pas connecté
  useEffect(() => {
    const protectedPaths = ['/mes-reservations', '/dashboard', '/modifier-profil']
    if (!isAuthenticated && protectedPaths.includes(window.location.pathname)) {
      navigate('/login')
    }
  }, [isAuthenticated, navigate])

  return (
    <nav className="menu-rapide">
      <button 
        className="menu-button" 
        onClick={() => setIsOpen((o) => !o)}
      >
        ☰
      </button>

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
              {/* Affichage du pseudo */}
              <div className="menu-user">
                Salut, <strong>{user.username || user.sub}</strong> !
              </div>
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
              <button onClick={() => { setIsOpen(false); navigate('/login') }}>
                Connexion
              </button>
              <Link to="/register">
                <button onClick={() => setIsOpen(false)}>
                  Créer un compte
                </button>
              </Link>
            </>
          )}
        </div>
      )}
    </nav>
  )
}
