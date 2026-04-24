<template>
  <nav class="navbar" :class="{ scrolled: isScrolled }">
    <div class="container navbar-inner">
      <router-link to="/" class="navbar-brand">
        <span class="brand-text">BRISA</span>
        <span class="brand-sub">MOBILE DETAILING</span>
      </router-link>

      <div class="navbar-links" :class="{ open: menuOpen }">
        <router-link to="/" class="nav-link" @click="menuOpen = false">{{ $t('nav.home') }}</router-link>
        <router-link to="/#services" class="nav-link" @click="menuOpen = false">{{ $t('nav.services') }}</router-link>
        <router-link to="/reviews" class="nav-link" @click="menuOpen = false">{{ $t('nav.reviews') || 'Reviews' }}</router-link>
        <a href="#contact" class="nav-link" @click="menuOpen = false">{{ $t('nav.contact') }}</a>
        <router-link to="/booking" class="nav-link" @click="menuOpen = false">
          <span class="nav-booking-btn">{{ $t('nav.booking') }}</span>
        </router-link>

        <div class="lang-selector">
          <select v-model="currentLocale" @change="changeLocale" class="lang-select">
            <option value="en">EN</option>
            <option value="es">ES</option>
            <option value="de">DE</option>
            <option value="uk">UA</option>
            <option value="ru">RU</option>
          </select>
        </div>
      </div>

      <button class="navbar-toggle" @click="menuOpen = !menuOpen">
        <span class="toggle-bar"></span>
        <span class="toggle-bar"></span>
        <span class="toggle-bar"></span>
      </button>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'

const { locale } = useI18n()
const currentLocale = ref(locale.value)
const isScrolled = ref(false)
const menuOpen = ref(false)

function changeLocale() {
  locale.value = currentLocale.value
  localStorage.setItem('locale', currentLocale.value)
}

function handleScroll() {
  isScrolled.value = window.scrollY > 50
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
  const saved = localStorage.getItem('locale')
  if (saved) {
    currentLocale.value = saved
    locale.value = saved
  }
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  padding: 16px 0;
  transition: var(--transition);
  background: transparent;
}

.navbar.scrolled {
  background: rgba(2, 5, 16, 0.92);
  backdrop-filter: blur(24px);
  border-bottom: 1px solid rgba(0, 180, 255, 0.12);
  padding: 12px 0;
  box-shadow: 0 4px 40px rgba(0, 0, 0, 0.6);
}

.navbar-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.navbar-brand {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.brand-text {
  font-family: var(--font-display);
  font-weight: 900;
  font-size: 1.6rem;
  letter-spacing: 0.22em;
  background: linear-gradient(135deg, #fff 20%, var(--accent-blue-light) 60%, var(--accent-blue) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: none;
  filter: drop-shadow(0 0 12px rgba(0, 180, 255, 0.5));
}

.brand-sub {
  font-family: var(--font-display);
  font-size: 0.55rem;
  font-weight: 600;
  letter-spacing: 0.18em;
  color: var(--accent-blue);
  text-transform: uppercase;
  opacity: 0.85;
}

.navbar-links {
  display: flex;
  align-items: center;
  gap: 32px;
}

.nav-link {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--text-secondary);
  transition: var(--transition);
  position: relative;
  letter-spacing: 0.03em;
}

.nav-link:hover {
  color: var(--text-primary);
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--accent-blue), var(--accent-blue-light));
  box-shadow: 0 0 8px var(--accent-blue-glow);
  transition: width 0.3s ease;
  border-radius: 2px;
}

.nav-link:hover::after {
  width: 100%;
}

.nav-booking-btn {
  background: linear-gradient(135deg, var(--accent-blue) 0%, var(--accent-blue-dark) 100%);
  color: #fff;
  padding: 8px 20px;
  border-radius: var(--radius-sm);
  font-weight: 600;
  transition: var(--transition);
  border: 1px solid rgba(0, 180, 255, 0.3);
  box-shadow: 0 0 16px rgba(0, 180, 255, 0.15);
}

.nav-booking-btn:hover {
  box-shadow: 0 0 28px rgba(0, 180, 255, 0.35);
  transform: translateY(-1px);
}

.nav-link:has(.nav-booking-btn)::after {
  display: none;
}

.lang-selector {
  position: relative;
}

.lang-select {
  background: var(--bg-card);
  border: 1px solid var(--border-blue);
  color: var(--text-secondary);
  padding: 6px 12px;
  border-radius: var(--radius-sm);
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  outline: none;
  transition: var(--transition);
}

.lang-select:hover, .lang-select:focus {
  border-color: var(--accent-blue);
  color: var(--text-primary);
  box-shadow: 0 0 10px rgba(0, 180, 255, 0.15);
}

.lang-select option {
  background: var(--bg-card);
  color: var(--text-primary);
}

.navbar-toggle {
  display: none;
  flex-direction: column;
  gap: 5px;
  padding: 4px;
}

.toggle-bar {
  width: 24px;
  height: 2px;
  background: var(--accent-blue);
  border-radius: 2px;
  transition: var(--transition);
}

@media (max-width: 768px) {
  .navbar-toggle {
    display: flex;
  }

  .navbar-links {
    position: fixed;
    top: 60px;
    left: 0;
    right: 0;
    background: rgba(2, 5, 16, 0.97);
    backdrop-filter: blur(24px);
    flex-direction: column;
    padding: 24px;
    gap: 20px;
    transform: translateY(-120%);
    transition: transform 0.3s ease;
    border-bottom: 1px solid var(--border-blue);
  }

  .navbar-links.open {
    transform: translateY(0);
  }

  .brand-sub {
    display: none;
  }
}
</style>
