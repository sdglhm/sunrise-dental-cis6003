# Architecture

The application follows a simple three-tier architecture.

- Presentation: HTML, CSS, vanilla JavaScript and servlet controllers returning JSON.
- Application: service classes validate use cases and coordinate DAOs.
- Data: DAO classes use JDBC and contain SQL; PostgreSQL stores data for this local demonstration.

Servlets do not contain SQL. DAOs do not know about HTTP. Browser code calls `/api` endpoints and does not contain business rules.

The intended patterns are MVC, DAO, Service Layer and the `DatabaseConnectionFactory` singleton for JDBC configuration.
