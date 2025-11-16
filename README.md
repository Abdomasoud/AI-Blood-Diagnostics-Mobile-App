# AI Blood Diagnostics Mobile App

A modern Android application for blood diagnostics with dual user flows: **Doctor** and **Patient** interfaces, featuring AI-powered analysis, patient-doctor connections, and real-time file uploads.

## 📱 Application Overview

This app provides a complete blood diagnostics platform with AI-powered analysis, patient-doctor connection management, file upload system, and real-time communication.

## 🎨 Design System

**Color Scheme:**
- Primary Blue: `#1E5FFF`
- Light Blue: `#C8D2FF`
- White: `#FFFFFF`

## 🚀 Application Flow

### 👨‍⚕️ Doctor Flow
1. **Login** → Doctor Dashboard
2. **Dashboard Features**:
   - View statistics (Connected Patients, Pending Requests, Completed Reports)
   - Review connection requests from patients
   - Approve/reject patient connection requests
   - View connected patients
   - Run AI Diagnosis on patient uploads
   - Chat with approved patients
3. **Patient Management**:
   - View patient profiles (only approved connections)
   - Access patient's uploaded tests
   - Update medical history and diagnoses
   - Communicate via chat
4. **Connection Requests**:
   - Review pending requests from patients
   - Approve or reject connection requests
   - View approved patient list
5. **AI Diagnosis**: Process patient blood test uploads and generate automated reports

### 👤 Patient Flow
1. **Signup/Login** → Patient Dashboard
2. **Dashboard Features**:
   - View personal health statistics
   - Browse available doctors
   - Send connection requests to doctors
   - Upload blood test files (CBC, MSI, etc.)
   - Chat with approved doctor
   - View diagnosis history
3. **Doctor Selection & Connection**:
   - Browse doctor list with specializations and ratings
   - View doctor profiles and experience
   - Send connection request to preferred doctor
   - Wait for doctor approval
   - Once approved: upload tests and communicate
4. **File Upload System**:
   - Select test type (CBC, MSI, Both)
   - Choose file type (Document/Image)
   - Upload blood test files from lab
   - Track upload status (pending, analyzed, completed)
   - Receive AI-powered analysis results
5. **Chat with Doctor**:
   - Real-time messaging with approved doctor
   - Share test results and concerns
   - Receive medical advice and treatment plans

### 💬 Chat System
- Real-time messaging between doctors and patients
- Messages appear instantly like WhatsApp/Messenger
- Separate chat interfaces for each user type
- Only available for approved doctor-patient connections

### 🔗 Connection System
- **Patients** initiate connection requests to doctors
- **Doctors** review and approve/reject requests
- Once approved: full access to chat, file sharing, and diagnosis
- Doctors can view all connected patients
- Patients can only connect to one doctor at a time (or multiple - configurable)

## 🗄️ Database Architecture

### PostgreSQL Database (Docker)

The app uses a PostgreSQL database running in Docker for data persistence.

#### Quick Start Database:
```bash
# Start PostgreSQL container
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f postgres

# Stop database
docker-compose down
```

#### Database Tables:
1. **doctors** - Doctor profiles, specializations, ratings
2. **patients** - Patient profiles, medical history
3. **doctor_patient_connections** - Connection requests and approvals
4. **test_uploads** - Uploaded blood test files with metadata
5. **diagnoses** - AI-generated diagnoses and doctor reviews
6. **chat_messages** - Doctor-patient communication
7. **appointments** - Appointment bookings and payments

**Connection Details:**
- Host: `localhost:5432`
- Database: `blood_diagnostics`
- User: `aiblood_user`
- Password: `aiblood_pass123`

See [database/README.md](database/README.md) for detailed database documentation.

## 🔑 Test Accounts

### Doctor Account
- **Email**: `doctor@hospital.com`
- **Password**: `doctor123`

### Patient Accounts
- **Email**: `patient@test.com` | **Password**: `patient123`
- **Email**: `sarah@patient.com` | **Password**: `patient123`
- **Email**: `ahmed@patient.com` | **Password**: `patient123`

## 🛠️ Setup Instructions

### Prerequisites
- **Docker Desktop**: For PostgreSQL database and backend API
- **Android Studio**: Flamingo (2022.2.1) or newer
- **Android SDK**: API 34+
- **JDK**: Java 17
- **Kotlin**: 1.9.22+

### Quick Start (5 Minutes)

**1. Start Backend Services:**
```bash
# From project root
docker-compose up -d --build
```

This starts:
- PostgreSQL database on port 5432
- Ktor backend API on port 8080

**2. Verify Services:**
```bash
# Check services are running
docker-compose ps

# Test API
curl http://localhost:8080/health
```

**3. Open in Android Studio:**
- Open project folder `e:\nabil`
- Sync Gradle dependencies
- Wait for build to complete

**4. Run the App:**
- Start Android emulator or connect device
- Click Run ▶️
- App will connect to backend automatically

### Detailed Setup

See [QUICK_START.md](QUICK_START.md) for complete setup guide.

### API & Database

- **Backend API**: `http://localhost:8080`
- **Database**: PostgreSQL on `localhost:5432`
- **API Docs**: See [backend/README.md](backend/README.md)
- **Database Docs**: See [database/README.md](database/README.md)

### Gradle Dependencies
The app uses these key libraries (automatically handled):
- **Jetpack Compose**: Modern UI toolkit
- **Room Database**: Local data storage
- **Hilt**: Dependency injection
- **Navigation Compose**: Screen navigation
- **Coroutines & Flow**: Asynchronous operations

## 🏗️ Architecture

### System Architecture
```
┌─────────────────────────────────────────┐
│         Android Mobile App              │
│         (Jetpack Compose)               │
│                                         │
│  ┌──────────┐  ┌──────────┐           │
│  │ Doctor   │  │ Patient  │           │
│  │Dashboard │  │Dashboard │           │
│  └────┬─────┘  └─────┬────┘           │
│       │              │                 │
│       └──────┬───────┘                 │
│              │                         │
│         ┌────▼────┐                    │
│         │Retrofit │                    │
│         │  API    │                    │
│         └────┬────┘                    │
└──────────────┼─────────────────────────┘
               │ HTTP/JSON
               │
         ┌─────▼─────┐
         │  Backend  │
         │ Ktor API  │
         │  (8080)   │
         └─────┬─────┘
               │ JDBC
               │
      ┌────────▼────────┐
      │   PostgreSQL    │
      │    Database     │
      │     (5432)      │
      │                 │
      │ ┌─────────────┐ │
      │ │ Doctors     │ │
      │ │ Patients    │ │
      │ │ Connections │ │
      │ │ Uploads     │ │
      │ │ Diagnoses   │ │
      │ │ Chat        │ │
      │ └─────────────┘ │
      └─────────────────┘
```

### Technology Stack

**Mobile App:**
- **UI Framework**: Jetpack Compose + Material 3
- **Language**: Kotlin 1.9.22
- **Architecture**: MVVM (Model-View-ViewModel)
- **Local Cache**: Room (SQLite)
- **DI**: Hilt
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Coil

**Backend API:**
- **Framework**: Ktor 2.3.7
- **Language**: Kotlin
- **Database Access**: Exposed + HikariCP
- **Serialization**: Kotlinx Serialization

**Database:**
- **DBMS**: PostgreSQL 15
- **Connection Pooling**: HikariCP
- **Deployment**: Docker container

## 📁 Project Structure

```
app/src/main/java/com/aiblooddiagnostics/
├── data/
│   ├── dao/          # Database queries
│   ├── model/        # Data classes
│   └── repository/   # Data management
├── di/               # Dependency injection
├── ui/
│   ├── screens/      # All app screens
│   │   ├── auth/     # Login/Signup
│   │   ├── dashboard/# Doctor & Patient dashboards
│   │   ├── chat/     # Chat functionality
│   │   ├── patient/  # Patient management
│   │   └── request/  # Test requests
│   └── viewmodel/    # Business logic
└── navigation/       # Screen routing
```

## ✨ Key Features

### 🔐 Authentication
- Dual login system (Doctor/Patient)
- Form validation and error handling

### 📊 Dashboard
- **Doctor**: Patient statistics, management tools
- **Patient**: Personal health overview, test history

### 🩺 Medical Features
- AI-powered blood analysis (mock)
- Patient profile management
- Medical history tracking
- Test result storage

### 💬 Communication
- Real-time doctor-patient chat
- Message persistence
- User-type specific interfaces

### 📱 UI/UX
- Material 3 design system
- Blue theme (#3366FF)
- Responsive layouts
- Smooth animations
