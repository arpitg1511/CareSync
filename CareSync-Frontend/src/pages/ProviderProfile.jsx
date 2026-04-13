import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Shield, MapPin, Briefcase, Plus, Save, CheckCircle, GraduationCap, Building } from 'lucide-react';
import FloatingNav from '../components/FloatingNav';
import api from '../services/api';

const ProviderProfile = () => {
  const [loading, setLoading] = useState(false);
  const [saved, setSaved] = useState(false);
  const [profile, setProfile] = useState({
    specialization: '',
    experienceMonths: 12,
    clinicName: '',
    address: '',
    contact: ''
  });

  const handleSave = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
        await api.post('/api/providers/profile', profile);
        setSaved(true);
        setTimeout(() => setSaved(false), 3000);
    } catch (err) {
        alert("Submission failed. Ensure you are logged in as a DOCTOR.");
    } finally {
        setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-background p-8 pt-32 pb-24 text-white">
      <FloatingNav />

      <div className="max-w-5xl mx-auto">
        <header className="mb-16">
          <h2 className="text-5xl font-bold mb-4 tracking-tight">Professional Studio</h2>
          <p className="text-white/40 text-lg">Define your medical practice and credentials.</p>
        </header>

        <form onSubmit={handleSave} className="grid grid-cols-1 lg:grid-cols-3 gap-12">
           {/* 🛡️ Status Panel */}
           <div className="space-y-8">
              <div className="glass-card p-10 text-center bg-gradient-to-br from-medical-500/10 to-transparent">
                 <div className="w-32 h-32 rounded-3xl bg-medical-500/20 flex items-center justify-center mx-auto mb-8 border border-medical-500/30 text-medical-400">
                    <Shield size={48} />
                 </div>
                 <h3 className="text-2xl font-bold mb-2 uppercase tracking-tight">Practitioner</h3>
                 <div className="py-2 px-4 rounded-full bg-white/5 border border-white/10 text-xs font-bold text-white/40 inline-block uppercase tracking-widest">
                    Verification Needed
                 </div>
              </div>

              <div className="glass-card p-8 space-y-6">
                 <h4 className="text-xs font-bold uppercase tracking-widest text-medical-400">System Checklist</h4>
                 <div className="flex items-center gap-3 text-sm text-white/60">
                    <CheckCircle size={16} className="text-medical-400" /> Professional Identity
                 </div>
                 <div className="flex items-center gap-3 text-sm text-white/30">
                    <div className="w-4 h-4 rounded-full border border-white/20" /> Medical Verification
                 </div>
              </div>
           </div>

           {/* 📝 Credential Forms */}
           <div className="lg:col-span-2 space-y-10">
              <section className="glass-card p-12">
                 <h4 className="text-2xl font-bold mb-10 flex items-center gap-3">
                    <GraduationCap className="text-medical-400" /> Medical Background
                 </h4>
                 
                 <div className="grid grid-cols-1 sm:grid-cols-2 gap-8 mb-10">
                    <div className="space-y-3">
                       <label className="text-[10px] font-bold text-white/30 uppercase tracking-widest ml-1">Specialization</label>
                       <div className="relative">
                          <Plus className="absolute left-5 top-1/2 -translate-y-1/2 text-white/20" size={18} />
                          <input 
                            className="input-field pl-12"
                            placeholder="e.g. Cardiologist"
                            value={profile.specialization}
                            onChange={(e) => setProfile({...profile, specialization: e.target.value})}
                            required
                          />
                       </div>
                    </div>

                    <div className="space-y-3">
                       <label className="text-[10px] font-bold text-white/30 uppercase tracking-widest ml-1">Total Experience (Months)</label>
                       <div className="relative">
                          <Briefcase className="absolute left-5 top-1/2 -translate-y-1/2 text-white/20" size={18} />
                          <input 
                            type="number"
                            className="input-field pl-12"
                            placeholder="Months"
                            value={profile.experienceMonths}
                            onChange={(e) => setProfile({...profile, experienceMonths: parseInt(e.target.value)})}
                            required
                          />
                       </div>
                    </div>
                 </div>

                 <h4 className="text-2xl font-bold mb-10 flex items-center gap-3 pt-8 border-t border-white/5">
                    <Building className="text-medical-400" /> Clinical Details
                 </h4>

                 <div className="space-y-8">
                    <div className="space-y-3">
                       <label className="text-[10px] font-bold text-white/30 uppercase tracking-widest ml-1">Clinic Name</label>
                       <input 
                         className="input-field"
                         placeholder="e.g. CareSync General Hospital"
                         value={profile.clinicName}
                         onChange={(e) => setProfile({...profile, clinicName: e.target.value})}
                         required
                       />
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-8">
                       <div className="space-y-3">
                          <label className="text-[10px] font-bold text-white/30 uppercase tracking-widest ml-1">Contact Number</label>
                          <input 
                            className="input-field"
                            placeholder="+1 234..."
                            value={profile.contact}
                            onChange={(e) => setProfile({...profile, contact: e.target.value})}
                            required
                          />
                       </div>
                       <div className="space-y-3">
                          <label className="text-[10px] font-bold text-white/30 uppercase tracking-widest ml-1">Practice Location</label>
                          <div className="relative">
                             <MapPin className="absolute left-5 top-1/2 -translate-y-1/2 text-white/20" size={18} />
                             <input 
                               className="input-field pl-12"
                               placeholder="Street, City, Zip"
                               value={profile.address}
                               onChange={(e) => setProfile({...profile, address: e.target.value})}
                               required
                             />
                          </div>
                       </div>
                    </div>
                 </div>
              </section>

              <button 
                type="submit"
                disabled={loading}
                className="premium-btn w-full flex items-center justify-center gap-3 h-20 text-xl"
              >
                 {loading ? 'Submitting Credentials...' : (saved ? 'Application Sent' : 'Register Clinic Profile')}
                 {!loading && (saved ? <CheckCircle size={28} className="text-emerald-400" /> : <Save size={24} />)}
              </button>
              <p className="text-center text-white/20 text-xs uppercase tracking-[0.2em] font-bold">Requires Admin Validation</p>
           </div>
        </form>
      </div>
    </div>
  );
};

export default ProviderProfile;
