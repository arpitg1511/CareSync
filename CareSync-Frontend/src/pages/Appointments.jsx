import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Calendar, Clock, CheckCircle, XCircle, RefreshCw, Loader2, AlertTriangle, ChevronRight, X } from 'lucide-react';
import FloatingNav from '../components/FloatingNav';
import { appointmentService } from '../services/api';

const statusStyle = {
  SCHEDULED:   'bg-medical-500/10 text-medical-400 border-medical-500/20',
  RESCHEDULED: 'bg-blue-500/10 text-blue-400 border-blue-500/20',
  COMPLETED:   'bg-emerald-500/10 text-emerald-400 border-emerald-500/20',
  CANCELLED:   'bg-rose-500/10 text-rose-400 border-rose-500/20',
  NO_SHOW:     'bg-amber-500/10 text-amber-400 border-amber-500/20',
};

const RescheduleModal = ({ apt, onClose, onRescheduled }) => {
  const [newDateTime, setNewDateTime] = useState('');
  const [saving, setSaving] = useState(false);

  const handleReschedule = async (e) => {
    e.preventDefault();
    if (!newDateTime) { alert('Please select a new date and time.'); return; }
    setSaving(true);
    try {
      await appointmentService.reschedule(apt.appointmentId, {
        appointmentDateTime: new Date(newDateTime).toISOString().slice(0, 19),
      });
      onRescheduled();
      onClose();
    } catch (err) {
      alert(err.response?.data?.message || 'Reschedule failed.');
    } finally { setSaving(false); }
  };

  return (
    <div className="fixed inset-0 z-[200] flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm">
      <motion.div initial={{ scale: 0.9, opacity: 0 }} animate={{ scale: 1, opacity: 1 }} exit={{ scale: 0.9, opacity: 0 }}
        className="glass-card w-full max-w-md p-8 relative">
        <button onClick={onClose} className="absolute top-4 right-4 text-white/30 hover:text-white transition-colors"><X size={20} /></button>
        <h3 className="text-2xl font-bold mb-2 flex items-center gap-2"><RefreshCw className="text-medical-400" size={22}/> Reschedule</h3>
        <p className="text-white/40 text-sm mb-6">Appointment <span className="text-white font-medium">#{apt.appointmentId}</span></p>
        <form onSubmit={handleReschedule} className="space-y-5">
          <div>
            <label className="text-[10px] font-bold text-white/30 uppercase tracking-widest mb-2 block">New Date & Time</label>
            <input type="datetime-local" className="input-field" value={newDateTime}
              onChange={e => setNewDateTime(e.target.value)} required
              min={new Date().toISOString().slice(0, 16)} />
          </div>
          <button type="submit" disabled={saving} className="premium-btn w-full flex items-center justify-center gap-2">
            {saving ? <><Loader2 size={16} className="animate-spin"/> Rescheduling...</> : <><RefreshCw size={16}/> Confirm Reschedule</>}
          </button>
        </form>
      </motion.div>
    </div>
  );
};

const Appointments = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeFilter, setActiveFilter] = useState('ALL');
  const [rescheduleTarget, setRescheduleTarget] = useState(null);
  const role = localStorage.getItem('role');
  const providerId = localStorage.getItem('providerId');

  const fetchAppointments = async () => {
    setLoading(true);
    try {
      let res;
      if (role === 'ADMIN') res = await appointmentService.getAllAdmin();
      else if (role === 'DOCTOR' && providerId) res = await appointmentService.getProviderSchedule(providerId);
      else res = await appointmentService.getMy();
      setAppointments(res.data || []);
    } catch (err) {
      console.error('Failed to load appointments', err);
      setAppointments([]);
    } finally { setLoading(false); }
  };

  useEffect(() => { fetchAppointments(); }, []);

  const handleCancel = async (id) => {
    if (!confirm('Cancel this appointment?')) return;
    try { await appointmentService.cancel(id); fetchAppointments(); }
    catch (err) { alert(err.response?.data?.message || 'Cancel failed.'); }
  };

  const handleComplete = async (apt) => {
    const notes = prompt('Enter clinical notes for this session (optional):') ?? '';
    try { await appointmentService.complete(apt.appointmentId, notes); fetchAppointments(); }
    catch (err) { alert(err.response?.data?.message || 'Complete failed.'); }
  };

  const handleNoShow = async (id) => {
    if (!confirm('Mark as No-Show?')) return;
    try { await appointmentService.noShow(id); fetchAppointments(); }
    catch (err) { alert('Failed.'); }
  };

  const FILTERS = ['ALL', 'SCHEDULED', 'RESCHEDULED', 'COMPLETED', 'CANCELLED', 'NO_SHOW'];
  const filtered = activeFilter === 'ALL' ? appointments : appointments.filter(a => a.status === activeFilter);

  return (
    <div className="min-h-screen bg-background p-4 sm:p-8 pt-28 sm:pt-32 pb-24">
      <FloatingNav />
      <div className="max-w-5xl mx-auto">
        <header className="mb-8">
          <h2 className="text-3xl sm:text-4xl font-bold mb-2">Appointment Centre</h2>
          <p className="text-white/40 text-sm">
            {role === 'DOCTOR' ? 'Your patient schedule and appointment queue.' :
             role === 'ADMIN'  ? 'All platform appointments — full lifecycle view.' :
             'Track, manage, and reschedule your consultations.'}
          </p>
        </header>

        {/* Filter chips */}
        <div className="flex gap-2 overflow-x-auto pb-2 mb-6 scrollbar-hide">
          {FILTERS.map(f => (
            <button key={f} onClick={() => setActiveFilter(f)}
              className={`px-3 py-1.5 rounded-xl text-xs font-bold whitespace-nowrap transition-all
                ${activeFilter === f ? 'bg-medical-500 text-white' : 'bg-white/5 text-white/40 hover:bg-white/10 hover:text-white'}`}>
              {f} {f !== 'ALL' && appointments.filter(a => a.status === f).length > 0 ? `(${appointments.filter(a => a.status === f).length})` : ''}
            </button>
          ))}
        </div>

        {loading ? (
          <div className="flex justify-center py-24"><Loader2 size={36} className="animate-spin text-medical-400" /></div>
        ) : filtered.length === 0 ? (
          <div className="glass-card p-16 text-center">
            <Calendar size={40} className="mx-auto mb-4 text-white/20" />
            <p className="text-white/40 text-lg">No appointments found.</p>
          </div>
        ) : (
          <div className="space-y-4">
            {filtered.map((apt, i) => (
              <motion.div key={apt.appointmentId}
                initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 0.06 }}
                className="glass-card p-4 sm:p-6 hover:bg-white/[0.04] transition-colors">
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                  {/* Left: Info */}
                  <div className="flex items-start gap-4">
                    <div className="w-12 h-12 rounded-2xl bg-medical-500/10 flex items-center justify-center text-medical-400 shrink-0">
                      <Calendar size={22} />
                    </div>
                    <div>
                      <div className="flex items-center gap-2 flex-wrap mb-1">
                        <h3 className="font-bold text-base">Appointment #{apt.appointmentId}</h3>
                        <span className={`text-[10px] px-2.5 py-0.5 rounded-full font-bold border ${statusStyle[apt.status] || 'text-white/40'}`}>
                          {apt.status}
                        </span>
                      </div>
                      <div className="flex flex-wrap gap-x-4 gap-y-1 text-xs text-white/40">
                        {apt.appointmentDateTime && (
                          <span className="flex items-center gap-1">
                            <Clock size={11} />
                            {new Date(apt.appointmentDateTime).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' })}
                          </span>
                        )}
                        {apt.modeOfConsultation && <span>{apt.modeOfConsultation.replace('_', '-')}</span>}
                        {apt.serviceType && <span>{apt.serviceType}</span>}
                      </div>
                      {(role === 'ADMIN' || role === 'DOCTOR') && (
                        <p className="text-xs text-white/30 mt-1">Patient #{apt.patientId} · Provider #{apt.providerId}</p>
                      )}
                      {apt.reason && <p className="text-xs text-white/50 mt-1 italic">"{apt.reason}"</p>}
                      {apt.doctorNotes && (
                        <p className="text-xs text-emerald-400/70 mt-1">📋 Notes: {apt.doctorNotes}</p>
                      )}
                    </div>
                  </div>

                  {/* Right: Actions */}
                  <div className="flex flex-wrap gap-2 sm:flex-col sm:items-end">
                    {/* Doctor actions */}
                    {role === 'DOCTOR' && (apt.status === 'SCHEDULED' || apt.status === 'RESCHEDULED') && (
                      <>
                        <button onClick={() => handleComplete(apt)}
                          className="flex items-center gap-1.5 px-3 py-2 rounded-xl bg-emerald-500/10 text-emerald-400 hover:bg-emerald-500/20 transition-all text-xs font-bold">
                          <CheckCircle size={14} /> Complete
                        </button>
                        <button onClick={() => handleNoShow(apt.appointmentId)}
                          className="flex items-center gap-1.5 px-3 py-2 rounded-xl bg-amber-500/10 text-amber-400 hover:bg-amber-500/20 transition-all text-xs font-bold">
                          <AlertTriangle size={14} /> No-Show
                        </button>
                      </>
                    )}
                    {/* Patient / Admin actions */}
                    {(role === 'PATIENT' || role === 'ADMIN') && (apt.status === 'SCHEDULED' || apt.status === 'RESCHEDULED') && (
                      <>
                        <button onClick={() => setRescheduleTarget(apt)}
                          className="flex items-center gap-1.5 px-3 py-2 rounded-xl bg-blue-500/10 text-blue-400 hover:bg-blue-500/20 transition-all text-xs font-bold">
                          <RefreshCw size={14} /> Reschedule
                        </button>
                        <button onClick={() => handleCancel(apt.appointmentId)}
                          className="flex items-center gap-1.5 px-3 py-2 rounded-xl bg-rose-500/10 text-rose-400 hover:bg-rose-500/20 transition-all text-xs font-bold">
                          <XCircle size={14} /> Cancel
                        </button>
                      </>
                    )}
                    {/* Cancel for doctor too */}
                    {role === 'DOCTOR' && (apt.status === 'SCHEDULED' || apt.status === 'RESCHEDULED') && (
                      <button onClick={() => handleCancel(apt.appointmentId)}
                        className="flex items-center gap-1.5 px-3 py-2 rounded-xl bg-rose-500/10 text-rose-400 hover:bg-rose-500/20 transition-all text-xs font-bold">
                        <XCircle size={14} /> Cancel
                      </button>
                    )}
                  </div>
                </div>
              </motion.div>
            ))}
          </div>
        )}
      </div>

      <AnimatePresence>
        {rescheduleTarget && (
          <RescheduleModal
            apt={rescheduleTarget}
            onClose={() => setRescheduleTarget(null)}
            onRescheduled={fetchAppointments}
          />
        )}
      </AnimatePresence>
    </div>
  );
};

export default Appointments;
