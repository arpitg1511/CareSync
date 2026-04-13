import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Star, MessageSquare, Shield, CheckCircle } from 'lucide-react';
import FloatingNav from '../components/FloatingNav';
import { reviewService } from '../services/api';

const Reviews = () => {
    const [reviews, setReviews] = useState([]);
    const [avgRating, setAvgRating] = useState(0);
    const providerId = localStorage.getItem('userId');

    useEffect(() => {
        const fetchReviews = async () => {
            const res = await reviewService.getByProvider(providerId);
            setReviews(res.data);
            const avgRes = await reviewService.getAvgRating(providerId);
            setAvgRating(avgRes.data);
        };
        fetchReviews();
    }, []);

    return (
        <div className="min-h-screen bg-background p-8 pt-32 text-foreground">
            <FloatingNav />
            <div className="max-w-5xl mx-auto">
                <header className="mb-20 text-center">
                    <motion.div 
                        initial={{ scale: 0.9, opacity: 0 }}
                        animate={{ scale: 1, opacity: 1 }}
                        className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-medical-500/10 border border-medical-500/20 text-medical-400 text-xs font-bold uppercase tracking-widest mb-8"
                    >
                        <Star size={14} fill="currentColor" /> Trust & Feedback
                    </motion.div>
                    <h1 className="text-6xl font-bold mb-6">Patient Reviews</h1>
                    <div className="flex items-center justify-center gap-4 text-3xl font-bold">
                        <span className="text-medical-400">{avgRating.toFixed(1)}</span>
                        <div className="flex gap-1">
                            {[1, 2, 3, 4, 5].map((s) => (
                                <Star key={s} size={28} className={s <= avgRating ? "text-amber-400 fill-amber-400" : "text-white/10"} />
                            ))}
                        </div>
                        <span className="text-white/20 ml-2">({reviews.length} Ratings)</span>
                    </div>
                </header>

                <div className="grid grid-cols-1 gap-8">
                    {reviews.map((review, i) => (
                        <motion.div 
                            initial={{ opacity: 0, y: 20 }}
                            whileInView={{ opacity: 1, y: 0 }}
                            viewport={{ once: true }}
                            transition={{ delay: i * 0.1 }}
                            key={review.reviewId}
                            className="glass-card p-10 relative overflow-hidden group"
                        >
                            <div className="absolute top-0 right-0 p-8 text-white/5 opacity-0 group-hover:opacity-100 transition-opacity">
                                <MessageSquare size={120} />
                            </div>
                            
                            <div className="relative z-10">
                                <div className="flex items-center justify-between mb-8">
                                    <div className="flex items-center gap-6">
                                        <div className="w-14 h-14 rounded-2xl bg-white/5 flex items-center justify-center font-bold text-xl border border-white/10">
                                            {review.isAnonymous ? "A" : "P"}
                                        </div>
                                        <div>
                                            <h4 className="text-xl font-bold">{review.isAnonymous ? "Anonymous Patient" : "Verified Patient"}</h4>
                                            <p className="text-white/30 text-xs uppercase tracking-widest">{review.reviewDate.split('T')[0]}</p>
                                        </div>
                                    </div>
                                    <div className="flex gap-1">
                                        {[1, 2, 3, 4, 5].map((s) => (
                                            <Star key={s} size={16} className={s <= review.rating ? "text-amber-400 fill-amber-400" : "text-white/10"} />
                                        ))}
                                    </div>
                                </div>

                                <p className="text-xl text-white/70 leading-relaxed mb-8 italic">
                                    "{review.comment}"
                                </p>

                                <div className="flex items-center gap-6 pt-8 border-t border-white/5">
                                    <div className="flex items-center gap-2 text-[10px] font-bold uppercase tracking-widest text-emerald-400">
                                        <CheckCircle size={14} /> Verified Consultation
                                    </div>
                                    <div className="flex items-center gap-2 text-[10px] font-bold uppercase tracking-widest text-white/30">
                                        <Shield size={14} /> HIPAA Compliant
                                    </div>
                                </div>
                            </div>
                        </motion.div>
                    ))}

                    {reviews.length === 0 && (
                        <div className="glass-card p-20 text-center border-dashed">
                            <p className="text-white/20 text-xl">No patient reviews yet.</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default Reviews;
