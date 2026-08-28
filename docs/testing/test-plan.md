# Test plan

Tests use JUnit 5 and run with `mvn test`.

| Area | Planned test focus |
| --- | --- |
| Connection setup | Singleton configuration factory |
| Authentication | Valid credentials, invalid credentials and inactive users |
| Appointment validation | Required fields, valid dates/times and identifiers |
| Double booking | Service validation and database unique constraint |
| Search and persistence | DAO save/find/update behaviour |
| Billing | `BigDecimal` treatment price, consultation fee and total |

DAO SQL is isolated in JDBC DAO classes and should be checked against a separate local MySQL schema before demonstration. The automated suite currently covers deterministic business rules without requiring an installed MySQL server.
