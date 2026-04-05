import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '../api/client'

export const useBookingStore = defineStore('booking', () => {
  const services = ref([])
  const selectedServices = ref([])
  const availableSlots = ref([])
  const selectedDate = ref('')
  const selectedSlot = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const workingDays = ref([])

  async function fetchServices() {
    try {
      loading.value = true
      const { data } = await api.get('/services')
      services.value = data
    } catch (e) {
      error.value = 'Failed to load services'
    } finally {
      loading.value = false
    }
  }

  async function fetchAvailableSlots(date, serviceIds) {
    try {
      loading.value = true
      availableSlots.value = []
      const { data } = await api.get('/schedule/available-slots', {
        params: { date, serviceIds: serviceIds.join(',') }
      })
      availableSlots.value = data
    } catch (e) {
      error.value = 'Failed to load available slots'
    } finally {
      loading.value = false
    }
  }

  async function fetchWorkingDays() {
    try {
      const { data } = await api.get('/schedule/working-days')
      workingDays.value = data
    } catch (e) {
      error.value = 'Failed to load schedule'
    }
  }

  function toggleService(serviceId) {
    const idx = selectedServices.value.indexOf(serviceId)
    if (idx > -1) {
      selectedServices.value.splice(idx, 1)
    } else {
      selectedServices.value.push(serviceId)
    }
    // Reset slot when services change
    selectedSlot.value = null
    availableSlots.value = []
  }

  function totalDuration() {
    return services.value
      .filter(s => selectedServices.value.includes(s.id))
      .reduce((sum, s) => sum + s.durationMinutes, 0)
  }

  function totalPrice() {
    return services.value
      .filter(s => selectedServices.value.includes(s.id))
      .reduce((sum, s) => sum + (parseFloat(s.price) || 0), 0)
  }

  function selectedServiceDetails() {
    return services.value.filter(s => selectedServices.value.includes(s.id))
  }

  function reset() {
    selectedServices.value = []
    availableSlots.value = []
    selectedDate.value = ''
    selectedSlot.value = null
    error.value = null
  }

  return {
    services, selectedServices, availableSlots, selectedDate, selectedSlot,
    loading, error, workingDays,
    fetchServices, fetchAvailableSlots, fetchWorkingDays,
    toggleService, totalDuration, totalPrice, selectedServiceDetails, reset
  }
})
