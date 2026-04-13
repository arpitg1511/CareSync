import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Calendar as CalendarIcon, Clock, Plus, Trash2, CheckCircle, AlertCircle } from 'lucide-react';
import FloatingNav from '../components/FloatingNav';
import { scheduleService } from '../services/api';

const ManageAvailability = () => {
    const [slots, setSlots] = useState([]);
    const [loading, setLoading] = useState(true);
    const [newSlot, setNewSlot] = useState({ date: '', startTime: '', endTime: '', durationMinutes: 30 });
    const providerId = localStorage.getItem('userId');

    useEffect(() => {
        fetchSlots();
    }, []);

    const fetchSlots = async () => {
        try {
            const res = await scheduleService.getByProvider(providerId);
            setSlots(res.data);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleAddSlot = async () => {
        try {
            await scheduleService.addSlot({ ...newSlot, providerId });
            fetchSlots();
            setNewSlot({ date: '', startTime: '', endTime: '', durationMinutes: 30 });
        } catch (err) {
            alert("Failed to add slot");
        }
    };

    const handleDeleteSlot = async (id) => {
        try {
            await scheduleService.deleteSlot(id);
            fetchSlots();
        } catch (err) {
            alert("Failed to delete slot");
        }
    };

    return (
        <div className="min-h-screen bg-background p-8 pt-32">
            <FloatingNav />
            <div className="max-w-6xl mx-auto">
                <header className="mb-12">
                    <h1 className="text-4xl font-bold mb-2">Manage Availability</h1>
                    <p className="text-white/40">Configure your clinical hours and time slots.</p>
                </header>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-12">
                    {/* Add Slot Panel */}
                    <div className="glass-card p-8 h-fit">
                        <h2 className="text-xl font-bold mb-6 flex items-center gap-2">
                            <Plus className="text-medical-400" size={20} /> Create New Slot
                        </h2>
                        <div className="space-y-6">
                            <div>
                                <label className="text-xs font-bold uppercase tracking-widest text-white/30 mb-2 block">Date</label>
                                <input 
                                    type="date" 
                                    className="input-field" 
                                    value={newSlot.date}
                                    onChange={(e) => setNewSlot({...newSlot, date: e.target.value})}
                                />
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                                <div>
                                    <label className="text-xs font-bold uppercase tracking-widest text-white/30 mb-2 block">Start</label>
                                    <input 
                                        type="time" 
                                        className="input-field" 
                                        value={newSlot.startTime}
                                        onChange={(e) => setNewSlot({...newSlot, startTime: e.target.value})}
                                    />
                                </div>
                                <div>
                                    <label className="text-xs font-bold uppercase tracking-widest text-white/30 mb-2 block">End</label>
                                    <input 
                                        type="time" 
                                        className="input-field" 
                                        value={newSlot.endTime}
                                        onChange={(e) => setNewSlot({...newSlot, endTime: e.target.value})}
                                    />
                                </div>
                            </div>
                            <button onClick={handleAddSlot} className="premium-btn w-full">Generate Slot</button>
                        </div>
                    </div>

                    {/* Slots List */}
                    <div className="lg:col-span-2 space-y-4">
                        <h2 className="text-xl font-bold mb-6">Your Scheduled Slots</h2>
                        {loading ? (
                            <p>Loading...</p>
                        ) : slots.length === 0 ? (
                            <div className="glass-card p-12 text-center border-dashed">
                                <AlertCircle className="mx-auto text-white/20 mb-4" size={48} />
                                <p className="text-white/40">No slots defined yet.</p>
                            </div>
                        ) : (
                            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                                {slots.map((slot) => (
                                    <motion.div 
                                        layout
                                        key={slot.slotId} 
                                        className="glass-card p-6 flex items-center justify-between group"
                                    >
                                        <div>
                                            <p className="font-bold flex items-center gap-2">
                                                <CalendarIcon size={14} className="text-medical-400" /> {slot.date}
                                            </p>
                                            <p className="text-white/40 text-sm flex items-center gap-2">
                                                <Clock size={14} /> {slot.startTime} - {slot.endTime}
                                            </p>
                                            <div className="mt-2 text-[10px] font-bold uppercase tracking-tighter">
                                                {slot.isBooked ? (
                                                    <span className="text-amber-400">Booked</span>
                                                ) : (
                                                    <span className="text-emerald-400">Available</span>
                                                )}
                                            </div>
                                        </div>
                                        <button 
                                            onClick={() => handleDeleteSlot(slot.slotId)}
                                            className="p-3 rounded-xl hover:bg-rose-500/10 text-white/20 hover:text-rose-500 transition-colors"
                                        >
                                            <Trash2 size={18} />
                                        </button>
                                    </motion.div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ManageAvailability;
