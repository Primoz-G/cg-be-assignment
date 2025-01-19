## cg-be-assignment (store-application)

Spring Boot CRUD application (backend developer assignment).

## Getting started

### Prerequisites

* JDK 17 or newer
* Docker and docker compose [optional] - not needed if you supply an external database
* Git

### Running the application 🚀

1. Clone this repository: `https://github.com/Primoz-G/cg-be-assignment.git`
2. Start the database:
    1. From project root, run `docker compose -f development/docker/postgres/compose.yml up [-d]`
3. Run the application: `./mvnw spring-boot:run`

### Using an external database 🐘

If you have an external database, you have to supply the datasource connection parameters to the application,
like this:

```
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments='
-Dspring.datasource.url=jdbc:postgresql://<server-name>:5432/<db-name>
-Dspring.datasource.username=<username>
-Dspring.datasource.password=<password>'
```
