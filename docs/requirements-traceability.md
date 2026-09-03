# Requirements traceability

| Requirement | UML | Implementation | API | Database | Automated evidence | Report evidence |
| --- | --- | --- | --- | --- | --- | --- |
| Login | Use case, login sequence | `AuthServlet`, `AuthenticationService`, `JdbcUserDao` | `POST /api/auth/login` | `users` | `AuthenticationServiceTest`, `AuthServletTest` | Login and validation screens |
| Session protection | Login sequence | `AuthenticationFilter`, `ProtectedPageFilter` | All protected `/api/*` routes | N/A | `AuthenticationFilterTest`, `AuthServletTest` | Unauthorized API response |
| Register appointment | Use case, registration sequence | `AppointmentServlet`, `AppointmentService`, `JdbcAppointmentDao` | `POST /api/appointments` | `patients`, `appointments` | Appointment service and Servlet creation tests | Registration form and confirmation |
| Dentist availability | Registration sequence | `AppointmentService`, `JdbcAppointmentDao` | Appointment POST/PUT | Active-slot unique constraint | Duplicate-slot service test, 409 Servlet test | Conflict message |
| Search/display | Use case | `AppointmentService`, appointment UI | `GET /api/appointments/{number}` | Appointment joins | Lookup/missing tests, Servlet retrieval test | Search and detail panel |
| Edit appointment | Use case | `AppointmentService`, edit form | `PUT /api/appointments/{number}` | `patients`, `appointments` | Update service and Servlet tests | Edited detail panel |
| Cancel appointment | Use case | `AppointmentService`, cancel action | `DELETE /api/appointments/{number}` | `appointments.status` | Cancellation service and Servlet tests | CANCELLED detail/list status |
| Calculate bill | Use case, billing sequence | `BillingService`, `JdbcClinicSettingsDao` | Bill preview/generation | `treatments`, `clinic_settings` | Billing arithmetic and invalid-amount tests | Itemized receipt |
| Generate/retrieve bill | Billing sequence | `BillingService`, `JdbcBillDao` | GET/POST appointment bill | `bills` | Receipt, duplicate, cancelled, missing-bill tests | Bill number and persisted receipt |
| Print bill | Use case, billing sequence | `receipt.html`, `receipt.js` | Bill POST | `bills` | Billing response test | Browser print action |
| Reports/dashboard | Use case | `ReportService`, `JdbcReportDao`, reports UI | Summary, daily, dentists, treatments, revenue | `appointments`, `bills` | `ReportServiceTest`, `ReportServletTest` | Dashboard and report tables |
| Help | Use case | `help.html`, `ProtectedPageFilter` | N/A | N/A | Authentication filter coverage | Help page |
| Logout | Use case, login sequence | `AuthServlet`, logout action | `POST /api/auth/logout` | N/A | Logout/session invalidation test | Returned login screen |
| Web services | Sequence diagrams | Servlet controllers, `JsonResponse`, `ApiErrorHandler` | JSON API inventory | N/A | Controller/filter test suites | Browser network/API evidence |
| PostgreSQL persistence | Class and sequence diagrams | JDBC DAOs, `DatabaseConnectionFactory` | Server-side only | Schema, seed, migration | Static/build verification only | Schema/database screenshot |
| Design patterns | Class diagram | MVC views/Servlets/models, DAO interfaces, services, connection factory | N/A | N/A | Injectable controller tests | Architecture document |
| Automated testing | N/A | `src/test` | Main routes covered without server | No live DB | Maven Surefire test run | Test output screenshot |
