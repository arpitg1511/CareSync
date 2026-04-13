import React from 'react';
import { motion } from 'framer-motion';
import { Activity, LayoutDashboard, Calendar, User, LogOut, Shield } from 'lucide-react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { cn } from '../lib/utils';

const FloatingNav = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const role = localStorage.getItem('role'); // PATIENT, PROVIDER, ADMIN
  const token = localStorage.getItem('token');

  const getNavItems = () => {
    if (!token) return [
      { icon: Activity, label: 'Features', path: '/#features' },
      { icon: User, label: 'Specialists', path: '/search' },
    ];

    if (role === 'ADMIN') return [
      { icon: LayoutDashboard, label: 'Dash', path: '/dashboard' },
      { icon: Shield, label: 'Admin Hub', path: '/admin' },
      { icon: User, label: 'All Doctors', path: '/search' },
      { icon: Calendar, label: 'Registry', path: '/appointments' },
    ];

    if (role === 'DOCTOR') return [
      { icon: LayoutDashboard, label: 'Hub', path: '/dashboard' },
      { icon: User, label: 'My Studio', path: '/provider-profile' },
      { icon: Calendar, label: 'Workload', path: '/appointments' },
    ];

    return [
      { icon: LayoutDashboard, label: 'Hub', path: '/dashboard' },
      { icon: User, label: 'My Profile', path: '/profile' },
      { icon: Calendar, label: 'My Bookings', path: '/appointments' },
      { icon: Activity, label: 'Discover', path: '/search' },
    ];
  };

  const navItems = getNavItems();

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  return (
    <motion.nav 
      initial={{ y: -100, x: '-50%', opacity: 0 }}
      animate={{ y: 0, x: '-50%', opacity: 1 }}
      className="glass-nav"
    >
      <Link to="/" className="flex items-center gap-2 mr-4 group">
        <div className="w-10 h-10 bg-medical-500 rounded-xl flex items-center justify-center group-hover:rotate-12 transition-transform">
          <Activity className="text-white" size={20} />
        </div>
        <span className="font-bold text-xl tracking-tight hidden md:block">CareSync</span>
      </Link>

      <div className="h-6 w-[1px] bg-white/10 mx-2" />

      {navItems.map((item) => (
        <Link
          key={item.path}
          to={item.path}
          className={cn(
            "flex items-center gap-2 px-4 py-2 rounded-xl transition-all duration-300",
            "hover:bg-white/10 text-white/70 hover:text-white"
          )}
        >
          <item.icon size={18} />
          <span className="text-sm font-medium hidden sm:block">{item.label}</span>
        </Link>
      ))}

      {token && (
        <button 
          onClick={handleLogout}
          className="p-3 rounded-xl hover:bg-rose-500/10 text-white/30 hover:text-rose-500 transition-all ml-2"
          title="Sign Out"
        >
          <LogOut size={20} />
        </button>
      )}

      {!token && (
        <Link to="/login" className="premium-btn ml-4">
          Get Started
        </Link>
      )}
    </motion.nav>
  );
};

export default FloatingNav;
