import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { Calendar, Clock, User, Heart, Activity, Plus, Shield, Users, Briefcase, Star, FileText, TrendingUp, Bell } from 'lucide-react';
import { Link } from 'react-router-dom';
import FloatingNav from '../components/FloatingNav';
import { appointmentService, notificationService } from '../services/api';

const Dashboard = () => {
  const [appointments, setAppointments] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);
  const role = localStorage.getItem('role') || 'PATIENT';
  const userId = localStorage.getItem('userId');

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = role === 'ADMIN' 
          ? await appointmentService.getAllAdmin() 
          : (role === 'DOCTOR' ? await appointmentService.getByProvider(userId) : await appointmentService.getUpcoming(userId));
        setAppointments(res.data || []);
        
        const notifRes = await notificationService.getUnreadCount(userId);
        setUnreadCount(notifRes.data || 0);
      } catch (err) {
        console.error("Failed to load hub data", err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [role, userId]);

  const getStats = () => {
    if (role === 'ADMIN') return [
      { label: 'Total Volume', value: appointments.length.toString(), icon: TrendingUp, color: 'text-amber-400' },
      { label: 'Security', value: 'Active', icon: Shield, color: 'text-emerald-400' },
      { label: 'System Health', value: '100%', icon: Activity, color: 'text-medical-400' }
    ];
    
    if (role === 'DOCTOR') return [
      { label: 'Today Queue', value: appointments.filter(a => a.appointmentDate === new Date().toISOString().split('T')[0]).length.toString(), icon: Users, color: 'text-blue-400' },
      { label: 'Consultations', value: appointments.length.toString(), icon: Activity, color: 'text-medical-400' },
      { label: 'Avg Rating', value: '4.9', icon: Star, color: 'text-amber-400' }
    ];

    return [
      { label: 'My Bookings', value: appointments.length.toString(), icon: Calendar, color: 'text-blue-400' },
      { label: 'Notifications', value: unreadCount.toString(), icon: Bell, color: 'text-cyan-400' },
      { label: 'Health Score', value: '94', icon: Heart, color: 'text-rose-400' }
    ];
  };

    if (role === 'DOCTOR') return [
      { id: 'SCHEDULE', label: 'My Schedule', icon: Calendar },
      { id: 'AVAILABILITY', label: 'Manage Slots', icon: Clock },
      { id: 'REVIEWS', label: 'Patient Reviews', icon: Star },
      { id: 'EARNINGS', label: 'Earnings', icon: DollarSign }
    ];
    return [
      { id: 'OVERVIEW', label: 'Overview', icon: Activity },
      { id: 'APPOINTMENTS', label: 'Bookings', icon: Calendar },
      { id: 'RECORDS', label: 'Health Records', icon: FileText }
    ];
  };

  const [activeTab, setActiveTab] = useState(role === 'PATIENT' ? 'OVERVIEW' : 'SCHEDULE');

  const stats = getStats();

  return (
    <div className="min-h-screen bg-background p-4 sm:p-8 pt-24 sm:pt-32">
      <FloatingNav />
      <div className="absolute top-0 right-0 w-[500px] h-[500px] bg-medical-500/5 rounded-full blur-[120px] pointer-events-none" />
      
      <div className="max-w-7xl mx-auto relative z-10">
        <header className="mb-12 flex flex-col lg:flex-row lg:items-end justify-between gap-8">
          <div>
            <div className="flex items-center gap-3 mb-4">
               <span className="px-3 py-1 rounded-lg bg-medical-500/10 text-medical-400 text-[10px] font-bold tracking-widest uppercase border border-medical-500/20">
                  {role} Portal
               </span>
               <div className="h-1 w-1 bg-white/20 rounded-full" />
               <span className="text-white/30 text-[10px] font-bold tracking-widest uppercase">E2E Encrypted</span>
            </div>
            <motion.h2 
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              className="text-4xl sm:text-6xl font-bold mb-2 tracking-tight"
            >
              Control Hub
            </motion.h2>
            <p className="text-white/40 text-base sm:text-lg">Unified management interface for CareSync microservices.</p>
          </div>

          <div className="flex flex-wrap gap-4">
            {dashboardLinks().map((link) => (
              <button 
                key={link.id} 
                onClick={() => {
                    if (link.to) window.location.href = link.to;
                    else setActiveTab(link.id);
                }}
                className={`flex items-center gap-3 p-4 px-6 rounded-2xl border backdrop-blur-xl transition-all duration-300 ${
                    activeTab === link.id 
                    ? 'bg-medical-500 border-medical-400 text-white shadow-lg shadow-medical-500/20' 
                    : 'bg-white/[0.03] border-white/10 text-white/40 hover:bg-white/5'
                }`}
              >
                <link.icon className={activeTab === link.id ? 'text-white' : 'text-white/40'} size={18} />
                <span className="text-sm font-semibold">{link.label}</span>
              </button>
            ))}
          </div>
        </header>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 sm:gap-8 mb-12">
          {stats.map((stat, i) => (
            <motion.div
              key={i}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.1 }}
              className="glass-card p-8 flex items-center justify-between group cursor-pointer"
            >
              <div>
                <p className="text-white/40 text-[10px] font-bold uppercase tracking-widest mb-1">{stat.label}</p>
                <h4 className="text-4xl font-bold">{stat.value}</h4>
              </div>
              <div className={`${stat.color} p-4 rounded-2xl bg-white/[0.02] border border-white/5`}>
                <stat.icon size={32} />
              </div>
            </motion.div>
          ))}
        </div>

        <motion.div 
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="min-h-[500px]"
        >
          {activeTab === 'EARNINGS' && (
             <div className="space-y-8">
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                    {[
                        { label: 'Total Revenue', value: '$45,280', icon: DollarSign, color: 'text-emerald-400' },
                        { label: 'Pending Payout', value: '$2,400', icon: Clock, color: 'text-amber-400' },
                        { label: 'Avg / Consultation', value: '$120', icon: TrendingUp, color: 'text-blue-400' },
                        { label: 'Net Profit', value: '$38,400', icon: Heart, color: 'text-rose-400' }
                    ].map((s, i) => (
                        <div key={i} className="glass-card p-8 border-white/10">
                            <p className="text-[10px] font-bold uppercase tracking-widest text-white/30 mb-2">{s.label}</p>
                            <div className="flex items-center justify-between">
                                <h4 className="text-2xl font-bold">{s.value}</h4>
                                <s.icon className={s.color} size={20} />
                            </div>
                        </div>
                    ))}
                </div>
                <div className="glass-card p-0 overflow-hidden border-white/10">
                    <div className="p-8 border-b border-white/5 flex justify-between items-center">
                        <h4 className="font-bold">Monthly Revenue Breakdown</h4>
                        <button className="text-xs text-medical-400 font-bold uppercase tracking-widest">Download Report</button>
                    </div>
                    <div className="p-8 space-y-6">
                        {['October', 'September', 'August'].map(month => (
                            <div key={month} className="flex items-center justify-between">
                                <span className="text-sm text-white/50">{month} 2026</span>
                                <div className="flex-1 mx-12 h-1.5 bg-white/5 rounded-full overflow-hidden">
                                    <div className="h-full w-2/3 bg-medical-500 rounded-full" />
                                </div>
                                <span className="font-bold text-sm">$4,200.00</span>
                            </div>
                        ))}
                    </div>
                </div>
             </div>
          )}

          {(activeTab === 'SCHEDULE' || activeTab === 'OVERVIEW') && (
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
              <div className="lg:col-span-2 space-y-6">
                <h3 className="text-2xl font-semibold mb-4 flex items-center gap-3">
                   <Calendar size={24} className="text-medical-400" /> Active Schedule
                </h3>
                
                {appointments.length === 0 ? (
                  <div className="glass-card p-20 text-center border-dashed border-white/10">
                    <p className="text-white/20">No active sessions found.</p>
                  </div>
                ) : (
                  appointments.map((apt, i) => (
                    <div key={i} className="glass-card p-6 flex flex-col sm:flex-row items-start sm:items-center justify-between gap-6 hover:bg-white/[0.05] transition-colors group border-white/5">
                      <div className="flex items-center gap-6">
                         <div className="w-16 h-16 rounded-2xl bg-medical-500/10 flex items-center justify-center text-medical-400 border border-medical-500/20">
                           <User size={28} />
                         </div>
                         <div>
                           <h4 className="text-xl font-bold group-hover:text-medical-400 transition-colors">
                              {role === 'DOCTOR' ? `Patient #${apt.patientId}` : `Provider #${apt.providerId}`}
                           </h4>
                           <p className="text-white/40 text-[10px] font-black uppercase tracking-widest">{apt.serviceType || 'General Consultation'}</p>
                         </div>
                      </div>
                      <div className="w-full sm:w-auto flex sm:flex-col items-center sm:items-end justify-between sm:justify-center gap-2">
                        <p className="text-white font-bold flex items-center gap-2 bg-white/5 px-4 py-2 rounded-xl border border-white/5 text-sm">
                          <Clock size={14} className="text-medical-400" /> {apt.startTime || '10:00 AM'}
                        </p>
                        <span className="text-[10px] px-3 py-1 rounded-full bg-medical-500/10 text-medical-400 font-black uppercase tracking-widest border border-medical-500/20">
                          {apt.status}
                        </span>
                      </div>
                    </div>
                  ))
                )}
              </div>

              <div className="space-y-8">
                 <div className="glass-card p-10 bg-gradient-to-br from-medical-600 to-medical-400 border-none shadow-[0_20px_50px_rgba(14,165,233,0.3)]">
                    <h3 className="text-2xl font-bold text-white mb-2">Sync New Event</h3>
                    <p className="text-white/70 mb-8 text-sm leading-relaxed">Schedule a new consultation across our distributed provider network instantly.</p>
                    <Link to="/search" className="w-full py-4 bg-white text-medical-600 rounded-2xl font-black uppercase tracking-widest text-[10px] flex items-center justify-center gap-2 hover:bg-medical-50 transition-all hover:shadow-xl active:scale-95">
                       <Plus size={18} /> New Appointment
                    </Link>
                 </div>

                 <div className="glass-card p-8 border-white/10">
                    <h4 className="text-[10px] font-bold text-white/20 uppercase tracking-[0.2em] mb-8 flex items-center gap-3">
                       <Activity size={16} className="text-medical-400" /> Network Status
                    </h4>
                    <div className="space-y-6">
                       {[
                         { name: 'Auth Node', status: 'Online', p: 'w-full' },
                         { name: 'Schedule Node', status: 'Syncing', p: 'w-4/5' },
                         { name: 'Payment Gateway', status: 'Online', p: 'w-full' }
                       ].map(s => (
                         <div key={s.name}>
                            <div className="flex justify-between text-[10px] font-bold mb-2 uppercase tracking-widest">
                               <span className="text-white/40">{s.name}</span>
                               <span className={s.status === 'Online' ? 'text-emerald-400' : 'text-amber-400'}>{s.status}</span>
                            </div>
                            <div className="h-1 w-full bg-white/5 rounded-full overflow-hidden">
                               <div className={`h-full ${s.p} ${s.status === 'Online' ? 'bg-emerald-400' : 'bg-amber-400'} rounded-full`} />
                            </div>
                         </div>
                       ))}
                    </div>
                 </div>
              </div>
            </div>
          )}
        </motion.div>
      </div>
    </div>
  );
};

export default Dashboard;
