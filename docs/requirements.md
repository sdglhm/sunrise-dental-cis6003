# Requirements

The application provides authenticated staff access to appointment registration, search, display, editing, cancellation, billing, reports, help, and logout.

## Business rules

- Users must be active and provide matching hashed credentials.
- Patient name, address, contact number, dentist, treatment, date, and time are required.
- Dentist and treatment selections must exist and be active.
- Appointment dates cannot be in the past.
- An active dentist slot cannot be booked twice.
- Cancellation preserves appointment history.
- Billing uses the stored treatment price and consultation fee with `BigDecimal` arithmetic.
- One persistent bill is allowed per appointment.
- Protected pages and API endpoints require an authenticated session.

The current API inventory is maintained in the project README. Code-aligned diagrams are in `docs/uml/`.
