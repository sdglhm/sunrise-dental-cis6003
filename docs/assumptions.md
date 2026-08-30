# Assumptions

- Apache Tomcat 10.1 or later is used because the application uses the Jakarta Servlet namespace.
- Passwords will be stored as secure hashes, never plain text.
- A cancelled appointment releases its dentist date/time slot for a future active appointment.
- The consultation fee is currently a clearly named application configuration value of Rs. 1,000.00.
- Monetary values are represented with `BigDecimal` in Java and `DECIMAL(12,2)` in the database.
- This sibling demonstration project uses a local PostgreSQL 16 database. MySQL 8 remains the original assessment deployment target.
