# Enterprise Portfolio Management System

A robust backend service built with Spring Boot for managing portfolios with user authentication and role-based access control.

##  Features

- User authentication and authorization
- Role-based access control
- RESTful API endpoints
- Exception handling and validation
- Docker containerization
- CI/CD ready (GitHub Actions)

##  Tech Stack

- **Backend**: Java 17, Spring Boot 3.x
- **Security**: Spring Security, JWT
- **Database**: (Add your database, e.g., PostgreSQL/MySQL)
- **Containerization**: Docker
- **Build Tool**: Maven


### Prerequisites
- Java 17+
- Maven 3.8.6+
- Docker (optional)
-  # Build and run 
   docker build -t portfolio-backend .
   docker run -p 8080:8080 portfolio-backend

### Running Locally
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run


### CI/CD with GitHub Actions

This project uses GitHub Actions for continuous integration and deployment. The workflow:

1. Runs on every push to `main` and pull requests
2. Builds and tests the application
3. On `main` branch, builds and pushes a Docker image to Docker Hub

### Required Secrets
- `DOCKERHUB_USERNAME`: Your Docker Hub username
- `DOCKERHUB_TOKEN`: Docker Hub access token with write permissions

### View Workflow Status
Visit the [Actions](https://github.com/yourusername/yourrepo/actions) tab to monitor workflow runs.
