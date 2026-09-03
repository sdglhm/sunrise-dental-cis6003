# UML diagrams

The PlantUML sources and SVG exports describe the implemented staff role, Servlet endpoints, services, DAOs, domain records, and PostgreSQL persistence flow.

Assumptions:

- Clinic staff is the only implemented actor and authorization level.
- Printing is a browser action after a bill has been generated or retrieved.
- Cancellation preserves the appointment and releases its active dentist slot.
- The database unique constraint is the final protection against concurrent double booking.
