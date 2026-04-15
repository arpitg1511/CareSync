import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Bell, CheckCheck, Trash2, Loader2, BellOff, Calendar, CreditCard, Star, FileText, Info } from 'lucide-react';
import FloatingNav from '../components/FloatingNav';
import { notificationService } from '../services/api';

const relatedIcon = { APPOINTMENT: Calendar, PAYMENT: CreditCard, REVIEW: Star, RECORD: FileText };

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL'); // ALL | UNREAD

  const userId = localStorage.getItem('userId');
  const role = localStorage.getItem('role');

  useEffect(() => { fetchNotifications(); }, []);

  const fetchNotifications = async () => {
    setLoading(true);
    try {
      const res = role === 'ADMIN'
        ? await notificationService.getAllAdmin()
        : await notificationService.getByRecipient(userId);
      setNotifications(res.data || []);
    } catch (err) {
      console.error('Failed to load notifications', err);
      setNotifications([]);
    } finally { setLoading(false); }
  };

  const markRead = async (id) => {
    try { await notificationService.markAsRead(id); fetchNotifications(); }
    catch (err) { console.error(err); }
  };

  const markAllRead = async () => {
    try { await notificationService.markAllRead(userId); fetchNotifications(); }
    catch (err) { console.error(err); }
  };

  const deleteNotif = async (id) => {
    try { await notificationService.delete(id); fetchNotifications(); }
    catch (err) { console.error(err); }
  };

  const filtered = filter === 'UNREAD' ? notifications.filter(n => !n.isRead) : notifications;
  const unreadCount = notifications.filter(n => !n.isRead).length;

  return (
    <div className="min-h-screen bg-background p-4 sm:p-8 pt-28 sm:pt-32 pb-24">
      <FloatingNav />
      <div className="max-w-3xl mx-auto">
        <header className="mb-8 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h2 className="text-3xl sm:text-4xl font-bold mb-1 flex items-center gap-3">
              Notifications
              {unreadCount > 0 && (
                <span className="px-2.5 py-0.5 rounded-full bg-medical-500 text-white text-sm font-bold">{unreadCount}</span>
              )}
            </h2>
            <p className="text-white/40 text-sm">{notifications.length} total · {unreadCount} unread</p>
          </div>
          <div className="flex gap-3">
            {unreadCount > 0 && (
              <button onClick={markAllRead}
                className="flex items-center gap-2 px-4 py-2 rounded-xl bg-white/5 border border-white/10 hover:bg-medical-500/10 hover:text-medical-400 transition-all text-sm font-medium">
                <CheckCheck size={16} /> Mark all read
              </button>
            )}
          </div>
        </header>

        {/* Filter Tabs */}
        <div className="flex gap-2 mb-6">
          {['ALL', 'UNREAD'].map(f => (
            <button key={f} onClick={() => setFilter(f)}
              className={`px-4 py-2 rounded-xl text-sm font-bold transition-all ${filter === f ? 'bg-medical-500 text-white' : 'bg-white/5 text-white/40 hover:bg-white/10'}`}>
              {f} {f === 'UNREAD' && unreadCount > 0 ? `(${unreadCount})` : ''}
            </button>
          ))}
        </div>

        {loading ? (
          <div className="flex justify-center py-32"><Loader2 size={40} className="animate-spin text-medical-400" /></div>
        ) : filtered.length === 0 ? (
          <div className="glass-card p-16 text-center">
            <BellOff size={48} className="mx-auto mb-4 text-white/20" />
            <p className="text-white/40 text-lg">{filter === 'UNREAD' ? 'No unread notifications.' : 'No notifications yet.'}</p>
          </div>
        ) : (
          <AnimatePresence>
            <div className="space-y-3">
              {filtered.map((n, i) => {
                const Icon = relatedIcon[n.relatedType] || Info;
                return (
                  <motion.div key={n.notificationId}
                    initial={{ opacity: 0, x: -16 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: 16 }}
                    transition={{ delay: i * 0.05 }}
                    className={`glass-card p-4 sm:p-5 flex items-start gap-4 transition-colors group
                      ${!n.isRead ? 'border-medical-500/30 bg-medical-500/5' : 'hover:bg-white/[0.03]'}`}>
                    {/* Icon */}
                    <div className={`w-10 h-10 rounded-xl flex items-center justify-center shrink-0
                      ${!n.isRead ? 'bg-medical-500/20 text-medical-400' : 'bg-white/5 text-white/40'}`}>
                      <Icon size={18} />
                    </div>
                    {/* Content */}
                    <div className="flex-1 min-w-0">
                      <div className="flex items-start justify-between gap-2">
                        <p className={`font-semibold text-sm ${!n.isRead ? 'text-white' : 'text-white/70'}`}>{n.title}</p>
                        <span className="text-xs text-white/30 shrink-0">
                          {n.createdAt ? new Date(n.createdAt).toLocaleDateString() : ''}
                        </span>
                      </div>
                      <p className="text-white/50 text-sm mt-0.5 leading-relaxed">{n.message}</p>
                      {n.relatedType && (
                        <p className="text-xs text-white/30 mt-1">{n.relatedType} #{n.relatedId}</p>
                      )}
                    </div>
                    {/* Actions */}
                    <div className="flex gap-1 shrink-0 opacity-0 group-hover:opacity-100 transition-opacity">
                      {!n.isRead && (
                        <button onClick={() => markRead(n.notificationId)} title="Mark as read"
                          className="p-2 rounded-lg bg-white/5 hover:bg-medical-500/20 hover:text-medical-400 transition-all">
                          <CheckCheck size={14} />
                        </button>
                      )}
                      <button onClick={() => deleteNotif(n.notificationId)} title="Delete"
                        className="p-2 rounded-lg bg-white/5 hover:bg-rose-500/10 hover:text-rose-400 transition-all">
                        <Trash2 size={14} />
                      </button>
                    </div>
                    {/* Unread dot */}
                    {!n.isRead && (
                      <div className="w-2 h-2 rounded-full bg-medical-400 mt-2 shrink-0" />
                    )}
                  </motion.div>
                );
              })}
            </div>
          </AnimatePresence>
        )}
      </div>
    </div>
  );
};

export default Notifications;
