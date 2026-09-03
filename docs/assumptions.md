# Assumptions

- Clinic staff is the only implemented role. Every authenticated staff user has access to appointment, billing, and report functions.
- Apache Tomcat 10.1+ is used because the application uses Jakarta Servlet 6.
- PostgreSQL 8 is the persistent database for this implementation.
- Passwords are stored as salted PBKDF2 hashes and invalid or inactive accounts receive the same login error.
- Patient contact numbers may contain digits, spaces, one leading plus sign, and hyphens within the supported length.
- Appointment dates cannot be in the past. Clinic working hours are not restricted beyond a valid time value.
- One active appointment is allowed for a dentist at a specific date and time. Service validation gives a clear message and the database constraint protects concurrent writes.
- Cancelling changes the status to `CANCELLED`, preserves the record, and releases the active dentist slot.
- Treatment prices and the consultation fee come from the database. Seed data sets the consultation fee to Rs. 1,000.00.
- One bill is stored per appointment. Repeating bill generation returns the existing bill rather than adding a duplicate.
- A cancelled appointment cannot receive a new bill, but a bill generated before cancellation remains retrievable.
- Browser printing is the supported receipt-printing mechanism.
