# Document Approval Workflow

An enterprise-style Spring Boot application for managing business requests through a role-based, multi-level approval workflow.

## 🚧 Project Status

**Phase 1 Complete | Phase 2 In Progress**

## 🎯 Objective

The system allows employees to create and submit requests that move through Manager and Admin approval stages with role-based authorization, ownership checks, audit logging, approval history, and notifications.

## 🔄 Approval Workflow

Employee
↓
Create Request
↓
Submit
↓
Manager Review
↓
Manager Approve / Reject / Return
↓
Admin Review
↓
Final Approval
↓
Completed

## 👥 Roles

- **Employee** — Create, update, submit and manage owned requests
- **Manager** — Review, approve, reject or return requests
- **Admin** — Perform final approval and manage workflow activity

## 🛠️ Technology Stack

- Java 17
- Spring Boot 3.5.6
- Spring Security
- JWT Authentication
- Spring Data JPA / Hibernate
- MySQL
- JUnit 5
- Mockito
- Maven
- Swagger / OpenAPI
- Git & GitHub
- GitHub Actions

## ✅ Completed

### Core Workflow

- Request management
- Request status management
- Employee → Manager → Admin approval flow
- Approve / Reject / Return operations
- Role-based authorization
- Request ownership enforcement
- Input validation
- Approval history
- Audit logging
- Notifications

### Security

- JWT authentication
- BCrypt password encryption
- Role-based access control
- Stateless security
- Ownership checks

### Testing

- Unit tests with JUnit 5 and Mockito
- **16 tests passing**
- **0 failures**
- **0 errors**

### API Documentation

- Swagger / OpenAPI integration
- JWT Bearer authentication support
- Swagger endpoints enabled through Spring Security

## 🚀 Phase 2 — In Progress

- [x] Swagger / OpenAPI
- [x] JWT support in Swagger
- [x] README documentation
- [ ] Docker Compose

## 🔮 Future Enhancements

- Document upload
- Comments and discussion
- Advanced audit/history features
- Search and filtering
- React frontend
- Dashboard and analytics
- Docker deployment
- CI/CD improvements

## 📡 API Documentation

When the application is running:

`http://localhost:8080/swagger-ui/index.html`

OpenAPI specification:

`http://localhost:8080/v3/api-docs`

## 🗄️ Database

MySQL database with JPA/Hibernate entity mapping.

Database design documentation is available in:

`docs/database-design.md`

## 🏗️ Project Structure

```text
backend/
├── src/main/java/com/mahesh/daw/
│   ├── config/
│   ├── controller/
│   ├── entity/
│   ├── repository/
│   ├── security/
│   └── service/
├── src/main/resources/
└── src/test/

docs/
└── database-design.md
