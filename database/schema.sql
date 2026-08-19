CREATE DATABASE IF NOT EXISTS sunrise_dental;
USE sunrise_dental;

CREATE TABLE users (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE patients (
    patient_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact_number VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dentists (
    dentist_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE treatments (
    treatment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    treatment_name VARCHAR(100) NOT NULL UNIQUE,
    price DECIMAL(12,2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_treatment_price CHECK (price >= 0)
);

CREATE TABLE appointments (
    appointment_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    appointment_number VARCHAR(20) NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    dentist_id BIGINT NOT NULL,
    treatment_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status ENUM('ACTIVE', 'CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    CONSTRAINT fk_appointment_dentist FOREIGN KEY (dentist_id) REFERENCES dentists(dentist_id),
    CONSTRAINT fk_appointment_treatment FOREIGN KEY (treatment_id) REFERENCES treatments(treatment_id),
    INDEX idx_appointments_date (appointment_date),
    INDEX idx_appointments_dentist_date (dentist_id, appointment_date)
);

-- MySQL cannot use a partial unique index. The generated column is NULL for
-- cancelled appointments, allowing the time slot to be booked again.
ALTER TABLE appointments
    ADD COLUMN active_dentist_id BIGINT GENERATED ALWAYS AS
        (CASE WHEN status = 'ACTIVE' THEN dentist_id ELSE NULL END) STORED,
    ADD COLUMN active_appointment_date DATE GENERATED ALWAYS AS
        (CASE WHEN status = 'ACTIVE' THEN appointment_date ELSE NULL END) STORED,
    ADD COLUMN active_appointment_time TIME GENERATED ALWAYS AS
        (CASE WHEN status = 'ACTIVE' THEN appointment_time ELSE NULL END) STORED,
    ADD CONSTRAINT uq_active_dentist_slot
        UNIQUE (active_dentist_id, active_appointment_date, active_appointment_time);

CREATE TABLE bills (
    bill_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    appointment_id BIGINT NOT NULL UNIQUE,
    treatment_price DECIMAL(12,2) NOT NULL,
    consultation_fee DECIMAL(12,2) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bill_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id),
    CONSTRAINT chk_bill_amounts CHECK (treatment_price >= 0 AND consultation_fee >= 0 AND total_amount >= 0)
);
