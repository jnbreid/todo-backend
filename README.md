# Task Management Backend

This is the backend of a Task Management system built with **Spring Boot** and **JWT Authentication**. The project exposes a set of RESTful APIs for task management functionalities, including user authentication, task creation, task retrieval, and task completion. The application is designed to be stateless and utilizes JWT for secure user authentication.

## Features

- User Registration and Login (JWT Authentication)
- Create, Retrieve, and Update Tasks
- Secure Endpoints (JWT Required)
- Swagger UI for API Documentation

## Technologies Used

- **Spring Boot** for building the backend.
- **Spring Security** for authentication and authorization.
- **JWT** (JSON Web Token) for secure user authentication.
- **PostgreSQL** as the database.
- **Swagger UI** for API testing.

## Setup and Installation

### 1. Clone the repository

```bash
git clone https://github.com/jnbreid/todo-backend.git
cd todo-backend
```

### 2. Build the application

```bash
docker compose -f docker-compose.yml build
```

### 3. Run the application

```bash
docker compose -f docker-compose.yml build
```
The application will run at [http://localhost:8080](http://localhost:8080).


### TODO

- Adjust tests to support JWT authentication.

> **Note:** This project is a work in progress.

## License

This project is licensed under the MIT License - see the [LICENSE file](LICENSE) for details.
