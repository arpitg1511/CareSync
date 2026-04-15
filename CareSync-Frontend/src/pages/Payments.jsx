import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { CreditCard, Wallet, Smartphone, Banknote, TrendingUp, RefreshCw, CheckCircle, Clock, XCircle, Loader2 } from 'lucide-react';
import FloatingNav from '../components/FloatingNav';
import { paymentService, appointmentService } from '../services/api';

const modeIcon = { CARD: CreditCard, UPI: Smartphone, WALLET: Wallet, CASH: Banknote, NET_BANKING: Banknote };
const statusStyle = {
  PAID: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20',
  PENDING: 'bg-amber-500/10 text-amber-400 border-amber-500/20',
  REFUNDED: 'bg-blue-500/10 text-blue-400 border-blue-500/20',
  FAILED: 'bg-rose-500/10 text-rose-400 border-rose-500/20',
};

const Payments = () => {
  const [payments, setPayments] = useState([]);
  const [revenue, setRevenue] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showPayModal, setShowPayModal] = useState(false);
  const [payForm, setPayForm] = useState({ appointmentId: '', amount: '', mode: 'CARD', transactionId: '' });
  const [paying, setPaying] = useState(false);

  const role = localStorage.getItem('role');
  const userId = localStorage.getItem('userId');

  useEffect(() => { fetchPayments(); }, []);

  const fetchPayments = async () => {
    setLoading(true);
    try {
      let res;
      if (role === 'ADMIN') res = await paymentService.getAllAdmin();
      else if (role === 'DOCTOR') {
        res = await paymentService.getByProvider(userId);
        const rev = await paymentService.getRevenue(userId);
        setRevenue(rev.data);
      } else {
        res = await paymentService.getByPatient(userId);
      }
      setPayments(res.data || []);
    } catch (err) {
      console.error('Failed to load payments', err);
      setPayments([]);
    } finally { setLoading(false); }
  };

  const handlePay = async (e) => {
    e.preventDefault();
    setPaying(true);
    try {
      await paymentService.process({
        appointmentId: parseInt(payForm.appointmentId),
        patientId: parseInt(userId),
        amount: parseFloat(payForm.amount),
        mode: payForm.mode,
        transactionId: payForm.transactionId || undefined,
      });
      setShowPayModal(false);
      setPayForm({ appointmentId: '', amount: '', mode: 'CARD', transactionId: '' });
      fetchPayments();
    } catch (err) {
      alert(err.response?.data?.message || 'Payment failed.');
    } finally { setPaying(false); }
  };

  const handleRefund = async (appointmentId) => {
    if (!confirm('Request refund for this payment?')) return;
    try {
      await paymentService.refund(appointmentId);
      fetchPayments();
    } catch (err) { alert('Refund failed: ' + (err.response?.data?.message || err.message)); }
  };

  const totalPaid = payments.filter(p => p.status === 'PAID').reduce((s, p) => s + (p.amount || 0), 0);
  const totalRefunded = payments.filter(p => p.status === 'REFUNDED').reduce((s, p) => s + (p.amount || 0), 0);

  return (
    <div className="min-h-screen bg-background p-4 sm:p-8 pt-28 sm:pt-32 pb-24">
      <FloatingNav />
      <div className="max-w-5xl mx-auto">
        <header className="mb-10 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h2 className="text-3xl sm:text-4xl font-bold mb-1">
              {role === 'DOCTOR' ? 'Earnings Dashboard' : 'Payment History'}
            </h2>
            <p className="text-white/40 text-sm">
              {role === 'DOCTOR' ? 'Track your revenue and pending collections.' : 'All your transactions in one place.'}
            </p>
          </div>
          {role === 'PATIENT' && (
            <button onClick={() => setShowPayModal(true)} className="premium-btn flex items-center gap-2 whitespace-nowrap">
              <CreditCard size={18} /> Make Payment
            </button>
          )}
        </header>

        {/* Stats Row */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
          {[
            { label: role === 'DOCTOR' ? 'Total Revenue' : 'Total Paid', value: `₹${(revenue ?? totalPaid).toLocaleString()}`, icon: TrendingUp, color: 'text-emerald-400' },
            { label: 'Pending', value: payments.filter(p => p.status === 'PENDING').length, icon: Clock, color: 'text-amber-400' },
            { label: 'Refunded', value: `₹${totalRefunded.toLocaleString()}`, icon: RefreshCw, color: 'text-blue-400' },
          ].map((s, i) => (
            <motion.div key={i} initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 0.08 }}
              className="glass-card p-5 flex items-center justify-between">
              <div>
                <p className="text-white/40 text-xs uppercase tracking-widest mb-1">{s.label}</p>
                <p className="text-2xl font-bold">{s.value}</p>
              </div>
              <s.icon size={28} className={s.color} />
            </motion.div>
          ))}
        </div>

        {/* Pay Modal */}
        {showPayModal && (
          <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm">
            <motion.div initial={{ scale: 0.95, opacity: 0 }} animate={{ scale: 1, opacity: 1 }}
              className="glass-card w-full max-w-md p-8 relative">
              <button onClick={() => setShowPayModal(false)} className="absolute top-4 right-4 text-white/30 hover:text-white">✕</button>
              <h3 className="text-2xl font-bold mb-6">Make a Payment</h3>
              <form onSubmit={handlePay} className="space-y-4">
                <div>
                  <label className="label-xs">Appointment ID</label>
                  <input className="input-field" placeholder="e.g. 42" value={payForm.appointmentId}
                    onChange={e => setPayForm({ ...payForm, appointmentId: e.target.value })} required />
                </div>
                <div>
                  <label className="label-xs">Amount (₹)</label>
                  <input type="number" className="input-field" placeholder="e.g. 500" value={payForm.amount}
                    onChange={e => setPayForm({ ...payForm, amount: e.target.value })} required />
                </div>
                <div>
                  <label className="label-xs">Payment Mode</label>
                  <select className="input-field" value={payForm.mode} onChange={e => setPayForm({ ...payForm, mode: e.target.value })}>
                    {['CARD', 'UPI', 'WALLET', 'CASH', 'NET_BANKING'].map(m => <option key={m} value={m}>{m}</option>)}
                  </select>
                </div>
                {payForm.mode !== 'CASH' && (
                  <div>
                    <label className="label-xs">Transaction ID (optional)</label>
                    <input className="input-field" placeholder="Auto-generated if blank" value={payForm.transactionId}
                      onChange={e => setPayForm({ ...payForm, transactionId: e.target.value })} />
                  </div>
                )}
                <button type="submit" disabled={paying} className="premium-btn w-full h-12 flex items-center justify-center gap-2">
                  {paying ? <Loader2 size={18} className="animate-spin" /> : <CreditCard size={18} />}
                  {paying ? 'Processing...' : 'Confirm Payment'}
                </button>
              </form>
            </motion.div>
          </div>
        )}

        {/* Payment List */}
        {loading ? (
          <div className="flex justify-center py-32"><Loader2 size={40} className="animate-spin text-medical-400" /></div>
        ) : payments.length === 0 ? (
          <div className="glass-card p-16 text-center">
            <CreditCard size={48} className="mx-auto mb-4 text-white/20" />
            <p className="text-white/40 text-lg">No payment records found.</p>
          </div>
        ) : (
          <div className="space-y-3">
            {payments.map((p, i) => {
              const Icon = modeIcon[p.mode] || CreditCard;
              return (
                <motion.div key={p.paymentId} initial={{ opacity: 0, x: -16 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 0.06 }}
                  className="glass-card p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-4 hover:bg-white/[0.04] transition-colors">
                  <div className="flex items-center gap-4">
                    <div className="w-11 h-11 rounded-xl bg-white/5 flex items-center justify-center text-white/50 shrink-0">
                      <Icon size={20} />
                    </div>
                    <div>
                      <p className="font-semibold">Appointment #{p.appointmentId}</p>
                      <p className="text-white/40 text-xs mt-0.5">
                        {p.mode} · {p.transactionId ? `TXN: ${p.transactionId}` : 'Pay-at-clinic'}
                        {p.paidAt ? ` · ${new Date(p.paidAt).toLocaleDateString()}` : ''}
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center gap-3 sm:gap-4 ml-15 sm:ml-0">
                    <span className="text-xl font-bold">₹{p.amount?.toLocaleString()}</span>
                    <span className={`text-xs px-3 py-1 rounded-full border font-bold ${statusStyle[p.status] || ''}`}>{p.status}</span>
                    {p.status === 'PAID' && role === 'PATIENT' && (
                      <button onClick={() => handleRefund(p.appointmentId)}
                        className="text-xs px-3 py-1 rounded-xl bg-white/5 hover:bg-blue-500/10 hover:text-blue-400 transition-all font-medium">
                        Refund
                      </button>
                    )}
                    {p.status === 'PENDING' && role === 'DOCTOR' && (
                      <button onClick={async () => { await paymentService.markPaid(p.appointmentId); fetchPayments(); }}
                        className="text-xs px-3 py-1 rounded-xl bg-emerald-500/10 text-emerald-400 hover:bg-emerald-500/20 transition-all font-medium">
                        Mark Paid
                      </button>
                    )}
                  </div>
                </motion.div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};

export default Payments;
