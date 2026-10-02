# Document Approval Workflow

An enterprise-style full-stack application for managing business requests through a secure, role-based, multi-level approval workflow.

## 🚧 Project Status

**Backend Phase 1 Complete | Phase 2 Complete | Phase 3 In Progress**

## 🎯 Objective

The Document Approval Workflow system allows employees to create and submit business requests that move through Manager and Admin approval stages.

The backend provides JWT authentication, role-based authorization, request ownership checks, approval history, audit logging, notifications, validation, API documentation, automated testing, and Docker support.

## 🔄 Approval Workflow

Employee
↓
Create Request
↓
Submit Request
↓
Manager Review
↓
Approve / Reject / Return
↓
Admin Review
↓
Final Approval
↓
Completed

## 👥 User Roles

### Employee
- Create requests
- Update owned requests
- Submit requests
- Track request status
- View workflow information

### Manager
- Review requests
- Approve requests
- Reject requests
- Return requests for correction

### Admin
- Perform final approval
- Manage workflow activity
- Access administrative operations
- Review audit information

## 🛠️ Technology Stack

### Backend
- Java 17
- Spring Boot 3.5.6
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- Jakarta Validation

### Database
- MySQL 8

### Testing
- JUnit 5
- Mockito
- Spring Boot Test
- Spring Security Test

### API & Documentation
- REST APIs
- Swagger / OpenAPI
- JWT Bearer Authentication

### DevOps & Tools
- Maven
- Git
- GitHub
- GitHub Actions
- Docker
- Docker Compose

### Frontend
- React
- JavaScript
- HTML
- CSS

## ✅ Completed

### Phase 1 — Core Workflow

- Request creation and management
- Request status management
- Employee → Manager → Admin approval workflow
- Manager approval
- Admin final approval
- Reject workflow
- Return workflow
- Role-based authorization
- Request ownership enforcement
- Input validation
- Approval history
- Audit logging
- Notifications
- Workflow status validation

### Security

- JWT authentication
- BCrypt password encryption
- Stateless Spring Security
- Role-based access control
- Manager/Admin endpoint protection
- Request ownership checks
- REST authentication and access-denied handling

### Testing

- Unit testing with JUnit 5 and Mockito
- Service-layer workflow tests
- Request ownership tests
- Validation and status-transition tests
- GitHub Actions automated build verification

**Current test status: 16 tests passing | 0 failures | 0 errors**

### Phase 2 — Demo & Deployment

- Swagger / OpenAPI integration
- JWT Bearer authentication in Swagger
- Swagger endpoints secured through Spring Security
- README documentation
- Dockerfile for Spring Boot
- MySQL Docker container
- Docker Compose configuration
- Spring Boot + MySQL container setup
- GitHub Actions CI verification

## 🚀 Phase 3 — Full-Stack Development

### Frontend

- [ ] React application
- [ ] Login page
- [ ] JWT authentication integration
- [ ] Employee dashboard
- [ ] Manager dashboard
- [ ] Admin dashboard
- [ ] Request creation UI
- [ ] Request details and status tracking
- [ ] Approve / Reject / Return UI

### Document Management

- [ ] Document upload
- [ ] Document metadata
- [ ] Document download/view
- [ ] File type validation
- [ ] Secure document access

### Collaboration

- [ ] Comments
- [ ] Request discussion
- [ ] Workflow activity history

### Advanced Features

- [ ] Search and filtering
- [ ] Dashboard analytics
- [ ] Advanced audit history
- [ ] Improved notification management

## 🧪 QA & Automation Roadmap

- [ ] REST API integration testing
- [ ] Security test coverage
- [ ] Complete workflow test scenarios
- [ ] Selenium UI automation
- [ ] TestNG automation suite
- [ ] CI execution of automated tests

## 📡 API Documentation

When the application is running:

`http://localhost:8080/swagger-ui/index.html`

OpenAPI specification:

`http://localhost:8080/v3/api-docs`

## 🐳 Docker

The project includes Docker support for running the Spring Boot backend with MySQL.

Start the application:

`docker compose up --build`

Stop the application:

`docker compose down`

## 🗄️ Database

MySQL is used as the primary relational database.

JPA/Hibernate manages entity persistence and database relationships.

Database design documentation:

`docs/database-design.md`

## 🏗️ Project Structure

```text
document-approval-workflow/
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/mahesh/daw/
│   │   │   │   ├── config/
│   │   │   │   ├── controller/
│   │   │   │   ├── entity/
│   │   │   │   ├── repository/
│   │   │   │   ├── security/
│   │   │   │   └── service/
│   │   │   └── resources/
│   │   └── test/
│   │
│   ├── Dockerfile
│   └── pom.xml
│
├── docs/
│   └── database-design.md
│
├── docker-compose.yml
└── README.md
