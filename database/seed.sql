USE sunrise_dental;

-- Demonstration login: username staff, password staff123. Change this after setup.
-- The stored value is a PBKDF2 hash, never a plain-text password.
INSERT INTO users (username, password_hash, full_name) VALUES
('staff', 'PBKDF2$120000$FXIpsTxJtyYFugRnvk5t6g==$6Z9IWzDwC3bUf76pSRigJByGD0x5vszjE5fN99xwJDc=', 'Clinic Staff');

INSERT INTO dentists (full_name) VALUES ('Dr. A. Perera'), ('Dr. S. Fernando');
INSERT INTO treatments (treatment_name, price) VALUES ('Consultation', 1500.00), ('Cleaning', 2500.00), ('Filling', 3500.00);
