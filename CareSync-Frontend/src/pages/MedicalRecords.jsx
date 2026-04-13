import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { FileText, Search, Download, Plus, ChevronRight, Activity } from 'lucide-react';
import FloatingNav from '../components/FloatingNav';
import { recordService } from '../services/api';

const MedicalRecords = () => {
    const [records, setRecords] = useState([]);
    const [selectedRecord, setSelectedRecord] = useState(null);
    const userId = localStorage.getItem('userId');
    const role = localStorage.getItem('role');

    useEffect(() => {
        const fetchRecords = async () => {
            const res = role === 'PATIENT' ? await recordService.getByPatient(userId) : await recordService.getByProvider(userId);
            setRecords(res.data);
        };
        fetchRecords();
    }, []);

    return (
        <div className="min-h-screen bg-background p-8 pt-32">
            <FloatingNav />
            <div className="max-w-7xl mx-auto">
                <header className="mb-16 flex justify-between items-end">
                    <div>
                        <h1 className="text-5xl font-bold mb-4">Health Archive</h1>
                        <p className="text-white/40">Secure access to diagnoses, prescriptions, and medical history.</p>
                    </div>
                </header>

                <div className="grid grid-cols-1 lg:grid-cols-12 gap-12">
                    {/* Records List */}
                    <div className="lg:col-span-4 space-y-4">
                        <div className="relative mb-8">
                            <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-white/20" size={18} />
                            <input type="text" placeholder="Search records..." className="input-field pl-12" />
                        </div>
                        
                        {records.map((record) => (
                            <motion.button
                                key={record.recordId}
                                onClick={() => setSelectedRecord(record)}
                                className={`w-full text-left p-6 rounded-3xl transition-all duration-300 border ${
                                    selectedRecord?.recordId === record.recordId 
                                    ? 'bg-medical-500 border-medical-400 shadow-[0_0_30px_rgba(14,165,233,0.3)]' 
                                    : 'bg-white/[0.03] border-white/5 hover:bg-white/[0.05]'
                                }`}
                            >
                                <div className="flex items-center justify-between mb-2">
                                    <span className={`text-[10px] font-bold uppercase tracking-widest ${
                                        selectedRecord?.recordId === record.recordId ? 'text-white/70' : 'text-medical-400'
                                    }`}>
                                        {record.createdAt.split('T')[0]}
                                    </span>
                                    {selectedRecord?.recordId === record.recordId && <ChevronRight size={16} />}
                                </div>
                                <h3 className="text-xl font-bold mb-1 truncate">{record.diagnosis}</h3>
                                <p className={`text-sm ${selectedRecord?.recordId === record.recordId ? 'text-white/60' : 'text-white/30'}`}>
                                    ID: #{record.recordId}
                                </p>
                            </motion.button>
                        ))}
                    </div>

                    {/* Record Detail View */}
                    <div className="lg:col-span-8">
                        <AnimatePresence mode="wait">
                            {selectedRecord ? (
                                <motion.div
                                    key={selectedRecord.recordId}
                                    initial={{ opacity: 0, x: 20 }}
                                    animate={{ opacity: 1, x: 0 }}
                                    exit={{ opacity: 0, x: -20 }}
                                    className="glass-card p-12 min-h-[600px] flex flex-col"
                                >
                                    <div className="flex justify-between items-start mb-12">
                                        <div className="flex items-center gap-6">
                                            <div className="w-20 h-20 rounded-3xl bg-medical-500/10 flex items-center justify-center text-medical-400 border border-medical-500/20">
                                                <FileText size={36} />
                                            </div>
                                            <div>
                                                <h2 className="text-3xl font-bold mb-1">{selectedRecord.diagnosis}</h2>
                                                <p className="text-white/40">Clinical Analysis & Summary</p>
                                            </div>
                                        </div>
                                        <button className="premium-btn py-3 px-6 flex items-center gap-2">
                                            <Download size={18} /> Export PDF
                                        </button>
                                    </div>

                                    <div className="grid grid-cols-2 gap-12 mb-12">
                                        <div className="space-y-2">
                                            <p className="text-[10px] font-bold uppercase tracking-widest text-white/20">Prescription Details</p>
                                            <div className="p-6 rounded-2xl bg-white/[0.02] border border-white/5 italic text-lg leading-relaxed">
                                                "{selectedRecord.prescription}"
                                            </div>
                                        </div>
                                        <div className="space-y-4">
                                            <p className="text-[10px] font-bold uppercase tracking-widest text-white/20">Vitals & Metrics</p>
                                            <div className="grid grid-cols-2 gap-4">
                                                <div className="p-4 rounded-xl bg-emerald-500/5 border border-emerald-500/20 text-center">
                                                    <p className="text-xs text-emerald-400 mb-1">Status</p>
                                                    <p className="font-bold">Stable</p>
                                                </div>
                                                <div className="p-4 rounded-xl bg-medical-500/5 border border-medical-500/20 text-center">
                                                    <p className="text-xs text-medical-400 mb-1">Follow up</p>
                                                    <p className="font-bold">{selectedRecord.followUpDate || 'None'}</p>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div className="mt-auto pt-12 border-t border-white/5">
                                        <p className="text-[10px] font-bold uppercase tracking-widest text-white/20 mb-4">Clinical Notes</p>
                                        <p className="text-white/50 leading-loose">
                                            {selectedRecord.notes || "No additional clinical notes provided for this consultation record."}
                                        </p>
                                    </div>
                                </motion.div>
                            ) : (
                                <div className="glass-card p-24 text-center flex flex-col items-center justify-center min-h-[600px] border-dashed">
                                    <div className="w-24 h-24 rounded-full bg-white/5 flex items-center justify-center mb-8 border border-white/10">
                                        <Activity size={48} className="text-white/20" />
                                    </div>
                                    <h2 className="text-2xl font-bold mb-2">Select a record</h2>
                                    <p className="text-white/30">Choose a consultation from the left to view full medical details.</p>
                                </div>
                            )}
                        </AnimatePresence>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default MedicalRecords;
