import { Routes, Route } from 'react-router-dom';
import Home from '../pages/Home';
import Evenements from '../pages/Evenements';
import Login from '../pages/Login';
import Register from '../pages/Register';
import Admin from '../pages/Admin';
import Reservation from '../pages/Reservation';
import MesReservations from '../pages/MesReservations'; // ✅ Importation de la nouvelle page
import PrivateRoute from './PrivateRoute';

function AppRouter() {
    return (
        <Routes> {/* ✅ Suppression du BrowserRouter ici */}
            <Route path="/" element={<Home />} />
            <Route path="/evenements" element={<Evenements />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/admin" element={<PrivateRoute element={<Admin />} />} />
            <Route path="/reservations" element={<PrivateRoute element={<Reservation />} />} />
            <Route path="/mes-reservations" element={<PrivateRoute element={<MesReservations />} />} /> {/* ✅ Nouvelle route protégée */}
        </Routes>
    );
}

export default AppRouter;
