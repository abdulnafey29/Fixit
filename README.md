# FixIt: Campus Issue Management System

> **Enterprise-grade Centralized Campus Complaint & Maintenance Management Web Application**  
> Built with **Java 17**, **Spring Boot 3.2**, **Spring Security 6**, **JWT**, **Spring Data JPA / Hibernate**, and a modern responsive dashboard interface.

---

## 🌟 Key Highlights

- **3 Dedicated User Roles**:
  - 🎓 **Student**: Report issues with photo evidence, choose location and category, track 4-step real-time lifecycle, rate resolutions with 1–5 stars.
  - 🛡️ **Administrator**: Centralized control center, interactive Chart.js analytics, ticket assignment with technician workload balancing, category/location master configs.
  - 🔧 **Technician**: Dedicated work order queue sorted by urgency (Critical/High), one-click status transitions (`IN_PROGRESS`, `RESOLVED`), repair audit logs.
- **Audited Lifecycle State Machine**:  
  `SUBMITTED` $\rightarrow$ `ASSIGNED` $\rightarrow$ `IN PROGRESS` $\rightarrow$ `RESOLVED` (every transition recorded in an immutable audit table).
- **Dual Database Profiles**:
  - **H2 In-Memory (Default)**: Zero setup required; pre-seeds with sample users, categories, locations, and complaints on boot.
  - **MySQL 8 (Production)**: Enterprise relational database with foreign key cascades and indexes.
- **Self-Contained Maven Wrapper**: Run immediately via `./mvnw` with no manual Maven installation required.

---

## 🚀 Quick Start & Running Locally

### 1. Prerequisites
- **Java JDK 17 or higher** installed.

### 2. Run the Application
In your terminal, navigate to the project directory and run:

```bash
# Using the bundled Maven wrapper
./mvnw spring-boot:run
```

Or run the pre-built JAR directly:
```bash
java -jar target/fixit-campus-system-1.0.0.jar
```

### 3. Open in Browser
- **Web Portal**: [http://localhost:8080](http://localhost:8080)
- **H2 Database Console**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
  *(JDBC URL: `jdbc:h2:mem:fixitdb`, User: `sa`, Password: leave blank)*

---

## 🔑 Pre-Seeded Test Accounts

The system automatically seeds demo accounts with realistic campus data upon first boot:

| Role | Email | Password | Details |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@campus.edu` | `Admin@123` | Campus Facility Administrator |
| **Student** | `rahul.student@campus.edu` | `Student@123` | Student with active & resolved tickets |
| **Student** | `priya.student@campus.edu` | `Student@123` | Student |
| **Technician** | `arun.tech@campus.edu` | `Tech@123` | IT & Equipment Specialist |
| **Technician** | `ravi.tech@campus.edu` | `Tech@123` | Electrical Specialist |
| **Technician** | `rajesh.tech@campus.edu` | `Tech@123` | Plumbing & Sanitization Specialist |

> 💡 **Tip**: The login page includes convenient **1-Click Demo Buttons** to instantly log in as any role during your viva demonstration!

---

## 🏛️ System Architecture & Package Structure

```
com.fixit
├── config/              # SecurityConfig, CorsConfig, WebMvcConfig, DatabaseSeeder
├── controller/          # Auth, Complaint, Admin, Technician, Category, Location, Analytics
├── dto/
│   ├── request/         # LoginRequest, RegisterRequest, ComplaintCreateRequest, etc.
│   └── response/        # AuthResponse, ComplaintResponse, AnalyticsSummary, ApiResponse
├── entity/              # BaseEntity, User, Complaint, Category, Location, ComplaintUpdate, Feedback
├── enums/               # Role, ComplaintStatus, Priority
├── exception/           # GlobalExceptionHandler, ResourceNotFoundException, UnauthorizedException
├── repository/          # Spring Data JPA Repositories
├── security/            # JwtTokenProvider, JwtAuthenticationFilter, UserPrincipal
└── service/             # Business interfaces & Service implementations
```

---

## 📡 REST API Summary

### Authentication (`/api/auth`)
- `POST /api/auth/register` — Register a student account
- `POST /api/auth/login` — Universal login (returns JWT token & role)
- `GET /api/auth/me` — Get current logged-in user profile

### Student Endpoints (`/api/complaints`)
- `POST /api/complaints` — Submit complaint (`multipart/form-data` with title, category, location, priority, description, image)
- `GET /api/complaints/my` — List complaints submitted by logged-in student
- `GET /api/complaints/{id}` — Get complaint details with complete audit timeline
- `POST /api/complaints/{id}/feedback` — Submit 1–5 star rating and feedback for resolved issue

### Admin Endpoints (`/api/admin`)
- `GET /api/admin/complaints` — Search and multi-filter complaints
- `PUT /api/admin/complaints/{id}/assign` — Assign technician
- `PUT /api/admin/complaints/{id}/priority` — Update priority
- `PUT /api/admin/complaints/{id}/status` — Status override
- `GET /api/admin/technicians` — Technician workload monitoring
- `POST /api/admin/technicians` — Register new technician
- `GET /api/admin/users` — List campus users by role

### Technician Endpoints (`/api/technician`)
- `GET /api/technician/complaints` — View assigned work orders
- `PUT /api/technician/complaints/{id}/start` — Advance status to `IN_PROGRESS`
- `PUT /api/technician/complaints/{id}/resolve` — Mark `RESOLVED` with repair documentation

### Analytics & Masters
- `GET /api/analytics/summary` — High-level KPI metrics & chart data
- `GET /api/categories` & `POST /api/categories`
- `GET /api/locations` & `POST /api/locations`

---

## 🎓 Viva Questions & Answers

### Q1: Why did you use Spring Boot instead of raw Servlets and JSP?
> **Answer**: Servlets require writing extensive boilerplate code for request dispatching, object mapping, and manual connection pooling. Spring Boot provides an inversion of control (IoC) container, automatic configuration, built-in HikariCP connection pooling, and an embedded Tomcat server, enabling a clean 3-tier enterprise architecture.

### Q2: Why use DTOs instead of returning JPA Entities directly?
> **Answer**: Entities represent the database schema and Hibernate relationships. DTOs (Data Transfer Objects) prevent over-posting security vulnerabilities, avoid exposing sensitive columns (like password hashes), eliminate circular reference loops during JSON serialization, and decouple API contracts from the database design.

### Q3: How is authentication and role-based access control secured?
> **Answer**: We use stateless JSON Web Tokens (JWT). Passwords are encrypted using BCrypt with adaptive salting. Upon login, a cryptographically signed JWT with claims (`userId`, `role`) is returned. The custom `JwtAuthenticationFilter` intercepts requests and injects the `UserPrincipal` into Spring's `SecurityContextHolder`. Method security is enforced using `@PreAuthorize("hasRole('...')")`.

### Q4: How is data consistency maintained during complaint state transitions?
> **Answer**: All state changes are executed inside `@Transactional` service methods. When a ticket advances (e.g. from `ASSIGNED` to `IN_PROGRESS`), Hibernate updates the `complaints` record and inserts an immutable audit log entry into `complaint_updates`. If any step fails, the entire transaction is rolled back.
