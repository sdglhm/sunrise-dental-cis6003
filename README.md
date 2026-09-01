# Sunrise Dental Clinic Appointment & Patient Management System

University Advanced Programming assessment project built with Java 17, Maven WAR packaging, Jakarta Servlets, JDBC, Jackson and vanilla HTML/CSS/JavaScript.

## Current status

The core API, staff login, patient management, appointment registration, catalogue management, billing, reports and staff pages are implemented. Staff can add, find and update patients, then reuse an existing patient during appointment registration. Maven tests and WAR packaging pass locally. This sibling demonstration project is configured for PostgreSQL; the original project remains configured for MySQL 8.

## Prerequisites

- Java 17
- Maven 3.9+
- PostgreSQL 16 for this local demonstration project
- Apache Tomcat 10.1+ (Jakarta Servlet 6 compatible)

## Local setup

1. Run `database/schema.sql` then `database/seed.sql` in the selected database. The demonstration login is `staff` / `staff123`; change it after setup.
2. Copy `src/main/resources/database.properties.example` to `database.properties` in the same directory and enter local credentials. This local file is ignored by Git.
3. Run `mvn clean test`.
4. Run `mvn package`; deploy `target/sunrise-dental.war` to Tomcat.

## Design

The project uses MVC, DAO, Service Layer and a small singleton connection factory. See `docs/architecture.md` for the intended boundaries.
