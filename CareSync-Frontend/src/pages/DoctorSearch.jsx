import React, { useState, useEffect, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Search, MapPin, Star, Calendar, Clock, X, ChevronRight, Loader2, Filter, Stethoscope } from 'lucide-react';
import FloatingNav from '../components/FloatingNav';
import { appointmentService, slotService } from '../services/api';
import api from '../services/api';

const StarDisplay = ({ rating }) => (
  <div className="flex items-center gap-0.5">
    {[1,2,3,4,5].map(n => (
      <Star key={n} size={12} className={n <= Math.round(rating || 0) ? 'fill-amber-400 text-amber-400' : 'text-white/20'} />
    ))}
    <span className="text-xs text-white/40 ml-1">{rating ? rating.toFixed(1) : 'New'}</span>
  </div>
);

const BookingModal = ({ doc, onClose, onBooked }) => {
  const [slots, setSlots] = useState([]);
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().slice(0, 10));
  const [mode, setMode] = useState('IN_PERSON');
  const [serviceType, setServiceType] = useState('General Consultation');
  const [reason, setReason] = useState('');
  const [loadingSlots, setLoadingSlots] = useState(false);
  const [booking, setBooking] = useState(false);

  useEffect(() => {
    const fetchSlots = async () => {
      setLoadingSlots(true);
      try {
        const res = await slotService.getAvailable(doc.providerId || doc.id, selectedDate);
        setSlots(res.data || []);
      } catch {
        setSlots([]);
      } finally { setLoadingSlots(false); }
    };
    if (selectedDate) fetchSlots();
  }, [selectedDate, doc]);

  const handleBook = async () => {
    setBooking(true);
    try {
      await appointmentService.book({
        providerId: doc.providerId || doc.id,
        slotId: selectedSlot?.slotId || null,
        appointmentDateTime: selectedSlot
          ? `${selectedDate}T${selectedSlot.startTime}`
          : `${selectedDate}T10:00:00`,
        modeOfConsultation: mode,
        serviceType,
        reason,
      });
      onBooked();
      onClose();
    } catch (err) {
      alert(err.response?.data?.message || 'Only patients can book appointments. Please log in as a patient.');
    } finally { setBooking(false); }
  };

  return (
    <div className="fixed inset-0 z-[200] flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm">
      <motion.div initial={{ scale: 0.9, opacity: 0 }} animate={{ scale: 1, opacity: 1 }} exit={{ scale: 0.9, opacity: 0 }}
        className="glass-card w-full max-w-lg p-6 sm:p-8 relative max-h-[90vh] overflow-y-auto">
        <button onClick={onClose} className="absolute top-4 right-4 text-white/30 hover:text-white transition-colors"><X size={20} /></button>

        <div className="flex items-center gap-4 mb-6">
          <div className="w-14 h-14 rounded-2xl bg-medical-500/10 flex items-center justify-center text-medical-400 shrink-0">
            <Stethoscope size={26} />
          </div>
          <div>
            <h3 className="text-xl font-bold">{doc.fullName || doc.name}</h3>
            <p className="text-medical-400 text-sm font-medium">{doc.specialization}</p>
          </div>
        </div>

        <div className="space-y-4">
          {/* Date picker */}
          <div>
            <label className="text-[10px] font-bold text-white/30 uppercase tracking-widest mb-1.5 block">Select Date</label>
            <input type="date" className="input-field" value={selectedDate}
              min={new Date().toISOString().slice(0, 10)}
              onChange={e => { setSelectedDate(e.target.value); setSelectedSlot(null); }} />
          </div>

          {/* Available slots */}
          <div>
            <label className="text-[10px] font-bold text-white/30 uppercase tracking-widest mb-1.5 block">Available Time Slots</label>
            {loadingSlots ? (
              <div className="flex justify-center py-6"><Loader2 size={24} className="animate-spin text-medical-400" /></div>
            ) : slots.length === 0 ? (
              <p className="text-white/40 text-sm text-center py-4">No slots available for this date.</p>
            ) : (
              <div className="grid grid-cols-3 gap-2 max-h-36 overflow-y-auto pr-1">
                {slots.map(slot => (
                  <button key={slot.slotId} onClick={() => setSelectedSlot(slot)}
                    className={`p-2 rounded-xl text-xs font-semibold transition-all border
                      ${selectedSlot?.slotId === slot.slotId
                        ? 'bg-medical-500 border-medical-500 text-white'
                        : 'bg-white/5 border-white/10 text-white/60 hover:border-medical-500/50 hover:text-white'}`}>
                    {slot.startTime}
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Mode */}
          <div>
            <label className="text-[10px] font-bold text-white/30 uppercase tracking-widest mb-1.5 block">Consultation Mode</label>
            <div className="grid grid-cols-2 gap-2">
              {['IN_PERSON', 'TELECONSULTATION'].map(m => (
                <button key={m} type="button" onClick={() => setMode(m)}
                  className={`py-2 rounded-xl border text-xs font-bold transition-all
                    ${mode === m ? 'bg-medical-500 border-medical-500 text-white' : 'border-white/10 text-white/40 hover:border-white/30'}`}>
                  {m.replace('_', ' ')}
                </button>
              ))}
            </div>
          </div>

          {/* Service type */}
          <div>
            <label className="text-[10px] font-bold text-white/30 uppercase tracking-widest mb-1.5 block">Service Type</label>
            <select className="input-field" value={serviceType} onChange={e => setServiceType(e.target.value)}>
              {['General Consultation', 'Follow-Up', 'Specialist Consultation', 'Dental Checkup', 'Mental Health', 'Emergency'].map(s => (
                <option key={s} value={s}>{s}</option>
              ))}
            </select>
          </div>

          {/* Reason */}
          <div>
            <label className="text-[10px] font-bold text-white/30 uppercase tracking-widest mb-1.5 block">Reason (optional)</label>
            <textarea className="input-field resize-none h-16 text-sm" placeholder="Briefly describe your concern..."
              value={reason} onChange={e => setReason(e.target.value)} />
          </div>
        </div>

        <button onClick={handleBook} disabled={booking || (!selectedSlot && slots.length > 0)}
          className="premium-btn w-full flex items-center justify-center gap-2 mt-6">
          {booking ? <><Loader2 size={16} className="animate-spin" /> Booking...</> : <><Calendar size={16} /> Confirm Appointment</>}
        </button>
        {slots.length > 0 && !selectedSlot && (
          <p className="text-center text-xs text-amber-400 mt-2">Please select a time slot to continue.</p>
        )}
      </motion.div>
    </div>
  );
};

const DoctorSearch = () => {
  const [query, setQuery] = useState('');
  const [doctors, setDoctors] = useState([]);
  const [selectedDoc, setSelectedDoc] = useState(null);
  const [loading, setLoading] = useState(true);
  const [booked, setBooked] = useState(false);

  const fetchDoctors = useCallback(async () => {
    setLoading(true);
    try {
      const res = await api.get(`/api/providers/search?query=${encodeURIComponent(query)}`);
      setDoctors(res.data || []);
    } catch { setDoctors([]); }
    finally { setLoading(false); }
  }, [query]);

  useEffect(() => {
    const t = setTimeout(fetchDoctors, 400);
    return () => clearTimeout(t);
  }, [fetchDoctors]);

  useEffect(() => {
    if (booked) {
      alert('✅ Appointment booked successfully!');
      setBooked(false);
    }
  }, [booked]);

  return (
    <div className="min-h-screen bg-background p-4 sm:p-8 pt-28 sm:pt-32 pb-24">
      <FloatingNav />

      <div className="max-w-7xl mx-auto">
        {/* Search header */}
        <div className="mb-8 sm:mb-12">
          <h2 className="text-3xl sm:text-4xl font-bold tracking-tight mb-4">Find Specialists</h2>
          <div className="relative max-w-2xl">
            <Search className="absolute left-5 top-1/2 -translate-y-1/2 text-white/30" size={20} />
            <input type="text" placeholder="Search by name, specialization, or location..."
              className="input-field pl-14 py-4 rounded-2xl text-base w-full"
              value={query} onChange={e => setQuery(e.target.value)} />
          </div>
        </div>

        {loading ? (
          <div className="flex justify-center py-20"><Loader2 size={36} className="animate-spin text-medical-400" /></div>
        ) : doctors.length === 0 ? (
          <div className="glass-card p-16 text-center">
            <Stethoscope size={40} className="mx-auto mb-4 text-white/20" />
            <p className="text-white/40 text-lg">{query ? `No providers found for "${query}"` : 'No providers available.'}</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            {doctors.map((doc, i) => (
              <motion.div key={doc.providerId || i}
                initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 0.06 }}
                onClick={() => setSelectedDoc(doc)}
                className="glass-card p-5 sm:p-6 cursor-pointer hover:scale-[1.02] hover:border-medical-500/30 transition-all group">
                <div className="flex items-start gap-3 mb-4">
                  <div className="w-12 h-12 rounded-2xl bg-medical-500/10 flex items-center justify-center text-medical-400 shrink-0">
                    <Stethoscope size={22} />
                  </div>
                  <div className="min-w-0">
                    <h3 className="font-bold text-base leading-tight truncate">{doc.fullName || doc.name}</h3>
                    <p className="text-medical-400 text-xs font-semibold uppercase tracking-wider mt-0.5">{doc.specialization}</p>
                  </div>
                </div>

                {(doc.address || doc.city) && (
                  <p className="text-white/40 text-xs flex items-center gap-1.5 mb-3">
                    <MapPin size={12} className="shrink-0" /> {doc.address || doc.city}
                  </p>
                )}

                <div className="flex items-center justify-between pt-3 border-t border-white/5">
                  <StarDisplay rating={doc.avgRating} />
                  <span className="text-medical-400 font-bold flex items-center gap-1 text-sm group-hover:gap-2 transition-all">
                    Book <ChevronRight size={14} />
                  </span>
                </div>
              </motion.div>
            ))}
          </div>
        )}
      </div>

      <AnimatePresence>
        {selectedDoc && (
          <BookingModal
            doc={selectedDoc}
            onClose={() => setSelectedDoc(null)}
            onBooked={() => { setBooked(true); setSelectedDoc(null); }}
          />
        )}
      </AnimatePresence>
    </div>
  );
};

export default DoctorSearch;
