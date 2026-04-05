import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '../api/client'

export const useAdminStore = defineStore('admin', () => {
  const isAuthenticated = ref(!!localStorage.getItem('admin_token'))
  const bookings = ref([])
  const services = ref([])
  const schedule = ref([])
  const breakConfig = ref({})
  const loading = ref(false)
  const error = ref(null)

  async function login(username, password) {
    try {
      loading.value = true
      error.value = null
      const { data } = await api.post('/admin/login', { username, password })
      if (data.success) {
        localStorage.setItem('admin_token', data.token)
        isAuthenticated.value = true
        return true
      }
      error.value = 'Invalid credentials'
      return false
    } catch (e) {
      error.value = 'Login failed'
      return false
    } finally {
      loading.value = false
    }
  }

  function logout() {
    localStorage.removeItem('admin_token')
    isAuthenticated.value = false
  }

  async function fetchBookings() {
    try {
      loading.value = true
      const { data } = await api.get('/admin/bookings')
      bookings.value = data
    } catch (e) {
      error.value = 'Failed to load bookings'
    } finally {
      loading.value = false
    }
  }

  async function updateBookingStatus(id, status) {
    try {
      await api.put(`/admin/bookings/${id}/status?status=${status}`)
      await fetchBookings()
    } catch (e) {
      error.value = 'Failed to update booking'
    }
  }

  async function deleteBooking(id) {
    try {
      await api.delete(`/admin/bookings/${id}`)
      await fetchBookings()
    } catch (e) {
      error.value = 'Failed to delete booking'
    }
  }

  async function fetchServices() {
    try {
      loading.value = true
      const { data } = await api.get('/admin/services')
      services.value = data
    } catch (e) {
      error.value = 'Failed to load services'
    } finally {
      loading.value = false
    }
  }

  async function createService(service) {
    try {
      await api.post('/admin/services', service)
      await fetchServices()
    } catch (e) {
      error.value = 'Failed to create service'
    }
  }

  async function updateService(id, service) {
    try {
      await api.put(`/admin/services/${id}`, service)
      await fetchServices()
    } catch (e) {
      error.value = 'Failed to update service'
    }
  }

  async function deleteService(id) {
    try {
      await api.delete(`/admin/services/${id}`)
      await fetchServices()
    } catch (e) {
      error.value = 'Failed to delete service'
    }
  }

  async function fetchSchedule() {
    try {
      loading.value = true
      const { data } = await api.get('/admin/schedule')
      schedule.value = data
    } catch (e) {
      error.value = 'Failed to load schedule'
    } finally {
      loading.value = false
    }
  }

  async function updateSchedule(scheduleData) {
    try {
      const { data } = await api.put('/admin/schedule', scheduleData)
      schedule.value = data
    } catch (e) {
      error.value = 'Failed to update schedule'
    }
  }

  async function fetchBreakConfig() {
    try {
      const { data } = await api.get('/admin/breaks')
      breakConfig.value = data
    } catch (e) {
      error.value = 'Failed to load break config'
    }
  }

  async function updateBreakConfig(config) {
    try {
      const { data } = await api.put('/admin/breaks', config)
      breakConfig.value = data
    } catch (e) {
      error.value = 'Failed to update break config'
    }
  }

  return {
    isAuthenticated, bookings, services, schedule, breakConfig, loading, error,
    login, logout,
    fetchBookings, updateBookingStatus, deleteBooking,
    fetchServices, createService, updateService, deleteService,
    fetchSchedule, updateSchedule,
    fetchBreakConfig, updateBreakConfig
  }
})
