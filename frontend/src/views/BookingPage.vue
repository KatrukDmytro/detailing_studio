<template>
  <div class="booking-page">
    <div class="container">
      <div class="booking-header">
        <h1 class="section-title">{{ $t('booking.title') }}</h1>
        <div class="divider"></div>
      </div>

      <!-- Success State -->
      <div v-if="submitted" class="success-card glass-card animate-fade-in-up">
        <div class="success-icon">✓</div>
        <h2>{{ $t('booking.success.title') }}</h2>
        <p>{{ $t('booking.success.message') }}</p>
        <div class="success-actions">
          <router-link to="/" class="btn btn-secondary">{{ $t('booking.success.backHome') }}</router-link>
          <button class="btn btn-primary" @click="resetBooking">{{ $t('booking.success.bookAnother') }}</button>
        </div>
      </div>

      <!-- Booking Form -->
      <template v-else>
        <!-- Stepper -->
        <div class="stepper">
          <template v-for="(stepItem, idx) in stepLabels" :key="idx">
            <div class="step" :class="{ active: currentStep === idx, completed: currentStep > idx }">
              <div class="step-number">{{ currentStep > idx ? '✓' : idx + 1 }}</div>
              <span class="step-label">{{ stepItem }}</span>
            </div>
            <div v-if="idx < stepLabels.length - 1" class="step-line" :class="{ completed: currentStep > idx }"></div>
          </template>
        </div>

        <!-- Step 1: Service Selection -->
        <div v-if="currentStep === 0" class="step-content animate-fade-in">
          <h3 class="step-title">{{ $t('booking.selectServices') }}</h3>
          <div class="services-select-grid">
            <div
              v-for="service in bookingStore.services"
              :key="service.id"
              class="service-select-card"
              :class="{ selected: bookingStore.selectedServices.includes(service.id) }"
              @click="bookingStore.toggleService(service.id)"
            >
              <div class="ssc-check">
                <svg v-if="bookingStore.selectedServices.includes(service.id)" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"/></svg>
              </div>
              <div class="ssc-info">
                <h4>{{ service.name }}</h4>
                <p>{{ service.description }}</p>
                <div class="ssc-meta">
                  <span>{{ service.durationMinutes }} {{ $t('booking.minutes') }}</span>
                  <span v-if="service.price" class="ssc-price">${{ service.price }}</span>
                </div>
              </div>
            </div>
          </div>
          <div v-if="bookingStore.selectedServices.length > 0" class="duration-summary glass-card">
            <span>{{ $t('booking.totalDuration') }}:</span>
            <strong>{{ bookingStore.totalDuration() }} {{ $t('booking.minutes') }}</strong>
            <span class="summary-divider">|</span>
            <strong>${{ bookingStore.totalPrice().toFixed(2) }}</strong>
          </div>
        </div>

        <!-- Step 2: Date & Time -->
        <div v-if="currentStep === 1" class="step-content animate-fade-in">
          <div class="datetime-grid">
            <div>
              <h3 class="step-title">{{ $t('booking.selectDate') }}</h3>
              <input type="date" v-model="bookingStore.selectedDate" class="form-input date-input"
                     :min="minDate" @change="onDateChange" />
            </div>
            <div>
              <h3 class="step-title">{{ $t('booking.selectTime') }}</h3>
              <div v-if="bookingStore.loading" class="slots-loading">Loading...</div>
              <div v-else-if="bookingStore.availableSlots.length === 0 && bookingStore.selectedDate" class="no-slots">
                {{ $t('booking.noSlots') }}
              </div>
              <div v-else class="time-slots-grid">
                <button
                  v-for="slot in bookingStore.availableSlots"
                  :key="slot.startTime"
                  class="time-slot"
                  :class="{ selected: bookingStore.selectedSlot?.startTime === slot.startTime, disabled: !slot.available }"
                  :disabled="!slot.available"
                  @click="bookingStore.selectedSlot = slot"
                >
                  {{ formatTime(slot.startTime) }}
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Step 3: Contact Info -->
        <div v-if="currentStep === 2" class="step-content animate-fade-in">
          <div class="contact-form glass-card">
            <div class="form-row">
              <div class="form-group">
                <label class="form-label">{{ $t('booking.contact.phone') }} *</label>
                <input type="tel" v-model="contactInfo.phone" class="form-input" :placeholder="$t('booking.contact.phonePlaceholder')" required />
              </div>
              <div class="form-group">
                <label class="form-label">{{ $t('booking.contact.email') }}</label>
                <input type="email" v-model="contactInfo.email" class="form-input" :placeholder="$t('booking.contact.emailPlaceholder')" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label class="form-label">{{ $t('booking.contact.whatsapp') }}</label>
                <input type="tel" v-model="contactInfo.whatsapp" class="form-input" :placeholder="$t('booking.contact.whatsappPlaceholder')" />
              </div>
              <div class="form-group">
                <label class="form-label">{{ $t('booking.contact.instagram') }}</label>
                <input type="text" v-model="contactInfo.instagram" class="form-input" :placeholder="$t('booking.contact.instagramPlaceholder')" />
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">{{ $t('booking.contact.address') }} *</label>
              <input type="text" v-model="contactInfo.address" class="form-input" :placeholder="$t('booking.contact.addressPlaceholder')" required />
            </div>
            <div class="form-group">
              <label class="form-label">{{ $t('booking.contact.notes') }}</label>
              <textarea v-model="contactInfo.notes" class="form-input" :placeholder="$t('booking.contact.notesPlaceholder')" rows="3"></textarea>
            </div>
          </div>
        </div>

        <!-- Step 4: Photos -->
        <div v-if="currentStep === 3" class="step-content animate-fade-in">
          <div class="photo-upload glass-card">
            <h3 class="step-title">{{ $t('booking.photos.title') }}</h3>
            <p class="photo-subtitle">{{ $t('booking.photos.subtitle') }}</p>
            <div class="dropzone" @dragover.prevent="dragOver = true" @dragleave="dragOver = false"
                 @drop.prevent="handleDrop" @click="$refs.fileInput.click()" :class="{ active: dragOver }">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--accent-gold)" stroke-width="1.5">
                <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/>
              </svg>
              <p>{{ $t('booking.photos.dropzone') }}</p>
              <small>{{ $t('booking.photos.maxSize') }}</small>
              <input ref="fileInput" type="file" multiple accept="image/*" @change="handleFiles" style="display:none" />
            </div>
            <div v-if="photos.length" class="photo-previews">
              <div v-for="(photo, idx) in photos" :key="idx" class="photo-preview">
                <img :src="photo.preview" :alt="photo.file.name" />
                <button class="photo-remove" @click="removePhoto(idx)">×</button>
                <span class="photo-name">{{ photo.file.name }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Step 5: Confirmation -->
        <div v-if="currentStep === 4" class="step-content animate-fade-in">
          <div class="confirm-card glass-card">
            <h3 class="step-title">{{ $t('booking.summary.title') }}</h3>

            <div class="summary-section">
              <h4>{{ $t('booking.summary.services') }}</h4>
              <ul class="summary-list">
                <li v-for="s in bookingStore.selectedServiceDetails()" :key="s.id">
                  {{ s.name }} — {{ s.durationMinutes }} {{ $t('booking.minutes') }}
                  <span v-if="s.price"> (${{ s.price }})</span>
                </li>
              </ul>
            </div>

            <div class="summary-section">
              <h4>{{ $t('booking.summary.dateTime') }}</h4>
              <p>{{ formatDate(bookingStore.selectedDate) }} at {{ formatTime(bookingStore.selectedSlot?.startTime) }}</p>
            </div>

            <div class="summary-section">
              <h4>{{ $t('booking.summary.duration') }}</h4>
              <p>{{ bookingStore.totalDuration() }} {{ $t('booking.minutes') }}</p>
            </div>

            <div class="summary-section">
              <h4>{{ $t('booking.summary.contact') }}</h4>
              <p>📱 {{ contactInfo.phone }}</p>
              <p v-if="contactInfo.email">✉️ {{ contactInfo.email }}</p>
              <p v-if="contactInfo.whatsapp">💬 {{ contactInfo.whatsapp }}</p>
              <p v-if="contactInfo.instagram">📷 {{ contactInfo.instagram }}</p>
              <p>📍 {{ contactInfo.address }}</p>
            </div>

            <div v-if="photos.length" class="summary-section">
              <h4>{{ $t('booking.summary.photos') }}</h4>
              <p>{{ $t('booking.summary.photosCount', { count: photos.length }) }}</p>
            </div>

            <div v-if="submitError" class="error-msg">{{ submitError }}</div>
          </div>
        </div>

        <!-- Navigation Buttons -->
        <div class="step-nav">
          <button v-if="currentStep > 0" class="btn btn-secondary" @click="currentStep--">{{ $t('booking.prev') }}</button>
          <div v-else></div>
          <button v-if="currentStep < 4" class="btn btn-primary" :disabled="!canProceed" @click="nextStep">
            {{ $t('booking.next') }}
          </button>
          <button v-else class="btn btn-primary" :disabled="submitting" @click="submitBooking">
            {{ submitting ? $t('booking.summary.submitting') : $t('booking.summary.submit') }}
          </button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useBookingStore } from '../stores/booking'
import api from '../api/client'

const bookingStore = useBookingStore()
const currentStep = ref(0)
const submitted = ref(false)
const submitting = ref(false)
const submitError = ref('')
const dragOver = ref(false)
const photos = ref([])
const contactInfo = ref({
  phone: '', email: '', whatsapp: '', instagram: '', address: '', notes: ''
})

const stepLabels = computed(() => {
  const { t } = useI18n()
  return [t('booking.steps.services'), t('booking.steps.datetime'), t('booking.steps.contact'), t('booking.steps.photos'), t('booking.steps.confirm')]
})

import { useI18n } from 'vue-i18n'

const minDate = computed(() => {
  const d = new Date()
  return d.toISOString().split('T')[0]
})

const canProceed = computed(() => {
  switch (currentStep.value) {
    case 0: return bookingStore.selectedServices.length > 0
    case 1: return bookingStore.selectedDate && bookingStore.selectedSlot
    case 2: return contactInfo.value.phone.trim() && contactInfo.value.address.trim()
    case 3: return true
    default: return true
  }
})

function nextStep() {
  if (canProceed.value) currentStep.value++
}

async function onDateChange() {
  if (bookingStore.selectedDate && bookingStore.selectedServices.length) {
    await bookingStore.fetchAvailableSlots(bookingStore.selectedDate, bookingStore.selectedServices)
  }
}

function formatTime(t) {
  if (!t) return ''
  if (typeof t === 'string' && t.includes(':')) {
    const parts = t.split(':')
    return `${parts[0]}:${parts[1]}`
  }
  return t
}

function formatDate(d) {
  if (!d) return ''
  return new Date(d + 'T00:00:00').toLocaleDateString(undefined, { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })
}

function handleFiles(e) {
  addFiles(Array.from(e.target.files))
}

function handleDrop(e) {
  dragOver.value = false
  addFiles(Array.from(e.dataTransfer.files))
}

function addFiles(files) {
  for (const file of files) {
    if (file.type.startsWith('image/') && file.size <= 10 * 1024 * 1024) {
      const preview = URL.createObjectURL(file)
      photos.value.push({ file, preview })
    }
  }
}

function removePhoto(idx) {
  URL.revokeObjectURL(photos.value[idx].preview)
  photos.value.splice(idx, 1)
}

async function submitBooking() {
  submitting.value = true
  submitError.value = ''
  try {
    const startTime = bookingStore.selectedSlot.startTime
    const dateStr = bookingStore.selectedDate
    const startParts = startTime.split ? startTime.split(':') : [String(startTime).padStart(2,'0'), '00']
    const dateTime = `${dateStr}T${startParts[0].padStart(2,'0')}:${startParts[1].padStart(2,'0')}:00`

    const booking = {
      customerPhone: contactInfo.value.phone,
      customerEmail: contactInfo.value.email || null,
      whatsapp: contactInfo.value.whatsapp || null,
      instagram: contactInfo.value.instagram || null,
      address: contactInfo.value.address,
      startTime: dateTime,
      serviceTypeIds: bookingStore.selectedServices,
      notes: contactInfo.value.notes || null
    }

    const formData = new FormData()
    formData.append('booking', new Blob([JSON.stringify(booking)], { type: 'application/json' }))
    for (const p of photos.value) {
      formData.append('photos', p.file)
    }

    await api.post('/bookings', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    submitted.value = true
  } catch (e) {
    submitError.value = e.response?.data?.error || 'Booking failed. Please try again.'
  } finally {
    submitting.value = false
  }
}

function resetBooking() {
  bookingStore.reset()
  currentStep.value = 0
  submitted.value = false
  contactInfo.value = { phone: '', email: '', whatsapp: '', instagram: '', address: '', notes: '' }
  photos.value.forEach(p => URL.revokeObjectURL(p.preview))
  photos.value = []
}

onMounted(() => {
  bookingStore.fetchServices()
  bookingStore.fetchWorkingDays()
})
</script>

<style scoped>
.booking-page {
  padding-top: 120px;
  padding-bottom: 80px;
  min-height: 100vh;
}

.booking-header {
  margin-bottom: 48px;
}

.step-content {
  margin-bottom: 32px;
}

.step-title {
  font-family: var(--font-display);
  font-size: 1.3rem;
  font-weight: 600;
  margin-bottom: 24px;
  color: var(--text-primary);
}

/* Service Selection */
.services-select-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.service-select-card {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: var(--bg-card);
  border: 2px solid var(--border);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: var(--transition);
}

.service-select-card:hover {
  border-color: var(--border-gold);
  background: var(--bg-card-hover);
}

.service-select-card.selected {
  border-color: var(--accent-gold);
  background: rgba(212, 168, 67, 0.05);
  box-shadow: 0 0 20px rgba(212, 168, 67, 0.1);
}

.ssc-check {
  width: 28px;
  height: 28px;
  border-radius: var(--radius-sm);
  border: 2px solid var(--border);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: var(--transition);
}

.service-select-card.selected .ssc-check {
  background: var(--accent-gold);
  border-color: var(--accent-gold);
  color: #0a0a0f;
}

.ssc-info h4 {
  font-size: 1rem;
  margin-bottom: 6px;
}

.ssc-info p {
  color: var(--text-secondary);
  font-size: 0.85rem;
  line-height: 1.5;
  margin-bottom: 12px;
}

.ssc-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.85rem;
  color: var(--text-muted);
}

.ssc-price {
  color: var(--accent-gold);
  font-weight: 700;
}

.duration-summary {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 16px 24px;
  font-size: 1rem;
}

.duration-summary strong {
  color: var(--accent-gold);
  font-size: 1.1rem;
}

.summary-divider {
  color: var(--text-muted);
}

/* Date & Time */
.datetime-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 32px;
}

.date-input {
  color-scheme: dark;
  font-size: 1rem;
}

.time-slots-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 10px;
  max-height: 400px;
  overflow-y: auto;
  padding: 4px;
}

.time-slot {
  padding: 12px 16px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  color: var(--text-primary);
  font-weight: 500;
  transition: var(--transition);
  text-align: center;
}

.time-slot:hover:not(.disabled) {
  border-color: var(--accent-gold);
  background: rgba(212, 168, 67, 0.05);
}

.time-slot.selected {
  background: var(--accent-gold);
  border-color: var(--accent-gold);
  color: #0a0a0f;
  font-weight: 700;
}

.time-slot.disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.no-slots, .slots-loading {
  text-align: center;
  padding: 40px;
  color: var(--text-muted);
  font-size: 0.95rem;
}

/* Contact */
.contact-form {
  max-width: 700px;
  margin: 0 auto;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

/* Photos */
.photo-upload {
  max-width: 700px;
  margin: 0 auto;
}

.photo-subtitle {
  color: var(--text-secondary);
  font-size: 0.9rem;
  margin-bottom: 24px;
}

.dropzone {
  border: 2px dashed var(--border);
  border-radius: var(--radius-md);
  padding: 48px;
  text-align: center;
  cursor: pointer;
  transition: var(--transition);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.dropzone:hover, .dropzone.active {
  border-color: var(--accent-gold);
  background: rgba(212, 168, 67, 0.03);
}

.dropzone p {
  color: var(--text-secondary);
  font-size: 0.95rem;
}

.dropzone small {
  color: var(--text-muted);
  font-size: 0.8rem;
}

.photo-previews {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
  margin-top: 20px;
}

.photo-preview {
  position: relative;
  border-radius: var(--radius-sm);
  overflow: hidden;
  aspect-ratio: 4/3;
}

.photo-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.photo-remove {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 24px;
  height: 24px;
  background: rgba(231, 76, 60, 0.9);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  line-height: 1;
}

.photo-name {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 4px 8px;
  background: rgba(0,0,0,0.7);
  font-size: 0.7rem;
  color: var(--text-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Confirmation */
.confirm-card {
  max-width: 700px;
  margin: 0 auto;
}

.summary-section {
  padding: 16px 0;
  border-bottom: 1px solid var(--border);
}

.summary-section:last-of-type {
  border-bottom: none;
}

.summary-section h4 {
  color: var(--accent-gold);
  font-size: 0.85rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 8px;
}

.summary-section p {
  color: var(--text-secondary);
  font-size: 0.95rem;
  line-height: 1.6;
}

.summary-list {
  list-style: none;
  padding: 0;
}

.summary-list li {
  padding: 4px 0;
  color: var(--text-secondary);
  font-size: 0.95rem;
}

.error-msg {
  margin-top: 16px;
  padding: 12px 16px;
  background: rgba(231, 76, 60, 0.1);
  border: 1px solid rgba(231, 76, 60, 0.3);
  border-radius: var(--radius-sm);
  color: var(--accent-red);
  font-size: 0.9rem;
}

/* Success */
.success-card {
  max-width: 500px;
  margin: 0 auto;
  text-align: center;
  padding: 60px 40px;
}

.success-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: rgba(46, 204, 113, 0.15);
  color: var(--accent-green);
  font-size: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
}

.success-card h2 {
  font-family: var(--font-display);
  margin-bottom: 12px;
}

.success-card p {
  color: var(--text-secondary);
  margin-bottom: 32px;
}

.success-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

/* Navigation */
.step-nav {
  display: flex;
  justify-content: space-between;
  margin-top: 32px;
}

@media (max-width: 768px) {
  .services-select-grid {
    grid-template-columns: 1fr;
  }
  .datetime-grid {
    grid-template-columns: 1fr;
  }
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
