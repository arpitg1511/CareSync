import React from 'react';
import { motion } from 'framer-motion';
import { ArrowRight, Shield, Zap, Heart } from 'lucide-react';
import FloatingNav from '../components/FloatingNav';

const Home = () => {
  return (
    <div className="relative min-h-screen w-full overflow-hidden">
      {/* 🌊 Liquid Background Blobs */}
      <div className="absolute top-[-10%] left-[-10%] w-[50%] h-[50%] bg-medical-500/20 rounded-full blur-[120px] animate-liquid" />
      <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] bg-medical-300/10 rounded-full blur-[100px] animate-liquid" />

      <FloatingNav />

      <main className="container mx-auto px-6 pt-48 pb-24 text-center relative z-10">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8 }}
        >
          <span className="px-4 py-2 rounded-full bg-white/5 border border-white/10 text-medical-400 text-sm font-medium mb-8 inline-block backdrop-blur-md">
            The Future of Healthcare Connectivity
          </span>
          <h1 className="text-6xl md:text-8xl font-bold tracking-tight mb-8 bg-gradient-to-b from-white to-white/40 bg-clip-text text-transparent">
            CareSync <br /> Microservices
          </h1>
          <p className="max-w-2xl mx-auto text-xl text-white/50 leading-relaxed mb-12">
            Experience a fluid, decoupled healthcare management system built on 
            distributed cloud architecture. Secure, fast, and patient-centric.
          </p>

          <div className="flex flex-col sm:flex-row items-center justify-center gap-6">
            <button className="premium-btn flex items-center gap-2 group">
              Get Started <ArrowRight size={20} className="group-hover:translate-x-1 transition-transform" />
            </button>
            <button className="px-8 py-4 rounded-2xl border border-white/10 hover:bg-white/5 transition-colors font-medium">
              Explore Services
            </button>
          </div>
        </motion.div>

        {/* 🧬 Modular Feature Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-48">
          {[
            { icon: Shield, title: 'Secure Vault', desc: 'Enterprise-grade JWT encryption across all services.' },
            { icon: Zap, title: 'Instant Feign', desc: 'Real-time inter-service communication with zero latency.' },
            { icon: Heart, title: 'Patient First', desc: 'Personalized profile management and smart scheduling.' }
          ].map((feature, i) => (
            <motion.div
              key={i}
              initial={{ opacity: 0, y: 40 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.4 + (i * 0.1) }}
              className="glass-card p-10 text-left hover:scale-[1.02] transition-transform duration-500 group"
            >
              <div className="w-14 h-14 bg-white/5 rounded-2xl flex items-center justify-center mb-8 border border-white/10 group-hover:bg-medical-500/20 transition-colors">
                <feature.icon className="text-medical-400" size={28} />
              </div>
              <h3 className="text-2xl font-semibold mb-4">{feature.title}</h3>
              <p className="text-white/40 leading-relaxed">{feature.desc}</p>
            </motion.div>
          ))}
        </div>
      </main>
    </div>
  );
};

export default Home;
