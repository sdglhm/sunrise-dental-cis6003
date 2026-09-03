# Test data

Automated service tests construct their own users, patients, dentists, treatments, appointments, bills, and report results. Controller tests use JSON requests and mocked services. Test data is recreated for each test and does not write database files.

The appointment DAO test creates its own H2 in-memory schema and records, then discards them after the test run.

The PostgreSQL demonstration seed contains:

- Staff login `staff` / `staff123`, stored as a PBKDF2 hash
- Dr. A. Perera and Dr. S. Fernando
- Consultation at Rs. 1,500.00
- Cleaning at Rs. 2,500.00
- Filling at Rs. 3,500.00
- Consultation fee at Rs. 1,000.00

Production data and credentials must not be used in automated tests.
