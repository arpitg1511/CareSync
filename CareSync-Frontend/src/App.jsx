import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import DoctorSearch from './pages/DoctorSearch';
import Profile from './pages/Profile';
import Appointments from './pages/Appointments';
import AdminHub from './pages/AdminHub';
import ProviderProfile from './pages/ProviderProfile';
import ManageAvailability from './pages/ManageAvailability';
import MedicalRecords from './pages/MedicalRecords';
import Reviews from './pages/Reviews';

function App() {
  return (
    <Router>
      <div className="bg-background min-h-screen text-foreground font-sans selection:bg-medical-500/30">
        <div className="fixed inset-0 pointer-events-none z-[9999] bg-grain" />
        
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/search" element={<DoctorSearch />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/provider-profile" element={<ProviderProfile />} />
          <Route path="/appointments" element={<Appointments />} />
          <Route path="/admin" element={<AdminHub />} />
          <Route path="/availability" element={<ManageAvailability />} />
          <Route path="/records" element={<MedicalRecords />} />
          <Route path="/reviews" element={<Reviews />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
