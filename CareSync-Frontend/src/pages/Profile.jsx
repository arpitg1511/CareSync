import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { User, Mail, MapPin, Activity, Save, CheckCircle, Heart, Phone } from 'lucide-react';
import FloatingNav from '../components/FloatingNav';
import { patientService } from '../services/api';

const Profile = () => {
  const [loading, setLoading] = useState(true);
  const [saved, setSaved] = useState(false);
  const [profile, setProfile] = useState({
    fullName: '',
    email: '',
    dateOfBirth: '',
    gender: 'MALE',
    bloodGroup: 'O+',
    address: '',
    emergencyContact: '',
    medicalHistory: ''
  });

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await patientService.getProfile();
        if (res.data) {
           setProfile(prev => ({ ...prev, ...res.data }));
        }
      } catch (err) {
        console.error("Profile sync failed", err);
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, []);

  const handleSave = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
        await patientService.saveProfile(profile);
        setSaved(true);
        setTimeout(() => setSaved(false), 3000);
    } catch (err) {
        alert("Failed to update health records. Check your connection.");
    } finally {
        setLoading(false);
    }
  };

  if (loading) return (
    <div className="min-h-screen bg-background flex items-center justify-center">
       <div className="w-12 h-12 border-4 border-medical-500 border-t-transparent rounded-full animate-spin" />
    </div>
  );

  return (
    <div className="min-h-screen bg-background p-8 pt-32 pb-24">
      <FloatingNav />

      <div className="max-w-5xl mx-auto">
        <header className="mb-12">
          <h2 className="text-4xl font-bold mb-2">Patient Studio</h2>
          <p className="text-white/40">Securely manage your clinical data and bio-identity.</p>
        </header>

        <form onSubmit={handleSave} className="grid grid-cols-1 lg:grid-cols-3 gap-8">
           {/* 👤 Identity Snapshot */}
           <div className="space-y-8">
              <div className="glass-card p-10 text-center">
                 <div className="w-32 h-32 rounded-3xl bg-medical-500/10 border border-white/10 flex items-center justify-center mx-auto mb-6 text-medical-400">
                    <User size={48} />
                 </div>
                 <h3 className="text-2xl font-bold">{profile.fullName}</h3>
                 <p className="text-white/30 text-xs font-bold uppercase tracking-widest mt-2">Verified Patient</p>
              </div>

              <div className="glass-card p-8">
                 <div className="flex items-center gap-2 mb-6 text-medical-400 font-bold text-xs uppercase tracking-widest">
                    <Activity size={16} /> Vitals Summary
                 </div>
                 <div className="grid grid-cols-2 gap-4">
                    <div className="p-4 rounded-2xl bg-white/5 border border-white/10">
                       <p className="text-[10px] text-white/30 font-bold uppercase">Blood</p>
                       <p className="text-lg font-bold">{profile.bloodGroup}</p>
                    </div>
                    <div className="p-4 rounded-2xl bg-white/5 border border-white/10">
                       <p className="text-[10px] text-white/30 font-bold uppercase">Gender</p>
                       <p className="text-lg font-bold">{profile.gender}</p>
                    </div>
                 </div>
              </div>
           </div>

           {/* 🧬 Clinical Records */}
           <div className="lg:col-span-2 space-y-8">
              <div className="glass-card p-10">
                 <h4 className="text-xl font-bold mb-8 flex items-center gap-2">
                    <Heart size={20} className="text-rose-500" /> Bio Information
                 </h4>
                 
                 <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 mb-8">
                    <div className="space-y-2">
                       <label className="text-[10px] font-bold text-white/30 uppercase tracking-widest">Date of Birth</label>
                       <input 
                         type="date"
                         className="input-field py-4"
                         value={profile.dateOfBirth}
                         onChange={(e) => setProfile({...profile, dateOfBirth: e.target.value})}
                       />
                    </div>
                    <div className="space-y-2">
                       <label className="text-[10px] font-bold text-white/30 uppercase tracking-widest">Blood Group</label>
                       <select 
                         className="input-field py-4"
                         value={profile.bloodGroup}
                         onChange={(e) => setProfile({...profile, bloodGroup: e.target.value})}
                       >
                          {['A+', 'A-', 'B+', 'B-', 'O+', 'O-', 'AB+', 'AB-'].map(bg => <option key={bg} value={bg}>{bg}</option>)}
                       </select>
                    </div>
                 </div>

                 <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 mb-8">
                    <div className="space-y-2">
                       <label className="text-[10px] font-bold text-white/30 uppercase tracking-widest">Emergency Contact</label>
                       <div className="relative">
                          <Phone className="absolute left-5 top-1/2 -translate-y-1/2 text-white/20" size={18} />
                          <input 
                            className="input-field pl-12 py-4"
                            placeholder="+1 234..."
                            value={profile.emergencyContact}
                            onChange={(e) => setProfile({...profile, emergencyContact: e.target.value})}
                          />
                       </div>
                    </div>
                    <div className="space-y-2">
                       <label className="text-[10px] font-bold text-white/30 uppercase tracking-widest">Current Address</label>
                       <div className="relative">
                          <MapPin className="absolute left-5 top-1/2 -translate-y-1/2 text-white/20" size={18} />
                          <input 
                            className="input-field pl-12 py-4"
                            placeholder="Street, City, Country"
                            value={profile.address}
                            onChange={(e) => setProfile({...profile, address: e.target.value})}
                          />
                       </div>
                    </div>
                 </div>

                 <div className="space-y-2">
                    <label className="text-[10px] font-bold text-white/30 uppercase tracking-widest">Medical History</label>
                    <textarea 
                      className="input-field min-h-[150px] p-6"
                      placeholder="List any chronic conditions, allergies, or past surgeries..."
                      value={profile.medicalHistory}
                      onChange={(e) => setProfile({...profile, medicalHistory: e.target.value})}
                    />
                 </div>
              </div>

              <button 
                type="submit"
                disabled={loading}
                className="premium-btn w-full flex items-center justify-center gap-3 h-16 text-lg"
              >
                 {loading ? 'Synchronizing...' : (saved ? 'Profile Encrypted' : 'Save Health Record')}
                 {!loading && (saved ? <CheckCircle size={24} className="text-emerald-400" /> : <Save size={20} />)}
              </button>
           </div>
        </form>
      </div>
    </div>
  );
};

export default Profile;
