# AI Blood Diagnostics Mobile App

A modern Android application for blood diagnostics with dual user flows: **Doctor** and **Patient** interfaces.

## 📱 Application Overview

This app provides a complete blood diagnostics platform with AI-powered analysis, patient management, and real-time communication between doctors and patients.

## 🚀 Application Flow

### 👨‍⚕️ Doctor Flow
1. **Login** → Doctor Dashboard
2. **Dashboard Features**:
   - View statistics (Total Patients, Pending Tests, Completed Reports)
   - Manage patients (Add, View, Delete)
   - Run AI Diagnosis on patient samples
   - Chat with patients
3. **Patient Management**:
   - Click eye icon on patient → View detailed patient profile
   - Update medical history, add notes
   - Upload new tests and reports
4. **AI Diagnosis**: Process blood samples and generate automated reports

### 👤 Patient Flow
1. **Login** → Patient Dashboard
2. **Dashboard Features**:
   - View personal health statistics
   - Chat with assigned doctor
   - Request new blood tests
   - View diagnosis history
3. **New Test Request**:
   - Select test type (CBC, MSI, Both)
   - Choose file type (Document/Image)
   - Upload blood test files
   - Submit to doctor for analysis

### 💬 Chat System
- Real-time messaging between doctors and patients
- Messages appear instantly like WhatsApp/Messenger
- Separate chat interfaces for each user type

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
- **Android Studio**: Flamingo (2022.2.1) or newer
- **Android SDK**: API 34+
- **JDK**: Java 17
- **Kotlin**: 1.9.22+

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd nabil
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory and open it

3. **Sync Dependencies**
   - Android Studio will automatically prompt to sync Gradle
   - Click "Sync Now" or use menu: File → Sync Project with Gradle Files

4. **Build and Run**
   - Connect an Android device (API 24+) or start an emulator
   - Click the "Run" button (▶️) or press `Shift + F10`

### Gradle Dependencies
The app uses these key libraries (automatically handled):
- **Jetpack Compose**: Modern UI toolkit
- **Room Database**: Local data storage
- **Hilt**: Dependency injection
- **Navigation Compose**: Screen navigation
- **Coroutines & Flow**: Asynchronous operations

## 🏗️ Architecture

- **Pattern**: MVVM (Model-View-ViewModel)
- **UI**: Jetpack Compose with Material 3
- **Database**: Room (SQLite)
- **DI**: Hilt for dependency injection
- **Threading**: Kotlin Coroutines

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
