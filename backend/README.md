# Backend API Setup Guide

## 🚀 Quick Start

### Prerequisites
- Docker Desktop installed and running
- JDK 17+ (for local development)

### Start Everything
```bash
# From project root (e:\nabil)
docker-compose up -d --build
```

This will start:
1. **PostgreSQL Database** on port `5432`
2. **Ktor Backend API** on port `8080`

### Verify Services

**Check all services are running:**
```bash
docker-compose ps
```

**Test API:**
```bash
curl http://localhost:8080/health
```

Expected response:
```json
{
  "status": "healthy",
  "service": "AI Blood Diagnostics API"
}
```

**Test Database Connection:**
```bash
docker exec -it aiblooddiagnostics_db psql -U aiblood_user -d blood_diagnostics -c "SELECT COUNT(*) FROM doctors;"
```

---

## 📡 API Endpoints

### Base URL
- Local: `http://localhost:8080`
- Android Emulator: `http://10.0.2.2:8080`
- Physical Device: `http://YOUR_IP:8080` (e.g., `http://192.168.1.100:8080`)

### Authentication

**POST `/api/auth/login`**
```json
Request:
{
  "email": "doctor@hospital.com",
  "password": "doctor123"
}

Response:
{
  "success": true,
  "message": "Login successful",
  "userId": "doctor_1",
  "userType": "doctor",
  "fullName": "Dr. Amira Mohamed",
  "email": "doctor@hospital.com"
}
```

**POST `/api/auth/signup`**
```json
Request (Doctor):
{
  "fullName": "Dr. John Smith",
  "email": "john@hospital.com",
  "password": "password123",
  "userType": "doctor",
  "mobileNumber": "+201234567890",
  "specialization": "Hematologist",
  "experienceYears": 10
}

Request (Patient):
{
  "fullName": "Jane Doe",
  "email": "jane@patient.com",
  "password": "password123",
  "userType": "patient",
  "mobileNumber": "+201098765432",
  "dateOfBirth": "1990-05-15",
  "gender": "Female",
  "bloodType": "O+"
}

Response:
{
  "success": true,
  "message": "Signup successful"
}
```

### Doctors

**GET `/api/doctors`** - Get all doctors
**GET `/api/doctors/{id}`** - Get specific doctor

### Connections

**GET `/api/connections?userId={userId}&userType={type}`** - Get user connections
**POST `/api/connections`** - Request doctor-patient connection
**PATCH `/api/connections/{id}`** - Approve/reject connection

### Test Uploads

**GET `/api/uploads?patientId={id}`** - Get patient uploads
**POST `/api/uploads`** - Upload new test file

### Chat

**GET `/api/chat?userId={id}&userType={type}`** - Get messages
**POST `/api/chat`** - Send message

### Diagnosis

**GET `/api/diagnosis?patientId={id}`** - Get patient diagnoses

---

## 🔧 Development

### Backend Structure
```
backend/
├── src/main/kotlin/com/aiblooddiagnostics/
│   ├── Application.kt              # Main entry point
│   ├── plugins/
│   │   ├── Database.kt             # PostgreSQL connection
│   │   ├── Routing.kt              # Route configuration
│   │   ├── Serialization.kt        # JSON handling
│   │   └── Security.kt             # CORS configuration
│   └── routes/
│       ├── AuthRoutes.kt           # Login/Signup
│       ├── DoctorRoutes.kt         # Doctor endpoints
│       └── PatientRoutes.kt        # Patient endpoints
├── build.gradle.kts                # Dependencies
└── Dockerfile                      # Docker build config
```

### Local Development (Without Docker)

**Run PostgreSQL in Docker:**
```bash
docker-compose up postgres -d
```

**Run Backend Locally:**
```bash
cd backend
./gradlew run
```

### View Logs

**All services:**
```bash
docker-compose logs -f
```

**Backend only:**
```bash
docker-compose logs -f backend
```

**Database only:**
```bash
docker-compose logs -f postgres
```

---

## 🔄 Android App Integration

### Network Configuration

The Android app uses Retrofit to communicate with the API.

**Emulator:** Uses `http://10.0.2.2:8080`
**Physical Device:** Update `NetworkModule.kt` with your IP:
```kotlin
val baseUrl = "http://192.168.1.100:8080/" // Replace with your IP
```

### Find Your IP Address

**Windows:**
```powershell
ipconfig
# Look for "IPv4 Address" under your network adapter
```

**macOS/Linux:**
```bash
ifconfig | grep inet
```

---

## 🛠️ Troubleshooting

### Port Already in Use

**Change Backend Port:**
Edit `docker-compose.yml`:
```yaml
backend:
  ports:
    - "8081:8080"  # Change 8080 to 8081
```

Then update Android app's `NetworkModule.kt`:
```kotlin
val baseUrl = "http://10.0.2.2:8081/"
```

### Backend Won't Start

**Check logs:**
```bash
docker-compose logs backend
```

**Rebuild:**
```bash
docker-compose down
docker-compose up --build -d
```

### Database Connection Failed

**Verify database is healthy:**
```bash
docker-compose ps
```

**Reset database:**
```bash
docker-compose down -v
docker-compose up -d
```

### Android App Can't Connect

1. **Verify API is running:**
   ```bash
   curl http://localhost:8080/health
   ```

2. **Check Android internet permission** in `AndroidManifest.xml`

3. **For physical device:** Use your computer's IP address, not `localhost`

4. **Check firewall:** Allow port 8080

---

## 📊 Database Access

**Connect with psql:**
```bash
docker exec -it aiblooddiagnostics_db psql -U aiblood_user -d blood_diagnostics
```

**Common queries:**
```sql
-- List all doctors
SELECT * FROM doctors;

-- List all patients
SELECT * FROM patients;

-- View connection requests
SELECT * FROM doctor_patient_connections;

-- View test uploads
SELECT * FROM test_uploads;
```

---

## 🚀 Deployment

### Production Considerations

1. **Environment Variables:**
   - Store passwords in `.env` file
   - Never commit secrets to git

2. **Security:**
   - Enable HTTPS/SSL
   - Implement JWT authentication
   - Hash passwords with bcrypt

3. **Scaling:**
   - Use cloud PostgreSQL (AWS RDS, Google Cloud SQL)
   - Deploy backend to cloud (Heroku, AWS, Google Cloud)
   - Add Redis for caching

4. **Monitoring:**
   - Add logging and monitoring
   - Set up error tracking (Sentry)
   - Monitor API performance

---

## 📝 Notes

- **Password Security:** Currently passwords are stored in plain text for development. **MUST** implement bcrypt hashing for production.
- **File Storage:** Test files will be stored on server. Consider cloud storage (AWS S3) for production.
- **Authentication:** Add JWT tokens for secure API access.
- **Rate Limiting:** Implement rate limiting to prevent abuse.

---

## 🔗 Useful Commands

**Start services:**
```bash
docker-compose up -d
```

**Stop services:**
```bash
docker-compose down
```

**Rebuild everything:**
```bash
docker-compose down -v
docker-compose up --build -d
```

**View all containers:**
```bash
docker ps -a
```

**Clean up:**
```bash
docker system prune -a
```
