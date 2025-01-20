## cg-be-assignment (store-application)

Spring Boot CRUD application (backend developer assignment).

## Getting started

### Prerequisites

* Git
* JRE 17 (or newer)
* Docker compose [optional] - not needed if you supply an external database

### Running the application 🚀

1. Clone this repository: `git clone https://github.com/Primoz-G/cg-be-assignment.git`
2. Start the database:
    1. From project root, run `docker compose -f development/docker/postgres/compose.yml up -d`
    2. Alternative: [Use an external database](#using-an-external-database-)
3. Start the application:
    1. Run `mvn spring-boot:run`
    2. Alternative:
        1. Build the project: `mvn install -DskipTests`
        2. Run `java -jar modules/store-application/target/store-application-0.0.1-SNAPSHOT.jar`

### Using an external database 🐘

If you want to use an external database, you have to pass the datasource connection properties to the application:

```
mvn spring-boot:run -Dspring-boot.run.jvmArguments='
-Dspring.datasource.url=jdbc:postgresql://<server>:<port>/<db-name>
-Dspring.datasource.username=<username>
-Dspring.datasource.password=<password>'
```

### Notes

If you do not have maven installed, use `./mvnw` or `mvnw.cmd` instead of `mvn`.
