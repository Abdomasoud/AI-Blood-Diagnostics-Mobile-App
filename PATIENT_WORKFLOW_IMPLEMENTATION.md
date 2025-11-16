# Patient Test Upload & Doctor Connection Workflow

## Overview
This document describes the complete implementation of the patient diagnostic file upload and doctor connection request feature in the AI Blood Diagnostics app.

## Feature Flow

### 1. Patient Uploads Test Results
- Patient navigates to Patient Dashboard
- Clicks "Upload Test" button
- Selects test type: **CBC**, **MSI**, or **Both**
- Chooses file from device (gallery/files)
- Optionally adds notes
- Uploads file to backend server

### 2. File Storage
Files are stored on the backend server in the `uploads/` directory with the following structure:
- **Original filename** is preserved in database
- **Unique filename** with UUID prefix is generated for storage
- **File metadata** stored in `test_uploads` table:
  - `patient_id` - Reference to patient
  - `test_type` - CBC, MSI, or Both
  - `file_type` - Document or Image (auto-detected)
  - `file_name` - Original filename
  - `file_path` - Server path to file
  - `file_size` - File size in bytes
  - `upload_date` - Timestamp
  - `status` - pending, analyzed, completed
  - `notes` - Optional patient notes

### 3. Doctor Selection
- After successful upload, patient is prompted to select a doctor
- System displays list of all available doctors with:
  - Full name
  - Specialization (Hematologist)
  - Years of experience
  - Rating (0.0 - 5.0)
  - Bio/description
- Patient selects a doctor and optionally adds a message

### 4. Connection Request
- System creates connection request in `doctor_patient_connections` table:
  - `patient_id` - Reference to patient
  - `doctor_id` - Reference to selected doctor
  - `status` - **pending** (later: approved/rejected)
  - `request_date` - Timestamp
  - `notes` - Optional patient message
- Connection links to the uploaded test (implicitly via patient_id)

## Backend API Endpoints

### Doctor Endpoints

#### GET `/api/doctors`
Returns list of all doctors ordered by rating (descending).

**Response:**
```json
{
  "success": true,
  "doctors": [
    {
      "id": 1,
      "userId": "doctor_1",
      "fullName": "Dr. Amira Mohamed",
      "email": "doctor@hospital.com",
      "specialization": "Hematologist",
      "experienceYears": 15,
      "rating": 4.9,
      "bio": "Specialized in blood disorders and diagnostics"
    }
  ]
}
```

#### GET `/api/doctors/{id}`
Returns a single doctor by ID or user_id.

### File Upload Endpoint

#### POST `/api/uploads`
Uploads a test file (multipart/form-data).

**Request:**
- `patientUserId` - Patient's user ID
- `testType` - CBC, MSI, or Both
- `notes` - Optional notes (nullable)
- `file` - Multipart file data

**Response:**
```json
{
  "success": true,
  "message": "File uploaded successfully",
  "uploadId": 1,
  "fileName": "unique_file_name.pdf"
}
```

#### GET `/api/uploads/patient/{userId}`
Returns all uploads for a specific patient.

**Response:**
```json
{
  "success": true,
  "uploads": [
    {
      "id": 1,
      "patientId": 1,
      "testType": "CBC",
      "fileType": "Document",
      "fileName": "blood_test.pdf",
      "filePath": "/app/uploads/uuid_blood_test.pdf",
      "fileSize": 1024000,
      "uploadDate": "2025-11-16 01:20:30",
      "status": "pending",
      "notes": "Recent blood work"
    }
  ]
}
```

### Connection Request Endpoints

#### POST `/api/connections`
Creates a connection request between patient and doctor.

**Request:**
```json
{
  "patientUserId": "patient_123",
  "doctorUserId": "doctor_1",
  "testUploadId": 1,
  "notes": "I need help understanding my CBC results"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Connection request sent successfully",
  "connectionId": 1
}
```

#### GET `/api/connections/patient/{userId}`
Returns all connection requests for a patient.

**Response:**
```json
{
  "success": true,
  "connections": [
    {
      "id": 1,
      "patientId": 1,
      "patientName": "Mohamed Ali",
      "doctorId": 1,
      "doctorName": "Dr. Amira Mohamed",
      "status": "pending",
      "requestDate": "2025-11-16 01:25:00",
      "notes": "I need help understanding my CBC results"
    }
  ]
}
```

## Android App Implementation

### New Screens

#### 1. UploadTestScreen.kt
Located: `app/src/main/java/com/aiblooddiagnostics/ui/screens/patient/UploadTestScreen.kt`

**Features:**
- Test type selection (CBC/MSI/Both) using filter chips
- File picker integration using ActivityResultContract
- File preview with name display
- Optional notes text field
- Upload progress indicator
- Success dialog with auto-navigation to doctor selection
- Error handling and display

#### 2. SelectDoctorScreen.kt
Located: `app/src/main/java/com/aiblooddiagnostics/ui/screens/patient/SelectDoctorScreen.kt`

**Features:**
- Scrollable list of all doctors
- Doctor cards showing:
  - Avatar (placeholder)
  - Full name
  - Specialization badge
  - Experience years with icon
  - Rating with star icon
  - Bio (truncated to 2 lines)
- Tap to select with confirmation dialog
- Optional message field in confirmation
- Loading state while fetching doctors
- Empty state when no doctors available

### New ViewModel

#### PatientViewModel.kt
Located: `app/src/main/java/com/aiblooddiagnostics/ui/viewmodel/PatientViewModel.kt`

**Responsibilities:**
- Load list of doctors from API
- Upload test files with URI to File conversion
- Create connection requests
- Manage loading states
- Handle success/error callbacks

**Key Methods:**
- `loadDoctors()` - Fetch all doctors from backend
- `uploadTestFile(Uri, testType, notes, callback)` - Upload file to server
- `requestDoctorConnection(doctorUserId, notes, callback)` - Create connection
- `loadTestUploads()` - Get patient's uploaded files
- `loadConnections()` - Get patient's connection requests

### Repository Updates

Added to `BloodDiagnosticsRepository.kt`:
- `getDoctorsFromApi()` - Fetch doctor list
- `uploadTestFile(patientUserId, testType, file, notes)` - Upload with multipart
- `getPatientUploads(userId)` - Get patient's uploads
- `createConnectionRequest(patientUserId, doctorUserId, testUploadId, notes)` - Request connection
- `getPatientConnections(userId)` - Get patient's connections

### API Interface Updates

Updated `BloodDiagnosticsApi.kt`:
```kotlin
@GET("api/doctors")
suspend fun getAllDoctors(): Response<DoctorListResponse>

@Multipart
@POST("api/uploads")
suspend fun uploadTest(
    @Part("patientUserId") patientUserId: RequestBody,
    @Part("testType") testType: RequestBody,
    @Part("notes") notes: RequestBody?,
    @Part file: MultipartBody.Part
): Response<TestUploadResponse>

@POST("api/connections")
suspend fun requestConnection(@Body request: ConnectionRequest): Response<ConnectionResponse>

@GET("api/connections/patient/{userId}")
suspend fun getPatientConnections(@Path("userId") userId: String): Response<PatientConnectionsResponse>

@GET("api/uploads/patient/{userId}")
suspend fun getPatientUploads(@Path("userId") userId: String): Response<TestUploadsResponse>
```

### Navigation Updates

Added routes to `Navigation.kt`:
- `upload_test` - Upload test screen
- `select_doctor` - Doctor selection screen

### Dashboard Updates

Updated `PatientDashboardScreen.kt`:
- Changed "New Request" button to "Upload Test"
- Button navigates to `upload_test` route
- Now shows actual logged-in user's name and ID from session

## Database Schema

### Tables Used

#### test_uploads
```sql
CREATE TABLE IF NOT EXISTS test_uploads (
    id SERIAL PRIMARY KEY,
    patient_id INTEGER NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    test_type VARCHAR(50) NOT NULL, -- CBC, MSI, Both
    file_type VARCHAR(20) NOT NULL, -- Document, Image
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'pending', -- pending, analyzed, completed
    notes TEXT
);
```

#### doctor_patient_connections
```sql
CREATE TABLE IF NOT EXISTS doctor_patient_connections (
    id SERIAL PRIMARY KEY,
    patient_id INTEGER NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id INTEGER NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'pending', -- pending, approved, rejected
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approval_date TIMESTAMP,
    notes TEXT,
    UNIQUE(patient_id, doctor_id)
);
```

#### doctors
Contains:
- Basic info: user_id, full_name, email, password_hash
- Professional: specialization, experience_years, rating, bio
- Contact: mobile_number
- Timestamps: created_at, updated_at

#### patients
Contains:
- Basic info: user_id, full_name, email, password_hash
- Medical: date_of_birth, gender, blood_type, medical_history
- Contact: mobile_number
- Timestamps: created_at, updated_at

## Sample Doctors in Database

1. **Dr. Amira Mohamed** (doctor@hospital.com)
   - Specialization: Hematologist
   - Experience: 15 years
   - Rating: 4.9
   - Bio: "Specialized in blood disorders and diagnostics"

2. **Dr. Mark Philopateer** (mark@hospital.com)
   - Specialization: Hematologist
   - Experience: 10 years
   - Rating: 4.7
   - Bio: "Expert in CBC analysis"

3. **Dr. Moataz Bahaa** (moataz@hospital.com)
   - Specialization: Hematologist
   - Experience: 12 years
   - Rating: 4.8
   - Bio: "Specialist in blood cancer diagnostics"

**Password for all doctors:** `doctor123`

## Testing the Feature

### Prerequisites
1. Backend Docker containers running (`docker-compose up -d`)
2. Database initialized with sample doctors
3. Android app connected to backend (10.0.2.2:8080 for emulator)
4. Patient logged in

### Test Steps

1. **Login as Patient**
   - Email: mohamed.ali@gmail.com
   - Password: patient123

2. **Upload Test**
   - Click "Upload Test" button on dashboard
   - Select test type (e.g., CBC)
   - Choose a PDF or image file from device
   - Add optional notes
   - Click "Upload & Continue"
   - Verify success dialog appears

3. **Select Doctor**
   - After upload, doctor selection screen appears
   - Browse list of 3 doctors
   - Tap on a doctor card
   - Add optional message in dialog
   - Click "Send Request"

4. **Verify in Database**
   ```sql
   -- Check uploaded file
   SELECT * FROM test_uploads WHERE patient_id = (SELECT id FROM patients WHERE email = 'mohamed.ali@gmail.com');
   
   -- Check connection request
   SELECT * FROM doctor_patient_connections WHERE patient_id = (SELECT id FROM patients WHERE email = 'mohamed.ali@gmail.com');
   ```

### Expected Results
- File saved in backend `uploads/` directory
- Entry in `test_uploads` table with status='pending'
- Entry in `doctor_patient_connections` table with status='pending'
- Patient returns to dashboard after successful connection request

## Future Enhancements

1. **Doctor Approval Workflow**
   - Doctor dashboard shows pending connection requests
   - Doctor can approve/reject requests
   - Update connection status in database

2. **AI Analysis Integration**
   - Trigger AI analysis on file upload
   - Store AI results in `diagnoses` table
   - Link diagnosis to test upload

3. **Notifications**
   - Notify doctor when connection request received
   - Notify patient when request approved/rejected
   - Notify patient when AI analysis complete

4. **File Download**
   - Doctor can download patient's uploaded files
   - Endpoint: GET `/api/uploads/{id}/download`

5. **Multiple File Upload**
   - Allow patient to upload multiple files for same test
   - Gallery view of all uploaded files

6. **Test History**
   - Patient can view all previous uploads
   - Track analysis status for each upload
   - View doctor's feedback on each test

## Security Considerations

1. **File Validation**
   - Verify file type (PDF, images only)
   - Limit file size (currently no limit)
   - Scan for malware

2. **Access Control**
   - Patient can only upload for their own account
   - Doctor can only view files from connected patients
   - Validate user ID in session matches request

3. **File Storage**
   - Store files outside web root
   - Use unique filenames to prevent overwriting
   - Implement file retention policy

4. **Connection Validation**
   - Prevent duplicate connection requests
   - Validate doctor exists before creating connection
   - Check patient hasn't exceeded connection limit

## Troubleshooting

### File Upload Fails
- Check backend `/uploads` directory exists and is writable
- Verify multipart encoding in request
- Check file size limits in server configuration
- Review backend logs for errors

### Doctor List Empty
- Verify database has doctors (SELECT * FROM doctors)
- Check network connectivity to backend
- Verify API endpoint returns 200 status
- Check for JSON parsing errors in Android logs

### Connection Request Fails
- Verify patient and doctor IDs are valid
- Check for existing connection (UNIQUE constraint)
- Review backend logs for database errors
- Verify request body JSON format

## Code Locations

### Backend
- `backend/src/main/kotlin/com/aiblooddiagnostics/routes/DoctorRoutes.kt` - Doctor endpoints
- `backend/src/main/kotlin/com/aiblooddiagnostics/routes/PatientRoutes.kt` - Upload & connection endpoints
- `database/init/01-schema.sql` - Database schema

### Android
- `app/src/main/java/com/aiblooddiagnostics/ui/screens/patient/UploadTestScreen.kt` - Upload UI
- `app/src/main/java/com/aiblooddiagnostics/ui/screens/patient/SelectDoctorScreen.kt` - Doctor selection UI
- `app/src/main/java/com/aiblooddiagnostics/ui/viewmodel/PatientViewModel.kt` - Business logic
- `app/src/main/java/com/aiblooddiagnostics/data/repository/BloodDiagnosticsRepository.kt` - API calls
- `app/src/main/java/com/aiblooddiagnostics/data/api/BloodDiagnosticsApi.kt` - Retrofit interface
- `app/src/main/java/com/aiblooddiagnostics/data/api/models/ApiModels.kt` - Request/response models
- `app/src/main/java/com/aiblooddiagnostics/ui/navigation/Navigation.kt` - Navigation routes
