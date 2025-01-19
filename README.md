## cg-be-assignment (store-application)

Spring Boot CRUD application (backend developer assignment).

## Getting started

### Prerequisites

* Git
* JRE 17
* Docker compose [optional] - not needed if you supply an external database

### Running the application 🚀

1. Clone this repository: `https://github.com/Primoz-G/cg-be-assignment.git`
2. Start the database:
    1. From project root, run `docker compose -f development/docker/postgres/compose.yml up [-d]`
3. Run the application: `./mvnw spring-boot:run`
4. Alternative:
    1. Build the project: `./mvnw install -DskipTests`
    2. Run `java -jar modules/store-application/target/store-application-1.0.jar`

### Using an external database 🐘

If you want to use an external database, you have to pass the datasource connection properties to the application:

```
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments='
-Dspring.datasource.url=jdbc:postgresql://<server>:<port>/<db-name>
-Dspring.datasource.username=<username>
-Dspring.datasource.password=<password>'
```
