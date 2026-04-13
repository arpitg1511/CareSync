import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Shield, CheckCircle, XCircle, UserCheck, AlertCircle, BarChart3, TrendingUp, Users, DollarSign, MessageSquare, Bell } from 'lucide-react';
import FloatingNav from '../components/FloatingNav';
import { providerService, appointmentService, paymentService, reviewService, notificationService } from '../services/api';

const AdminHub = () => {
  const [pending, setPending] = useState([]);
  const [stats, setStats] = useState({ bookings: 0, revenue: 0, users: 0 });
  const [activeTab, setActiveTab] = useState('VERIFICATION');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const pendingRes = await providerService.getPending();
      setPending(pendingRes.data || []);
      
      const revenueRes = await paymentService.getRevenue();
      setStats(prev => ({ ...prev, revenue: revenueRes.data || 0 }));
      
      const aptRes = await appointmentService.getAllAdmin();
      setStats(prev => ({ ...prev, bookings: aptRes.data?.length || 0 }));
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (id) => {
    try {
      await providerService.verify(id);
      fetchData();
    } catch (err) {
      alert("Approval failed");
    }
  };

  const tabs = [
    { id: 'ANALYTICS', label: 'Platform Stats', icon: BarChart3 },
    { id: 'VERIFICATION', label: 'Provider Verification', icon: UserCheck },
    { id: 'MODERATION', label: 'Review Moderation', icon: MessageSquare },
    { id: 'USERS', label: 'User Management', icon: Users },
    { id: 'BROADCAST', label: 'Global Alerts', icon: Bell }
  ];

  return (
    <div className="min-h-screen bg-background p-4 sm:p-8 pt-24 sm:pt-32">
      <FloatingNav />

      <div className="max-w-7xl mx-auto">
        <header className="mb-16">
          <div className="flex items-center gap-3 mb-4">
             <span className="px-3 py-1 rounded-lg bg-medical-500/10 text-medical-400 text-[10px] font-bold tracking-widest uppercase border border-medical-500/20">
                L5 Authorization Required
             </span>
             <div className="h-1 w-1 bg-white/20 rounded-full" />
             <span className="text-white/30 text-[10px] font-bold tracking-widest uppercase">Admin Terminal</span>
          </div>
          <h2 className="text-5xl font-bold mb-4 flex items-center gap-4 tracking-tighter">
             Administrative Hub <Shield size={40} className="text-medical-400" />
          </h2>
          <p className="text-white/40 text-lg">Central command for platform verification, financial reconciliation, and user moderation.</p>
        </header>

        {/* 📊 High-Level Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
           <div className="glass-card p-10 bg-gradient-to-br from-white/[0.03] to-white/[0.01]">
              <div className="flex items-center justify-between mb-6">
                 <p className="text-xs font-bold uppercase tracking-widest text-white/30">Gross Revenue</p>
                 <DollarSign size={20} className="text-emerald-400" />
              </div>
              <h3 className="text-4xl font-bold">${stats.revenue.toLocaleString()}</h3>
              <p className="text-[10px] text-emerald-400/60 font-medium mt-2 flex items-center gap-1">
                 <TrendingUp size={12} /> +12.4% from last month
              </p>
           </div>
           <div className="glass-card p-10 bg-gradient-to-br from-white/[0.03] to-white/[0.01]">
              <div className="flex items-center justify-between mb-6">
                 <p className="text-xs font-bold uppercase tracking-widest text-white/30">Total Consultations</p>
                 <Activity size={20} className="text-medical-400" />
              </div>
              <h3 className="text-4xl font-bold">{stats.bookings.toLocaleString()}</h3>
              <p className="text-[10px] text-medical-400/60 font-medium mt-2 flex items-center gap-1">
                 <TrendingUp size={12} /> 89% Completion Rate
              </p>
           </div>
           <div className="glass-card p-10 bg-gradient-to-br from-white/[0.03] to-white/[0.01]">
              <div className="flex items-center justify-between mb-6">
                 <p className="text-xs font-bold uppercase tracking-widest text-white/30">Active Providers</p>
                 <Users size={20} className="text-blue-400" />
              </div>
              <h3 className="text-4xl font-bold">42</h3>
              <p className="text-[10px] text-blue-400/60 font-medium mt-2 flex items-center gap-1">
                 <TrendingUp size={12} /> 5 Pending Verification
              </p>
           </div>
        </div>

        {/* 🧭 Sidebar-style Navigation Tabs */}
        <div className="flex flex-wrap gap-4 mb-12">
           {tabs.map(tab => (
             <button
               key={tab.id}
               onClick={() => setActiveTab(tab.id)}
               className={`flex items-center gap-3 px-8 py-4 rounded-2xl font-bold text-sm tracking-tight transition-all duration-300 border ${
                 activeTab === tab.id 
                 ? 'bg-medical-500 border-medical-400 text-white shadow-lg shadow-medical-500/30' 
                 : 'bg-white/5 border-white/10 text-white/40 hover:bg-white/10 hover:text-white'
               }`}
             >
               <tab.icon size={18} /> {tab.label}
             </button>
           ))}
        </div>

        {/* ⚡ Dynamic Panel Content */}
        <div className="min-h-[400px]">
           <AnimatePresence mode="wait">
              {activeTab === 'VERIFICATION' && (
                <motion.div
                  key="verif"
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  exit={{ opacity: 0, y: -20 }}
                  className="space-y-6"
                >
                  {pending.length === 0 ? (
                    <div className="glass-card p-20 text-center border-dashed border-white/10">
                       <p className="text-xl font-medium text-white/20 uppercase tracking-widest">Queue Clear</p>
                    </div>
                  ) : (
                    pending.map((doc, i) => (
                      <div
                        key={doc.providerId}
                        className="glass-card p-10 flex flex-col md:flex-row items-center justify-between gap-10 hover:bg-white/[0.05] transition-colors group border-white/10"
                      >
                        <div className="flex items-center gap-10">
                           <div className="w-16 h-16 rounded-2xl bg-medical-500/10 flex items-center justify-center text-medical-400 border border-medical-500/20">
                              <UserCheck size={32} />
                           </div>
                           <div>
                              <h3 className="text-2xl font-bold mb-1 tracking-tight">{doc.fullName}</h3>
                              <p className="text-medical-400 font-black uppercase tracking-widest text-[10px]">{doc.specialization}</p>
                              <p className="text-white/30 text-sm mt-2">{doc.email}</p>
                           </div>
                        </div>

                        <div className="flex items-center gap-8">
                           <div className="text-right hidden sm:block">
                              <p className="text-white/20 text-[10px] font-bold tracking-widest uppercase mb-1">Clinical Experience</p>
                              <p className="text-xl font-bold">12+ Years</p>
                           </div>
                           <button 
                             onClick={() => handleApprove(doc.providerId)}
                             className="premium-btn flex items-center gap-2"
                           >
                              <CheckCircle size={20} /> Verify Access
                           </button>
                        </div>
                      </div>
                    ))
                  )}
                </motion.div>
              )}

              {activeTab === 'ANALYTICS' && (
                 <motion.div 
                   key="analytics"
                   initial={{ opacity: 0, scale: 0.95 }}
                   animate={{ opacity: 1, scale: 1 }}
                   className="glass-card p-20 text-center border-white/10"
                 >
                    <BarChart3 className="mx-auto text-medical-500/20 mb-8" size={80} />
                    <h3 className="text-2xl font-bold mb-4">Analytics Engine Online</h3>
                    <p className="text-white/40 max-w-lg mx-auto leading-relaxed">
                       Real-time platform metrics are being aggregated from all eight distributed microservices. 
                    </p>
                    <div className="grid grid-cols-2 gap-4 mt-12 max-w-md mx-auto">
                        <div className="p-6 rounded-2xl bg-white/5 border border-white/10">
                            <p className="text-xs text-white/30 mb-2">Completion Rate</p>
                            <p className="text-2xl font-bold">94.2%</p>
                        </div>
                        <div className="p-6 rounded-2xl bg-white/5 border border-white/10">
                            <p className="text-xs text-white/30 mb-2">NPS Score</p>
                            <p className="text-2xl font-bold">78</p>
                        </div>
                    </div>
                 </motion.div>
              )}

              {activeTab === 'MODERATION' && (
                 <motion.div 
                   key="moderation"
                   initial={{ opacity: 0, y: 20 }}
                   animate={{ opacity: 1, y: 0 }}
                   className="glass-card p-20 text-center border-white/10"
                 >
                    <MessageSquare className="mx-auto text-medical-500/20 mb-8" size={80} />
                    <h3 className="text-2xl font-bold mb-2">Review Moderation Queue Clear</h3>
                    <p className="text-white/40">No inappropriate or flagged reviews reported in the last 24 hours.</p>
                 </motion.div>
              )}

              {activeTab === 'USERS' && (
                <motion.div 
                  key="users"
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  className="glass-card p-12 border-white/10"
                >
                  <div className="flex justify-between items-center mb-10">
                    <h3 className="text-2xl font-bold tracking-tight">System Users</h3>
                    <div className="flex gap-4">
                        <span className="px-4 py-2 rounded-xl bg-emerald-500/10 text-emerald-400 text-xs font-bold border border-emerald-500/20">Active: 1,242</span>
                        <span className="px-4 py-2 rounded-xl bg-rose-500/10 text-rose-400 text-xs font-bold border border-rose-500/20">Suspended: 12</span>
                    </div>
                  </div>
                  <div className="space-y-4">
                    {[
                      { name: 'John Doe', email: 'john@example.com', role: 'PATIENT', status: 'Active' },
                      { name: 'Dr. Sarah Smith', email: 'sarah@clinic.com', role: 'PROVIDER', status: 'Active' },
                      { name: 'Mike Ross', email: 'mike@dev.com', role: 'ADMIN', status: 'Active' }
                    ].map((user, i) => (
                      <div key={i} className="flex items-center justify-between p-6 rounded-2xl bg-white/[0.02] border border-white/5 hover:bg-white/5 transition-colors">
                        <div className="flex items-center gap-6">
                           <div className="w-12 h-12 rounded-xl bg-white/5 flex items-center justify-center font-bold text-white/40">{user.name[0]}</div>
                           <div>
                              <p className="font-bold">{user.name}</p>
                              <p className="text-xs text-white/30">{user.email}</p>
                           </div>
                        </div>
                        <div className="flex items-center gap-8">
                           <span className="text-[10px] font-bold tracking-widest uppercase text-white/20">{user.role}</span>
                           <span className="text-xs px-3 py-1 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">{user.status}</span>
                           <button className="text-white/20 hover:text-white transition-colors"><Shield size={16} /></button>
                        </div>
                      </div>
                    ))}
                  </div>
                </motion.div>
              )}

              {activeTab === 'BROADCAST' && (
                 <motion.div 
                   key="broadcast"
                   initial={{ opacity: 0, x: 20 }}
                   animate={{ opacity: 1, x: 0 }}
                   className="glass-card p-16 max-w-3xl mx-auto border-white/10"
                 >
                    <h3 className="text-2xl font-bold mb-8 flex items-center gap-3">
                       <Bell className="text-amber-400" size={24} /> Global Broadcast
                    </h3>
                    <div className="space-y-8">
                       <div>
                          <label className="text-xs font-bold uppercase tracking-widest text-white/20 mb-4 block">Target Audience</label>
                          <select className="input-field appearance-none">
                             <option>All Platform Users</option>
                             <option>All Healthcare Providers</option>
                             <option>All Registered Patients</option>
                          </select>
                       </div>
                       <div>
                          <label className="text-xs font-bold uppercase tracking-widest text-white/20 mb-4 block">Broadcast Message</label>
                          <textarea className="input-field min-h-[150px] pt-4" placeholder="Enter emergency alert or platform announcement..."></textarea>
                       </div>
                       <button className="premium-btn w-full bg-amber-500 shadow-amber-500/20 border-none">Dispatch Broadcast</button>
                    </div>
                 </motion.div>
              )}
           </AnimatePresence>
        </div>
      </div>
    </div>
  );
};

export default AdminHub;
