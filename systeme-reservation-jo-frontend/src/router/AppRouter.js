import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import PrivateRoute from './PrivateRoute';

import Home                   from '../pages/Home';
import Evenements             from '../pages/Evenements';
import Login                  from '../pages/Login';
import Register               from '../pages/Register';
import Admin                  from '../pages/Admin';
import DashboardUtilisateurs  from '../pages/DashboardUtilisateurs';
import MesReservations        from '../pages/MesReservations';
import ModifierProfil         from '../pages/ModifierProfil';
import Reservation            from '../pages/Reservation';
import Billet                 from '../pages/Billet';

function NotFound() {
  return (
    <div style={{ textAlign: 'center', padding: 50 }}>
      <h1>404 – Page non trouvée</h1>
      <p>La page demandée n’existe pas.</p>
    </div>
  );
}

export default function AppRouter() {
  return (
    <Routes>
      {/* PUBLIQUES */}
      <Route path="/" element={<Home />} />
      <Route path="/evenements" element={<Evenements />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      {/* DASHBOARD UTILISATEUR */}
      <Route
        path="/dashboard"
        element={
          <PrivateRoute allowedRoles={['UTILISATEUR']}>
            <DashboardUtilisateurs />
          </PrivateRoute>
        }
      />

      {/* DASHBOARD ADMIN */}
      <Route
        path="/admin"
        element={
          <PrivateRoute allowedRoles={['ADMINISTRATEUR']}>
            <Admin />
          </PrivateRoute>
        }
      />

      {/* AUTRES ROUTES PROTÉGÉES */}
      <Route
        path="/mes-reservations"
        element={
          <PrivateRoute allowedRoles={['UTILISATEUR']}>
            <MesReservations />
          </PrivateRoute>
        }
      />
      <Route
        path="/modifier-profil"
        element={
          <PrivateRoute allowedRoles={['UTILISATEUR']}>
            <ModifierProfil />
          </PrivateRoute>
        }
      />
      <Route
        path="/reservation"
        element={
          <PrivateRoute allowedRoles={['UTILISATEUR']}>
            <Reservation />
          </PrivateRoute>
        }
      />
      <Route
        path="/billet/:id"
        element={
          <PrivateRoute allowedRoles={['UTILISATEUR']}>
            <Billet />
          </PrivateRoute>
        }
      />

      {/* 404 */}
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}
