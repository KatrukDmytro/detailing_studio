<template>
  <nav class="navbar" :class="{ scrolled: isScrolled }">
    <div class="container navbar-inner">
      <router-link to="/" class="navbar-brand">
        <span class="brand-text">PASSLAVSKI</span>
      </router-link>

      <div class="navbar-links" :class="{ open: menuOpen }">
        <router-link to="/" class="nav-link" @click="menuOpen = false">{{ $t('nav.home') }}</router-link>
        <router-link to="/#services" class="nav-link" @click="menuOpen = false">{{ $t('nav.services') }}</router-link>
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
  background: rgba(10, 10, 15, 0.9);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--border);
  padding: 12px 0;
}

.navbar-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
}

.brand-text {
  font-family: var(--font-display);
  font-weight: 800;
  font-size: 1.4rem;
  letter-spacing: 0.2em;
  background: linear-gradient(135deg, var(--accent-gold), var(--accent-gold-light));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.navbar-links {
  display: flex;
  align-items: center;
  gap: 32px;
}

.nav-link {
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--text-secondary);
  transition: var(--transition);
  position: relative;
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
  background: var(--accent-gold);
  transition: width 0.3s ease;
}

.nav-link:hover::after {
  width: 100%;
}

.nav-booking-btn {
  background: linear-gradient(135deg, var(--accent-gold), var(--accent-gold-dark));
  color: #0a0a0f;
  padding: 8px 20px;
  border-radius: var(--radius-sm);
  font-weight: 600;
  transition: var(--transition);
}

.nav-booking-btn:hover {
  box-shadow: var(--shadow-gold);
}

.nav-link:has(.nav-booking-btn)::after {
  display: none;
}

.lang-selector {
  position: relative;
}

.lang-select {
  background: var(--bg-card);
  border: 1px solid var(--border);
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
  border-color: var(--accent-gold);
  color: var(--text-primary);
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
  background: var(--text-primary);
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
    background: rgba(10, 10, 15, 0.95);
    backdrop-filter: blur(20px);
    flex-direction: column;
    padding: 24px;
    gap: 20px;
    transform: translateY(-120%);
    transition: transform 0.3s ease;
    border-bottom: 1px solid var(--border);
  }

  .navbar-links.open {
    transform: translateY(0);
  }
}
</style>
