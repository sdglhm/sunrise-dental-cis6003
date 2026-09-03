# Sunrise Dental Clinic

A Java web application for clinic staff to manage appointments, persistent bills, and operational reports through a browser interface and JSON API.

## Technology

- Java 17
- Maven WAR packaging
- Jakarta Servlet 6
- Apache Tomcat 10.1+
- PostgreSQL 16 with JDBC
- Jackson JSON
- JUnit 5 and Mockito
- Bootstrap 5.3.8 with vanilla HTML and JavaScript

## Prerequisites

- JDK 17
- Maven 3.9+
- PostgreSQL 16
- Apache Tomcat 10.1+

## Database setup

For a fresh database, run `database/schema.sql` followed by `database/seed.sql` in PostgreSQL.

For a database created before persistent bill numbers and database-backed consultation fees were added, run `database/migrations/001_add_persistent_billing.sql` once.

Copy `src/main/resources/database.properties.example` to `src/main/resources/database.properties` and provide local PostgreSQL credentials. The real properties file is ignored by Git.

The local demonstration account is:

- Username: `staff`
- Password: `staff123`

The seed stores a PBKDF2 hash, not the plain-text password. Change the account before using the application outside a demonstration environment.

## Run and build

```bash
mvn clean test
mvn clean package
```

Deploy `target/sunrise-dental.war` to Tomcat, then open the application context in a browser. This repository does not include local Tomcat deployment automation.

## Staff workflows

- Secure login, session checking, and logout
- Separate pages for appointments, patients, doctors and treatments, registration, reports, and help
- Patient directory, search, and contact-detail editing
- Catalog-backed appointment registration
- Appointment search, detail, editing, and cancellation
- Dentist double-booking protection
- Persistent, itemized bill generation and browser printing
- Report summary and daily, dentist, treatment, and revenue reports
- Contextual success, validation, empty, and loading states
- Staff help page

## API overview

| Method | Endpoint | Purpose |
| --- | --- | --- |
| POST | `/api/auth/login` | Start a staff session |
| POST | `/api/auth/logout` | Invalidate the session |
| GET | `/api/auth/session` | Return the current session |
| GET | `/api/catalog/dentists` | List active dentists |
| GET | `/api/catalog/treatments` | List active treatments |
| POST | `/api/catalog/dentists` | Add a dentist |
| POST | `/api/catalog/treatments` | Add a treatment |
| GET, POST | `/api/patients` | List or add patients |
| GET, PUT | `/api/patients/{id}` | View or update a patient |
| GET, POST | `/api/appointments` | List or create appointments |
| GET, PUT, DELETE | `/api/appointments/{number}` | View, update, or cancel an appointment |
| GET | `/api/appointments/{number}/bill-preview` | Calculate a preview |
| GET | `/api/appointments/{number}/bill` | Retrieve a generated bill |
| POST | `/api/appointments/{number}/bill` | Generate or retrieve a bill |
| GET | `/api/reports/summary` | Return clinic summary totals |
| GET | `/api/reports/daily` | Return a date-filtered daily report |
| GET | `/api/reports/dentists` | Return dentist appointment totals |
| GET | `/api/reports/treatments` | Return treatment totals |
| GET | `/api/reports/revenue` | Return billed revenue by date |

## Project structure

```text
database/                 PostgreSQL schema, seed, and migrations
docs/                     Requirements, architecture, testing, traceability, UML
src/main/java/            Controllers, filters, services, DAOs, DTOs, models, utilities
src/main/resources/       Database configuration template
src/main/webapp/          Staff interface, help, receipt, and static assets
src/test/java/            Service, controller, filter, and utility tests
.github/workflows/        Maven CI workflow
```

## Testing and CI

The tests use deterministic fakes, mocks, and an isolated H2 database for appointment DAO persistence. They do not connect to a production database. Run them with `mvn test`. The GitHub Actions workflow checks out the project, configures Temurin 17, runs `mvn clean test`, and packages the WAR. A hosted successful-run screenshot must come from the repository where the project is submitted; it is not fabricated here.

The development history does not prove strict test-first ordering for every feature. The testing documents describe the evidence that genuinely exists.

The browser interface uses the official Bootstrap CDN build and does not maintain a custom clinic stylesheet. A network connection is therefore required for Bootstrap styling unless the compiled framework files are hosted locally later.
