import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Activity, LayoutDashboard, Calendar, User, LogOut, Shield, Search, Bell, CreditCard, Star, FileText, Clock, Menu, X } from 'lucide-react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { cn } from '../lib/utils';
import { notificationService } from '../services/api';

const FloatingNav = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const role = localStorage.getItem('role');
  const token = localStorage.getItem('token');
  const userId = localStorage.getItem('userId');
  const [unreadCount, setUnreadCount] = useState(0);
  const [mobileOpen, setMobileOpen] = useState(false);

  useEffect(() => {
    if (token && userId) {
      notificationService.getUnreadCount(userId)
        .then(res => setUnreadCount(res.data?.unreadCount || 0))
        .catch(() => {});
    }
  }, [token, userId, location.pathname]);

  const getNavItems = () => {
    if (!token) return [
      { icon: Activity, label: 'Features', path: '/#features' },
      { icon: Search, label: 'Specialists', path: '/search' },
    ];
    if (role === 'ADMIN') return [
      { icon: LayoutDashboard, label: 'Dashboard', path: '/dashboard' },
      { icon: Shield, label: 'Admin Hub', path: '/admin' },
      { icon: Search, label: 'Providers', path: '/search' },
      { icon: Calendar, label: 'Appointments', path: '/appointments' },
      { icon: CreditCard, label: 'Payments', path: '/payments' },
    ];
    if (role === 'DOCTOR') return [
      { icon: LayoutDashboard, label: 'Dashboard', path: '/dashboard' },
      { icon: User, label: 'My Profile', path: '/provider-profile' },
      { icon: Calendar, label: 'Appointments', path: '/appointments' },
      { icon: Clock, label: 'My Slots', path: '/slots' },
      { icon: FileText, label: 'Records', path: '/records' },
      { icon: Star, label: 'Reviews', path: '/reviews' },
      { icon: CreditCard, label: 'Earnings', path: '/payments' },
    ];
    // PATIENT
    return [
      { icon: LayoutDashboard, label: 'Dashboard', path: '/dashboard' },
      { icon: User, label: 'My Profile', path: '/profile' },
      { icon: Calendar, label: 'Appointments', path: '/appointments' },
      { icon: Search, label: 'Find Doctors', path: '/search' },
      { icon: FileText, label: 'Records', path: '/records' },
      { icon: Star, label: 'Reviews', path: '/reviews' },
      { icon: CreditCard, label: 'Payments', path: '/payments' },
    ];
  };

  const navItems = getNavItems();

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path;

  const NavLink = ({ item, onClick }) => (
    <Link
      to={item.path}
      onClick={onClick}
      className={cn(
        'flex items-center gap-2 px-3 py-2 rounded-xl transition-all duration-200',
        isActive(item.path)
          ? 'bg-medical-500/20 text-medical-400 font-semibold'
          : 'hover:bg-white/10 text-white/60 hover:text-white'
      )}
    >
      <item.icon size={16} />
      <span className="text-sm font-medium">{item.label}</span>
    </Link>
  );

  return (
    <>
      {/* Desktop floating nav */}
      <motion.nav
        initial={{ y: -100, x: '-50%', opacity: 0 }}
        animate={{ y: 0, x: '-50%', opacity: 1 }}
        className="glass-nav hidden md:flex"
        style={{ maxWidth: '95vw' }}
      >
        <Link to="/" className="flex items-center gap-2 mr-2 group shrink-0">
          <div className="w-9 h-9 bg-medical-500 rounded-xl flex items-center justify-center group-hover:rotate-12 transition-transform">
            <Activity className="text-white" size={18} />
          </div>
          <span className="font-bold text-lg tracking-tight">CareSync</span>
        </Link>

        <div className="h-5 w-[1px] bg-white/10 mx-2 shrink-0" />

        <div className="flex items-center gap-1 overflow-x-auto scrollbar-hide">
          {navItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              className={cn(
                'flex items-center gap-1.5 px-3 py-2 rounded-xl transition-all duration-200 shrink-0',
                isActive(item.path)
                  ? 'bg-medical-500/20 text-medical-400'
                  : 'hover:bg-white/10 text-white/60 hover:text-white'
              )}
            >
              <item.icon size={15} />
              <span className="text-sm font-medium">{item.label}</span>
            </Link>
          ))}
        </div>

        <div className="flex items-center gap-2 ml-2 shrink-0">
          {token && (
            <Link to="/notifications" className="relative p-2 rounded-xl hover:bg-white/10 text-white/50 hover:text-white transition-all">
              <Bell size={18} />
              {unreadCount > 0 && (
                <span className="absolute -top-0.5 -right-0.5 w-4 h-4 bg-medical-500 rounded-full text-[10px] font-bold flex items-center justify-center text-white">
                  {unreadCount > 9 ? '9+' : unreadCount}
                </span>
              )}
            </Link>
          )}
          {token ? (
            <button onClick={handleLogout} className="p-2 rounded-xl hover:bg-rose-500/10 text-white/30 hover:text-rose-400 transition-all" title="Sign Out">
              <LogOut size={18} />
            </button>
          ) : (
            <Link to="/login" className="premium-btn text-sm py-2 px-5">Get Started</Link>
          )}
        </div>
      </motion.nav>

      {/* Mobile nav */}
      <div className="md:hidden fixed top-4 left-0 right-0 z-50 px-4">
        <div className="glass-card px-4 py-3 flex items-center justify-between rounded-2xl">
          <Link to="/" className="flex items-center gap-2">
            <div className="w-8 h-8 bg-medical-500 rounded-lg flex items-center justify-center">
              <Activity className="text-white" size={16} />
            </div>
            <span className="font-bold">CareSync</span>
          </Link>
          <div className="flex items-center gap-2">
            {token && (
              <Link to="/notifications" className="relative p-2">
                <Bell size={18} className="text-white/60" />
                {unreadCount > 0 && (
                  <span className="absolute top-0 right-0 w-4 h-4 bg-medical-500 rounded-full text-[9px] font-bold flex items-center justify-center">
                    {unreadCount > 9 ? '9+' : unreadCount}
                  </span>
                )}
              </Link>
            )}
            <button onClick={() => setMobileOpen(!mobileOpen)} className="p-2 rounded-xl bg-white/5 border border-white/10">
              {mobileOpen ? <X size={18} /> : <Menu size={18} />}
            </button>
          </div>
        </div>

        <AnimatePresence>
          {mobileOpen && (
            <motion.div
              initial={{ opacity: 0, y: -8, scale: 0.97 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, y: -8, scale: 0.97 }}
              className="glass-card mt-2 p-4 rounded-2xl"
            >
              <div className="grid grid-cols-2 gap-2">
                {navItems.map(item => (
                  <NavLink key={item.path} item={item} onClick={() => setMobileOpen(false)} />
                ))}
              </div>
              {token ? (
                <button onClick={handleLogout}
                  className="mt-3 w-full flex items-center justify-center gap-2 py-2.5 rounded-xl bg-rose-500/10 text-rose-400 font-medium text-sm hover:bg-rose-500/20 transition-all">
                  <LogOut size={16} /> Sign Out
                </button>
              ) : (
                <Link to="/login" onClick={() => setMobileOpen(false)}
                  className="mt-3 premium-btn w-full flex items-center justify-center text-sm py-2.5">
                  Get Started
                </Link>
              )}
            </motion.div>
          )}
        </AnimatePresence>
      </div>
    </>
  );
};

export default FloatingNav;
