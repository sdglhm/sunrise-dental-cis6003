# Test plan

Tests use JUnit 5 and run with `mvn test`.

| Area | Automated or manual test focus |
| --- | --- |
| Connection setup | Singleton configuration factory |
| Authentication | Valid credentials, invalid credentials and inactive users |
| Appointment validation | Required fields, valid dates/times and identifiers |
| Double booking | Service validation and database unique constraint |
| Search and persistence | Service search behaviour; local API checks cover JDBC save/find/update |
| Billing | `BigDecimal` treatment price, consultation fee and total |

The automated suite currently contains 10 JUnit tests for deterministic business rules and does not require a database server. The local PostgreSQL demonstration has been manually checked for authentication, appointment save/find/update/cancel, filtered lists, catalogue operations, billing and reports. A dedicated isolated DAO integration test suite remains a future improvement.
