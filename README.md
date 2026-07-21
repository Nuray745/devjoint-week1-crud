# Library Management System (REST API)

A Spring Boot-based RESTful API for managing a library system.  
The application provides CRUD operations for authors, books, and members with pagination, validation, global exception handling, Swagger API documentation, and unit testing.

---

## Features

- CRUD operations for Authors, Books, and Members
- RESTful API architecture
- DTO-based architecture (Request / Response DTOs)
- Entity to DTO mapping
- Service layer business logic
- Pagination and sorting support
- Request validation using Jakarta Validation
- Global Exception Handling
- Custom exceptions
- MySQL database integration
- Swagger / OpenAPI documentation
- Unit testing with JUnit 5 and Mockito

---

## Tech Stack

| Technology | Description |
|---|---|
| Java 17 | Programming language |
| Spring Boot 3 | Backend framework |
| Spring Data JPA | Database interaction |
| Hibernate | ORM framework |
| MySQL | Relational database |
| Lombok | Reduces boilerplate code |
| Swagger / OpenAPI | API documentation |
| JUnit 5 | Unit testing |
| Mockito | Mocking framework |
| Maven | Dependency management |

---

## Project Architecture

The project follows a layered architecture:

```
Controller Layer
        |
        ↓
Service Layer
        |
        ↓
Repository Layer
        |
        ↓
Database
```

### Layers

| Layer | Responsibility |
|---|---|
| Controller | Handles HTTP requests and responses |
| Service | Contains business logic |
| Repository | Communicates with database |
| Entity | Represents database tables |
| DTO | Transfers data between client and API |
| Mapper | Converts Entity ↔ DTO |
| Exception | Handles application errors |

---

## Project Structure

```
src/main/java/org/ironhack/project/library/

├── config/
│   └── OpenApiConfig.java
│       # Swagger/OpenAPI configuration
│
├── controller/
│   ├── AuthorController.java
│   ├── BookController.java
│   └── MemberController.java
│       # REST API controllers
│
├── dto/
│   ├── request/
│   │   ├── AuthorRequest.java
│   │   ├── BookRequest.java
│   │   └── MemberRequest.java
│   │
│   └── response/
│       ├── AuthorResponse.java
│       ├── BookResponse.java
│       └── MemberResponse.java
│       # Request and Response DTOs
│
├── entity/
│   ├── Author.java
│   ├── Book.java
│   └── Member.java
│       # JPA Entities
│
├── exception/
│   ├── ErrorResponse.java
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
│       # Exception handling
│
├── mapper/
│   ├── AuthorMapper.java
│   ├── BookMapper.java
│   └── MemberMapper.java
│       # Entity and DTO conversion
│
├── repository/
│   ├── AuthorRepository.java
│   ├── BookRepository.java
│   └── MemberRepository.java
│       # Database repositories
│
└── service/
    ├── AuthorService.java
    ├── BookService.java
    └── MemberService.java
        # Business logic
```

---

## Database

The application uses **MySQL** database.

### Main Entities

| Entity | Description |
|---|---|
| Author | Stores author information |
| Book | Stores book information and author relationship |
| Member | Stores library member information |

---

# API Documentation

Detailed API endpoint documentation:

[API Documentation](docs/API.md)

---

## API Overview

| Resource | Endpoint | Operations |
|---|---|---|
| Authors | `/authors` | Create, Read, Update, Delete |
| Books | `/books` | Create, Read, Update, Delete |
| Members | `/members` | Create, Read, Update, Delete |

---

## Pagination and Sorting

All GET collection endpoints support pagination and sorting.

Example:

```
GET /books?page=0&size=10&sortBy=title
```

### Parameters

| Parameter | Type | Default Value | Description |
|---|---|---|---|
| page | Integer | 0 | Page number |
| size | Integer | 5 | Number of records per page |
| sortBy | String | id | Sorting field |

---

## Validation

The application uses Jakarta Validation for request validation.

Invalid requests return:

```
400 BAD REQUEST
```

Example response:

```json
{
  "message": "Validation failed"
}
```

---

## Exception Handling

The project uses global exception handling with:

```
GlobalExceptionHandler.java
```

Example error response:

```json
{
  "timestamp": "2026-07-21T12:00:00",
  "status": 404,
  "message": "Resource not found"
}
```

---

## Swagger Documentation

Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

Swagger provides:

- Available endpoints
- Request body examples
- Response examples
- API testing interface

---

## Installation and Running

### 1. Clone Repository

```bash
git clone <repository-url>
```

### 2. Configure Database

Update:

```
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/library
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 3. Run Application

Using Maven:

```bash
mvn spring-boot:run
```

Application runs on:

```
http://localhost:8080
```

---

## Testing

The project includes unit tests using:

- JUnit 5
- Mockito

Tests cover:

- Service layer logic
- CRUD operations
- Successful scenarios
- Exception scenarios

Run tests:

```bash
mvn test
```


---

## Future Improvements

- Spring Security authentication
- Role-based authorization
- Advanced search functionality
- Docker support
- Integration testing

---

## Author

Developed as a Spring Boot REST API project.