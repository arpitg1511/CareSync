import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Search, MapPin, Star, Filter, Heart, Loader2, Calendar, Clock, X, ChevronRight, Activity, ShieldCircle } from 'lucide-react';
import FloatingNav from '../components/FloatingNav';
import { providerService, scheduleService, appointmentService } from '../services/api';

const DoctorSearch = () => {
  const [query, setQuery] = useState('');
  const [doctors, setDoctors] = useState([]);
  const [selectedDoc, setSelectedDoc] = useState(null);
  const [availableSlots, setAvailableSlots] = useState([]);
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [booking, setBooking] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchDoctors = async () => {
      setLoading(true);
      try {
        const res = await providerService.search(query);
        setDoctors(res.data || []);
      } catch (err) {
        console.error("Search failed", err);
      } finally {
        setLoading(false);
      }
    };

    const timeoutId = setTimeout(fetchDoctors, 500);
    return () => clearTimeout(timeoutId);
  }, [query]);

  const handleSelectDoc = async (doc) => {
    setSelectedDoc(doc);
    setSelectedSlot(null);
    try {
        const date = new Date().toISOString().split('T')[0];
        const res = await scheduleService.getAvailable(doc.providerId, date);
        setAvailableSlots(res.data || []);
    } catch (err) {
        console.error("Failed to load slots", err);
    }
  };

  const handleBook = async () => {
    if (!selectedSlot) return alert("Please select a time slot");
    setBooking(true);
    try {
        await appointmentService.book({
            providerId: selectedDoc.providerId,
            slotId: selectedSlot.slotId,
            appointmentDate: selectedSlot.date,
            startTime: selectedSlot.startTime,
            endTime: selectedSlot.endTime,
            status: 'SCHEDULED'
        });
        alert("Success: Your session is locked.");
        setSelectedDoc(null);
    } catch (err) {
        alert("Booking failed. Ensure you are logged in as a Patient.");
    } finally {
        setBooking(false);
    }
  }

  return (
    <div className="min-h-screen bg-background p-4 sm:p-8 pt-24 sm:pt-32">
      <FloatingNav />
      {/* Dynamic Background Glow */}
      <div className={`fixed top-0 left-0 w-full h-full bg-medical-500/5 transition-opacity duration-1000 select-none pointer-events-none ${loading ? 'opacity-100' : 'opacity-0'}`} />

      {/* 📅 Booking Engine Side Panel */}
      <AnimatePresence>
        {selectedDoc && (
          <div className="fixed inset-0 z-[100] flex items-center justify-end p-0">
            <motion.div 
               initial={{ opacity: 0 }}
               animate={{ opacity: 1 }}
               exit={{ opacity: 0 }}
               onClick={() => setSelectedDoc(null)}
               className="absolute inset-0 bg-background/80 backdrop-blur-md"
            />
            <motion.div 
               initial={{ x: '100%' }}
               animate={{ x: 0 }}
               exit={{ x: '100%' }}
               transition={{ type: 'spring', damping: 25, stiffness: 200 }}
               className="glass-card w-full max-w-xl h-full rounded-none border-y-0 border-r-0 p-8 sm:p-16 relative flex flex-col shadow-2xl"
            >
               <button onClick={() => setSelectedDoc(null)} className="absolute top-8 right-8 text-white/20 hover:text-white transition-colors bg-white/5 p-3 rounded-2xl">
                  <X size={24} />
               </button>

               <div className="mb-12">
                  <span className="px-3 py-1 rounded-lg bg-medical-500/10 text-medical-400 text-[10px] font-bold tracking-widest uppercase border border-medical-500/20 mb-6 inline-block">
                     Available Specialist
                  </span>
                  <h3 className="text-4xl font-bold mb-2 tracking-tighter">{selectedDoc.fullName}</h3>
                  <p className="text-white/40 group flex items-center gap-2">
                     <ShieldCircle size={16} className="text-emerald-400" /> {selectedDoc.specialization} • Verified Practitioner
                  </p>
               </div>

               <div className="flex-1 overflow-y-auto pr-4 space-y-10 custom-scrollbar">
                  <div>
                     <p className="text-[10px] font-bold uppercase tracking-[0.2em] text-white/20 mb-6 underline decoration-medical-500 decoration-2 underline-offset-8">Provider Bio</p>
                     <p className="text-lg text-white/50 leading-relaxed italic">
                        "{selectedDoc.bio || "Primary healthcare specialist focusing on comprehensive patient wellness and advanced diagnostic solutions."}"
                     </p>
                  </div>

                  <div>
                     <p className="text-[10px] font-bold uppercase tracking-[0.2em] text-white/20 mb-6">Select Available Session</p>
                     {availableSlots.length === 0 ? (
                        <div className="p-8 rounded-3xl bg-white/5 border border-white/10 text-center">
                           <p className="text-white/30 text-sm">No slots published for today.</p>
                        </div>
                     ) : (
                        <div className="grid grid-cols-2 gap-4">
                           {availableSlots.map((slot) => (
                             <button
                               key={slot.slotId}
                               onClick={() => setSelectedSlot(slot)}
                               className={`p-6 rounded-2xl border transition-all duration-300 text-left group ${
                                 selectedSlot?.slotId === slot.slotId 
                                 ? 'bg-medical-500 border-medical-400 shadow-[0_10px_30px_rgba(14,165,233,0.3)]' 
                                 : 'bg-white/5 border-white/10 hover:border-white/20'
                               }`}
                             >
                               <Clock size={16} className={`mb-3 ${selectedSlot?.slotId === slot.slotId ? 'text-white' : 'text-medical-400'}`} />
                               <p className={`font-bold text-lg ${selectedSlot?.slotId === slot.slotId ? 'text-white' : 'text-white/80'}`}>{slot.startTime}</p>
                               <p className={`text-[10px] uppercase font-bold tracking-widest ${selectedSlot?.slotId === slot.slotId ? 'text-white/60' : 'text-white/20'}`}>Morning Slot</p>
                             </button>
                           ))}
                        </div>
                     )}
                  </div>
               </div>

               <div className="pt-12 border-t border-white/5 bg-background relative mt-auto">
                  <div className="flex items-center justify-between mb-8 px-2">
                     <div>
                        <p className="text-[10px] font-bold uppercase tracking-widest text-white/20">Consultation Fee</p>
                        <p className="text-3xl font-bold">$120.00</p>
                     </div>
                     <div className="text-right">
                        <p className="text-[10px] font-bold uppercase tracking-widest text-white/20">Insurance</p>
                        <p className="text-emerald-400 font-bold">Accepted</p>
                     </div>
                  </div>
                  <button 
                    disabled={booking || !selectedSlot}
                    onClick={handleBook}
                    className="premium-btn w-full py-6 text-lg tracking-tight disabled:opacity-50 disabled:grayscale"
                  >
                     {booking ? 'Locking Appointment...' : 'Sync Session'}
                  </button>
               </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>

      <div className="max-w-7xl mx-auto">
        <header className="mb-20">
          <h2 className="text-6xl font-black tracking-tighter mb-4 bg-gradient-to-b from-white to-white/40 bg-clip-text text-transparent">Find Specialists</h2>
          <p className="text-white/40 text-xl max-w-2xl">Access our global network of verified medical practitioners across all distributed healthcare nodes.</p>
        </header>

        <div className="relative mb-16">
          <Search className="absolute left-8 top-1/2 -translate-y-1/2 text-medical-400/50" size={28} />
          <input 
            type="text" 
            placeholder="Search by specialization, name, or clinic..." 
            className="input-field pl-20 py-8 rounded-[40px] text-2xl w-full bg-white/[0.03] border-white/10 focus:bg-white/[0.05] transition-all shadow-2xl"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
          {loading && <div className="absolute right-8 top-1/2 -translate-y-1/2"><Loader2 className="animate-spin text-medical-400" /></div>}
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8 sm:gap-12">
           {doctors.map((doc, i) => (
             <motion.div 
               key={i} 
               initial={{ opacity: 0, y: 20 }}
               whileInView={{ opacity: 1, y: 0 }}
               viewport={{ once: true }}
               transition={{ delay: i * 0.05 }}
               onClick={() => handleSelectDoc(doc)}
               className="glass-card p-10 group cursor-pointer hover:-translate-y-2 transition-all duration-500 relative overflow-hidden"
             >
                <div className="absolute -top-12 -right-12 w-32 h-32 bg-medical-500/5 rounded-full blur-3xl group-hover:bg-medical-500/10 transition-colors" />
                <div className="flex items-center gap-1 mb-6">
                   {[1,2,3,4,5].map(s => <Star key={s} size={12} className="text-amber-400 fill-amber-400" />)}
                </div>
                <h3 className="text-3xl font-bold mb-1 tracking-tight group-hover:text-medical-400 transition-colors">{doc.fullName}</h3>
                <p className="text-white/30 font-black uppercase tracking-[0.2em] text-[10px] mb-8">{doc.specialization}</p>
                
                <div className="space-y-4 mb-10">
                   <p className="text-white/40 text-sm flex items-center gap-3"><MapPin size={16} className="text-medical-400" /> {doc.clinicAddress || "Global Virtual Clinic"}</p>
                   <p className="text-white/40 text-sm flex items-center gap-3"><Activity size={16} className="text-emerald-400" /> {doc.experienceYears || "10+"} Years Exp.</p>
                </div>

                <div className="flex items-center justify-between pt-8 border-t border-white/5 relative z-10">
                   <div className="bg-white/5 px-4 py-2 rounded-xl border border-white/5">
                      <span className="text-sm font-bold">$120</span>
                   </div>
                   <span className="text-medical-400 font-bold flex items-center gap-2 group-hover:translate-x-2 transition-transform">
                      Sync Session <ChevronRight size={18} />
                   </span>
                </div>
             </motion.div>
           ))}
        </div>

        {!loading && doctors.length === 0 && (
          <div className="py-40 text-center">
            <h3 className="text-4xl font-bold text-white/10 uppercase tracking-widest">No Node Results</h3>
          </div>
        )}
      </div>
    </div>
  );
};

export default DoctorSearch;
