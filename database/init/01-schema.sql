-- AI Blood Diagnostics Database Schema
-- PostgreSQL Initialization Script

-- Create Doctors Table
CREATE TABLE IF NOT EXISTS doctors (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(20),
    specialization VARCHAR(100) DEFAULT 'Hematologist',
    experience_years INTEGER DEFAULT 0,
    rating DECIMAL(3,2) DEFAULT 0.00,
    bio TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Patients Table
CREATE TABLE IF NOT EXISTS patients (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(20),
    date_of_birth DATE,
    gender VARCHAR(10),
    blood_type VARCHAR(5),
    medical_history TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Test Uploads Table (moved before connections table due to FK dependency)
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

-- Create Doctor-Patient Relationship Table (Connection Requests)
CREATE TABLE IF NOT EXISTS doctor_patient_connections (
    id SERIAL PRIMARY KEY,
    patient_id INTEGER NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id INTEGER NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    test_upload_id INTEGER REFERENCES test_uploads(id) ON DELETE SET NULL,
    status VARCHAR(20) DEFAULT 'pending', -- pending, approved, rejected
    request_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approval_date TIMESTAMP,
    notes TEXT,
    UNIQUE(patient_id, doctor_id)
);

-- Create Chat Rooms Table
CREATE TABLE IF NOT EXISTS chat_rooms (
    id SERIAL PRIMARY KEY,
    room_id VARCHAR(100) UNIQUE NOT NULL, -- format: doctor_{doctorId}_patient_{patientId}
    doctor_id INTEGER NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    patient_id INTEGER NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    connection_id INTEGER REFERENCES doctor_patient_connections(id) ON DELETE SET NULL,
    last_message TEXT,
    last_message_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(doctor_id, patient_id)
);

-- Create Diagnoses Table
CREATE TABLE IF NOT EXISTS diagnoses (
    id SERIAL PRIMARY KEY,
    patient_id INTEGER NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id INTEGER NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    test_upload_id INTEGER REFERENCES test_uploads(id) ON DELETE SET NULL,
    diagnosis_type VARCHAR(50) NOT NULL,
    result TEXT NOT NULL,
    ai_confidence DECIMAL(5,2),
    recommendations TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Chat Messages Table
CREATE TABLE IF NOT EXISTS chat_messages (
    id SERIAL PRIMARY KEY,
    room_id VARCHAR(100) NOT NULL,
    sender_id INTEGER NOT NULL,
    sender_type VARCHAR(10) NOT NULL, -- doctor, patient
    receiver_id INTEGER NOT NULL,
    receiver_type VARCHAR(10) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES chat_rooms(room_id) ON DELETE CASCADE
);

-- Create Appointments Table
CREATE TABLE IF NOT EXISTS appointments (
    id SERIAL PRIMARY KEY,
    patient_id INTEGER NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    doctor_id INTEGER NOT NULL REFERENCES doctors(id) ON DELETE CASCADE,
    appointment_date TIMESTAMP NOT NULL,
    duration_minutes INTEGER DEFAULT 30,
    status VARCHAR(20) DEFAULT 'scheduled', -- scheduled, completed, cancelled
    amount DECIMAL(10,2),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Indexes for Performance
CREATE INDEX idx_doctors_email ON doctors(email);
CREATE INDEX idx_patients_email ON patients(email);
CREATE INDEX idx_connections_patient ON doctor_patient_connections(patient_id);
CREATE INDEX idx_connections_doctor ON doctor_patient_connections(doctor_id);
CREATE INDEX idx_connections_status ON doctor_patient_connections(status);
CREATE INDEX idx_test_uploads_patient ON test_uploads(patient_id);
CREATE INDEX idx_diagnoses_patient ON diagnoses(patient_id);
CREATE INDEX idx_diagnoses_doctor ON diagnoses(doctor_id);
CREATE INDEX idx_chat_rooms_doctor ON chat_rooms(doctor_id);
CREATE INDEX idx_chat_rooms_patient ON chat_rooms(patient_id);
CREATE INDEX idx_chat_messages_room ON chat_messages(room_id);
CREATE INDEX idx_chat_messages_sender ON chat_messages(sender_id, sender_type);
CREATE INDEX idx_chat_messages_receiver ON chat_messages(receiver_id, receiver_type);

-- Insert Sample Doctors
-- Passwords: doctor123 for all doctors
INSERT INTO doctors (user_id, full_name, email, password_hash, mobile_number, specialization, experience_years, rating, bio)
VALUES 
    ('doctor_1', 'Dr. Amira Mohamed', 'doctor@hospital.com', '$2a$10$hFj.VhWRnUbGMwCg6ChLv.s3A5fdj2/RGYZXpyvTUi5VhVc1z7.pe', '+201234567890', 'Hematologist', 15, 4.9, 'Specialized in blood disorders and diagnostics'),
    ('doctor_2', 'Dr. Mark Philopateer', 'mark@hospital.com', '$2a$10$hFj.VhWRnUbGMwCg6ChLv.s3A5fdj2/RGYZXpyvTUi5VhVc1z7.pe', '+201234567891', 'Hematologist', 10, 4.7, 'Expert in CBC analysis'),
    ('doctor_3', 'Dr. Moataz Bahaa', 'moataz@hospital.com', '$2a$10$hFj.VhWRnUbGMwCg6ChLv.s3A5fdj2/RGYZXpyvTUi5VhVc1z7.pe', '+201234567892', 'Hematologist', 12, 4.8, 'Specialist in blood cancer diagnostics');

-- Insert Sample Patients
-- Passwords: patient123 for all patients
INSERT INTO patients (user_id, full_name, email, password_hash, mobile_number, date_of_birth, gender, blood_type)
VALUES 
    ('patient_1', 'Ahmed Ali', 'patient@test.com', '$2a$10$RaKgB1.wJj8h00zgLnBwCufkbRzQPQVPbU8XNKsrBkQygW9zi7kNO', '+201098765432', '1990-05-15', 'Male', 'O+'),
    ('patient_2', 'Sarah Mohamed', 'sarah@patient.com', '$2a$10$RaKgB1.wJj8h00zgLnBwCufkbRzQPQVPbU8XNKsrBkQygW9zi7kNO', '+201098765433', '1985-08-22', 'Female', 'A+'),
    ('patient_3', 'John Smith', 'ahmed@patient.com', '$2a$10$RaKgB1.wJj8h00zgLnBwCufkbRzQPQVPbU8XNKsrBkQygW9zi7kNO', '+201098765434', '1992-03-10', 'Male', 'B+');

COMMENT ON TABLE doctors IS 'Stores doctor user information and profiles';
COMMENT ON TABLE patients IS 'Stores patient user information and medical data';
COMMENT ON TABLE doctor_patient_connections IS 'Manages patient requests to connect with doctors';
COMMENT ON TABLE chat_rooms IS 'Stores chat rooms between doctors and patients';
COMMENT ON TABLE test_uploads IS 'Stores uploaded lab test files and metadata';
COMMENT ON TABLE diagnoses IS 'Stores AI-generated diagnoses and doctor reviews';
COMMENT ON TABLE chat_messages IS 'Stores chat messages between doctors and patients';
COMMENT ON TABLE appointments IS 'Stores appointment bookings and payment information';
