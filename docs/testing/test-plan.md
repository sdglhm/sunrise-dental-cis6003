# Test plan

## Approach

JUnit 5 tests run with Maven. Service tests use small in-memory fakes to exercise business rules without a production database. Controller and filter tests use Mockito to verify JSON responses, status codes, session behavior, and service coordination without starting Tomcat.

The Git history contains tests alongside several feature implementations. It does not establish strict Red-Green-Refactor ordering for every change, so the project does not claim evidence it cannot demonstrate.

## Automated coverage

| Area | Scenarios |
| --- | --- |
| Authentication service | Correct password, wrong password, blank input, inactive user |
| Authentication HTTP | Login, session rotation, invalid input, invalid credentials, logout |
| Authentication filter | Unauthorized block and authenticated pass-through |
| Appointment service | Creation, generated number, past date, contact validation, inactive catalog, lookup, missing record, conflict, update, cancellation |
| Appointment HTTP | Retrieve, create, update, cancel, conflict 409, billing route, controlled 500 |
| Billing service | Preview total, complete receipt, duplicate generation, cancelled appointment, invalid amounts, missing bill |
| Reports | Date selection, invalid date, empty data, dashboard summary, report HTTP response |
| Appointment DAO | Isolated H2 insert, read, update, and active-slot persistence |
| Build | Java 17 compilation, test discovery, WAR packaging |

## Exclusions

The automated suite does not start PostgreSQL or Tomcat and does not claim live PostgreSQL integration or deployed browser-flow coverage. The appointment DAO test uses an isolated H2 database. Live environment results should be recorded separately when performed.

## Commands

```bash
mvn clean test
mvn clean package
```

A passing run must report zero failures and errors and produce `target/sunrise-dental.war`.
