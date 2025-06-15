/* src/styles/MenuRapide.css */
.menu-rapide {
  position: fixed;
  top: 10px;
  left: 10px;
  right: 10px;              /* pour occuper toute la largeur */
  z-index: 1000;
  display: flex;
  justify-content: space-between; /* bouton à gauche, user à droite */
  align-items: center;
  font-family: sans-serif;
}

/* Le “☰” */
.menu-button {
  background: #333;
  color: #fff;
  font-size: 20px;
  padding: 10px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.3s ease;
}
.menu-button:hover {
  background: #555;
}

/* Le pseudo ou lien Connexion en haut à droite */
.user-info-top,
.login-link {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  text-decoration: none;
}
.login-link {
  background: #007bff;
  color: #fff;
  padding: 6px 12px;
  border-radius: 4px;
}
.login-link:hover {
  background: #0056b3;
}

/* Le menu déroulant sous le bouton */
.menu-items {
  position: absolute;
  top: 50px;  /* hauteur juste en-dessous du bouton */
  left: 10px; /* aligné sur le bouton */
  background: #ffffff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  border-radius: 6px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  min-width: 180px;
}

.menu-items button,
.menu-items a > button {
  background: none;
  border: none;
  color: #333;
  padding: 8px;
  text-align: left;
  width: 100%;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s ease;
}
.menu-items button:hover,
.menu-items a > button:hover {
  background: #f0f0f0;
}

.menu-separator {
  margin: 0.5rem 0;
  border-top: 1px solid #ddd;
}
