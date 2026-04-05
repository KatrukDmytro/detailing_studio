<template>
  <div class="home-page">
    <!-- HERO SECTION -->
    <section class="hero" id="hero">
      <div class="hero-bg">
        <div class="hero-gradient"></div>
        <div class="hero-particles">
          <div v-for="n in 20" :key="n" class="particle" :style="particleStyle(n)"></div>
        </div>
      </div>
      <div class="container hero-content">
        <div class="hero-text animate-fade-in-up">
          <h1 class="hero-title">
            {{ $t('hero.title') }}
            <span class="hero-highlight">{{ $t('hero.titleHighlight') }}</span>
          </h1>
          <p class="hero-subtitle">{{ $t('hero.subtitle') }}</p>
          <div class="hero-actions">
            <router-link to="/booking" class="btn btn-primary btn-lg">
              {{ $t('hero.cta') }}
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
            </router-link>
            <a href="#services" class="btn btn-secondary">{{ $t('hero.ctaSecondary') }}</a>
          </div>
        </div>
        <div class="hero-visual">
          <div class="hero-glow"></div>
          <div class="hero-badge">
            <span class="badge-icon">✦</span>
            <span class="badge-label">PREMIUM<br>QUALITY</span>
          </div>
        </div>
      </div>
      <div class="hero-scroll-indicator">
        <div class="scroll-line"></div>
      </div>
    </section>

    <!-- SERVICES SECTION -->
    <section class="section services-section" id="services">
      <div class="container">
        <h2 class="section-title">{{ $t('services.title') }}</h2>
        <div class="divider"></div>
        <p class="section-subtitle">{{ $t('services.subtitle') }}</p>
        <div class="services-grid">
          <div v-for="service in services" :key="service.id" class="service-card card">
            <div class="service-icon">
              <component :is="getIconComponent(service.icon)" :size="28" />
            </div>
            <h3 class="service-name">{{ service.name }}</h3>
            <p class="service-desc">{{ service.description }}</p>
            <div class="service-meta">
              <span class="service-duration">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                {{ $t('services.duration', { minutes: service.durationMinutes }) }}
              </span>
              <span class="service-price" v-if="service.price">
                {{ $t('services.from') }} ${{ service.price }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- WHY US SECTION -->
    <section class="section why-section">
      <div class="container">
        <h2 class="section-title">{{ $t('why.title') }}</h2>
        <div class="divider"></div>
        <div class="why-grid">
          <div class="why-card glass-card">
            <div class="why-icon">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="var(--accent-gold)" stroke-width="1.5"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0116 0z"/><circle cx="12" cy="10" r="3"/></svg>
            </div>
            <h3>{{ $t('why.mobile.title') }}</h3>
            <p>{{ $t('why.mobile.desc') }}</p>
          </div>
          <div class="why-card glass-card">
            <div class="why-icon">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="var(--accent-gold)" stroke-width="1.5"><path d="M14.7 6.3a1 1 0 000 1.4l1.6 1.6a1 1 0 001.4 0l3.77-3.77a6 6 0 01-7.94 7.94l-6.91 6.91a2.12 2.12 0 01-3-3l6.91-6.91a6 6 0 017.94-7.94l-3.76 3.76z"/></svg>
            </div>
            <h3>{{ $t('why.professional.title') }}</h3>
            <p>{{ $t('why.professional.desc') }}</p>
          </div>
          <div class="why-card glass-card">
            <div class="why-icon">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="var(--accent-gold)" stroke-width="1.5"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87"/><path d="M16 3.13a4 4 0 010 7.75"/></svg>
            </div>
            <h3>{{ $t('why.experience.title') }}</h3>
            <p>{{ $t('why.experience.desc') }}</p>
          </div>
          <div class="why-card glass-card">
            <div class="why-icon">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="var(--accent-gold)" stroke-width="1.5"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><polyline points="9 12 12 15 16 10" stroke-width="2"/></svg>
            </div>
            <h3>{{ $t('why.guarantee.title') }}</h3>
            <p>{{ $t('why.guarantee.desc') }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA SECTION -->
    <section class="section cta-section">
      <div class="container">
        <div class="cta-card glass-card">
          <div class="cta-glow"></div>
          <h2 class="cta-title">{{ $t('cta.title') }}</h2>
          <p class="cta-subtitle">{{ $t('cta.subtitle') }}</p>
          <router-link to="/booking" class="btn btn-primary btn-lg">
            {{ $t('cta.button') }}
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
          </router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref, h } from 'vue'
import { useBookingStore } from '../stores/booking'
import { Sparkles, SprayCan, Wind, Shield, Car, Crown } from 'lucide-vue-next'

const store = useBookingStore()
const services = ref([])

const iconMap = { sparkles: Sparkles, 'spray-can': SprayCan, wind: Wind, shield: Shield, car: Car, crown: Crown }

function getIconComponent(iconName) {
  return iconMap[iconName] || Sparkles
}

function particleStyle(n) {
  const x = Math.random() * 100
  const y = Math.random() * 100
  const size = 2 + Math.random() * 4
  const delay = Math.random() * 5
  const duration = 3 + Math.random() * 4
  return {
    left: `${x}%`,
    top: `${y}%`,
    width: `${size}px`,
    height: `${size}px`,
    animationDelay: `${delay}s`,
    animationDuration: `${duration}s`
  }
}

onMounted(async () => {
  await store.fetchServices()
  services.value = store.services
})
</script>

<style scoped>
/* Hero */
.hero {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
}

.hero-gradient {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at 20% 50%, rgba(212, 168, 67, 0.08) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 20%, rgba(74, 144, 217, 0.05) 0%, transparent 40%),
    radial-gradient(ellipse at 50% 80%, rgba(212, 168, 67, 0.04) 0%, transparent 40%);
}

.hero-particles {
  position: absolute;
  inset: 0;
}

.particle {
  position: absolute;
  border-radius: 50%;
  background: var(--accent-gold);
  opacity: 0;
  animation: float-particle linear infinite;
}

@keyframes float-particle {
  0% { opacity: 0; transform: translateY(0) scale(0); }
  20% { opacity: 0.6; transform: translateY(-20px) scale(1); }
  80% { opacity: 0.3; transform: translateY(-80px) scale(0.5); }
  100% { opacity: 0; transform: translateY(-120px) scale(0); }
}

.hero-content {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  align-items: center;
  padding-top: 80px;
}

.hero-title {
  font-family: var(--font-display);
  font-size: 3.8rem;
  font-weight: 800;
  line-height: 1.1;
  margin-bottom: 20px;
}

.hero-highlight {
  display: block;
  background: linear-gradient(135deg, var(--accent-gold), var(--accent-gold-light));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 1.15rem;
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: 36px;
  max-width: 500px;
}

.hero-actions {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.btn-lg {
  padding: 16px 36px;
  font-size: 1.05rem;
}

.hero-visual {
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
}

.hero-glow {
  width: 350px;
  height: 350px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(212, 168, 67, 0.12) 0%, transparent 70%);
  animation: pulse-glow 4s ease-in-out infinite;
}

.hero-badge {
  position: absolute;
  display: flex;
  align-items: center;
  gap: 12px;
  background: var(--bg-glass);
  backdrop-filter: blur(20px);
  border: 1px solid var(--border-gold);
  padding: 20px 28px;
  border-radius: var(--radius-lg);
  animation: fadeInUp 1s ease-out 0.5s both;
}

.badge-icon {
  font-size: 2rem;
  color: var(--accent-gold);
}

.badge-label {
  font-family: var(--font-display);
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.15em;
  color: var(--text-primary);
  line-height: 1.4;
}

.hero-scroll-indicator {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
}

.scroll-line {
  width: 2px;
  height: 40px;
  background: linear-gradient(to bottom, var(--accent-gold), transparent);
  animation: scroll-pulse 2s ease-in-out infinite;
}

@keyframes scroll-pulse {
  0%, 100% { opacity: 0.3; transform: scaleY(0.5); }
  50% { opacity: 1; transform: scaleY(1); }
}

/* Services */
.services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(340px, 1fr));
  gap: 24px;
}

.service-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.service-icon {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-md);
  background: rgba(212, 168, 67, 0.1);
  color: var(--accent-gold);
}

.service-name {
  font-family: var(--font-display);
  font-size: 1.2rem;
  font-weight: 600;
}

.service-desc {
  color: var(--text-secondary);
  font-size: 0.9rem;
  line-height: 1.6;
  flex: 1;
}

.service-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid var(--border);
}

.service-duration {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--text-muted);
  font-size: 0.85rem;
}

.service-price {
  color: var(--accent-gold);
  font-weight: 700;
  font-size: 1.1rem;
}

/* Why Us */
.why-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
  margin-top: 48px;
}

.why-card {
  text-align: center;
  padding: 40px 24px;
}

.why-card:hover {
  border-color: var(--border-gold);
  box-shadow: var(--shadow-gold);
  transform: translateY(-4px);
}

.why-icon {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  background: rgba(212, 168, 67, 0.08);
  border-radius: 50%;
}

.why-card h3 {
  font-family: var(--font-display);
  font-size: 1.1rem;
  margin-bottom: 12px;
}

.why-card p {
  color: var(--text-secondary);
  font-size: 0.9rem;
  line-height: 1.6;
}

/* CTA */
.cta-section {
  padding-bottom: 120px;
}

.cta-card {
  text-align: center;
  padding: 80px 40px;
  position: relative;
  overflow: hidden;
}

.cta-glow {
  position: absolute;
  top: -50%;
  left: 50%;
  transform: translateX(-50%);
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(212, 168, 67, 0.08), transparent 60%);
  pointer-events: none;
}

.cta-title {
  font-family: var(--font-display);
  font-size: 2.4rem;
  font-weight: 700;
  margin-bottom: 16px;
  position: relative;
}

.cta-subtitle {
  color: var(--text-secondary);
  font-size: 1.1rem;
  margin-bottom: 36px;
  position: relative;
}

@media (max-width: 768px) {
  .hero-content {
    grid-template-columns: 1fr;
    text-align: center;
  }

  .hero-title {
    font-size: 2.4rem;
  }

  .hero-subtitle {
    margin: 0 auto 30px;
  }

  .hero-actions {
    justify-content: center;
  }

  .hero-visual {
    display: none;
  }

  .services-grid {
    grid-template-columns: 1fr;
  }

  .cta-title {
    font-size: 1.8rem;
  }
}
</style>
