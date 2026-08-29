# Sunrise Dental Clinic Appointment & Patient Management System

University Advanced Programming assessment project built with Java 17, Maven WAR packaging, Jakarta Servlets, JDBC, MySQL, Jackson and vanilla HTML/CSS/JavaScript.

## Current status

Milestone 1 source setup is complete: the Maven project, database schema, package structure, connection factory, documentation skeleton and CI workflow are in place. Application features are planned but not yet implemented. Local Maven verification is pending because Maven is not installed on this machine.

## Prerequisites

- Java 17
- Maven 3.9+
- MySQL 8
- Apache Tomcat 10.1+ (Jakarta Servlet 6 compatible)

## Local setup

1. Run `database/schema.sql` then `database/seed.sql` in MySQL. The demonstration login is `staff` / `staff123`; change it after setup.
2. Copy `src/main/resources/database.properties.example` to `database.properties` in the same directory and enter local credentials. This local file is ignored by Git.
3. Run `mvn clean test`.
4. Run `mvn package`; deploy `target/sunrise-dental.war` to Tomcat.

## Design

The project uses MVC, DAO, Service Layer and a small singleton connection factory. See `docs/architecture.md` for the intended boundaries.
