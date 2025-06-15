import React, { useState, useEffect, useContext } from 'react'
import { useNavigate, Link }               from 'react-router-dom'
import { UserContext }                     from '../contexts/UserContext'
import '../styles/MenuRapide.css'

export default function MenuRapide() {
  const [isOpen, setIsOpen] = useState(false)
  const { user, setUser }   = useContext(UserContext)
  const navigate            = useNavigate()
  const isAuth              = Boolean(user)

  const handleLogout = () => {
    localStorage.removeItem('token')
    setUser(null)
    setIsOpen(false)
    navigate('/login')
  }

  // protège les pages privées
  useEffect(() => {
    const prot = ['/mes-reservations', '/dashboard', '/modifier-profil']
    if (!isAuth && prot.includes(window.location.pathname)) {
      navigate('/login')
    }
  }, [isAuth, navigate])

  return (
    <nav className="menu-rapide">
      {/* GAUCHE : bouton ☰ */}
      <button
        className="menu-button"
        onClick={() => setIsOpen(o => !o)}
      >
        ☰
      </button>

      {/* DROITE : pseudo ou lien Connexion */}
      {isAuth ? (
        <div className="user-info-top">
          Salut, <strong>{user.username || user.sub}</strong> !
        </div>
      ) : (
        <Link className="login-link" to="/login">
          Connexion
        </Link>
      )}

      {/* MENU DÉROULANT sous le bouton */}
      {isOpen && (
        <div className="menu-items">
          <button onClick={() => { setIsOpen(false); navigate('/') }}>
            Accueil
          </button>
          <button onClick={() => { setIsOpen(false); navigate('/evenements') }}>
            Événements
          </button>

          {isAuth ? (
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
    </nav>
  )
}
