# Architecture

## Three tiers

The presentation tier contains the HTML, CSS, JavaScript, authentication filters, and Servlet HTTP entry points. Browser code renders data and sends JSON requests but does not execute SQL or decide booking and billing rules.

The application tier contains `AuthenticationService`, `AppointmentService`, `BillingService`, and `ReportService`. These classes enforce credentials, active catalog selections, dates, contact numbers, dentist availability, cancellation rules, billing calculations, and report input validation.

The data tier contains DAO interfaces and JDBC implementations. SQL is kept in JDBC DAO classes, and `DatabaseConnectionFactory` centralizes the connection settings.

## Patterns

### MVC

Domain records and DTOs form the model, HTML/JavaScript pages form the view, and Servlets translate HTTP requests into service calls. This keeps HTTP concerns separate from rules and persistence. The trade-off is manual request routing because the project deliberately avoids a larger web framework.

### DAO

DAO interfaces isolate JDBC and make service tests independent of PostgreSQL. JDBC implementations return typed records and DTOs. The additional interfaces add files, but they keep SQL replaceable and test boundaries clear.

### Service Layer

Services coordinate DAOs and own reusable rules. Servlets remain thin and can be tested with injected service instances. This adds one call layer but prevents rules from being duplicated in the UI and controllers.

### Connection Factory

One connection factory reads the local database properties and creates connections. This prevents URL and credential duplication. The current factory opens a connection per DAO operation rather than using a pool, which is acceptable for the assessment scale but would be reconsidered for higher traffic.

## Database and web-service choices

PostgreSQL provides foreign keys, unique constraints, transactions, indexes, and the partial unique index used to protect active dentist slots. Prices and totals use `DECIMAL(12,2)` in SQL and `BigDecimal` in Java.

The JSON API gives the browser a clear client-server boundary and consistent status codes. The application returns 409 for booking conflicts, 404 for missing records, 401 for unauthenticated requests, 400/422 for invalid input, and controlled 500 responses for unexpected failures.

The main trade-off is that deployment and full PostgreSQL integration require an external PostgreSQL and Tomcat environment. Automated tests therefore focus on deterministic service and controller behavior, with one isolated H2 appointment DAO test, without claiming live PostgreSQL coverage.
