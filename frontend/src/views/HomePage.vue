<template>
  <div class="home-page">

    <!-- HERO SECTION -->
    <section class="hero" id="hero">
      <div class="hero-bg">
        <div class="hero-gradient"></div>
        <div class="hero-car-wrap">
          <img class="hero-car-img" src="../assets/hero_car.png" alt="BRISA detailing car" />
        </div>
        <div class="hero-particles">
          <div v-for="n in 24" :key="n" class="particle" :style="particleStyle(n)"></div>
        </div>
        <div class="hex-grid">
          <div v-for="n in 8" :key="'h'+n" class="hex" :style="hexStyle(n)"></div>
        </div>
      </div>
      <div class="container hero-content">
        <div class="hero-text animate-fade-in-up">
          <div class="hero-eyebrow">PREMIUM MOBILE SERVICE</div>
          <h1 class="hero-title">
            {{ $t('hero.title') }}
            <span class="hero-highlight">{{ $t('hero.titleHighlight') }}</span>
          </h1>
          <p class="hero-subtitle">{{ $t('hero.subtitle') }}</p>
          <div class="hero-services-list">
            <span v-for="s in quickServices" :key="s" class="hero-service-chip">
              <span class="chip-dot"></span>{{ s }}
            </span>
          </div>
          <div class="hero-actions">
            <router-link to="/booking" class="btn btn-primary btn-lg">
              {{ $t('hero.cta') }}
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
            </router-link>
            <a href="tel:8288452303" class="btn btn-secondary">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 16.92v3a2 2 0 01-2.18 2 19.79 19.79 0 01-8.63-3.07A19.5 19.5 0 013.07 9.81a19.79 19.79 0 01-3.07-8.63A2 2 0 012 1h3a2 2 0 012 1.72c.127.96.361 1.903.7 2.81a2 2 0 01-.45 2.11L6.09 8.91a16 16 0 006 6l1.27-1.27a2 2 0 012.11-.45c.907.339 1.85.573 2.81.7A2 2 0 0122 16.92z"/></svg>
              Call 24/7
            </a>
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
              <span class="service-price" v-if="service.price">{{ $t('services.from') }} ${{ service.price }}</span>
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
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="var(--accent-blue)" stroke-width="1.5"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0116 0z"/><circle cx="12" cy="10" r="3"/></svg>
            </div>
            <h3>{{ $t('why.mobile.title') }}</h3>
            <p>{{ $t('why.mobile.desc') }}</p>
          </div>
          <div class="why-card glass-card">
            <div class="why-icon">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="var(--accent-blue)" stroke-width="1.5"><path d="M14.7 6.3a1 1 0 000 1.4l1.6 1.6a1 1 0 001.4 0l3.77-3.77a6 6 0 01-7.94 7.94l-6.91 6.91a2.12 2.12 0 01-3-3l6.91-6.91a6 6 0 017.94-7.94l-3.76 3.76z"/></svg>
            </div>
            <h3>{{ $t('why.professional.title') }}</h3>
            <p>{{ $t('why.professional.desc') }}</p>
          </div>
          <div class="why-card glass-card">
            <div class="why-icon">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="var(--accent-blue)" stroke-width="1.5"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87"/><path d="M16 3.13a4 4 0 010 7.75"/></svg>
            </div>
            <h3>{{ $t('why.experience.title') }}</h3>
            <p>{{ $t('why.experience.desc') }}</p>
          </div>
          <div class="why-card glass-card">
            <div class="why-icon">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="var(--accent-blue)" stroke-width="1.5"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/><polyline points="9 12 12 15 16 10" stroke-width="2"/></svg>
            </div>
            <h3>{{ $t('why.guarantee.title') }}</h3>
            <p>{{ $t('why.guarantee.desc') }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- CONTACT SECTION -->
    <section class="section contact-section" id="contact">
      <div class="container">
        <h2 class="section-title">{{ $t('contact.title') }}</h2>
        <div class="divider"></div>
        <p class="section-subtitle">{{ $t('contact.subtitle') }}</p>
        <div class="contact-grid">

          <!-- Phone card -->
          <a href="tel:8288452303" class="contact-card glass-card contact-phone">
            <div class="contact-card-glow"></div>
            <div class="contact-icon-wrap">
              <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M22 16.92v3a2 2 0 01-2.18 2 19.79 19.79 0 01-8.63-3.07A19.5 19.5 0 013.07 9.81a19.79 19.79 0 01-3.07-8.63A2 2 0 012 1h3a2 2 0 012 1.72c.127.96.361 1.903.7 2.81a2 2 0 01-.45 2.11L6.09 8.91a16 16 0 006 6l1.27-1.27a2 2 0 012.11-.45c.907.339 1.85.573 2.81.7A2 2 0 0122 16.92z"/></svg>
            </div>
            <div class="contact-text">
              <span class="contact-label">{{ $t('contact.callOrText') }}</span>
              <span class="contact-value">828-845-2303</span>
              <span class="contact-hint">{{ $t('contact.available') }}</span>
            </div>
          </a>

          <!-- Instagram card -->
          <a href="https://www.instagram.com/brisa_detailing/" target="_blank" rel="noopener" class="contact-card glass-card contact-instagram">
            <div class="contact-card-glow contact-card-glow--purple"></div>
            <div class="contact-icon-wrap contact-icon-wrap--purple">
              <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="2" y="2" width="20" height="20" rx="5" ry="5"/><path d="M16 11.37A4 4 0 1112.63 8 4 4 0 0116 11.37z"/><line x1="17.5" y1="6.5" x2="17.51" y2="6.5"/></svg>
            </div>
            <div class="contact-text">
              <span class="contact-label">{{ $t('contact.instagram') }}</span>
              <span class="contact-value">@brisa_detailing</span>
              <span class="contact-hint">{{ $t('contact.instagramHint') }}</span>
            </div>
          </a>

          <!-- Service area card -->
          <div class="contact-card glass-card contact-area">
            <div class="contact-icon-wrap contact-icon-wrap--green">
              <svg width="36" height="36" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0116 0z"/><circle cx="12" cy="10" r="3"/></svg>
            </div>
            <div class="contact-text">
              <span class="contact-label">{{ $t('contact.area') }}</span>
              <span class="contact-value">{{ $t('contact.areaValue') }}</span>
              <span class="contact-hint">{{ $t('contact.areaHint') }}</span>
            </div>
          </div>

        </div>

        <!-- CTA row -->
        <div class="contact-cta">
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
import { onMounted, ref } from 'vue'
import { useBookingStore } from '../stores/booking'
import { Sparkles, SprayCan, Wind, Shield, Car, Crown } from 'lucide-vue-next'

const store = useBookingStore()
const services = ref([])
const quickServices = ['Interior Detailing', 'Exterior Detailing', 'Paint Correction', 'Ceramic Coating', 'Polishing']

const iconMap = { sparkles: Sparkles, 'spray-can': SprayCan, wind: Wind, shield: Shield, car: Car, crown: Crown }
function getIconComponent(iconName) { return iconMap[iconName] || Sparkles }

function particleStyle(n) {
  const seed = n * 137.5
  const x = (seed * 7.3) % 100
  const y = (seed * 3.7) % 100
  const size = 2 + (seed % 4)
  const delay = (seed % 5)
  const duration = 3 + (seed % 4)
  return { left: `${x}%`, top: `${y}%`, width: `${size}px`, height: `${size}px`, animationDelay: `${delay}s`, animationDuration: `${duration}s` }
}

function hexStyle(n) {
  const positions = [
    { top: '8%', right: '5%', size: 60 }, { top: '15%', right: '12%', size: 40 },
    { top: '5%', right: '18%', size: 30 }, { top: '22%', right: '3%', size: 45 },
    { top: '35%', right: '8%', size: 35 }, { top: '2%', right: '28%', size: 25 },
    { top: '28%', right: '20%', size: 20 }, { top: '12%', right: '32%', size: 28 },
  ]
  const p = positions[(n - 1) % positions.length]
  return { top: p.top, right: p.right, width: `${p.size}px`, height: `${p.size}px`, animationDelay: `${n * 0.6}s` }
}

onMounted(async () => {
  await store.fetchServices()
  services.value = store.services
})
</script>

<style scoped>
/* ===== HERO ===== */
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
    /* side vignettes match page dark background */
    linear-gradient(90deg, #020510 0%, transparent 22%, transparent 78%, #020510 100%),
    /* top/bottom fades */
    linear-gradient(180deg, #020510 0%, transparent 18%, transparent 75%, #020510 100%),
    /* central blue glow */
    radial-gradient(ellipse at 50% 60%, rgba(0, 60, 180, 0.4) 0%, transparent 55%),
    linear-gradient(180deg, #020510 0%, #040c24 50%, #020510 100%);
  z-index: 1;
  pointer-events: none;
}

.hero-car-wrap {
  position: absolute;
  inset: 0;
  z-index: 2;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  overflow: hidden;
}

/* dark overlay over the car background to fade its grey edges */
.hero-car-wrap::after {
  content: '';
  position: absolute;
  inset: 0;
  background:
    /* left/right vignette */
    linear-gradient(to right, #020510 0%, transparent 20%, transparent 80%, #020510 100%),
    /* top/bottom vignette */
    linear-gradient(to bottom, #020510 0%, transparent 12%, transparent 75%, #020510 100%),
    /* center radial fade for car bg */
    radial-gradient(ellipse 70% 74% at 50% 65%, transparent 0%, transparent 42%, #020510 70%);
  pointer-events: none;
  z-index: 1;
}

.hero-car-img {
  position: relative;
  z-index: 0;
  width: 100%;
  max-width: 1200px;
  object-fit: contain;
  mix-blend-mode: screen;
  filter: drop-shadow(0 0 80px rgba(0, 150, 255, 0.55)) drop-shadow(0 0 200px rgba(0, 80, 200, 0.3));
  opacity: 0.95;
  animation: car-float 6s ease-in-out infinite;
  margin-bottom: -4%;
}

@keyframes car-float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-14px); }
}

.hero-particles {
  position: absolute;
  inset: 0;
  z-index: 4;
}

.particle {
  position: absolute;
  border-radius: 50%;
  background: var(--accent-blue-light);
  opacity: 0;
  animation: float-particle linear infinite;
}

@keyframes float-particle {
  0% { opacity: 0; transform: translateY(0) scale(0); }
  20% { opacity: 0.7; transform: translateY(-20px) scale(1); }
  80% { opacity: 0.2; transform: translateY(-80px) scale(0.5); }
  100% { opacity: 0; transform: translateY(-120px) scale(0); }
}

/* Hexagons */
.hex-grid {
  position: absolute;
  inset: 0;
  z-index: 2;
  pointer-events: none;
}

.hex {
  position: absolute;
  border: 1.5px solid rgba(0, 180, 255, 0.25);
  clip-path: polygon(50% 0%, 100% 25%, 100% 75%, 50% 100%, 0% 75%, 0% 25%);
  animation: hex-float 5s ease-in-out infinite;
}

@keyframes hex-float {
  0%, 100% { opacity: 0.15; transform: translateY(0) rotate(0deg); }
  50% { opacity: 0.4; transform: translateY(-14px) rotate(8deg); }
}

.hero-content {
  position: relative;
  z-index: 5;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  padding-top: 160px;
  padding-bottom: 80px;
  max-width: 720px;
  margin: 0 auto;
}

.hero-eyebrow {
  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.25em;
  color: var(--accent-blue-light);
  text-transform: uppercase;
  margin-bottom: 16px;
  opacity: 1;
}

.hero-title {
  font-family: var(--font-display);
  font-size: 3.6rem;
  font-weight: 900;
  line-height: 1.08;
  margin-bottom: 20px;
  color: #fff;
}

.hero-highlight {
  display: block;
  background: linear-gradient(135deg, #fff 0%, var(--accent-blue-light) 50%, var(--accent-blue) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  filter: drop-shadow(0 0 20px rgba(0, 180, 255, 0.5));
}

.hero-subtitle {
  font-size: 1.05rem;
  color: #ffffff;
  line-height: 1.7;
  margin-bottom: 24px;
  max-width: 540px;
}

.hero-services-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 32px;
}

.hero-service-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 5px 14px;
  background: rgba(0, 100, 200, 0.12);
  border: 1px solid rgba(0, 180, 255, 0.2);
  border-radius: 100px;
  font-size: 0.78rem;
  font-weight: 600;
  color: #c8ddf5;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.chip-dot {
  width: 5px;
  height: 5px;
  border-radius: 50%;
  background: var(--accent-blue);
  box-shadow: 0 0 6px var(--accent-blue);
  flex-shrink: 0;
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

/* Scroll indicator */
.hero-scroll-indicator {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 5;
}

.scroll-line {
  width: 2px;
  height: 40px;
  background: linear-gradient(to bottom, var(--accent-blue), transparent);
  animation: scroll-pulse 2s ease-in-out infinite;
  box-shadow: 0 0 8px var(--accent-blue-glow);
}

@keyframes scroll-pulse {
  0%, 100% { opacity: 0.3; transform: scaleY(0.5); }
  50% { opacity: 1; transform: scaleY(1); }
}

/* ===== SERVICES ===== */
.services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

.service-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
  border-color: rgba(0, 100, 200, 0.1);
}

.service-card:hover {
  border-color: var(--border-blue);
  box-shadow: var(--shadow-blue);
}

.service-icon {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-md);
  background: rgba(0, 100, 200, 0.12);
  color: var(--accent-blue);
}

.service-name {
  font-family: var(--font-display);
  font-size: 1.2rem;
  font-weight: 600;
}

.service-desc {
  color: #b8d0eb;
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
  color: #8ea8c8;
  font-size: 0.85rem;
}

.service-price {
  color: var(--accent-blue-light);
  font-weight: 700;
  font-size: 1.1rem;
}

/* ===== WHY US ===== */
.why-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 24px;
  margin-top: 48px;
}

.why-card {
  text-align: center;
  padding: 40px 24px;
  transition: var(--transition);
}

.why-card:hover {
  border-color: var(--border-blue);
  box-shadow: var(--shadow-blue);
  transform: translateY(-4px);
}

.why-icon {
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  background: rgba(0, 100, 200, 0.1);
  border-radius: 50%;
  border: 1px solid rgba(0, 180, 255, 0.15);
}

.why-card h3 {
  font-family: var(--font-display);
  font-size: 1.1rem;
  margin-bottom: 12px;
}

.why-card p {
  color: #b8d0eb;
  font-size: 0.9rem;
  line-height: 1.6;
}

/* ===== CONTACT SECTION ===== */
.contact-section {
  padding-bottom: 120px;
  position: relative;
}

.contact-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 600px;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--accent-blue), transparent);
  opacity: 0.3;
}

.contact-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 48px;
}

.contact-card {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 32px;
  position: relative;
  overflow: hidden;
  transition: var(--transition);
  text-decoration: none;
  color: inherit;
}

.contact-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-blue-lg);
}

.contact-card-glow {
  position: absolute;
  top: -30px;
  left: -30px;
  width: 150px;
  height: 150px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(0, 180, 255, 0.12), transparent 70%);
  pointer-events: none;
}

.contact-card-glow--purple {
  background: radial-gradient(circle, rgba(123, 95, 255, 0.12), transparent 70%);
}

.contact-icon-wrap {
  width: 72px;
  height: 72px;
  flex-shrink: 0;
  border-radius: var(--radius-md);
  background: rgba(0, 100, 200, 0.12);
  border: 1px solid rgba(0, 180, 255, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--accent-blue);
  box-shadow: 0 0 20px rgba(0, 180, 255, 0.1);
  transition: var(--transition);
}

.contact-card:hover .contact-icon-wrap {
  box-shadow: 0 0 30px rgba(0, 180, 255, 0.25);
  border-color: var(--accent-blue);
}

.contact-icon-wrap--purple {
  background: rgba(123, 95, 255, 0.12);
  border-color: rgba(123, 95, 255, 0.25);
  color: var(--accent-purple);
}

.contact-card:hover .contact-icon-wrap--purple {
  box-shadow: 0 0 30px rgba(123, 95, 255, 0.25);
  border-color: var(--accent-purple);
}

.contact-icon-wrap--green {
  background: rgba(0, 229, 160, 0.1);
  border-color: rgba(0, 229, 160, 0.2);
  color: var(--accent-green);
}

.contact-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.contact-label {
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: #8ea8c8;
}

.contact-value {
  font-family: var(--font-display);
  font-size: 1.4rem;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1.2;
}

.contact-hint {
  font-size: 0.82rem;
  color: #b8d0eb;
}

.contact-phone:hover .contact-value { color: var(--accent-blue-light); }
.contact-instagram:hover .contact-value { color: var(--accent-purple); }

.contact-cta {
  text-align: center;
}

/* ===== RESPONSIVE ===== */
@media (max-width: 900px) {
  .hero-car-wrap {
    -webkit-mask-image: radial-gradient(ellipse 90% 60% at 50% 56%, black 20%, transparent 100%);
    mask-image: radial-gradient(ellipse 90% 60% at 50% 56%, black 20%, transparent 100%);
  }
  .hero-car-img {
    width: 95%;
    opacity: 0.55;
  }
  .hero-content {
    padding-top: 180px;
  }
}

@media (max-width: 768px) {
  .hero-title { font-size: 2.4rem; }
  .hero-subtitle { max-width: 100%; }
  .hero-actions { justify-content: center; }
  .hero-content { padding-top: 140px; }

  .services-grid { grid-template-columns: 1fr; }
  .why-grid { grid-template-columns: 1fr 1fr; }
  .contact-grid { grid-template-columns: 1fr; }
}

@media (max-width: 500px) {
  .why-grid { grid-template-columns: 1fr; }
}
</style>
