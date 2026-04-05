import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../views/HomePage.vue'
import BookingPage from '../views/BookingPage.vue'
import AdminPage from '../views/AdminPage.vue'

const routes = [
  { path: '/', name: 'Home', component: HomePage },
  { path: '/booking', name: 'Booking', component: BookingPage },
  { path: '/admin', name: 'Admin', component: AdminPage }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition
    if (to.hash) return { el: to.hash, behavior: 'smooth' }
    return { top: 0, behavior: 'smooth' }
  }
})

export default router
