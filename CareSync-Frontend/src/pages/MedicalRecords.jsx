import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { FileText, Plus, X, Calendar, Stethoscope, Pill, ClipboardList, Paperclip, ChevronDown, ChevronUp, Loader2, Edit } from 'lucide-react';
import FloatingNav from '../components/FloatingNav';
import { recordService } from '../services/api';

const statusColors = {
  COMPLETED: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20',
};

const MedicalRecords = () => {
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [expanded, setExpanded] = useState(null);
  const [showCreate, setShowCreate] = useState(false);
  const [editRecord, setEditRecord] = useState(null);
  const [form, setForm] = useState({ appointmentId: '', patientId: '', providerId: '', diagnosis: '', prescription: '', notes: '', attachmentUrl: '', followUpDate: '' });
  const [saving, setSaving] = useState(false);

  const role = localStorage.getItem('role');
  const userId = localStorage.getItem('userId');

  useEffect(() => { fetchRecords(); }, []);

  const fetchRecords = async () => {
    setLoading(true);
    try {
      let res;
      if (role === 'ADMIN') res = await recordService.getAllAdmin();
      else if (role === 'DOCTOR') res = await recordService.getByProvider(userId);
      else res = await recordService.getByPatient(userId);
      setRecords(res.data || []);
    } catch (err) {
      console.error('Failed to load records', err);
      setRecords([]);
    } finally { setLoading(false); }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      if (editRecord) {
        await recordService.update(editRecord.recordId, form);
      } else {
        await recordService.create(form);
      }
      setShowCreate(false);
      setEditRecord(null);
      setForm({ appointmentId: '', patientId: '', providerId: '', diagnosis: '', prescription: '', notes: '', attachmentUrl: '', followUpDate: '' });
      fetchRecords();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to save record.');
    } finally { setSaving(false); }
  };

  const openEdit = (record) => {
    setEditRecord(record);
    setForm({
      appointmentId: record.appointmentId,
      patientId: record.patientId,
      providerId: record.providerId,
      diagnosis: record.diagnosis || '',
      prescription: record.prescription || '',
      notes: record.notes || '',
      attachmentUrl: record.attachmentUrl || '',
      followUpDate: record.followUpDate || '',
    });
    setShowCreate(true);
  };

  return (
    <div className="min-h-screen bg-background p-4 sm:p-8 pt-28 sm:pt-32 pb-24">
      <FloatingNav />
      <div className="max-w-5xl mx-auto">
        <header className="mb-10 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h2 className="text-3xl sm:text-4xl font-bold mb-1">Medical Records</h2>
            <p className="text-white/40 text-sm">Electronic health records and prescriptions.</p>
          </div>
          {role === 'DOCTOR' && (
            <button onClick={() => { setEditRecord(null); setForm({ appointmentId: '', patientId: '', providerId: '', diagnosis: '', prescription: '', notes: '', attachmentUrl: '', followUpDate: '' }); setShowCreate(true); }}
              className="premium-btn flex items-center gap-2 whitespace-nowrap">
              <Plus size={18} /> New Record
            </button>
          )}
        </header>

        {/* Create / Edit Modal */}
        <AnimatePresence>
          {showCreate && (
            <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm overflow-y-auto">
              <motion.div initial={{ scale: 0.95, opacity: 0 }} animate={{ scale: 1, opacity: 1 }} exit={{ scale: 0.95, opacity: 0 }}
                className="glass-card w-full max-w-2xl p-6 sm:p-10 relative my-4">
                <button onClick={() => { setShowCreate(false); setEditRecord(null); }} className="absolute top-4 right-4 text-white/30 hover:text-white">
                  <X size={22} />
                </button>
                <h3 className="text-2xl font-bold mb-6 flex items-center gap-2">
                  <FileText className="text-medical-400" size={22} />
                  {editRecord ? 'Edit Medical Record' : 'Create Medical Record'}
                </h3>
                <form onSubmit={handleSubmit} className="space-y-4">
                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                    <div>
                      <label className="label-xs">Appointment ID</label>
                      <input className="input-field" placeholder="e.g. 42" value={form.appointmentId}
                        onChange={e => setForm({ ...form, appointmentId: e.target.value })} required />
                    </div>
                    <div>
                      <label className="label-xs">Patient ID</label>
                      <input className="input-field" placeholder="e.g. 7" value={form.patientId}
                        onChange={e => setForm({ ...form, patientId: e.target.value })} required />
                    </div>
                    <div>
                      <label className="label-xs">Provider ID</label>
                      <input className="input-field" placeholder="e.g. 3" value={form.providerId}
                        onChange={e => setForm({ ...form, providerId: e.target.value })} required />
                    </div>
                  </div>
                  <div>
                    <label className="label-xs">Diagnosis *</label>
                    <textarea className="input-field min-h-[80px] resize-none" placeholder="Enter diagnosis..." value={form.diagnosis}
                      onChange={e => setForm({ ...form, diagnosis: e.target.value })} required />
                  </div>
                  <div>
                    <label className="label-xs">Prescription</label>
                    <textarea className="input-field min-h-[80px] resize-none" placeholder="Medications, dosage..." value={form.prescription}
                      onChange={e => setForm({ ...form, prescription: e.target.value })} />
                  </div>
                  <div>
                    <label className="label-xs">Clinical Notes</label>
                    <textarea className="input-field min-h-[80px] resize-none" placeholder="Additional notes..." value={form.notes}
                      onChange={e => setForm({ ...form, notes: e.target.value })} />
                  </div>
                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                    <div>
                      <label className="label-xs">Attachment URL</label>
                      <input className="input-field" placeholder="https://..." value={form.attachmentUrl}
                        onChange={e => setForm({ ...form, attachmentUrl: e.target.value })} />
                    </div>
                    <div>
                      <label className="label-xs">Follow-Up Date</label>
                      <input type="date" className="input-field" value={form.followUpDate}
                        onChange={e => setForm({ ...form, followUpDate: e.target.value })} />
                    </div>
                  </div>
                  <button type="submit" disabled={saving} className="premium-btn w-full flex items-center justify-center gap-2 h-12">
                    {saving ? <><Loader2 size={18} className="animate-spin" /> Saving...</> : (editRecord ? 'Update Record' : 'Create Record')}
                  </button>
                </form>
              </motion.div>
            </div>
          )}
        </AnimatePresence>

        {loading ? (
          <div className="flex justify-center py-32"><Loader2 size={40} className="animate-spin text-medical-400" /></div>
        ) : records.length === 0 ? (
          <div className="glass-card p-16 text-center">
            <FileText size={48} className="mx-auto mb-4 text-white/20" />
            <p className="text-white/40 text-lg">No medical records found.</p>
          </div>
        ) : (
          <div className="space-y-4">
            {records.map((rec, i) => (
              <motion.div key={rec.recordId} initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 0.07 }}
                className="glass-card overflow-hidden">
                {/* Header */}
                <div className="p-5 sm:p-6 flex flex-col sm:flex-row sm:items-center justify-between gap-4 cursor-pointer"
                  onClick={() => setExpanded(expanded === rec.recordId ? null : rec.recordId)}>
                  <div className="flex items-center gap-4">
                    <div className="w-12 h-12 rounded-2xl bg-medical-500/10 flex items-center justify-center text-medical-400 shrink-0">
                      <FileText size={22} />
                    </div>
                    <div>
                      <h4 className="font-bold text-lg">Record #{rec.recordId}</h4>
                      <p className="text-white/40 text-sm">Appointment #{rec.appointmentId} · Patient #{rec.patientId}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    {rec.followUpDate && (
                      <span className="flex items-center gap-1 text-xs px-3 py-1 rounded-full bg-amber-500/10 text-amber-400 border border-amber-500/20">
                        <Calendar size={12} /> Follow-up: {rec.followUpDate}
                      </span>
                    )}
                    {role === 'DOCTOR' && (
                      <button onClick={e => { e.stopPropagation(); openEdit(rec); }}
                        className="p-2 rounded-xl bg-white/5 hover:bg-medical-500/20 hover:text-medical-400 transition-all">
                        <Edit size={16} />
                      </button>
                    )}
                    {expanded === rec.recordId ? <ChevronUp size={18} className="text-white/40" /> : <ChevronDown size={18} className="text-white/40" />}
                  </div>
                </div>
                {/* Expanded Detail */}
                <AnimatePresence>
                  {expanded === rec.recordId && (
                    <motion.div initial={{ height: 0, opacity: 0 }} animate={{ height: 'auto', opacity: 1 }} exit={{ height: 0, opacity: 0 }}
                      className="overflow-hidden border-t border-white/5">
                      <div className="p-5 sm:p-6 grid grid-cols-1 sm:grid-cols-2 gap-6">
                        <div className="space-y-1">
                          <p className="label-xs flex items-center gap-1"><Stethoscope size={12} /> Diagnosis</p>
                          <p className="text-white/80 text-sm leading-relaxed">{rec.diagnosis}</p>
                        </div>
                        {rec.prescription && (
                          <div className="space-y-1">
                            <p className="label-xs flex items-center gap-1"><Pill size={12} /> Prescription</p>
                            <p className="text-white/80 text-sm leading-relaxed">{rec.prescription}</p>
                          </div>
                        )}
                        {rec.notes && (
                          <div className="sm:col-span-2 space-y-1">
                            <p className="label-xs flex items-center gap-1"><ClipboardList size={12} /> Clinical Notes</p>
                            <p className="text-white/80 text-sm leading-relaxed">{rec.notes}</p>
                          </div>
                        )}
                        {rec.attachmentUrl && (
                          <div className="sm:col-span-2">
                            <a href={rec.attachmentUrl} target="_blank" rel="noreferrer"
                              className="flex items-center gap-2 text-medical-400 hover:underline text-sm">
                              <Paperclip size={14} /> View Attachment
                            </a>
                          </div>
                        )}
                        <div className="text-xs text-white/30">
                          Created: {rec.createdAt ? new Date(rec.createdAt).toLocaleString() : '—'}
                        </div>
                        {rec.updatedAt && rec.updatedAt !== rec.createdAt && (
                          <div className="text-xs text-white/30">
                            Updated: {new Date(rec.updatedAt).toLocaleString()}
                          </div>
                        )}
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>
              </motion.div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default MedicalRecords;
