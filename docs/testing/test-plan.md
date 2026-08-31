# Test plan

Tests use JUnit 5 and run with `mvn test`.

| Area | Automated or manual test focus |
| --- | --- |
| Connection setup | Singleton configuration factory |
| Authentication | Valid credentials, invalid credentials and inactive users |
| Appointment validation | Required fields, valid dates/times and identifiers |
| Patient management | Required patient details and trimmed values |
| Double booking | Service validation and database unique constraint |
| Search and persistence | H2-backed JDBC test covers DAO save/find/update/filter/cancel behaviour |
| Billing | `BigDecimal` treatment price, consultation fee and total |

The automated suite contains 11 JUnit tests and does not require a database server. The JDBC appointment DAO test uses an H2 in-memory database configured under `src/test/resources`, while the application remains configured for PostgreSQL outside test runs. The local PostgreSQL demonstration has also been manually checked for authentication, appointment save/find/update/cancel, filtered lists, catalogue operations, billing and reports.
