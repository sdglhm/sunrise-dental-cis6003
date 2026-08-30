# Requirements

The system supports authorised staff login, appointment registration and management, conflict prevention, billing, reports, help and secure logout. The full agreed requirement list is maintained in the project brief and will be traced to implementation as milestones are completed.

## Implemented API

| Method | Endpoint | Purpose |
| --- | --- | --- |
| POST | `/api/auth/login` | Starts a staff session |
| POST | `/api/auth/logout` | Invalidates the current session |
| GET | `/api/auth/session` | Returns session status |
| GET/POST | `/api/appointments` | Lists/creates appointments |
| GET/PUT/DELETE | `/api/appointments/{number}` | Finds, edits or cancels an appointment |
| GET | `/api/appointments/{number}/bill-preview` | Calculates a bill preview |
| POST | `/api/appointments/{number}/bill` | Generates a bill |
| GET/POST | `/api/catalog/dentists` | Lists active dentists or adds a dentist |
| GET/POST | `/api/catalog/treatments` | Lists active treatments or adds a treatment |
| GET | `/api/reports/daily`, `/dentists`, `/treatments`, `/revenue` | Returns report data |

## Technology

Java 17, Maven WAR, Jakarta Servlets on Tomcat, JDBC, Jackson, JUnit 5 and vanilla HTML/CSS/JavaScript. The sibling demonstration project is configured for PostgreSQL 16; the original project remains configured for MySQL 8.
