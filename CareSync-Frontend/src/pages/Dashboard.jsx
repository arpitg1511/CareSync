import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { Calendar, Clock, User, Heart, Activity, Plus, Shield, Users, Star, CreditCard, Bell, FileText, ChevronRight, Loader2, TrendingUp, CheckCircle } from 'lucide-react';
import { Link } from 'react-router-dom';
import FloatingNav from '../components/FloatingNav';
import { appointmentService, notificationService, paymentService } from '../services/api';

const statusColors = {
  SCHEDULED:   'text-medical-400 bg-medical-500/10',
  RESCHEDULED: 'text-blue-400 bg-blue-500/10',
  COMPLETED:   'text-emerald-400 bg-emerald-500/10',
  CANCELLED:   'text-rose-400 bg-rose-500/10',
  NO_SHOW:     'text-amber-400 bg-amber-500/10',
};

const StatCard = ({ icon: Icon, label, value, color, delay }) => (
  <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ delay }}
    className="glass-card p-5 sm:p-6 flex items-center justify-between">
    <div>
      <p className="text-white/40 text-xs uppercase tracking-wider mb-1">{label}</p>
      <p className="text-2xl sm:text-3xl font-bold">{value}</p>
    </div>
    <div className={`w-12 h-12 rounded-2xl flex items-center justify-center ${color} bg-white/5`}>
      <Icon size={24} />
    </div>
  </motion.div>
);

const Dashboard = () => {
  const [appointments, setAppointments] = useState([]);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const role = localStorage.getItem('role');
  const userId = localStorage.getItem('userId');
  const providerId = localStorage.getItem('providerId');

  useEffect(() => {
    const load = async () => {
      try {
        let aptsRes;
        if (role === 'ADMIN') aptsRes = await appointmentService.getAllAdmin();
        else if (role === 'DOCTOR' && providerId) aptsRes = await appointmentService.getProviderSchedule(providerId);
        else aptsRes = await appointmentService.getMy();
        setAppointments(aptsRes.data || []);

        if (userId) {
          const notifRes = role === 'ADMIN'
            ? await notificationService.getAllAdmin()
            : await notificationService.getByRecipient(userId);
          const all = notifRes.data || [];
          setNotifications(all.slice(0, 4));
          setUnreadCount(all.filter(n => !n.isRead).length);
        }
      } catch (err) {
        console.error('Dashboard load error', err);
      } finally { setLoading(false); }
    };
    load();
  }, [role, userId, providerId]);

  const upcoming = appointments
    .filter(a => a.status === 'SCHEDULED' || a.status === 'RESCHEDULED')
    .slice(0, 5);
  const completed = appointments.filter(a => a.status === 'COMPLETED').length;
  const cancelled = appointments.filter(a => a.status === 'CANCELLED').length;

  const getStats = () => {
    if (role === 'ADMIN') return [
      { label: 'Total Appointments', value: appointments.length, icon: Calendar, color: 'text-medical-400', delay: 0 },
      { label: 'Completed', value: completed, icon: CheckCircle, color: 'text-emerald-400', delay: 0.1 },
      { label: 'Notifications', value: unreadCount + ' unread', icon: Bell, color: 'text-amber-400', delay: 0.2 },
    ];
    if (role === 'DOCTOR') return [
      { label: 'Patient Queue', value: upcoming.length, icon: Users, color: 'text-blue-400', delay: 0 },
      { label: 'Completed', value: completed, icon: CheckCircle, color: 'text-emerald-400', delay: 0.1 },
      { label: 'Notifications', value: unreadCount + ' unread', icon: Bell, color: 'text-amber-400', delay: 0.2 },
    ];
    return [
      { label: 'My Bookings', value: appointments.length, icon: Calendar, color: 'text-medical-400', delay: 0 },
      { label: 'Completed', value: completed, icon: CheckCircle, color: 'text-emerald-400', delay: 0.1 },
      { label: 'Notifications', value: unreadCount + ' unread', icon: Bell, color: 'text-amber-400', delay: 0.2 },
    ];
  };

  const getQuickActions = () => {
    if (role === 'ADMIN') return [
      { icon: Shield, label: 'Admin Hub', path: '/admin', color: 'bg-medical-500/10 text-medical-400' },
      { icon: Calendar, label: 'Appointments', path: '/appointments', color: 'bg-blue-500/10 text-blue-400' },
      { icon: CreditCard, label: 'Payments', path: '/payments', color: 'bg-amber-500/10 text-amber-400' },
      { icon: Bell, label: 'Notifications', path: '/notifications', color: 'bg-rose-500/10 text-rose-400' },
    ];
    if (role === 'DOCTOR') return [
      { icon: Calendar, label: 'My Schedule', path: '/appointments', color: 'bg-medical-500/10 text-medical-400' },
      { icon: Clock, label: 'Manage Slots', path: '/slots', color: 'bg-blue-500/10 text-blue-400' },
      { icon: FileText, label: 'Records', path: '/records', color: 'bg-emerald-500/10 text-emerald-400' },
      { icon: CreditCard, label: 'Earnings', path: '/payments', color: 'bg-amber-500/10 text-amber-400' },
    ];
    return [
      { icon: Plus, label: 'Book Appointment', path: '/search', color: 'bg-medical-500 text-white', highlight: true },
      { icon: Calendar, label: 'My Appointments', path: '/appointments', color: 'bg-blue-500/10 text-blue-400' },
      { icon: FileText, label: 'Health Records', path: '/records', color: 'bg-emerald-500/10 text-emerald-400' },
      { icon: CreditCard, label: 'Payments', path: '/payments', color: 'bg-amber-500/10 text-amber-400' },
    ];
  };

  return (
    <div className="min-h-screen bg-background p-4 sm:p-8 pt-28 sm:pt-32 pb-24">
      <FloatingNav />
      <div className="absolute top-0 right-0 w-[400px] h-[400px] bg-medical-500/5 rounded-full blur-[100px] pointer-events-none" />

      <div className="max-w-7xl mx-auto relative z-10">
        {/* Header */}
        <header className="mb-8 sm:mb-12">
          <motion.div initial={{ opacity: 0, y: -12 }} animate={{ opacity: 1, y: 0 }} className="flex items-center gap-2 mb-3">
            <span className="px-3 py-1 rounded-lg bg-medical-500/10 text-medical-400 text-[10px] font-bold tracking-widest uppercase border border-medical-500/20">
              {role} Account
            </span>
            <div className="h-1 w-1 bg-white/20 rounded-full" />
            <span className="text-white/30 text-[10px] font-bold tracking-widest uppercase">Secure Session</span>
          </motion.div>
          <motion.h2 initial={{ opacity: 0, x: -16 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 0.05 }}
            className="text-3xl sm:text-5xl font-bold tracking-tight mb-2">
            {role === 'ADMIN' ? 'Admin Dashboard' : role === 'DOCTOR' ? 'Provider Hub' : 'My Health Hub'}
          </motion.h2>
          <p className="text-white/40">
            {role === 'ADMIN' ? 'Platform analytics, oversight, and management.' :
             role === 'DOCTOR' ? 'Your patient schedule, slots, records, and earnings.' :
             'Your health journey at a glance.'}
          </p>
        </header>

        {loading ? (
          <div className="flex justify-center py-20"><Loader2 size={36} className="animate-spin text-medical-400" /></div>
        ) : (
          <>
            {/* Stats */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8 sm:mb-10">
              {getStats().map((s, i) => <StatCard key={i} {...s} />)}
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 sm:gap-8">
              {/* Upcoming Appointments */}
              <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.3 }}
                className="lg:col-span-2 space-y-4">
                <div className="flex items-center justify-between mb-2">
                  <h3 className="text-lg sm:text-xl font-semibold">
                    {role === 'DOCTOR' ? "Today's Queue" : 'Upcoming Appointments'}
                  </h3>
                  <Link to="/appointments" className="text-medical-400 text-sm font-medium flex items-center gap-1 hover:underline">
                    View All <ChevronRight size={14} />
                  </Link>
                </div>

                {upcoming.length === 0 ? (
                  <div className="glass-card p-10 text-center">
                    <Calendar size={32} className="mx-auto mb-3 text-white/20" />
                    <p className="text-white/40">No upcoming appointments.</p>
                    {role === 'PATIENT' && (
                      <Link to="/search" className="mt-4 premium-btn inline-flex items-center gap-2 text-sm py-2.5 px-5">
                        <Plus size={16} /> Book Now
                      </Link>
                    )}
                  </div>
                ) : (
                  upcoming.map((apt, i) => (
                    <div key={apt.appointmentId}
                      className="glass-card p-4 sm:p-5 flex items-center justify-between gap-4 hover:bg-white/[0.04] transition-colors">
                      <div className="flex items-center gap-3 sm:gap-4 min-w-0">
                        <div className="w-10 h-10 sm:w-12 sm:h-12 rounded-2xl bg-medical-500/10 flex items-center justify-center text-medical-400 shrink-0">
                          <User size={18} />
                        </div>
                        <div className="min-w-0">
                          <p className="font-semibold text-sm truncate">Appointment #{apt.appointmentId}</p>
                          {apt.appointmentDateTime && (
                            <p className="text-white/40 text-xs flex items-center gap-1 mt-0.5">
                              <Clock size={10} />
                              {new Date(apt.appointmentDateTime).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' })}
                            </p>
                          )}
                          {apt.serviceType && <p className="text-xs text-white/30">{apt.serviceType}</p>}
                        </div>
                      </div>
                      <span className={`text-[10px] px-2.5 py-1 rounded-full font-bold shrink-0 ${statusColors[apt.status] || ''}`}>
                        {apt.status}
                      </span>
                    </div>
                  ))
                )}
              </motion.div>

              {/* Right Column */}
              <motion.div initial={{ opacity: 0, x: 16 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 0.4 }}
                className="space-y-5">
                {/* Quick Actions */}
                <div className="glass-card p-5 sm:p-6">
                  <h4 className="text-sm font-bold uppercase tracking-widest text-white/30 mb-4">Quick Actions</h4>
                  <div className="grid grid-cols-2 gap-3">
                    {getQuickActions().map((action, i) => (
                      <Link key={i} to={action.path}
                        className={`flex flex-col items-center gap-2 p-3 rounded-2xl transition-all hover:scale-105 text-center ${action.highlight ? action.color + ' shadow-lg shadow-medical-500/20' : 'bg-white/5 hover:bg-white/10'}`}>
                        <action.icon size={20} className={action.highlight ? 'text-white' : action.color.split(' ')[1]} />
                        <span className={`text-[11px] font-semibold leading-tight ${action.highlight ? 'text-white' : 'text-white/60'}`}>
                          {action.label}
                        </span>
                      </Link>
                    ))}
                  </div>
                </div>

                {/* Recent Notifications */}
                {notifications.length > 0 && (
                  <div className="glass-card p-5 sm:p-6">
                    <div className="flex items-center justify-between mb-4">
                      <h4 className="text-sm font-bold uppercase tracking-widest text-white/30 flex items-center gap-2">
                        <Bell size={14} /> Notifications
                      </h4>
                      <Link to="/notifications" className="text-medical-400 text-xs hover:underline">View all</Link>
                    </div>
                    <div className="space-y-3">
                      {notifications.map(n => (
                        <div key={n.notificationId}
                          className={`flex items-start gap-2.5 text-xs ${!n.isRead ? 'opacity-100' : 'opacity-60'}`}>
                          <div className={`w-1.5 h-1.5 rounded-full mt-1.5 shrink-0 ${!n.isRead ? 'bg-medical-400' : 'bg-white/20'}`} />
                          <div>
                            <p className="font-semibold text-white/80">{n.title}</p>
                            <p className="text-white/40 leading-relaxed">{n.message.slice(0, 60)}{n.message.length > 60 ? '…' : ''}</p>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </motion.div>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
