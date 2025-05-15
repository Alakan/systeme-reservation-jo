import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Evenements from '../pages/Evenements';
import Login from '../pages/Login';
import Admin from '../pages/Admin';

function AppRouter() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Evenements />} />
                <Route path="/login" element={<Login />} />
                <Route path="/admin" element={<Admin />} />
            </Routes>
        </BrowserRouter>
    );
}

export default AppRouter;
