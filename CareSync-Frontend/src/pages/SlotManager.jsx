import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Clock, Plus, Lock, Unlock, Trash2, RefreshCw, Loader2, Calendar, X } from 'lucide-react';
import FloatingNav from '../components/FloatingNav';
import { slotService } from '../services/api';

const SlotManager = () => {
  const [slots, setSlots] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showAdd, setShowAdd] = useState(false);
  const [showRecurring, setShowRecurring] = useState(false);
  const [saving, setSaving] = useState(false);

  const userId = localStorage.getItem('userId');

  const [form, setForm] = useState({
    providerId: userId || '',
    date: '',
    startTime: '09:00',
    endTime: '09:30',
    durationMinutes: 30,
  });

  const [recurForm, setRecurForm] = useState({
    providerId: userId || '',
    date: '',
    endDate: '',
    startTime: '09:00',
    endTime: '09:30',
    durationMinutes: 30,
    recurrencePattern: 'DAILY',
  });

  useEffect(() => { fetchSlots(); }, []);

  const fetchSlots = async () => {
    setLoading(true);
    try {
      const res = await slotService.getByProvider(userId);
      setSlots(res.data || []);
    } catch (err) {
      console.error('Failed to load slots', err);
      setSlots([]);
    } finally { setLoading(false); }
  };

  const handleAddSlot = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await slotService.addSlot({ ...form, providerId: parseInt(form.providerId), durationMinutes: parseInt(form.durationMinutes) });
      setShowAdd(false);
      fetchSlots();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to add slot.');
    } finally { setSaving(false); }
  };

  const handleRecurring = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      const res = await slotService.generateRecurring({
        ...recurForm,
        providerId: parseInt(recurForm.providerId),
        durationMinutes: parseInt(recurForm.durationMinutes),
      });
      alert(`✅ Generated ${res.data.length} recurring slots!`);
      setShowRecurring(false);
      fetchSlots();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to generate slots.');
    } finally { setSaving(false); }
  };

  const handleBlock = async (slotId, isBlocked) => {
    try {
      isBlocked ? await slotService.unblock(slotId) : await slotService.block(slotId);
      fetchSlots();
    } catch (err) { alert('Action failed.'); }
  };

  const handleDelete = async (slotId) => {
    if (!confirm('Delete this slot?')) return;
    try { await slotService.delete(slotId); fetchSlots(); }
    catch (err) { alert('Delete failed.'); }
  };

  const statusColor = (slot) => {
    if (slot.isBlocked) return 'border-rose-500/30 bg-rose-500/5';
    if (slot.isBooked) return 'border-emerald-500/30 bg-emerald-500/5';
    return '';
  };

  const statusLabel = (slot) => {
    if (slot.isBlocked) return <span className="text-xs px-2 py-0.5 rounded-full bg-rose-500/10 text-rose-400 border border-rose-500/20">Blocked</span>;
    if (slot.isBooked) return <span className="text-xs px-2 py-0.5 rounded-full bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">Booked</span>;
    return <span className="text-xs px-2 py-0.5 rounded-full bg-medical-500/10 text-medical-400 border border-medical-500/20">Available</span>;
  };

  const FormModal = ({ title, onClose, onSubmit, formData, setFormData, isRecurring }) => (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm overflow-y-auto">
      <motion.div initial={{ scale: 0.95, opacity: 0 }} animate={{ scale: 1, opacity: 1 }}
        className="glass-card w-full max-w-md p-8 relative my-4">
        <button onClick={onClose} className="absolute top-4 right-4 text-white/30 hover:text-white"><X size={22} /></button>
        <h3 className="text-2xl font-bold mb-6">{title}</h3>
        <form onSubmit={onSubmit} className="space-y-4">
          <div>
            <label className="label-xs">Provider ID</label>
            <input className="input-field" value={formData.providerId}
              onChange={e => setFormData({ ...formData, providerId: e.target.value })} required />
          </div>
          <div className={`grid gap-4 ${isRecurring ? 'grid-cols-2' : 'grid-cols-1'}`}>
            <div>
              <label className="label-xs">{isRecurring ? 'Start Date' : 'Date'}</label>
              <input type="date" className="input-field" value={formData.date}
                onChange={e => setFormData({ ...formData, date: e.target.value })} required />
            </div>
            {isRecurring && (
              <div>
                <label className="label-xs">End Date</label>
                <input type="date" className="input-field" value={formData.endDate}
                  onChange={e => setFormData({ ...formData, endDate: e.target.value })} required />
              </div>
            )}
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="label-xs">Start Time</label>
              <input type="time" className="input-field" value={formData.startTime}
                onChange={e => setFormData({ ...formData, startTime: e.target.value })} required />
            </div>
            <div>
              <label className="label-xs">End Time</label>
              <input type="time" className="input-field" value={formData.endTime}
                onChange={e => setFormData({ ...formData, endTime: e.target.value })} required />
            </div>
          </div>
          <div className={`grid gap-4 ${isRecurring ? 'grid-cols-2' : 'grid-cols-1'}`}>
            <div>
              <label className="label-xs">Duration (min)</label>
              <input type="number" className="input-field" value={formData.durationMinutes}
                onChange={e => setFormData({ ...formData, durationMinutes: e.target.value })} required />
            </div>
            {isRecurring && (
              <div>
                <label className="label-xs">Recurrence</label>
                <select className="input-field" value={formData.recurrencePattern}
                  onChange={e => setFormData({ ...formData, recurrencePattern: e.target.value })}>
                  <option value="DAILY">Daily</option>
                  <option value="WEEKLY">Weekly</option>
                </select>
              </div>
            )}
          </div>
          <button type="submit" disabled={saving} className="premium-btn w-full h-12 flex items-center justify-center gap-2">
            {saving ? <Loader2 size={18} className="animate-spin" /> : <Plus size={18} />}
            {saving ? 'Saving...' : (isRecurring ? 'Generate Slots' : 'Add Slot')}
          </button>
        </form>
      </motion.div>
    </div>
  );

  return (
    <div className="min-h-screen bg-background p-4 sm:p-8 pt-28 sm:pt-32 pb-24">
      <FloatingNav />
      <div className="max-w-5xl mx-auto">
        <header className="mb-10 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h2 className="text-3xl sm:text-4xl font-bold mb-1">Availability Manager</h2>
            <p className="text-white/40 text-sm">Manage your time slots and availability.</p>
          </div>
          <div className="flex gap-3">
            <button onClick={() => setShowRecurring(true)}
              className="flex items-center gap-2 px-4 py-2.5 rounded-xl bg-white/5 border border-white/10 hover:bg-white/10 transition-all text-sm font-medium">
              <RefreshCw size={16} /> Recurring
            </button>
            <button onClick={() => setShowAdd(true)} className="premium-btn flex items-center gap-2">
              <Plus size={18} /> Add Slot
            </button>
          </div>
        </header>

        <AnimatePresence>
          {showAdd && <FormModal title="Add Single Slot" onClose={() => setShowAdd(false)} onSubmit={handleAddSlot} formData={form} setFormData={setForm} isRecurring={false} />}
          {showRecurring && <FormModal title="Generate Recurring Slots" onClose={() => setShowRecurring(false)} onSubmit={handleRecurring} formData={recurForm} setFormData={setRecurForm} isRecurring={true} />}
        </AnimatePresence>

        {/* Stats */}
        <div className="grid grid-cols-3 gap-4 mb-8">
          {[
            { label: 'Available', val: slots.filter(s => !s.isBooked && !s.isBlocked).length, color: 'text-medical-400' },
            { label: 'Booked', val: slots.filter(s => s.isBooked).length, color: 'text-emerald-400' },
            { label: 'Blocked', val: slots.filter(s => s.isBlocked).length, color: 'text-rose-400' },
          ].map((s, i) => (
            <div key={i} className="glass-card p-4 text-center">
              <p className={`text-2xl font-bold ${s.color}`}>{s.val}</p>
              <p className="text-white/40 text-xs mt-1">{s.label}</p>
            </div>
          ))}
        </div>

        {loading ? (
          <div className="flex justify-center py-32"><Loader2 size={40} className="animate-spin text-medical-400" /></div>
        ) : slots.length === 0 ? (
          <div className="glass-card p-16 text-center">
            <Calendar size={48} className="mx-auto mb-4 text-white/20" />
            <p className="text-white/40 text-lg">No slots yet. Add your first availability slot.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {slots.sort((a, b) => new Date(a.date) - new Date(b.date)).map((slot, i) => (
              <motion.div key={slot.slotId} initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 0.04 }}
                className={`glass-card p-5 ${statusColor(slot)}`}>
                <div className="flex items-center justify-between mb-3">
                  <div className="flex items-center gap-2 text-white/60">
                    <Calendar size={14} />
                    <span className="text-sm font-medium">{slot.date}</span>
                  </div>
                  {statusLabel(slot)}
                </div>
                <div className="flex items-center gap-2 text-white mb-3">
                  <Clock size={16} className="text-white/40" />
                  <span className="font-bold">{slot.startTime} – {slot.endTime}</span>
                  <span className="text-xs text-white/40">{slot.durationMinutes}min</span>
                </div>
                {slot.recurrence && slot.recurrence !== 'NONE' && (
                  <p className="text-xs text-white/30 mb-3">↻ {slot.recurrence}</p>
                )}
                {!slot.isBooked && (
                  <div className="flex gap-2">
                    <button onClick={() => handleBlock(slot.slotId, slot.isBlocked)}
                      className={`flex-1 py-2 rounded-xl text-xs font-bold flex items-center justify-center gap-1 transition-all
                        ${slot.isBlocked ? 'bg-emerald-500/10 text-emerald-400 hover:bg-emerald-500/20' : 'bg-rose-500/10 text-rose-400 hover:bg-rose-500/20'}`}>
                      {slot.isBlocked ? <><Unlock size={13} /> Unblock</> : <><Lock size={13} /> Block</>}
                    </button>
                    <button onClick={() => handleDelete(slot.slotId)}
                      className="p-2 rounded-xl bg-white/5 hover:bg-rose-500/10 hover:text-rose-400 transition-all">
                      <Trash2 size={14} />
                    </button>
                  </div>
                )}
              </motion.div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default SlotManager;
