# Test data

`database/seed.sql` contains two dentists, three treatments and a demonstration staff account (`staff` / `staff123`) for local development only. Its password is stored as a PBKDF2 hash. Appointments entered through the staff page create the associated patient records.

The appointment DAO test creates its own in-memory dentists, treatments, patient and appointment data. It does not use local PostgreSQL or production data.
