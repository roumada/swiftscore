## Swiftscore

Swiftscore is a football league-simulating application enabling competition creation and simulation.
Currently only one-cycle league type is supported where each club plays another two times, one at home stadium and one away.

## Prerequisites

- Java 17+
- Docker (if running the application with the database via Docker Compose or running non-unit tests)
- MongoDB (running locally if launching the application directly)


The application uses MongoDB as its database.
You either need to have it running locally while launching the application directly (available at `localhost:27017`) or use a included Docker Compose file which comes with application and database definition for convenient deployment.

## Installation

Clone the repository:

```
git clone https://github.com/roumada/swiftscore/
cd swiftscore
```

### Running the Application Directly

To run the application manually without Docker, ensure MongoDB is running locally, then follow these steps:

Build the application using Gradle:

```
./gradlew build
```

Run the Spring Boot application:

```
./gradlew bootRun
```


Applications endpoints are accessed at http://localhost:8080

Swagger UI is also accessible at http://localhost:8080/swagger-ui/index.html#/

### Running with Docker Compose

Ensure Docker is running.

Run the following command to start the application:

```
docker compose up -d
```

This command will build and start the containers in detached mode (-d).

Verify that the containers are running:

```
docker ps
```


Applications endpoints are accessed at http://localhost:8090

Swagger UI is also accessible at http://localhost:8090/swagger-ui/index.html#/

## Instructions

The application loads objects defined in the `resources/data` folder file if they are present (for now football clubs and competitions are supported).
Objects can be removed or added to the list according to the existing schema.
The database is cleared from previous data upon application startup (called `swiftscore` by default; name can be changed in the `/src/main/resources/application.properties` file)

Core of simulations are competitions which are simulated according to simulation variations provided.
Endpoints for results of last matches for given club as well as competition standings are also available.
