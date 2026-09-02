CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE patients (
    patient_id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact_number VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dentists (
    dentist_id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE treatments (
    treatment_id BIGSERIAL PRIMARY KEY,
    treatment_name VARCHAR(100) NOT NULL UNIQUE,
    price DECIMAL(12,2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_treatment_price CHECK (price >= 0)
);

CREATE TABLE appointments (
    appointment_id BIGSERIAL PRIMARY KEY,
    appointment_number VARCHAR(20) NOT NULL UNIQUE,
    patient_id BIGINT NOT NULL,
    dentist_id BIGINT NOT NULL,
    treatment_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patients(patient_id),
    CONSTRAINT fk_appointment_dentist FOREIGN KEY (dentist_id) REFERENCES dentists(dentist_id),
    CONSTRAINT fk_appointment_treatment FOREIGN KEY (treatment_id) REFERENCES treatments(treatment_id),
    CONSTRAINT chk_appointment_status CHECK (status IN ('ACTIVE', 'CANCELLED'))
);

CREATE INDEX idx_appointments_date ON appointments (appointment_date);
CREATE INDEX idx_appointments_dentist_date ON appointments (dentist_id, appointment_date);
CREATE UNIQUE INDEX uq_active_dentist_slot
    ON appointments (dentist_id, appointment_date, appointment_time)
    WHERE status = 'ACTIVE';

CREATE TABLE bills (
    bill_id BIGSERIAL PRIMARY KEY,
    bill_number VARCHAR(20) NOT NULL UNIQUE,
    appointment_id BIGINT NOT NULL UNIQUE,
    treatment_price DECIMAL(12,2) NOT NULL,
    consultation_fee DECIMAL(12,2) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bill_appointment FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id),
    CONSTRAINT chk_bill_amounts CHECK (treatment_price >= 0 AND consultation_fee >= 0 AND total_amount >= 0)
);
