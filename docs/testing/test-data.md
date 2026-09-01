# Test data

`database/seed.sql` contains two dentists, three treatments and a demonstration staff account (`staff` / `staff123`) for local development only. Its password is stored as a PBKDF2 hash. Staff can add patients directly or select an existing patient while registering an appointment.

The appointment DAO test creates its own in-memory dentists, treatments, patient and appointment data. It does not use local PostgreSQL or production data.
