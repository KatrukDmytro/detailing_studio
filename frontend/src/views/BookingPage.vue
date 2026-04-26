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

        <div v-if="currentStep === 1" class="step-content animate-fade-in">
          <div class="datetime-layout">
            <div class="calendar-column">
              <h3 class="step-title">{{ $t('booking.selectDate') }}</h3>
              <div class="datepicker-container" :class="{ 'is-loading': bookingStore.loading }">
                <!-- Custom Calendar -->
              <div class="custom-calendar">
                <!-- Month Navigation Header -->
                <div class="cal-header">
                  <button class="cal-nav" @click="prevMonth" :disabled="isPrevMonthDisabled">&#8249;</button>
                  <span class="cal-month-label">{{ calMonthLabel }}</span>
                  <button class="cal-nav" @click="nextMonth">&#8250;</button>
                </div>
                <!-- Day Labels -->
                <div class="cal-weekdays">
                  <span v-for="d in weekDays" :key="d">{{ d }}</span>
                </div>
                <!-- Date Grid -->
                <div class="cal-grid">
                  <div
                    v-for="(cell, idx) in calendarCells"
                    :key="idx"
                    class="cal-cell"
                    :class="{
                      'cal-empty': !cell,
                      'cal-available': cell && isAvailable(cell),
                      'cal-unavailable': cell && !isAvailable(cell),
                      'cal-selected': cell && selectedDateStr === toDateStr(cell),
                      'cal-today': cell && toDateStr(cell) === todayStr,
                      'cal-past': cell && cell < todayDate,
                    }"
                    @click="cell && isAvailable(cell) && selectDate(cell)"
                  >
                    <span v-if="cell">{{ cell.getDate() }}</span>
                  </div>
                </div>
              </div>
                <div v-if="bookingStore.loading && !bookingStore.availableDates.length" class="datepicker-overlay">
                  <div class="loader"></div>
                </div>
              </div>
            </div>
            
            <div class="slots-column">
              <h3 class="step-title">
                {{ $t('booking.selectTime') }}
                <span v-if="bookingStore.selectedDate" class="selected-date-preview">
                  — {{ formatDateShort(bookingStore.selectedDate) }}
                </span>
              </h3>
              
              <div v-if="bookingStore.loading" class="slots-loading">
                <div class="loader"></div>
                <span>{{ $t('common.loading') || 'Loading slots...' }}</span>
              </div>
              <div v-else-if="!bookingStore.selectedDate" class="slots-prompt">
                {{ $t('booking.pleaseSelectDate') || 'Please select a date to see available times' }}
              </div>
              <div v-else-if="bookingStore.availableSlots.length === 0" class="no-slots">
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

const { t, locale } = useI18n()

// ── Custom Calendar ──────────────────────────────────────────────────────────
const calYear  = ref(new Date().getFullYear())
const calMonth = ref(new Date().getMonth()) // 0-indexed

const weekDays = ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa']

const todayDate = new Date()
todayDate.setHours(0,0,0,0)
const todayStr = toDateStr(todayDate)

function toDateStr(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

const calMonthLabel = computed(() => {
  return new Date(calYear.value, calMonth.value, 1)
    .toLocaleDateString('en', { month: 'long', year: 'numeric' })
})

const calendarCells = computed(() => {
  const firstDay = new Date(calYear.value, calMonth.value, 1).getDay() // 0=Sun
  const daysInMonth = new Date(calYear.value, calMonth.value + 1, 0).getDate()
  const cells = []
  for (let i = 0; i < firstDay; i++) cells.push(null)
  for (let d = 1; d <= daysInMonth; d++) {
    cells.push(new Date(calYear.value, calMonth.value, d))
  }
  return cells
})

const isPrevMonthDisabled = computed(() => {
  const now = new Date()
  return calYear.value === now.getFullYear() && calMonth.value <= now.getMonth()
})

function prevMonth() {
  if (isPrevMonthDisabled.value) return
  if (calMonth.value === 0) { calMonth.value = 11; calYear.value-- }
  else calMonth.value--
  loadMonthDates()
}
function nextMonth() {
  if (calMonth.value === 11) { calMonth.value = 0; calYear.value++ }
  else calMonth.value++
  loadMonthDates()
}
function loadMonthDates() {
  bookingStore.fetchAvailableDates(calYear.value, calMonth.value + 1)
}

function isAvailable(date) {
  if (date < todayDate) return false
  if (bookingStore.availableDates.length === 0) return false
  return bookingStore.availableDates.includes(toDateStr(date))
}

const selectedDateStr = computed(() => bookingStore.selectedDate)

async function selectDate(date) {
  bookingStore.selectedDate = toDateStr(date)
  bookingStore.selectedSlot = null
  await bookingStore.fetchAvailableSlots(bookingStore.selectedDate, bookingStore.selectedServices)
}
// ─────────────────────────────────────────────────────────────────────────────

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

import { watch } from 'vue'

watch(currentStep, async (newVal) => {
  if (newVal === 1) {
    await bookingStore.fetchAvailableDates(calYear.value, calMonth.value + 1)
  }
})

// Removed: datePickerDate watcher (replaced by selectDate())

// Removed: isDateDisabled, handleMonthYearChange (replaced by custom calendar)


function formatTime(t) {
  if (!t) return ''
  if (typeof t === 'string' && t.includes(':')) {
    const parts = t.split(':')
    return `${parts[0]}:${parts[1]}`
  }
  return t
}

function formatDateShort(d) {
  if (!d) return ''
  return new Date(d + 'T00:00:00').toLocaleDateString(locale.value, { month: 'short', day: 'numeric' })
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

    const photoUrls = []
    for (const p of photos.value) {
      const file = p.file
      const presignResp = await api.get('/uploads/presigned-url', {
        params: { filename: file.name, contentType: file.type }
      })
      const { uploadUrl, fileUrl } = presignResp.data
      
      await fetch(uploadUrl, {
        method: 'PUT',
        body: file,
        headers: { 'Content-Type': file.type }
      })
      photoUrls.push(fileUrl)
    }

    const booking = {
      customerPhone: contactInfo.value.phone,
      customerEmail: contactInfo.value.email || null,
      whatsapp: contactInfo.value.whatsapp || null,
      instagram: contactInfo.value.instagram || null,
      address: contactInfo.value.address,
      startTime: dateTime,
      serviceTypeIds: bookingStore.selectedServices,
      notes: contactInfo.value.notes || null,
      photoUrls: photoUrls
    }

    await api.post('/bookings', booking, {
      headers: { 'Content-Type': 'application/json' }
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

/* Date & Time Layout */
.datetime-layout {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

@media (min-width: 1024px) {
  .datetime-layout {
    flex-direction: row;
    align-items: flex-start;
  }
}

.calendar-column {
  flex: 1.2;
  width: 100%;
}

.slots-column {
  flex: 0.8;
  width: 100%;
}

.datepicker-container {
  background: var(--bg-card);
  border: 1px solid var(--border-blue);
  border-radius: var(--radius-lg);
  padding: 16px;
  box-shadow: var(--shadow-lg);
  width: 100%;
  position: relative;
  min-height: 380px;
}

.datepicker-overlay {
  position: absolute;
  inset: 0;
  background: rgba(2, 5, 16, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  border-radius: var(--radius-lg);
}

.selected-date-preview {
  color: var(--accent-blue);
  font-size: 0.9rem;
  font-weight: 500;
}

/* ── Custom Calendar ──────────────────────────────────────────────── */
.custom-calendar {
  width: 100%;
  user-select: none;
}

.cal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding: 0 4px;
}

.cal-month-label {
  font-size: 1.1rem;
  font-weight: 700;
  color: var(--text-primary);
}

.cal-nav {
  background: none;
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  color: var(--accent-blue);
  width: 36px;
  height: 36px;
  font-size: 1.4rem;
  line-height: 1;
  cursor: pointer;
  transition: var(--transition);
  display: flex;
  align-items: center;
  justify-content: center;
}
.cal-nav:hover:not(:disabled) {
  background: rgba(72, 149, 239, 0.1);
  border-color: var(--accent-blue);
}
.cal-nav:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.cal-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: 8px;
}
.cal-weekdays span {
  text-align: center;
  font-size: 0.75rem;
  font-weight: 700;
  text-transform: uppercase;
  color: var(--text-secondary);
  padding: 4px 0;
}

.cal-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.cal-cell {
  aspect-ratio: 1;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.9rem;
  font-weight: 500;
  transition: var(--transition);
  cursor: default;
  border: 1px solid transparent;
}

.cal-empty {
  background: none;
}

.cal-available {
  color: var(--text-primary);
  cursor: pointer;
  background: rgba(255,255,255,0.04);
}
.cal-available:hover {
  background: rgba(72, 149, 239, 0.15);
  border-color: var(--accent-blue);
  color: var(--accent-blue);
  transform: scale(1.08);
}

.cal-unavailable {
  color: var(--text-muted);
  opacity: 0.4;
  text-decoration: line-through;
  cursor: not-allowed;
}

.cal-past {
  color: var(--text-muted);
  opacity: 0.3;
  cursor: not-allowed;
  text-decoration: none;
}

.cal-today {
  border-color: var(--accent-blue) !important;
}

.cal-selected {
  background: var(--accent-gold) !important;
  color: #0a0a0f !important;
  font-weight: 700 !important;
  border-color: var(--accent-gold) !important;
  box-shadow: 0 0 14px rgba(212, 168, 67, 0.4);
  transform: scale(1.05);
}
/* ───────────────────────────────────────────────────────────────────── */


.time-slots-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(110px, 1fr));
  gap: 12px;
  max-height: 500px;
  overflow-y: auto;
  padding: 4px;
}

.time-slot {
  padding: 14px;
  background: var(--bg-secondary);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  color: var(--text-primary);
  font-weight: 600;
  transition: var(--transition);
  text-align: center;
}

.time-slot:hover:not(.disabled) {
  border-color: var(--accent-blue);
  background: var(--bg-card-hover);
  transform: translateY(-2px);
}

.time-slot.selected {
  background: var(--accent-blue);
  border-color: var(--accent-blue);
  color: #fff;
  box-shadow: 0 4px 12px var(--accent-blue-glow);
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

.slots-prompt {
  text-align: center;
  padding: 60px;
  background: var(--bg-secondary);
  border: 1px dashed var(--border);
  border-radius: var(--radius-lg);
  color: var(--text-secondary);
  font-style: italic;
}

.loader {
  width: 24px;
  height: 24px;
  border: 3px solid var(--accent-blue-glow);
  border-top-color: var(--accent-blue);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin-bottom: 12px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
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
  .datetime-layout {
    gap: 24px;
  }
  
  :deep(.dp__cell_inner) {
    height: 44px !important;
    font-size: 1rem !important;
  }
  
  .slots-prompt {
    padding: 40px 20px;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
