<template>
  <div id="brisa-app">
    <AppNavbar />
    <main>
      <router-view v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    <footer class="site-footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-brand">
            <span class="footer-logo">BRISA</span>
            <p class="footer-tagline">{{ $t('footer.tagline') }}</p>
          </div>
          <p class="footer-copy">{{ $t('footer.rights') }}</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import AppNavbar from './components/AppNavbar.vue'
</script>

<style>
.page-enter-active,
.page-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}
.page-enter-from {
  opacity: 0;
  transform: translateY(10px);
}
.page-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.site-footer {
  background: var(--bg-secondary);
  border-top: 1px solid var(--border);
  padding: 40px 0;
  margin-top: 0;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.footer-logo {
  font-family: var(--font-display);
  font-weight: 800;
  font-size: 1.3rem;
  letter-spacing: 0.15em;
  background: linear-gradient(135deg, var(--accent-gold), var(--accent-gold-light));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.footer-tagline {
  color: var(--text-muted);
  font-size: 0.85rem;
  margin-top: 4px;
}

.footer-copy {
  color: var(--text-muted);
  font-size: 0.85rem;
}
</style>
