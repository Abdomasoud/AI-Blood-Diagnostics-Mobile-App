# 🚀 Quick Start Guide

## Complete Setup in 5 Minutes

### Step 1: Start Backend Services (2 minutes)

Open terminal in project root (`e:\nabil`):

```powershell
# Start PostgreSQL + Backend API
docker-compose up -d --build
```

**Wait for services to start** (about 1-2 minutes for first time)

```powershell
# Check status
docker-compose ps

# Should show:
# NAME                          STATUS
# aiblooddiagnostics_db        Up (healthy)
# aiblooddiagnostics_api       Up
```

---

### Step 2: Verify Backend is Running (30 seconds)

**Test API:**
```powershell
# PowerShell
Invoke-WebRequest -Uri http://localhost:8080/health | Select-Object -ExpandProperty Content

# Or open in browser:
# http://localhost:8080/health
```

**Expected Response:**
```json
{
  "status": "healthy",
  "service": "AI Blood Diagnostics API"
}
```

---

### Step 3: Build Android App (2 minutes)

1. **Open Android Studio**
2. **Open Project:** `e:\nabil`
3. **Sync Gradle** (Click "Sync Now" if prompted)
4. **Wait for build** to complete

---

### Step 4: Run the App (30 seconds)

1. **Start Emulator** or connect physical device
2. **Click Run** ▶️ button
3. **Wait for app to install**

---

### Step 5: Test Login

**Use these test accounts:**

**Doctor:**
- Email: `doctor@hospital.com`
- Password: `doctor123`

**Patient:**
- Email: `patient@test.com`
- Password: `patient123`

---

## 🎉 You're Ready!

The app now:
- ✅ Connects to PostgreSQL database
- ✅ Uses backend API for all data
- ✅ Shares data between doctors and patients
- ✅ Real-time doctor-patient communication

---

## 📱 For Physical Devices

If using a physical Android device:

### Find Your Computer's IP Address:
```powershell
ipconfig
# Look for "IPv4 Address" (e.g., 192.168.1.100)
```

### Update Android App:

Edit `e:\nabil\app\src\main\java\com\aiblooddiagnostics\di\NetworkModule.kt`:

Change this line:
```kotlin
val baseUrl = "http://10.0.2.2:8080/"  // For emulator
```

To:
```kotlin
val baseUrl = "http://192.168.1.100:8080/"  // Your computer's IP
```

Then rebuild the app.

---

## 🛑 Stop Services

When done:
```powershell
docker-compose down
```

To stop and remove all data:
```powershell
docker-compose down -v
```

---

## ❓ Troubleshooting

### Backend Won't Start
```powershell
# View logs
docker-compose logs backend

# Rebuild
docker-compose down
docker-compose up --build -d
```

### App Can't Connect to API
1. Check backend is running: `http://localhost:8080/health`
2. For emulator: Use `10.0.2.2:8080`
3. For device: Use your computer's IP
4. Check firewall allows port 8080

### Database Issues
```powershell
# Reset database
docker-compose down -v
docker-compose up -d
```

---

## 📚 Next Steps

- Read [backend/README.md](backend/README.md) for API documentation
- Read [database/README.md](database/README.md) for database details
- Read [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) for architecture

---

## 🔧 Development Workflow

**Daily workflow:**
```powershell
# Morning: Start services
docker-compose up -d

# Work on Android app in Android Studio

# Evening: Stop services
docker-compose down
```

**When you make database changes:**
```powershell
# Restart to apply schema changes
docker-compose down -v
docker-compose up -d
```

**When you change backend code:**
```powershell
# Rebuild backend
docker-compose up -d --build backend
```

---

**🎯 Status: Backend Integration Complete!**

Your app now uses a real PostgreSQL database and REST API for doctor-patient communication! 🚀
