# Assumptions

- Apache Tomcat 10.1 or later is used because the application uses the Jakarta Servlet namespace.
- Passwords will be stored as secure hashes, never plain text.
- A cancelled appointment releases its dentist date/time slot for a future active appointment.
- The consultation fee will be configured from a database-backed source in the billing milestone.
- Monetary values are represented with `BigDecimal` in Java and `DECIMAL(12,2)` in MySQL.
