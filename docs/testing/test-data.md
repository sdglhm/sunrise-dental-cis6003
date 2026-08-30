# Test data

`database/seed.sql` contains two dentists, three treatments and a demonstration staff account (`staff` / `staff123`) for local development only. Its password is stored as a PBKDF2 hash. Appointments entered through the staff page create the associated patient records.

Future isolated DAO integration tests will use explicitly created test users, patients, dentists, treatments and appointments. Production data must not be used during automated tests.
