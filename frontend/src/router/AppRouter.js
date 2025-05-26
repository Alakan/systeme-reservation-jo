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
      {/* Route pour modifier le profil - vous pouvez la protéger si besoin */}
      <Route path="/modifier-profil" element={
        <PrivateRoute element={<ModifierProfil />} />
      } />
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}

export default AppRouter;
