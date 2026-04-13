import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Mail, Lock, User, ArrowRight, ShieldCheck } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/api';

const Login = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({ email: '', password: '', fullName: '', role: 'PATIENT' });
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleAuth = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
        const res = isLogin 
            ? await authService.login({ email: formData.email, password: formData.password })
            : await authService.register(formData);
        
        if (isLogin) {
            localStorage.setItem('token', res.data.token);
            localStorage.setItem('role', res.data.role.replace('ROLE_', '')); // Handle Spring Security Prefix
            navigate('/dashboard');
        } else {
            setIsLogin(true);
            alert("Registration Successful. Please Login.");
        }
    } catch (err) {
        alert(err.response?.data?.message || "Authentication failed");
    } finally {
        setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-6 bg-background relative overflow-hidden">
      {/* 🌊 Background Glow */}
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-medical-500/10 rounded-full blur-[120px]" />

      <motion.div 
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        className="glass-card w-full max-w-[500px] p-12 relative z-10"
      >
        <div className="text-center mb-10">
          <div className="w-16 h-16 bg-medical-500/20 rounded-2xl flex items-center justify-center mx-auto mb-6 text-medical-400">
            <ShieldCheck size={32} />
          </div>
          <h2 className="text-3xl font-bold mb-2">
            {isLogin ? 'Welcome Back' : 'Create Account'}
          </h2>
          <p className="text-white/40">Enter your credentials to access the portal</p>
        </div>

        <form onSubmit={handleAuth} className="space-y-6">
          <AnimatePresence mode='wait'>
            {!isLogin && (
              <motion.div 
                initial={{ opacity: 0, y: -20 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -20 }}
                className="space-y-6"
              >
                {/* 🎭 Role Selector */}
                <div className="flex gap-4 mb-6">
                   {['PATIENT', 'DOCTOR', 'ADMIN'].map((r) => (
                     <button
                        key={r}
                        type="button"
                        onClick={() => setFormData({...formData, role: r})}
                        className={`flex-1 py-3 rounded-2xl border transition-all font-bold text-xs tracking-widest ${
                           formData.role === r 
                           ? 'bg-medical-500 border-medical-500 text-white shadow-lg' 
                           : 'border-white/10 text-white/30 hover:border-white/30'
                        }`}
                     >
                        {r}
                     </button>
                   ))}
                </div>

                <div className="relative">
                  <User className="absolute left-5 top-1/2 -translate-y-1/2 text-white/30" size={20} />
                  <input 
                    type="text" 
                    placeholder="Full Name" 
                    className="input-field pl-14" 
                    value={formData.fullName}
                    onChange={(e) => setFormData({...formData, fullName: e.target.value})}
                    required 
                  />
                </div>
              </motion.div>
            )}
          </AnimatePresence>

          <div className="relative">
            <Mail className="absolute left-5 top-1/2 -translate-y-1/2 text-white/30" size={20} />
            <input 
              type="email" 
              placeholder="Email Address" 
              className="input-field pl-14" 
              value={formData.email}
              onChange={(e) => setFormData({...formData, email: e.target.value})}
              required 
            />
          </div>

          <div className="relative">
            <Lock className="absolute left-5 top-1/2 -translate-y-1/2 text-white/30" size={20} />
            <input 
              type="password" 
              placeholder="Password" 
              className="input-field pl-14" 
              value={formData.password}
              onChange={(e) => setFormData({...formData, password: e.target.value})}
              required 
            />
          </div>

          <button 
            type="submit" 
            disabled={loading}
            className="premium-btn w-full flex items-center justify-center gap-2 group h-14"
          >
            {loading ? 'Processing...' : (isLogin ? 'Sign In' : 'Sign Up')}
            {!loading && <ArrowRight size={20} className="group-hover:translate-x-1 transition-transform" /> }
          </button>

          <div className="flex items-center gap-4 my-8">
             <div className="h-[1px] flex-1 bg-white/5" />
             <span className="text-[10px] font-bold text-white/20 uppercase tracking-widest">Or Secure Entry With</span>
             <div className="h-[1px] flex-1 bg-white/5" />
          </div>

          <button 
            type="button"
            className="w-full py-4 rounded-2xl bg-white/5 border border-white/10 flex items-center justify-center gap-3 font-medium hover:bg-white/10 transition-all active:scale-95"
          >
             <img src="https://www.gstatic.com/images/branding/product/1x/gsa_512dp.png" className="w-5 h-5" alt="G" />
             Continue with Google
          </button>
        </form>

        <div className="mt-8 text-center text-white/30">
          <button 
            onClick={() => setIsLogin(!isLogin)}
            className="text-medical-400 hover:text-medical-300 transition-colors font-medium"
          >
            {isLogin ? "Don't have an account? Sign Up" : "Already have an account? Sign In"}
          </button>
        </div>
      </motion.div>
    </div>
  );
};

export default Login;
