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
import PrivateRoute from './PrivateRoute';

function AppRouter() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/evenements" element={<Evenements />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/admin" element={<PrivateRoute element={<Admin />} />} />
      <Route path="/reservations" element={<PrivateRoute element={<Reservation />} />} />
      <Route path="/mes-reservations" element={<PrivateRoute element={<MesReservations />} />} />
      <Route path="/dashboard" element={<PrivateRoute element={<DashboardUtilisateurs />} />} />
      {/* Ajoute d'autres routes protégées si nécessaire */}
    </Routes>
  );
}

export default AppRouter;
