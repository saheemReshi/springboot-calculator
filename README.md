# Calculator REST API and CI/CD Pipeline

A Spring Boot calculator built for the CSE 816 Software Production Engineering mini-project. The application exposes four arithmetic operations as HTTP endpoints. This repository also contains a Jenkins pipeline that builds and tests the application, publishes a Docker image, and deploys it with Docker Compose behind Nginx.

## Requirements

- Java 21 and Maven 3 for running or building locally
- Docker with the Compose plugin for the container deployment
- Jenkins with Maven, Docker, Git access, JUnit report publishing, and email configured for the CI/CD pipeline

## Build, test, and run locally

```bash
mvn clean package
mvn test
java -jar target/calculator-1.0.0.jar
```

`mvn clean package` compiles the application, runs its tests, and creates the executable JAR. `mvn test` can be used on its own; JUnit XML reports are written to `target/surefire-reports/`. The application listens on port `8080` by default.

To run directly through Maven during development, use `mvn spring-boot:run` instead of the JAR command.

## API

All operations use `GET` and require numeric `a` and `b` query parameters.

| Operation | Endpoint | Result |
| --- | --- | --- |
| Add | `/api/v1/add?a=2&b=3` | `5` |
| Subtract | `/api/v1/subtract?a=5&b=3` | `2` |
| Multiply | `/api/v1/multiply?a=4&b=3` | `12` |
| Divide | `/api/v1/divide?a=10&b=2` | `5` |
| Health | `/actuator/health` | Spring Boot Actuator health status |

With the application running locally:

```bash
curl 'http://localhost:8080/api/v1/add?a=2&b=3'
```

The response has this structure (numeric values are serialized as floating-point numbers):

```json
{"operation":"add","a":2.0,"b":3.0,"result":5.0}
```

Missing or invalid inputs, an unknown operation, and division by zero return HTTP `400` with an error body. For example, `/api/v1/divide?a=10&b=0` returns:

```json
{"error":"BAD_REQUEST","message":"Division by zero"}
```

## Run with Docker

The multi-stage `Dockerfile` builds the JAR with Maven and runs it on a Java 21 JRE as a non-root user.

```bash
docker build -t calculator:local .
docker run --rm -p 8080:8080 calculator:local
```

The API is then available at `http://localhost:8080`.

## Run the Compose stack

```bash
docker compose up --build -d
docker compose ps
curl 'http://127.0.0.1:8081/api/v1/add?a=2&b=3'
curl 'http://127.0.0.1:8081/actuator/health'
```

Compose starts the calculator and an Nginx reverse proxy on a private Docker network. The calculator health check probes `/actuator/health`, and Nginx starts after the calculator is healthy. Nginx is exposed at `127.0.0.1:8081`, so this default configuration is accessible only from the host machine. To stop the stack, run `docker compose down`.

## Jenkins pipeline

The checked-in `Jenkinsfile` polls the configured Git repository every two minutes and runs these stages:

1. **Checkout:** fetch the source from Git.
2. **Build:** package the application with Maven, skipping tests in this stage.
3. **Test:** run `mvn test` and publish the JUnit XML reports.
4. **Docker Build:** tag the image with the Jenkins build number.
5. **Push:** authenticate with Jenkins credentials and push to Docker Hub.
6. **Deploy:** update the Compose stack and check its health endpoint through Nginx.

To use the pipeline, create a Jenkins job connected to this repository's `Jenkinsfile`. The Jenkins agent needs Java 21, Maven, Docker with the Compose plugin, and permission to use the Docker daemon. Create a Jenkins username/password credential with ID `dockerhub-credentials` for the Docker Hub account that can push to `saheemreshi/spring-boot-calculator`. Configure Jenkins email delivery so the pipeline's success and failure notifications can be sent. The deployment runs on the Jenkins agent's Docker host and binds the proxy to `127.0.0.1:8081` there.

The pipeline sets `CALCULATOR_IMAGE` to the newly built image tag before starting Compose. For a local `docker compose up`, the image defaults to `calculator:local`.

## Repository layout

| Path | Purpose |
| --- | --- |
| `src/main/java/calculator/` | Spring Boot application, HTTP controller, and arithmetic service |
| `src/test/java/calculator/` | JUnit service tests and MockMvc endpoint tests |
| `pom.xml` | Maven dependencies and Java version |
| `Dockerfile`, `.dockerignore` | Multi-stage image build and build-context exclusions |
| `docker-compose.yml`, `nginx/default.conf` | Calculator, health check, network, and reverse proxy |
| `Jenkinsfile` | Automated build, test, image push, deployment with notifications |
