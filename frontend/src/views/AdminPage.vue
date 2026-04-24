<template>
  <div class="admin-page">
    <div class="container">
      <!-- Login Gate -->
      <div v-if="!adminStore.isAuthenticated" class="login-container">
        <div class="login-card glass-card animate-fade-in-up">
          <h2 class="login-title">{{ $t('admin.login.title') }}</h2>
          <div class="divider"></div>
          <form @submit.prevent="handleLogin" class="login-form">
            <div class="form-group">
              <label class="form-label">{{ $t('admin.login.username') }}</label>
              <input v-model="loginUsername" type="text" class="form-input" autocomplete="username" />
            </div>
            <div class="form-group">
              <label class="form-label">{{ $t('admin.login.password') }}</label>
              <input v-model="loginPassword" type="password" class="form-input" autocomplete="current-password" />
            </div>
            <div v-if="loginError" class="error-msg">{{ $t('admin.login.error') }}</div>
            <button type="submit" class="btn btn-primary" style="width:100%" :disabled="adminStore.loading">
              {{ $t('admin.login.submit') }}
            </button>
          </form>
        </div>
      </div>

      <!-- Admin Dashboard -->
      <div v-else class="admin-dashboard">
        <div class="admin-header">
          <h1 class="section-title" style="text-align:left">BRISA Mobile Detailing Admin</h1>
          <button class="btn btn-secondary btn-small" @click="adminStore.logout()">{{ $t('admin.logout') }}</button>
        </div>

        <!-- Tabs -->
        <div class="tabs">
          <button class="tab" :class="{ active: activeTab === 'bookings' }" @click="activeTab = 'bookings'; loadTab()">
            {{ $t('admin.tabs.bookings') }}
          </button>
          <button class="tab" :class="{ active: activeTab === 'services' }" @click="activeTab = 'services'; loadTab()">
            {{ $t('admin.tabs.services') }}
          </button>
          <button class="tab" :class="{ active: activeTab === 'schedule' }" @click="activeTab = 'schedule'; loadTab()">
            {{ $t('admin.tabs.schedule') }}
          </button>
        </div>

        <!-- Bookings Tab -->
        <div v-if="activeTab === 'bookings'" class="tab-content animate-fade-in">
          <div v-if="adminStore.bookings.length === 0" class="empty-state">
            <p>{{ $t('admin.bookings.noBookings') }}</p>
          </div>
          <div v-else class="table-container">
            <table class="table">
              <thead>
                <tr>
                  <th>#</th>
                  <th>{{ $t('admin.bookings.customer') }}</th>
                  <th>{{ $t('admin.bookings.services') }}</th>
                  <th>{{ $t('admin.bookings.dateTime') }}</th>
                  <th>{{ $t('admin.bookings.duration') }}</th>
                  <th>{{ $t('admin.bookings.status') }}</th>
                  <th>{{ $t('admin.bookings.actions') }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="b in adminStore.bookings" :key="b.id">
                  <td>{{ b.id }}</td>
                  <td>
                    <div class="customer-info">
                      <strong>{{ b.customerPhone }}</strong>
                      <small v-if="b.customerEmail">{{ b.customerEmail }}</small>
                      <small v-if="b.whatsapp">WA: {{ b.whatsapp }}</small>
                      <small v-if="b.instagram">IG: {{ b.instagram }}</small>
                      <small>📍 {{ b.address }}</small>
                    </div>
                  </td>
                  <td>
                    <div class="service-tags">
                      <span v-for="s in b.services" :key="s.id" class="service-tag">{{ s.name }}</span>
                    </div>
                  </td>
                  <td>{{ formatDateTime(b.startTime) }}</td>
                  <td>{{ b.totalDurationMinutes }} min</td>
                  <td><span class="badge" :class="'badge-' + b.status.toLowerCase().replace('_','-')">{{ b.status }}</span></td>
                  <td>
                    <div class="action-btns">
                      <button v-if="b.status==='PENDING'" class="btn btn-success btn-small" @click="adminStore.updateBookingStatus(b.id,'CONFIRMED')">{{ $t('admin.bookings.confirm') }}</button>
                      <button v-if="b.status==='CONFIRMED'" class="btn btn-primary btn-small" @click="adminStore.updateBookingStatus(b.id,'IN_PROGRESS')">Start</button>
                      <button v-if="b.status==='IN_PROGRESS'" class="btn btn-success btn-small" @click="adminStore.updateBookingStatus(b.id,'COMPLETED')">{{ $t('admin.bookings.complete') }}</button>
                      <button v-if="b.status!=='CANCELLED'&&b.status!=='COMPLETED'" class="btn btn-danger btn-small" @click="adminStore.updateBookingStatus(b.id,'CANCELLED')">{{ $t('admin.bookings.cancel') }}</button>
                      <button class="btn btn-danger btn-small" @click="adminStore.deleteBooking(b.id)">{{ $t('admin.bookings.delete') }}</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Services Tab -->
        <div v-if="activeTab === 'services'" class="tab-content animate-fade-in">
          <div class="tab-header">
            <h3>{{ $t('admin.services.title') }}</h3>
            <button class="btn btn-primary btn-small" @click="showServiceForm = true; editingService = null; serviceForm = { name:'', description:'', durationMinutes:60, price:0, active:true, displayOrder:0, icon:'sparkles' }">
              {{ $t('admin.services.addNew') }}
            </button>
          </div>

          <!-- Service Form Modal -->
          <div v-if="showServiceForm" class="modal-overlay" @click.self="showServiceForm = false">
            <div class="modal glass-card animate-fade-in-up">
              <h3>{{ editingService ? $t('admin.services.edit') : $t('admin.services.addNew') }}</h3>
              <div class="form-group">
                <label class="form-label">{{ $t('admin.services.name') }}</label>
                <input v-model="serviceForm.name" class="form-input" />
              </div>
              <div class="form-group">
                <label class="form-label">{{ $t('admin.services.description') }}</label>
                <textarea v-model="serviceForm.description" class="form-input" rows="3"></textarea>
              </div>
              <div class="form-row">
                <div class="form-group">
                  <label class="form-label">{{ $t('admin.services.duration') }}</label>
                  <input v-model.number="serviceForm.durationMinutes" type="number" class="form-input" min="15" step="15" />
                </div>
                <div class="form-group">
                  <label class="form-label">{{ $t('admin.services.price') }}</label>
                  <input v-model.number="serviceForm.price" type="number" class="form-input" min="0" step="10" />
                </div>
              </div>
              <div class="form-checkbox">
                <input type="checkbox" v-model="serviceForm.active" id="svc-active" />
                <label for="svc-active">{{ $t('admin.services.active') }}</label>
              </div>
              <div class="modal-actions">
                <button class="btn btn-secondary" @click="showServiceForm = false">{{ $t('admin.services.cancel') }}</button>
                <button class="btn btn-primary" @click="saveService">{{ $t('admin.services.save') }}</button>
              </div>
            </div>
          </div>

          <div class="table-container">
            <table class="table">
              <thead>
                <tr>
                  <th>#</th>
                  <th>{{ $t('admin.services.name') }}</th>
                  <th>{{ $t('admin.services.duration') }}</th>
                  <th>{{ $t('admin.services.price') }}</th>
                  <th>{{ $t('admin.services.active') }}</th>
                  <th>{{ $t('admin.bookings.actions') }}</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="s in adminStore.services" :key="s.id">
                  <td>{{ s.id }}</td>
                  <td>
                    <strong>{{ s.name }}</strong>
                    <br><small style="color:var(--text-muted)">{{ s.description?.substring(0, 60) }}...</small>
                  </td>
                  <td>{{ s.durationMinutes }} min</td>
                  <td>${{ s.price }}</td>
                  <td><span class="badge" :class="s.active ? 'badge-completed' : 'badge-cancelled'">{{ s.active ? 'Yes' : 'No' }}</span></td>
                  <td>
                    <div class="action-btns">
                      <button class="btn btn-secondary btn-small" @click="editService(s)">{{ $t('admin.services.edit') }}</button>
                      <button class="btn btn-danger btn-small" @click="adminStore.deleteService(s.id)">{{ $t('admin.services.delete') }}</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Schedule Tab -->
        <div v-if="activeTab === 'schedule'" class="tab-content animate-fade-in">
          <div class="schedule-grid">
            <div class="schedule-section glass-card">
              <h3>{{ $t('admin.schedule.title') }}</h3>
              <div class="table-container">
                <table class="table">
                  <thead>
                    <tr>
                      <th>{{ $t('admin.schedule.day') }}</th>
                      <th>{{ $t('admin.schedule.start') }}</th>
                      <th>{{ $t('admin.schedule.end') }}</th>
                      <th>{{ $t('admin.schedule.working') }}</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="day in adminStore.schedule" :key="day.id">
                      <td>{{ $t('days.' + day.dayOfWeek) }}</td>
                      <td><input type="time" v-model="day.startTime" class="form-input" style="width:130px" /></td>
                      <td><input type="time" v-model="day.endTime" class="form-input" style="width:130px" /></td>
                      <td>
                        <input type="checkbox" v-model="day.isWorkingDay" style="accent-color:var(--accent-gold);width:20px;height:20px" />
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
              <button class="btn btn-primary" style="margin-top:16px" @click="saveSchedule">{{ $t('admin.schedule.save') }}</button>
            </div>

            <div class="schedule-section glass-card">
              <h3>{{ $t('admin.schedule.breaks') }}</h3>
              <div class="form-group">
                <label class="form-label">{{ $t('admin.schedule.breakBetween') }}</label>
                <input type="number" v-model.number="adminStore.breakConfig.breakBetweenAppointmentsMinutes" class="form-input" min="0" step="5" />
              </div>
              <div class="form-row">
                <div class="form-group">
                  <label class="form-label">{{ $t('admin.schedule.lunchStart') }}</label>
                  <input type="time" v-model="adminStore.breakConfig.lunchStart" class="form-input" />
                </div>
                <div class="form-group">
                  <label class="form-label">{{ $t('admin.schedule.lunchEnd') }}</label>
                  <input type="time" v-model="adminStore.breakConfig.lunchEnd" class="form-input" />
                </div>
              </div>
              <button class="btn btn-primary" @click="saveBreaks">{{ $t('admin.schedule.saveBreaks') }}</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAdminStore } from '../stores/admin'

const adminStore = useAdminStore()
const activeTab = ref('bookings')
const loginUsername = ref('')
const loginPassword = ref('')
const loginError = ref(false)
const showServiceForm = ref(false)
const editingService = ref(null)
const serviceForm = ref({
  name: '', description: '', durationMinutes: 60, price: 0, active: true, displayOrder: 0, icon: 'sparkles'
})

async function handleLogin() {
  loginError.value = false
  const ok = await adminStore.login(loginUsername.value, loginPassword.value)
  if (!ok) loginError.value = true
  else loadTab()
}

async function loadTab() {
  if (activeTab.value === 'bookings') await adminStore.fetchBookings()
  if (activeTab.value === 'services') await adminStore.fetchServices()
  if (activeTab.value === 'schedule') {
    await adminStore.fetchSchedule()
    await adminStore.fetchBreakConfig()
  }
}

function editService(s) {
  editingService.value = s.id
  serviceForm.value = { ...s }
  showServiceForm.value = true
}

async function saveService() {
  if (editingService.value) {
    await adminStore.updateService(editingService.value, serviceForm.value)
  } else {
    await adminStore.createService(serviceForm.value)
  }
  showServiceForm.value = false
}

async function saveSchedule() {
  await adminStore.updateSchedule(adminStore.schedule)
}

async function saveBreaks() {
  await adminStore.updateBreakConfig(adminStore.breakConfig)
}

function formatDateTime(dt) {
  if (!dt) return ''
  return new Date(dt).toLocaleString(undefined, {
    year: 'numeric', month: 'short', day: 'numeric',
    hour: '2-digit', minute: '2-digit'
  })
}

onMounted(() => {
  if (adminStore.isAuthenticated) loadTab()
})
</script>

<style scoped>
.admin-page {
  padding-top: 120px;
  padding-bottom: 80px;
  min-height: 100vh;
}

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
}

.login-card {
  width: 100%;
  max-width: 420px;
  text-align: center;
}

.login-title {
  font-family: var(--font-display);
  font-size: 1.6rem;
  margin-bottom: 8px;
}

.login-form {
  text-align: left;
  margin-top: 24px;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.tab-content {
  min-height: 400px;
}

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.tab-header h3 {
  font-family: var(--font-display);
  font-size: 1.2rem;
}

.customer-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.customer-info small {
  color: var(--text-muted);
  font-size: 0.8rem;
}

.service-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.service-tag {
  background: rgba(212, 168, 67, 0.1);
  color: var(--accent-gold);
  padding: 2px 10px;
  border-radius: 100px;
  font-size: 0.75rem;
  font-weight: 500;
}

.action-btns {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.empty-state {
  text-align: center;
  padding: 60px;
  color: var(--text-muted);
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1100;
  padding: 24px;
}

.modal {
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal h3 {
  font-family: var(--font-display);
  margin-bottom: 24px;
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.error-msg {
  padding: 10px 14px;
  background: rgba(231,76,60,0.1);
  border: 1px solid rgba(231,76,60,0.3);
  border-radius: var(--radius-sm);
  color: var(--accent-red);
  font-size: 0.875rem;
  margin-bottom: 16px;
}

/* Schedule */
.schedule-grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 24px;
}

.schedule-section h3 {
  font-family: var(--font-display);
  font-size: 1.1rem;
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .schedule-grid {
    grid-template-columns: 1fr;
  }
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
