import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Calendar, Clock, MapPin, ChevronRight, CheckCircle, XCircle, Loader2 } from 'lucide-react';
import FloatingNav from '../components/FloatingNav';
import { appointmentService } from '../services/api';

const Appointments = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const role = localStorage.getItem('role');

  const fetchAppointments = async () => {
    try {
      const res = role === 'ADMIN' 
        ? await appointmentService.getAllAdmin()
        : await appointmentService.getMy();
      setAppointments(res.data);
    } catch (err) {
      console.error("Failed to load appointments", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAppointments();
  }, []);

  const handleCancel = async (id) => {
    try {
        await appointmentService.cancel(id);
        fetchAppointments();
    } catch (err) {
        alert("Failed to cancel appointment.");
    }
  };

  return (
    <div className="min-h-screen bg-background p-8 pt-32">
      <FloatingNav />

      <div className="max-w-5xl mx-auto">
        <header className="mb-12">
          <h2 className="text-4xl font-bold mb-2">Appointment Center</h2>
          <p className="text-white/40">Track and manage your medical consultations.</p>
        </header>

        <div className="space-y-6">
          {appointments.map((apt, i) => (
            <motion.div
              key={apt.appointmentId}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.1 }}
              className="glass-card p-10 group hover:bg-white/[0.05] transition-all cursor-pointer border-l-4 border-l-medical-500"
            >
              <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-10">
                <div className="flex items-center gap-10">
                  <div className="w-20 h-20 rounded-3xl bg-white/5 border border-white/10 flex flex-col items-center justify-center text-medical-400">
                    <Calendar size={28} />
                  </div>
                  
                  <div>
                    <h3 className="text-2xl font-bold mb-1 group-hover:text-medical-400 transition-colors">
                        Appointment #{apt.appointmentId}
                    </h3>
                    <p className="text-white/40 font-medium mb-4">Clinic Session</p>
                    <div className="flex flex-wrap gap-6 text-sm">
                       <span className="flex items-center gap-2 text-white/60"><Clock size={16} /> Scheduled Time</span>
                       <span className="flex items-center gap-2 text-white/60"><Calendar size={16} /> {apt.appointmentDate}</span>
                    </div>
                  </div>
                </div>

                <div className="flex flex-col items-end gap-6 w-full md:w-auto">
                  <span className={`px-4 py-2 rounded-full text-xs font-bold tracking-widest ${
                    apt.status === 'APPROVED' ? 'bg-emerald-500/10 text-emerald-400' : 'bg-amber-500/10 text-amber-400'
                  }`}>
                    {apt.status}
                  </span>
                    {apt.status !== 'CANCELLED' && apt.status !== 'COMPLETED' && (
                        <div className="flex gap-4">
                            {role === 'DOCTOR' && (
                                <button 
                                    onClick={async () => {
                                        const notes = prompt("Enter clinical notes for this session:");
                                        if (notes) {
                                            try {
                                                await appointmentService.complete(apt.appointmentId, notes);
                                                fetchAppointments();
                                            } catch (err) {
                                                alert("Failed to complete session.");
                                            }
                                        }
                                    }}
                                    className="p-3 rounded-xl bg-emerald-500/10 text-emerald-400 hover:bg-emerald-500/20 transition-all"
                                    title="Mark as Completed"
                                >
                                    <CheckCircle size={20} />
                                </button>
                            )}
                            <button 
                                onClick={() => handleCancel(apt.appointmentId)}
                                className="p-3 rounded-xl bg-white/5 hover:bg-rose-500/10 hover:text-rose-500 transition-all"
                                title="Cancel Session"
                            >
                                <XCircle size={20} />
                            </button>
                        </div>
                    )}
                    <button className="px-6 py-3 rounded-xl bg-white/5 hover:bg-medical-500 transition-all font-bold">
                       View Details
                    </button>
                  </div>
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default Appointments;
