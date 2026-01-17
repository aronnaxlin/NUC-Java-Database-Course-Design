# Smart Property Management System

## 1. Project Overview
The **Smart Property Management System** is a comprehensive Java-based solution designed to modernize residential property administration. It integrates core property management functions—such as user archiving, asset tracking, and financial processing—with advanced AI-driven interactive capabilities.

### 1.1 Core Capabilities
- **Lifecycle Management**: Full-stack management of Owner, Property, and Tenant data lifecycles.
- **Financial Control**: Automated fee calculation, payment processing, and arrears analysis.
- **Operational Enforcement**: "Hard-lock" mechanisms that prevent utility (water/electricity) top-ups when property fees are in arrears (see *Business Logic Specification*).
- **Intelligent Assistance**: Integrated AI copilot (powered by Alibaba Qwen) for natural language business queries and decision support.
- **Data Visualization**: Real-time dashboards for financial health monitoring and occupancy statistics.

---

## 2. Technical Architecture

### 2.1 Technology Stack
- **Backend Framework**: Spring Boot (IOC, AOP, MVC)
- **Persistence Layer**: Spring JDBC (JdbcTemplate) for raw SQL performance and control.
- **Database**: MySQL 8.0 (Normalized to 3NF).
- **Frontend**: HTML5, CSS3, JavaScript (ES6+), ECharts.
- **External Integration**: Aliyun DashScope SDK (AI Services).

### 2.2 Architectural Pattern
The system adheres to a strict **Spring Boot 3-Tier Architecture** to ensure separation of concerns and maintainability.

| Layer | Component | Responsibility | Pattern/Annotation |
| :--- | :--- | :--- | :--- |
| **Presentation** | `controller` | Handles HTTP requests, input validation, and JSON response formatting. | `@RestController` |
| **Business** | `service` | Implements core logic, transaction management, and cross-component orchestration. | `@Service`, `@Transactional` |
| **Data Access** | `dao` | Manages database connectivity and SQL execution. | `@Repository` |
| **Model** | `entity` | Represents database schematics (POJOs). | N/A |
| **Transfer** | `dto` | Data Transfer Objects for decoupled API contracts. | N/A |

### 2.3 Directory Structure
```
src/main/java/site/aronnax
├── PropertyManagementApplication.java  # Application Entry Point
├── config/                             # Configuration (CORS, Interceptors)
├── controller/                         # REST Controllers
├── service/                            # Service Interfaces
├── service/impl/                       # Service Implementations
├── dao/                                # Data Access Objects
├── entity/                             # Domain Models
├── dto/                                # Data Transfer Objects
└── util/                               # Utilities (Encryption, Formatting)
```

---

## 3. Key Design Decisions

### 3.1 Pre-compiled SQL
To maximize security and performance, all database interactions utilize `PreparedStatement` via Spring's `JdbcTemplate`. This explicitly mitigates SQL injection risks and leverages database-side query caching.

### 3.2 Constructor Injection
The project enforces **Constructor-based Dependency Injection** for all managed components. This ensures:
- **Immutability**: Dependencies can be declared `final`.
- **Reliability**: Prevents `NullPointerException` on uninitialized beans.
- **Testability**: Facilitates easy mocking in unit tests.

### 3.3 Transaction Management
Critical write operations (e.g., fee generation, payment processing) are wrapped in `@Transactional` contexts to enforce atomic consistency (ACID properties).

---

## 4. Request Flow Diagram
A typical request (e.g., **User Login**) follows this synchronous execution path:

1.  **Request**: Client sends `POST /api/user/login` with JSON payload.
2.  **Controller**: `UserOwnerController` deserializes JSON to DTO.
3.  **Service**: `UserService` authenticates credentials and validates account status.
4.  **DAO**: `UserDAO` executes parameterized SQL (`SELECT * FROM users WHERE...`).
5.  **Response**: Result propagates up the stack, serialized to standard JSON envelope (`Result<T>`) for client consumption.
