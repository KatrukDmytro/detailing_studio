# 🚗 Passlavski — Premium Mobile Auto Detailing Studio

A full-stack web application for **Passlavski**, a mobile auto detailing studio that provides professional car care services at the customer's location. The application serves as both a **promotional website** and a **booking management system**.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
  - [Customer Features](#customer-features)
  - [Admin Features](#admin-features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
  - [Step 1: Clone the Repository](#step-1-clone-the-repository)
  - [Step 2: Database Setup](#step-2-database-setup)
  - [Step 3: Start the Backend](#step-3-start-the-backend)
  - [Step 4: Start the Frontend](#step-4-start-the-frontend)
- [Accessing the Application](#accessing-the-application)
- [API Reference](#api-reference)
- [Services & Scheduling Logic](#services--scheduling-logic)
- [Internationalization (i18n)](#internationalization-i18n)
- [Configuration](#configuration)
- [Default Data](#default-data)

---

## Overview

Passlavski is a mobile auto detailing studio specializing in:

- **Exterior Polishing** — Paint correction, swirl mark removal, oxidation treatment
- **Interior Cleaning** — Deep cleaning of all interior surfaces
- **Interior Dry Cleaning** — Professional chemical cleaning of upholstery and fabrics
- **Exterior Restoration** — Paint decontamination, clay bar treatment, ceramic coating
- **Interior Restoration** — Leather conditioning, plastic restoration, UV protection
- **Full Detail Package** — Complete interior + exterior transformation

The studio comes **to the customer's location** (home, office, etc.), making it a fully mobile service.

---

## Features

### Customer Features

| Feature | Description |
|---------|-------------|
| **Promotional Landing Page** | Premium dark-themed homepage showcasing services, benefits, and call-to-action |
| **Service Catalog** | Browse all available detailing services with descriptions, durations, and prices |
| **Online Booking** | 5-step booking wizard to schedule an appointment |
| **Service Selection** | Choose one or more services; durations are automatically summed |
| **Date & Time Picker** | View available time slots for any date based on work schedule and existing bookings |
| **Contact Information** | Provide phone number (required), email, WhatsApp, Instagram |
| **Location Input** | Specify address/location where the service should be performed |
| **Photo Upload** | Upload vehicle photos to help estimate work time (stored in database) |
| **Booking Confirmation** | Review full booking summary before submitting |
| **Multi-language Support** | Switch between English, Spanish, German, Ukrainian, and Russian |

### Admin Features

| Feature | Description |
|---------|-------------|
| **Secure Login** | Password-protected admin dashboard |
| **Bookings Management** | View all bookings, update status (Pending → Confirmed → In Progress → Completed / Cancelled), delete bookings |
| **Services CRUD** | Create, edit, and deactivate service types; modify duration and pricing |
| **Work Schedule** | Configure working hours for each day of the week (Monday–Sunday) |
| **Break Configuration** | Set break duration between appointments and lunch break times |
| **Customer Photos** | View uploaded vehicle photos for each booking |

---

## Tech Stack

### Backend
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 21 | Programming language |
| **Spring Boot** | 3.4.4 | Application framework |
| **Spring Data JPA** | 3.4.x | Database access (Hibernate ORM) |
| **Spring Security** | 6.x | Authentication & authorization |
| **Spring Validation** | 3.4.x | Request validation |
| **PostgreSQL** | 15+ | Production database |
| **H2 Database** | 2.x | Development/testing database |
| **Lombok** | 1.18.x | Boilerplate reduction |
| **Gradle** | 9.2 | Build tool |

### Frontend
| Technology | Version | Purpose |
|-----------|---------|---------|
| **Vue.js** | 3.x | UI framework (Composition API) |
| **Vite** | 8.x | Build tool & dev server |
| **Vue Router** | 4.x | Client-side routing |
| **Pinia** | 2.x | State management |
| **Axios** | 1.x | HTTP client |
| **vue-i18n** | 9.x | Internationalization |
| **Lucide Vue** | latest | Icon library |

---

## Project Structure

```
DetailingStudio/
│
├── app/                                    # Backend (Spring Boot)
│   ├── build.gradle                        # Gradle build config
│   └── src/
│       └── main/
│           ├── java/detailingstudio/
│           │   ├── App.java                # Spring Boot entry point
│           │   ├── config/
│           │   │   ├── SecurityConfig.java  # HTTP Basic auth, endpoint security
│           │   │   ├── WebConfig.java       # CORS configuration
│           │   │   └── DataInitializer.java # Seed data on first run
│           │   ├── controller/
│           │   │   ├── BookingController.java    # POST /api/bookings (public)
│           │   │   ├── ServiceTypeController.java# GET /api/services (public)
│           │   │   ├── ScheduleController.java   # GET /api/schedule/* (public)
│           │   │   └── AdminController.java      # /api/admin/* (protected)
│           │   ├── model/
│           │   │   ├── Booking.java         # Appointment entity
│           │   │   ├── ServiceType.java     # Service type entity
│           │   │   ├── WorkSchedule.java    # Day-of-week schedule entity
│           │   │   ├── BreakConfig.java     # Break settings entity
│           │   │   ├── BookingPhoto.java    # Photo stored as BYTEA
│           │   │   └── BookingStatus.java   # Status enum
│           │   ├── dto/                     # Data transfer objects
│           │   ├── repository/              # JPA repositories
│           │   ├── service/
│           │   │   ├── BookingService.java       # Booking CRUD + photo handling
│           │   │   ├── ScheduleService.java      # Schedule & service management
│           │   │   └── SlotCalculationService.java# Available slot algorithm
│           │   └── exception/
│           │       └── GlobalExceptionHandler.java
│           └── resources/
│               └── application.yml          # App configuration
│
├── frontend/                               # Frontend (Vue.js 3)
│   ├── package.json
│   ├── vite.config.js                      # Vite config + API proxy
│   ├── index.html                          # HTML entry with Google Fonts
│   └── src/
│       ├── main.js                         # Vue app bootstrap
│       ├── App.vue                         # Root component
│       ├── router/index.js                 # Routes: /, /booking, /admin
│       ├── api/client.js                   # Axios with auth interceptor
│       ├── stores/
│       │   ├── booking.js                  # Customer booking state
│       │   └── admin.js                    # Admin dashboard state
│       ├── views/
│       │   ├── HomePage.vue                # Promotional landing page
│       │   ├── BookingPage.vue             # 5-step booking wizard
│       │   └── AdminPage.vue               # Admin dashboard
│       ├── components/
│       │   └── AppNavbar.vue               # Navigation bar
│       ├── i18n/                           # Translations (en, es, de, uk, ru)
│       └── assets/styles/
│           └── main.css                    # Global design system
│
├── gradle/
│   ├── libs.versions.toml                  # Dependency version catalog
│   └── wrapper/                            # Gradle wrapper
├── settings.gradle
├── gradle.properties
├── gradlew                                 # Linux/Mac build script
└── gradlew.bat                             # Windows build script
```

---

## Prerequisites

Before running the application, ensure you have the following installed:

### Required
1. **Java 21** (JDK)
   - Download from [Adoptium](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/)
   - Verify: `java --version` should show `21.x.x`

2. **Node.js 20+** (with npm)
   - Download from [nodejs.org](https://nodejs.org/)
   - Verify: `node --version` and `npm --version`

### Database (choose one)

**Option A: PostgreSQL (Production)**
- Download from [postgresql.org](https://www.postgresql.org/download/)
- Create a database named `detailing_studio`:
  ```sql
  CREATE DATABASE detailing_studio;
  ```

**Option B: H2 (Development — no installation needed)**
- Embedded database, runs in-process
- Data stored in `./data/detailing_studio` file
- H2 Console available at `http://localhost:8080/h2-console`

---

## Getting Started

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd DetailingStudio
```

---

### Step 2: Database Setup

#### Option A: Using PostgreSQL

1. **Install PostgreSQL** if not already installed.

2. **Start PostgreSQL server** and connect to it:
   ```bash
   psql -U postgres
   ```

3. **Create the database**:
   ```sql
   CREATE DATABASE detailing_studio;
   ```

4. **Verify connection settings** in `app/src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/detailing_studio
       username: postgres
       password: postgres
   ```
   > ⚠️ Update `username` and `password` if your PostgreSQL credentials are different.

#### Option B: Using H2 (No setup required)

No database installation needed. Simply start the backend with the `h2` profile (see Step 3).

---

### Step 3: Start the Backend

Open a terminal in the project root directory (`DetailingStudio/`).

#### With PostgreSQL:

**Linux / macOS:**
```bash
./gradlew bootRun
```

**Windows:**
```cmd
.\gradlew.bat bootRun
```

#### With H2 (no PostgreSQL needed):

**Linux / macOS:**
```bash
./gradlew bootRun --args='--spring.profiles.active=h2'
```

**Windows:**
```cmd
.\gradlew.bat bootRun --args="--spring.profiles.active=h2"
```

#### What to expect:

1. Gradle will download all dependencies on first run (may take 1-2 minutes)
2. Spring Boot will start and you'll see:
   ```
   Started App in X.XXX seconds
   ```
3. The backend is now running at **http://localhost:8080**
4. On first startup, the application automatically creates:
   - 6 default service types (Polishing, Cleaning, Dry Cleaning, etc.)
   - Work schedule (Monday–Saturday, 8:00–18:00, Sunday off)
   - Break configuration (30 min between appointments, 12:00–13:00 lunch)

#### Verify the backend is working:

Open a browser and visit:
- **http://localhost:8080/api/services** — should return JSON with 6 services
- **http://localhost:8080/h2-console** — H2 database console (only in H2 profile)

---

### Step 4: Start the Frontend

Open a **second terminal** (keep the backend running in the first one).

1. **Navigate to the frontend directory**:
   ```bash
   cd frontend
   ```

2. **Install dependencies** (first time only):
   ```bash
   npm install
   ```

3. **Start the development server**:
   ```bash
   npm run dev
   ```

4. You'll see:
   ```
   VITE v8.x.x ready in XXX ms

   ➜  Local:   http://localhost:5173/
   ```

5. **Open your browser** and go to **http://localhost:5173/**

> 💡 The Vite dev server automatically proxies API calls (`/api/*`) to the backend at `http://localhost:8080`, so you don't need to worry about CORS during development.

---

## Accessing the Application

| Page | URL | Description |
|------|-----|-------------|
| **Homepage** | http://localhost:5173/ | Promotional landing page |
| **Booking** | http://localhost:5173/booking | Customer booking wizard |
| **Admin** | http://localhost:5173/admin | Admin dashboard (login required) |

### Admin Credentials

| Field | Value |
|-------|-------|
| Username | `admin` |
| Password | `passlavski2026` |

> These credentials are configured in `app/src/main/resources/application.yml` and should be changed for production.

---

## API Reference

### Public Endpoints (no authentication)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/services` | List all active service types |
| `GET` | `/api/schedule/available-slots?date=YYYY-MM-DD&serviceIds=1,2,3` | Get available time slots for a date |
| `GET` | `/api/schedule/working-days` | Get work schedule for all days |
| `POST` | `/api/bookings` | Create a booking (multipart: JSON + photos) |
| `GET` | `/api/bookings/photos/{photoId}` | Retrieve a specific photo |

### Admin Endpoints (HTTP Basic authentication required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/admin/login` | Validate admin credentials |
| `GET` | `/api/admin/bookings` | List all bookings |
| `GET` | `/api/admin/bookings/{id}` | Get booking details |
| `PUT` | `/api/admin/bookings/{id}/status?status=CONFIRMED` | Update booking status |
| `DELETE` | `/api/admin/bookings/{id}` | Delete a booking |
| `GET` | `/api/admin/services` | List all services (including inactive) |
| `POST` | `/api/admin/services` | Create a new service type |
| `PUT` | `/api/admin/services/{id}` | Update a service type |
| `DELETE` | `/api/admin/services/{id}` | Deactivate a service type |
| `GET` | `/api/admin/schedule` | Get work schedule |
| `PUT` | `/api/admin/schedule` | Update work schedule |
| `GET` | `/api/admin/breaks` | Get break configuration |
| `PUT` | `/api/admin/breaks` | Update break configuration |

---

## Services & Scheduling Logic

### How Service Duration Works

Each service type has a `durationMinutes` value. When a customer selects multiple services, their durations are **summed** to determine the total appointment length.

**Example:**
| Selected Services | Duration |
|-------------------|----------|
| Exterior Polishing | 180 min |
| Interior Cleaning | 120 min |
| **Total** | **300 min (5 hours)** |

### How Available Slots Are Calculated

The slot calculation algorithm (`SlotCalculationService`) works as follows:

1. **Check the work schedule** for the selected date's day of week
2. **Skip non-working days** (e.g., Sunday by default)
3. **Get all existing bookings** for that date (excluding cancelled ones)
4. **Get break configuration** (inter-appointment gap + lunch break)
5. **Generate candidate slots** every 30 minutes within working hours
6. **For each candidate slot**, check:
   - Does the slot + total duration fit within working hours?
   - Does it overlap with any existing booking (including gap buffer)?
   - Does it overlap with the lunch break?
   - Is it in the past (for today's date)?
7. **Return only valid slots** with availability status

### Booking Status Flow

```
PENDING → CONFIRMED → IN_PROGRESS → COMPLETED
                ↓
            CANCELLED
```

### Photo Storage

Customer photos are stored **directly in the PostgreSQL database** as `BYTEA` columns. Each photo record includes:
- Original filename
- Content type (MIME)
- Binary data

This eliminates the need for external file storage. Maximum upload size: **10MB per file**, **50MB per request**.

---

## Internationalization (i18n)

The application supports 5 languages, switchable via the language selector in the navigation bar:

| Code | Language | Flag |
|------|----------|------|
| `en` | English (default) | 🇬🇧 |
| `es` | Spanish | 🇪🇸 |
| `de` | German | 🇩🇪 |
| `uk` | Ukrainian | 🇺🇦 |
| `ru` | Russian | 🇷🇺 |

Translation files are located in `frontend/src/i18n/`. The selected language is persisted in `localStorage`.

---

## Configuration

### Backend Configuration (`application.yml`)

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8080` | Backend server port |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/detailing_studio` | Database connection URL |
| `spring.datasource.username` | `postgres` | Database username |
| `spring.datasource.password` | `postgres` | Database password |
| `spring.jpa.hibernate.ddl-auto` | `update` | Auto-create/update schema |
| `spring.servlet.multipart.max-file-size` | `10MB` | Max photo upload size |
| `spring.servlet.multipart.max-request-size` | `50MB` | Max total request size |
| `app.admin.username` | `admin` | Admin login username |
| `app.admin.password` | `passlavski2026` | Admin login password |

### Frontend Configuration (`vite.config.js`)

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `5173` | Dev server port |
| `server.proxy./api.target` | `http://localhost:8080` | Backend API proxy target |

---

## Default Data

On first startup, the application seeds the following data:

### Services

| Service | Duration | Price |
|---------|----------|-------|
| Exterior Polishing | 180 min | $250 |
| Interior Cleaning | 120 min | $150 |
| Interior Dry Cleaning | 150 min | $200 |
| Exterior Restoration | 300 min | $450 |
| Interior Restoration | 240 min | $350 |
| Full Detail Package | 480 min | $650 |

### Work Schedule

| Day | Hours | Working |
|-----|-------|---------|
| Monday | 08:00 – 18:00 | ✅ |
| Tuesday | 08:00 – 18:00 | ✅ |
| Wednesday | 08:00 – 18:00 | ✅ |
| Thursday | 08:00 – 18:00 | ✅ |
| Friday | 08:00 – 18:00 | ✅ |
| Saturday | 08:00 – 18:00 | ✅ |
| Sunday | 08:00 – 18:00 | ❌ |

### Break Configuration

| Setting | Value |
|---------|-------|
| Break between appointments | 30 minutes |
| Lunch break | 12:00 – 13:00 |

> All default data can be modified via the Admin dashboard after login.

---

## 📝 License

© 2026 Passlavski. All rights reserved.
