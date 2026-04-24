<template>
  <div class="reviews-page">
    <section class="section header-section">
      <div class="container text-center">
        <h1 class="page-title">{{ $t('reviews.title') || 'Customer Reviews' }}</h1>
        <p class="page-subtitle">{{ $t('reviews.subtitle') || 'See what our clients say about us' }}</p>
        <button class="btn btn-primary" @click="showForm = !showForm">
          {{ showForm ? 'Close Form' : ($t('reviews.leaveReview') || 'Leave a Review') }}
        </button>
      </div>
    </section>

    <!-- Review Form Section -->
    <transition name="slide-fade">
      <section v-if="showForm" class="section form-section">
        <div class="container">
          <div class="card form-card">
            <h2 class="form-title">{{ $t('reviews.leaveReview') || 'Leave a Review' }}</h2>
            <form @submit.prevent="submitReview" class="review-form">
              
              <div class="form-row">
                <div class="form-group">
                  <label>{{ $t('reviews.name') || 'Your Name' }} *</label>
                  <input v-model="form.customerName" type="text" :placeholder="$t('reviews.namePlaceholder') || 'John Doe'" required class="form-input" />
                </div>
                <div class="form-group">
                  <label>{{ $t('reviews.contactInfo') || 'Contact Info (Phone/Email)' }}</label>
                  <input v-model="form.contactInfo" type="text" :placeholder="$t('reviews.contactPlaceholder') || 'Optional'" class="form-input" />
                </div>
              </div>

              <div class="form-group">
                <label>{{ $t('reviews.rating') || 'Rating' }} *</label>
                <div class="star-rating-input">
                  <span v-for="star in 5" :key="star" class="star" 
                        :class="{ active: star <= form.rating }"
                        @click="form.rating = star">
                    ★
                  </span>
                </div>
              </div>

              <div class="form-group">
                <label>{{ $t('reviews.loved') || 'What did you love?' }}</label>
                <textarea v-model="form.lovedText" :placeholder="$t('reviews.lovedPlaceholder') || 'Tell us what we did great...'" rows="3" class="form-input"></textarea>
              </div>

              <div class="form-group">
                <label>{{ $t('reviews.improve') || 'What can we improve?' }}</label>
                <textarea v-model="form.improveText" :placeholder="$t('reviews.improvePlaceholder') || 'Any suggestions...'" rows="3" class="form-input"></textarea>
              </div>

              <div class="form-group">
                <label>{{ $t('reviews.photos') || 'Upload Photos' }}</label>
                <div 
                  class="photo-dropzone" 
                  @drop.prevent="handleDrop" 
                  @dragover.prevent 
                  @click="$refs.fileInput.click()"
                >
                  <input 
                    type="file" 
                    ref="fileInput" 
                    multiple 
                    accept="image/*" 
                    class="hidden" 
                    @change="handleFileSelect" 
                  />
                  <div class="dropzone-content">
                    <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>
                    <span>Click to browse or drop photos here</span>
                  </div>
                </div>
                <div v-if="files.length > 0" class="file-list">
                  <div v-for="(file, index) in files" :key="index" class="file-item">
                    <span>{{ file.name }}</span>
                    <button type="button" @click.prevent="removeFile(index)" class="btn-remove">×</button>
                  </div>
                </div>
              </div>

              <div class="form-actions">
                <button type="submit" class="btn btn-primary btn-block" :disabled="isSubmitting">
                  {{ isSubmitting ? ($t('reviews.submitting') || 'Submitting...') : ($t('reviews.submit') || 'Submit Review') }}
                </button>
              </div>

              <div v-if="submitSuccess" class="success-message">
                {{ $t('reviews.success') || 'Thank you for your review!' }}
              </div>
            </form>
          </div>
        </div>
      </section>
    </transition>

    <!-- Reviews List -->
    <section class="section list-section">
      <div class="container">
        <div v-if="loading" class="loading-state">Loading reviews...</div>
        <div v-else-if="reviews.length === 0" class="empty-state">
          <p>{{ $t('reviews.noReviews') || 'No reviews yet. Be the first to leave one!' }}</p>
        </div>
        <div v-else class="reviews-grid">
          <div v-for="review in reviews" :key="review.id" class="review-card glass-card">
            <div class="review-header">
              <div class="review-author">
                <div class="author-avatar">{{ review.customerName.charAt(0).toUpperCase() }}</div>
                <div class="author-info">
                  <h3 class="author-name">{{ review.customerName }}</h3>
                  <span class="review-date">{{ formatDate(review.createdAt) }}</span>
                </div>
              </div>
              <div class="star-rating-display">
                <span v-for="star in 5" :key="star" class="star" :class="{ active: star <= review.rating }">★</span>
              </div>
            </div>
            
            <div class="review-body">
              <div v-if="review.lovedText" class="review-block loved">
                <span class="block-icon">👍</span>
                <p>{{ review.lovedText }}</p>
              </div>
              <div v-if="review.improveText" class="review-block improve">
                <span class="block-icon">💡</span>
                <p>{{ review.improveText }}</p>
              </div>
            </div>

            <div v-if="review.hasPhotos && review.photos.length > 0" class="review-photos">
              <div v-for="photo in review.photos" :key="photo.id" class="photo-thumb" @click="openPhoto(photo.id)">
                <img :src="`/api/reviews/photos/${photo.id}`" alt="Review Photo" loading="lazy" />
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Photo Modal -->
    <div v-if="activePhotoId" class="photo-modal" @click="activePhotoId = null">
      <div class="modal-content" @click.stop>
        <button class="modal-close" @click="activePhotoId = null">×</button>
        <img :src="`/api/reviews/photos/${activePhotoId}`" alt="Full screen review photo" />
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL || '/api'

const reviews = ref([])
const loading = ref(true)
const showForm = ref(false)
const isSubmitting = ref(false)
const submitSuccess = ref(false)
const files = ref([])
const activePhotoId = ref(null)

const form = ref({
  customerName: '',
  contactInfo: '',
  rating: 5,
  lovedText: '',
  improveText: ''
})

const fileInput = ref(null)

const fetchReviews = async () => {
  try {
    loading.value = true
    const response = await axios.get(`${API_URL}/reviews`)
    reviews.value = response.data
  } catch (error) {
    console.error('Failed to load reviews', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('en-US', { year: 'numeric', month: 'short', day: 'numeric' }).format(date)
}

const handleFileSelect = (event) => {
  const selectedFiles = Array.from(event.target.files)
  files.value = [...files.value, ...selectedFiles]
}

const handleDrop = (event) => {
  const droppedFiles = Array.from(event.dataTransfer.files)
  files.value = [...files.value, ...droppedFiles.filter(f => f.type.startsWith('image/'))]
}

const removeFile = (index) => {
  files.value.splice(index, 1)
}

const submitReview = async () => {
  if (isSubmitting.value) return
  isSubmitting.value = true
  submitSuccess.value = false

  try {
    const formData = new FormData()
    formData.append('review', JSON.stringify({
      customerName: form.value.customerName,
      contactInfo: form.value.contactInfo,
      rating: form.value.rating,
      lovedText: form.value.lovedText,
      improveText: form.value.improveText
    }))
    
    files.value.forEach(file => {
      formData.append('photos', file)
    })

    await axios.post(`${API_URL}/reviews`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })

    submitSuccess.value = true
    
    // Reset form
    form.value = {
      customerName: '',
      contactInfo: '',
      rating: 5,
      lovedText: '',
      improveText: ''
    }
    files.value = []
    
    // Refresh list
    await fetchReviews()
    
    // Hide form after delay
    setTimeout(() => {
      showForm.value = false
      submitSuccess.value = false
    }, 2000)
    
  } catch (error) {
    console.error('Failed to submit review', error)
    alert('Failed to submit review. Please try again.')
  } finally {
    isSubmitting.value = false
  }
}

const openPhoto = (id) => {
  activePhotoId.value = id
}

onMounted(() => {
  fetchReviews()
})
</script>

<style scoped>
.reviews-page {
  padding-top: 100px;
  min-height: 100vh;
}

.header-section {
  padding: 40px 0;
}

.page-title {
  font-family: var(--font-display);
  font-size: 2.5rem;
  margin-bottom: 12px;
  background: linear-gradient(135deg, #fff, var(--accent-blue-light));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.page-subtitle {
  color: var(--text-muted);
  margin-bottom: 24px;
}

.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}
.slide-fade-leave-active {
  transition: all 0.3s cubic-bezier(1, 0.5, 0.8, 1);
}
.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(-20px);
  opacity: 0;
}

.form-card {
  max-width: 700px;
  margin: 0 auto;
  padding: 32px;
  border-radius: var(--radius-lg);
  background: rgba(2, 5, 16, 0.8);
  border: 1px solid var(--border-blue);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
}

.form-title {
  font-family: var(--font-display);
  margin-bottom: 24px;
  font-size: 1.5rem;
  text-align: center;
}

.form-row {
  display: flex;
  gap: 16px;
}
.form-row .form-group {
  flex: 1;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 0.9rem;
  font-weight: 600;
  color: var(--text-secondary);
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  color: #fff;
  transition: var(--transition);
}

.form-input:focus {
  outline: none;
  border-color: var(--accent-blue);
  box-shadow: 0 0 10px rgba(0, 180, 255, 0.2);
}

.star-rating-input {
  display: flex;
  gap: 8px;
  font-size: 2rem;
  cursor: pointer;
}

.star {
  color: #333;
  transition: color 0.2s;
}

.star.active {
  color: var(--accent-gold);
  text-shadow: 0 0 10px rgba(255, 215, 0, 0.5);
}

.photo-dropzone {
  border: 2px dashed var(--border);
  border-radius: var(--radius-sm);
  padding: 32px;
  text-align: center;
  cursor: pointer;
  transition: var(--transition);
  background: rgba(255, 255, 255, 0.02);
}

.photo-dropzone:hover {
  border-color: var(--accent-blue);
  background: rgba(0, 180, 255, 0.05);
}

.dropzone-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  color: var(--text-muted);
}

.hidden {
  display: none;
}

.file-list {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.file-item {
  background: rgba(0, 180, 255, 0.1);
  border: 1px solid rgba(0, 180, 255, 0.2);
  padding: 6px 12px;
  border-radius: 100px;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-remove {
  background: none;
  border: none;
  color: #ff4d4d;
  cursor: pointer;
  font-weight: bold;
}

.form-actions {
  margin-top: 32px;
}

.btn-block {
  width: 100%;
}

.success-message {
  margin-top: 16px;
  padding: 12px;
  background: rgba(0, 229, 160, 0.1);
  color: var(--accent-green);
  border: 1px solid rgba(0, 229, 160, 0.2);
  border-radius: var(--radius-sm);
  text-align: center;
  font-weight: bold;
}

/* Reviews List */
.reviews-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
}

.review-card {
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: transform 0.3s;
}

.review-card:hover {
  transform: translateY(-4px);
  border-color: rgba(0, 180, 255, 0.2);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.review-author {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--accent-blue), var(--accent-purple));
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  font-weight: bold;
  color: #fff;
  box-shadow: 0 4px 10px rgba(0, 180, 255, 0.3);
}

.author-name {
  font-weight: 600;
  font-size: 1.1rem;
}

.review-date {
  font-size: 0.8rem;
  color: var(--text-muted);
}

.star-rating-display {
  display: flex;
  gap: 2px;
  font-size: 1.2rem;
}

.star-rating-display .star {
  color: #333;
}

.star-rating-display .star.active {
  color: var(--accent-gold);
}

.review-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.review-block {
  display: flex;
  gap: 12px;
  background: rgba(255, 255, 255, 0.03);
  padding: 12px;
  border-radius: var(--radius-sm);
  font-size: 0.95rem;
  line-height: 1.5;
  color: var(--text-secondary);
}

.review-block p {
  margin: 0;
}

.block-icon {
  font-size: 1.2rem;
}

.review-photos {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.photo-thumb {
  width: 80px;
  height: 80px;
  flex-shrink: 0;
  border-radius: var(--radius-sm);
  overflow: hidden;
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.photo-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.photo-thumb:hover img {
  transform: scale(1.1);
}

/* Modal */
.photo-modal {
  position: fixed;
  inset: 0;
  z-index: 2000;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
}

.modal-content {
  position: relative;
  max-width: 90%;
  max-height: 90vh;
}

.modal-content img {
  max-width: 100%;
  max-height: 90vh;
  object-fit: contain;
  border-radius: var(--radius-md);
  box-shadow: 0 0 50px rgba(0, 0, 0, 0.8);
}

.modal-close {
  position: absolute;
  top: -40px;
  right: -40px;
  background: none;
  border: none;
  color: #fff;
  font-size: 2.5rem;
  cursor: pointer;
  padding: 8px;
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 0;
  }
  .modal-close {
    top: -40px;
    right: 0;
  }
}
</style>
