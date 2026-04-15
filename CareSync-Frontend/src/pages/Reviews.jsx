import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Star, Plus, X, Flag, Trash2, Loader2, MessageSquare } from 'lucide-react';
import FloatingNav from '../components/FloatingNav';
import { reviewService } from '../services/api';

const StarRating = ({ value, onChange, readonly = false }) => (
  <div className="flex gap-1">
    {[1, 2, 3, 4, 5].map(n => (
      <button key={n} type="button" onClick={() => !readonly && onChange && onChange(n)}
        className={`transition-transform ${!readonly ? 'hover:scale-110 cursor-pointer' : 'cursor-default'}`}>
        <Star size={22} className={n <= value ? 'fill-amber-400 text-amber-400' : 'text-white/20'} />
      </button>
    ))}
  </div>
);

const Reviews = () => {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showSubmit, setShowSubmit] = useState(false);
  const [form, setForm] = useState({ appointmentId: '', patientId: '', providerId: '', rating: 5, comment: '', isAnonymous: false });
  const [submitting, setSubmitting] = useState(false);

  const role = localStorage.getItem('role');
  const userId = localStorage.getItem('userId');

  useEffect(() => { fetchReviews(); }, []);

  const fetchReviews = async () => {
    setLoading(true);
    try {
      let res;
      if (role === 'ADMIN') res = await reviewService.getAllAdmin();
      else if (role === 'DOCTOR') res = await reviewService.getByProvider(userId);
      else res = await reviewService.getByPatient(userId);
      setReviews(res.data || []);
    } catch (err) {
      console.error('Failed to load reviews', err);
      setReviews([]);
    } finally { setLoading(false); }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      await reviewService.submit({
        ...form,
        appointmentId: parseInt(form.appointmentId),
        patientId: parseInt(form.patientId || userId),
        providerId: parseInt(form.providerId),
      });
      setShowSubmit(false);
      setForm({ appointmentId: '', patientId: '', providerId: '', rating: 5, comment: '', isAnonymous: false });
      fetchReviews();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to submit review.');
    } finally { setSubmitting(false); }
  };

  const handleFlag = async (reviewId) => {
    try { await reviewService.flag(reviewId); fetchReviews(); }
    catch (err) { alert('Failed to flag review.'); }
  };

  const handleDelete = async (reviewId) => {
    if (!confirm('Delete this review permanently?')) return;
    try { await reviewService.delete(reviewId); fetchReviews(); }
    catch (err) { alert('Failed to delete review.'); }
  };

  const handleUnflag = async (reviewId) => {
    try { await reviewService.unflag(reviewId); fetchReviews(); }
    catch (err) { alert('Failed to unflag.'); }
  };

  const avgRating = reviews.length ? (reviews.reduce((s, r) => s + r.rating, 0) / reviews.length).toFixed(1) : '—';

  return (
    <div className="min-h-screen bg-background p-4 sm:p-8 pt-28 sm:pt-32 pb-24">
      <FloatingNav />
      <div className="max-w-4xl mx-auto">
        <header className="mb-10 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h2 className="text-3xl sm:text-4xl font-bold mb-1">
              {role === 'DOCTOR' ? 'My Reviews' : role === 'ADMIN' ? 'Review Moderation' : 'My Reviews'}
            </h2>
            <p className="text-white/40 text-sm">
              {reviews.length} review{reviews.length !== 1 ? 's' : ''} · Avg Rating:
              <span className="text-amber-400 font-bold ml-1">{avgRating}</span>
            </p>
          </div>
          {role === 'PATIENT' && (
            <button onClick={() => setShowSubmit(true)} className="premium-btn flex items-center gap-2 whitespace-nowrap">
              <Plus size={18} /> Write Review
            </button>
          )}
        </header>

        {/* Submit Modal */}
        <AnimatePresence>
          {showSubmit && (
            <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm">
              <motion.div initial={{ scale: 0.95, opacity: 0 }} animate={{ scale: 1, opacity: 1 }} exit={{ scale: 0.95, opacity: 0 }}
                className="glass-card w-full max-w-lg p-8 relative">
                <button onClick={() => setShowSubmit(false)} className="absolute top-4 right-4 text-white/30 hover:text-white"><X size={22} /></button>
                <h3 className="text-2xl font-bold mb-6 flex items-center gap-2"><MessageSquare className="text-medical-400" size={22} /> Submit Review</h3>
                <form onSubmit={handleSubmit} className="space-y-4">
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="label-xs">Appointment ID</label>
                      <input className="input-field" placeholder="e.g. 42" value={form.appointmentId}
                        onChange={e => setForm({ ...form, appointmentId: e.target.value })} required />
                    </div>
                    <div>
                      <label className="label-xs">Provider ID</label>
                      <input className="input-field" placeholder="e.g. 3" value={form.providerId}
                        onChange={e => setForm({ ...form, providerId: e.target.value })} required />
                    </div>
                  </div>
                  <div>
                    <label className="label-xs mb-2 block">Your Rating</label>
                    <StarRating value={form.rating} onChange={v => setForm({ ...form, rating: v })} />
                  </div>
                  <div>
                    <label className="label-xs">Comment</label>
                    <textarea className="input-field min-h-[100px] resize-none" placeholder="Share your experience..." value={form.comment}
                      onChange={e => setForm({ ...form, comment: e.target.value })} />
                  </div>
                  <label className="flex items-center gap-3 cursor-pointer">
                    <input type="checkbox" checked={form.isAnonymous}
                      onChange={e => setForm({ ...form, isAnonymous: e.target.checked })}
                      className="w-4 h-4 rounded border-white/20 bg-white/5 accent-medical-500" />
                    <span className="text-sm text-white/60">Post anonymously</span>
                  </label>
                  <button type="submit" disabled={submitting} className="premium-btn w-full h-12 flex items-center justify-center gap-2">
                    {submitting ? <Loader2 size={18} className="animate-spin" /> : <Star size={18} />}
                    {submitting ? 'Submitting...' : 'Submit Review'}
                  </button>
                </form>
              </motion.div>
            </div>
          )}
        </AnimatePresence>

        {loading ? (
          <div className="flex justify-center py-32"><Loader2 size={40} className="animate-spin text-medical-400" /></div>
        ) : reviews.length === 0 ? (
          <div className="glass-card p-16 text-center">
            <Star size={48} className="mx-auto mb-4 text-white/20" />
            <p className="text-white/40 text-lg">No reviews yet.</p>
          </div>
        ) : (
          <div className="space-y-4">
            {reviews.map((rev, i) => (
              <motion.div key={rev.reviewId} initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: i * 0.07 }}
                className={`glass-card p-5 sm:p-6 ${rev.isFlagged ? 'border-rose-500/30' : ''}`}>
                <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-4">
                  <div className="flex-1">
                    <div className="flex items-center gap-3 mb-2 flex-wrap">
                      <StarRating value={rev.rating} readonly />
                      <span className="text-white/40 text-xs">
                        {rev.isAnonymous ? 'Anonymous' : `Patient #${rev.patientId}`}
                        {rev.reviewDate ? ` · ${new Date(rev.reviewDate).toLocaleDateString()}` : ''}
                      </span>
                      {rev.isFlagged && (
                        <span className="text-xs px-2 py-0.5 rounded-full bg-rose-500/10 text-rose-400 border border-rose-500/20">Flagged</span>
                      )}
                    </div>
                    <p className="text-xs text-white/30 mb-2">Appointment #{rev.appointmentId} · Provider #{rev.providerId}</p>
                    {rev.comment && <p className="text-white/70 text-sm leading-relaxed">{rev.comment}</p>}
                  </div>
                  <div className="flex gap-2 shrink-0">
                    {role === 'DOCTOR' && !rev.isFlagged && (
                      <button onClick={() => handleFlag(rev.reviewId)} title="Flag for moderation"
                        className="p-2 rounded-xl bg-white/5 hover:bg-rose-500/10 hover:text-rose-400 transition-all">
                        <Flag size={16} />
                      </button>
                    )}
                    {role === 'ADMIN' && (
                      <>
                        {rev.isFlagged && (
                          <button onClick={() => handleUnflag(rev.reviewId)} title="Unflag"
                            className="p-2 rounded-xl bg-white/5 hover:bg-emerald-500/10 hover:text-emerald-400 transition-all text-xs font-bold px-3">
                            Unflag
                          </button>
                        )}
                        <button onClick={() => handleDelete(rev.reviewId)} title="Delete review"
                          className="p-2 rounded-xl bg-white/5 hover:bg-rose-500/10 hover:text-rose-400 transition-all">
                          <Trash2 size={16} />
                        </button>
                      </>
                    )}
                  </div>
                </div>
              </motion.div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Reviews;
