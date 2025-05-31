// src/router/AppRouter.js
import { Routes, Route } from 'react-router-dom';
import Home from '../pages/Home';
import Evenements from '../pages/Evenements';
import Login from '../pages/Login';
import Register from '../pages/Register';
import Admin from '../pages/Admin';
import Reservation from '../pages/Reservation';
import MesReservations from '../pages/MesReservations';
import DashboardUtilisateurs from '../pages/DashboardUtilisateurs';
import ModifierProfil from '../pages/ModifierProfil';
import AjouterUtilisateur from '../pages/AjouterUtilisateur';
import ModifierUtilisateur from '../pages/ModifierUtilisateur';
import AjouterEvenement from '../pages/AjouterEvenement';
import ModifierEvenement from '../pages/ModifierEvenement';
import AjouterReservation from '../pages/AjouterReservation';
import ModifierReservation from '../pages/ModifierReservation';
import Billet from '../pages/Billet'; // Importez le composant Billet
import PrivateRoute from './PrivateRoute';

// Composant NotFound pour les URL non reconnues
const NotFound = () => (
  <div style={{ textAlign: 'center', padding: '50px' }}>
    <h1>404 - Page non trouvée</h1>
    <p>La page que vous recherchez n'existe pas.</p>
  </div>
);

function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/evenements" element={<Evenements />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      
      {/* Routes protégées */}
      <Route path="/admin" element={<PrivateRoute element={<Admin />} />} />
      <Route path="/reservations" element={<PrivateRoute element={<Reservation />} />} />
      <Route path="/mes-reservations" element={<PrivateRoute element={<MesReservations />} />} />
      <Route path="/dashboard" element={<PrivateRoute element={<DashboardUtilisateurs />} />} />
      <Route path="/modifier-profil" element={<PrivateRoute element={<ModifierProfil />} />} />

      {/* Routes pour ajouter / modifier utilisateurs */}
      <Route path="/admin/ajouter-utilisateur" element={<PrivateRoute element={<AjouterUtilisateur />} />} />
      <Route path="/admin/modifier-utilisateur/:id" element={<PrivateRoute element={<ModifierUtilisateur />} />} />

      {/* Routes pour ajouter / modifier événements */}
      <Route path="/admin/ajouter-evenement" element={<PrivateRoute element={<AjouterEvenement />} />} />
      <Route path="/admin/modifier-evenement/:id" element={<PrivateRoute element={<ModifierEvenement />} />} />

      {/* Routes pour ajouter / modifier réservations */}
      <Route path="/admin/ajouter-reservation" element={<PrivateRoute element={<AjouterReservation />} />} />
      <Route path="/admin/modifier-reservation/:id" element={<PrivateRoute element={<ModifierReservation />} />} />

      {/* Nouvelle route pour afficher un billet */}
      <Route path="/billet/:id" element={<PrivateRoute element={<Billet />} />} />

      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}

export default AppRouter;
