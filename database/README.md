# Database Setup Guide

## PostgreSQL Docker Container

This project uses PostgreSQL as the database backend, running in a Docker container for easy setup and portability.

### Prerequisites
- Docker Desktop installed
- Docker Compose installed

### Quick Start

1. **Start the Database**
   ```bash
   docker-compose up -d
   ```

2. **Check Status**
   ```bash
   docker-compose ps
   ```

3. **View Logs**
   ```bash
   docker-compose logs -f postgres
   ```

4. **Stop the Database**
   ```bash
   docker-compose down
   ```

5. **Reset Database (Delete all data)**
   ```bash
   docker-compose down -v
   docker-compose up -d
   ```

### Database Connection Details

- **Host**: `localhost`
- **Port**: `5432`
- **Database**: `blood_diagnostics`
- **Username**: `aiblood_user`
- **Password**: `aiblood_pass123`

### Connection String
```
jdbc:postgresql://localhost:5432/blood_diagnostics?user=aiblood_user&password=aiblood_pass123
```

### Schema Overview

#### Tables:
1. **doctors** - Doctor profiles and credentials
2. **patients** - Patient profiles and medical information
3. **doctor_patient_connections** - Patient requests to connect with doctors
4. **test_uploads** - Uploaded lab test files (CBC, MSI, etc.)
5. **diagnoses** - AI-generated diagnoses and doctor reviews
6. **chat_messages** - Doctor-patient communication
7. **appointments** - Appointment bookings and payments

### Sample Data

The database is pre-populated with:
- 3 Sample Doctors
- 3 Sample Patients

#### Test Accounts:

**Doctors:**
- doctor@hospital.com / doctor123
- mark@hospital.com / doctor123
- moataz@hospital.com / doctor123

**Patients:**
- patient@test.com / patient123
- sarah@patient.com / patient123
- ahmed@patient.com / patient123

### Manual Database Access

Connect using psql:
```bash
docker exec -it aiblooddiagnostics_db psql -U aiblood_user -d blood_diagnostics
```

Common commands:
```sql
-- List all tables
\dt

-- Describe table structure
\d doctors

-- View all doctors
SELECT * FROM doctors;

-- View all patients
SELECT * FROM patients;

-- View connections
SELECT * FROM doctor_patient_connections;
```

### Backup and Restore

**Backup:**
```bash
docker exec aiblooddiagnostics_db pg_dump -U aiblood_user blood_diagnostics > backup.sql
```

**Restore:**
```bash
docker exec -i aiblooddiagnostics_db psql -U aiblood_user blood_diagnostics < backup.sql
```

### Troubleshooting

**Port already in use:**
```bash
# Change port in docker-compose.yml from "5432:5432" to "5433:5432"
# Then use localhost:5433 to connect
```

**Cannot connect:**
```bash
# Check if container is running
docker ps

# Check logs for errors
docker logs aiblooddiagnostics_db

# Restart container
docker-compose restart
```

### Production Deployment

For production, update these in `docker-compose.yml`:
1. Change `POSTGRES_PASSWORD` to a strong password
2. Enable SSL connections
3. Configure backups
4. Set up proper volume management
