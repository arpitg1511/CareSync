import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Shield, CheckCircle, XCircle, UserCheck, Users, BarChart2, MessageSquare, Bell, Activity, Loader2, Send, Flag, Trash2, RefreshCw } from 'lucide-react';
import FloatingNav from '../components/FloatingNav';
import { providerService, appointmentService, paymentService, reviewService, notificationService } from '../services/api';

const TABS = [
  { id: 'providers', label: 'Provider Verification', icon: UserCheck },
  { id: 'appointments', label: 'All Appointments', icon: Activity },
  { id: 'payments', label: 'Payments', icon: BarChart2 },
  { id: 'reviews', label: 'Reviews', icon: MessageSquare },
  { id: 'notify', label: 'Broadcast', icon: Bell },
];

const statusStyle = {
  SCHEDULED: 'bg-medical-500/10 text-medical-400', RESCHEDULED: 'bg-blue-500/10 text-blue-400',
  COMPLETED: 'bg-emerald-500/10 text-emerald-400', CANCELLED: 'bg-rose-500/10 text-rose-400',
  NO_SHOW: 'bg-amber-500/10 text-amber-400',
  PAID: 'bg-emerald-500/10 text-emerald-400', PENDING: 'bg-amber-500/10 text-amber-400',
  REFUNDED: 'bg-blue-500/10 text-blue-400', FAILED: 'bg-rose-500/10 text-rose-400',
};

const AdminHub = () => {
  const [tab, setTab] = useState('providers');
  const [pending, setPending] = useState([]);
  const [appointments, setAppointments] = useState([]);
  const [payments, setPayments] = useState([]);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(false);
  const [broadcast, setBroadcast] = useState({ title: '', message: '', recipientRole: 'ALL' });
  const [sending, setSending] = useState(false);

  useEffect(() => { loadTab(tab); }, [tab]);

  const loadTab = async (t) => {
    setLoading(true);
    try {
      if (t === 'providers') { const r = await providerService.getPending(); setPending(r.data || []); }
      if (t === 'appointments') { const r = await appointmentService.getAllAdmin(); setAppointments(r.data || []); }
      if (t === 'payments') { const r = await paymentService.getAllAdmin(); setPayments(r.data || []); }
      if (t === 'reviews') { const r = await reviewService.getAllAdmin(); setReviews(r.data || []); }
    } catch (err) { console.error(err); }
    finally { setLoading(false); }
  };

  const approve = async (id) => {
    try { await providerService.approve(id); loadTab('providers'); } catch { alert('Approval failed'); }
  };
  const reject = async (id) => {
    try { await providerService.reject(id); loadTab('providers'); } catch { alert('Rejection failed'); }
  };
  const deleteReview = async (id) => {
    if (!confirm('Delete this review?')) return;
    try { await reviewService.delete(id); loadTab('reviews'); } catch { alert('Delete failed'); }
  };
  const unflagReview = async (id) => {
    try { await reviewService.unflag(id); loadTab('reviews'); } catch { alert('Unflag failed'); }
  };
  const refundPayment = async (apptId) => {
    if (!confirm('Trigger refund for this payment?')) return;
    try { await paymentService.refund(apptId); loadTab('payments'); } catch { alert('Refund failed'); }
  };

  const sendBroadcast = async (e) => {
    e.preventDefault();
    if (!broadcast.title || !broadcast.message) return;
    setSending(true);
    try {
      // Build a single admin notification (in a real system, this would fan out to all users)
      await notificationService.send({
        recipientId: 0,
        recipientRole: broadcast.recipientRole,
        title: broadcast.title,
        message: broadcast.message,
        type: 'IN_APP',
      });
      alert('Broadcast sent successfully!');
      setBroadcast({ title: '', message: '', recipientRole: 'ALL' });
    } catch { alert('Broadcast failed.'); }
    finally { setSending(false); }
  };

  // Analytics summary from loaded data
  const analytics = {
    totalBookings: appointments.length,
    completed: appointments.filter(a => a.status === 'COMPLETED').length,
    cancelled: appointments.filter(a => a.status === 'CANCELLED').length,
    totalRevenue: payments.filter(p => p.status === 'PAID').reduce((s, p) => s + (p.amount || 0), 0),
  };

  return (
    <div className="min-h-screen bg-background p-4 sm:p-8 pt-28 sm:pt-32 pb-24">
      <FloatingNav />
      <div className="max-w-7xl mx-auto">
        <header className="mb-8">
          <h2 className="text-3xl sm:text-4xl font-bold mb-1 flex items-center gap-3">Admin Hub <Shield size={28} className="text-medical-400" /></h2>
          <p className="text-white/40 text-sm">Platform oversight, provider verification and analytics.</p>
        </header>

        {/* Quick Stats */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-8">
          {[
            { label: 'Total Bookings', val: analytics.totalBookings, color: 'text-medical-400' },
            { label: 'Completed', val: analytics.completed, color: 'text-emerald-400' },
            { label: 'Cancelled', val: analytics.cancelled, color: 'text-rose-400' },
            { label: 'Revenue', val: `₹${analytics.totalRevenue.toLocaleString()}`, color: 'text-amber-400' },
          ].map((s, i) => (
            <motion.div key={i} initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 0.08 }}
              className="glass-card p-4">
              <p className="text-white/40 text-xs mb-1">{s.label}</p>
              <p className={`text-xl font-bold ${s.color}`}>{s.val}</p>
            </motion.div>
          ))}
        </div>

        {/* Tab Navigation */}
        <div className="flex gap-2 overflow-x-auto pb-2 mb-6 scrollbar-hide">
          {TABS.map(t => (
            <button key={t.id} onClick={() => setTab(t.id)}
              className={`flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-bold whitespace-nowrap transition-all
                ${tab === t.id ? 'bg-medical-500 text-white' : 'bg-white/5 text-white/40 hover:bg-white/10 hover:text-white'}`}>
              <t.icon size={15} /> {t.label}
            </button>
          ))}
        </div>

        {loading ? (
          <div className="flex justify-center py-20"><Loader2 size={36} className="animate-spin text-medical-400" /></div>
        ) : (
          <>
            {/* Provider Verification */}
            {tab === 'providers' && (
              <div className="space-y-4">
                {pending.length === 0 ? (
                  <div className="glass-card p-14 text-center">
                    <Shield size={40} className="mx-auto mb-3 text-white/20" />
                    <p className="text-white/40">No pending provider registrations.</p>
                  </div>
                ) : pending.map((doc, i) => (
                  <motion.div key={doc.providerId} initial={{ opacity: 0, x: -16 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 0.08 }}
                    className="glass-card p-5 sm:p-6 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                    <div className="flex items-center gap-4">
                      <div className="w-12 h-12 rounded-2xl bg-medical-500/10 flex items-center justify-center text-medical-400 shrink-0">
                        <UserCheck size={24} />
                      </div>
                      <div>
                        <h3 className="font-bold text-lg">{doc.fullName}</h3>
                        <p className="text-medical-400 text-sm">{doc.specialization}</p>
                        <p className="text-white/30 text-xs">{doc.email} · {doc.experienceMonths} months exp.</p>
                      </div>
                    </div>
                    <div className="flex gap-3">
                      <button onClick={() => approve(doc.providerId)}
                        className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-emerald-500 text-white font-bold hover:bg-emerald-400 transition-colors text-sm">
                        <CheckCircle size={16} /> Approve
                      </button>
                      <button onClick={() => reject(doc.providerId)}
                        className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-white/5 border border-rose-500/30 text-rose-400 font-bold hover:bg-rose-500/10 transition-colors text-sm">
                        <XCircle size={16} /> Reject
                      </button>
                    </div>
                  </motion.div>
                ))}
              </div>
            )}

            {/* All Appointments */}
            {tab === 'appointments' && (
              <div className="space-y-3">
                {appointments.length === 0 ? <div className="glass-card p-14 text-center"><p className="text-white/40">No appointments found.</p></div>
                  : appointments.map((apt, i) => (
                    <motion.div key={apt.appointmentId} initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: i * 0.04 }}
                      className="glass-card p-4 sm:p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                      <div>
                        <p className="font-semibold">Appointment #{apt.appointmentId}</p>
                        <p className="text-white/40 text-xs">Patient #{apt.patientId} · Provider #{apt.providerId}
                          {apt.appointmentDateTime ? ` · ${new Date(apt.appointmentDateTime).toLocaleString()}` : ''}
                        </p>
                        {apt.modeOfConsultation && <p className="text-xs text-white/30 mt-0.5">{apt.modeOfConsultation} · {apt.serviceType || 'General'}</p>}
                      </div>
                      <span className={`text-xs px-3 py-1 rounded-full font-bold ${statusStyle[apt.status] || 'text-white/40'}`}>{apt.status}</span>
                    </motion.div>
                  ))}
              </div>
            )}

            {/* Payments */}
            {tab === 'payments' && (
              <div className="space-y-3">
                {payments.length === 0 ? <div className="glass-card p-14 text-center"><p className="text-white/40">No payments found.</p></div>
                  : payments.map((p, i) => (
                    <motion.div key={p.paymentId} initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: i * 0.04 }}
                      className="glass-card p-4 sm:p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                      <div>
                        <p className="font-semibold">Payment #{p.paymentId} · Appointment #{p.appointmentId}</p>
                        <p className="text-white/40 text-xs">Patient #{p.patientId} · {p.mode}
                          {p.transactionId ? ` · ${p.transactionId}` : ''}
                        </p>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className="font-bold">₹{p.amount?.toLocaleString()}</span>
                        <span className={`text-xs px-3 py-1 rounded-full font-bold ${statusStyle[p.status] || ''}`}>{p.status}</span>
                        {p.status === 'PAID' && (
                          <button onClick={() => refundPayment(p.appointmentId)}
                            className="text-xs px-3 py-1 rounded-xl bg-blue-500/10 text-blue-400 hover:bg-blue-500/20 transition-all font-medium">
                            <RefreshCw size={12} className="inline mr-1" />Refund
                          </button>
                        )}
                      </div>
                    </motion.div>
                  ))}
              </div>
            )}

            {/* Review Moderation */}
            {tab === 'reviews' && (
              <div className="space-y-3">
                {reviews.length === 0 ? <div className="glass-card p-14 text-center"><p className="text-white/40">No reviews found.</p></div>
                  : reviews.map((rev, i) => (
                    <motion.div key={rev.reviewId} initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: i * 0.04 }}
                      className={`glass-card p-4 sm:p-5 flex flex-col sm:flex-row sm:items-start justify-between gap-3 ${rev.isFlagged ? 'border-rose-500/30' : ''}`}>
                      <div className="flex-1">
                        <div className="flex items-center gap-2 mb-1 flex-wrap">
                          <span className="font-semibold text-sm">Review #{rev.reviewId}</span>
                          {'★'.repeat(rev.rating)}<span className="text-white/30">{'★'.repeat(5 - rev.rating)}</span>
                          {rev.isFlagged && <span className="text-xs px-2 py-0.5 rounded-full bg-rose-500/10 text-rose-400 border border-rose-500/20">Flagged</span>}
                        </div>
                        <p className="text-white/40 text-xs">Patient #{rev.patientId} · Provider #{rev.providerId} · Appointment #{rev.appointmentId}</p>
                        {rev.comment && <p className="text-white/70 text-sm mt-1 leading-relaxed">{rev.comment}</p>}
                      </div>
                      <div className="flex gap-2 shrink-0">
                        {rev.isFlagged && (
                          <button onClick={() => unflagReview(rev.reviewId)}
                            className="px-3 py-1.5 rounded-xl bg-emerald-500/10 text-emerald-400 hover:bg-emerald-500/20 transition-all text-xs font-bold">
                            Unflag
                          </button>
                        )}
                        <button onClick={() => deleteReview(rev.reviewId)}
                          className="p-2 rounded-xl bg-white/5 hover:bg-rose-500/10 hover:text-rose-400 transition-all">
                          <Trash2 size={15} />
                        </button>
                      </div>
                    </motion.div>
                  ))}
              </div>
            )}

            {/* Broadcast Notification */}
            {tab === 'notify' && (
              <div className="max-w-xl mx-auto">
                <div className="glass-card p-8">
                  <h3 className="text-xl font-bold mb-6 flex items-center gap-2"><Bell className="text-medical-400" size={20} /> Platform Broadcast</h3>
                  <form onSubmit={sendBroadcast} className="space-y-4">
                    <div>
                      <label className="label-xs">Target Audience</label>
                      <select className="input-field" value={broadcast.recipientRole}
                        onChange={e => setBroadcast({ ...broadcast, recipientRole: e.target.value })}>
                        <option value="ALL">All Users</option>
                        <option value="PATIENT">Patients Only</option>
                        <option value="DOCTOR">Providers Only</option>
                      </select>
                    </div>
                    <div>
                      <label className="label-xs">Notification Title</label>
                      <input className="input-field" placeholder="e.g. System Maintenance Notice" value={broadcast.title}
                        onChange={e => setBroadcast({ ...broadcast, title: e.target.value })} required />
                    </div>
                    <div>
                      <label className="label-xs">Message</label>
                      <textarea className="input-field min-h-[120px] resize-none" placeholder="Write your message..."
                        value={broadcast.message} onChange={e => setBroadcast({ ...broadcast, message: e.target.value })} required />
                    </div>
                    <button type="submit" disabled={sending} className="premium-btn w-full h-12 flex items-center justify-center gap-2">
                      {sending ? <Loader2 size={18} className="animate-spin" /> : <Send size={18} />}
                      {sending ? 'Sending...' : 'Send Broadcast'}
                    </button>
                  </form>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default AdminHub;
