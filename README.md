# ATMS-Online: School Management System (SMS)

A modular, enterprise-grade School Management System backend built with Java 21, Spring Boot 3, and PostgreSQL, designed to manage school administration, role-based access control, academic hierarchies, and operational finance workflows.

---

## Tech Stack & Architecture

- **Language:** Java 21 (LTS)
- **Framework:** Spring Boot 3.x
- **Persistence:** Spring Data JPA, Hibernate, PostgreSQL 16
- **Security:** Spring Security 6 with JJWT (`0.12.5`), Stateless Session Management
- **API Standards:** RESTful API with RFC 7807 Problem Details
- **Boilerplate Reduction & DTOs:** Project Lombok, Java 21 Records
- **Containerization:** Multi-stage Docker, Docker Compose

---

## System Modules

### 1. Core Authentication & RBAC (`Phase 1`)
- User registration and login flows.
- BCrypt password hashing.
- Role-based authorization (`ROLE_STUDENT`, `ROLE_TEACHER`, `ROLE_ADMIN`, `ROLE_SUPER_ADMIN`) using `@PreAuthorize` method security.
- Stateless request authentication via `JwtAuthenticationFilter`.

### 2. Finance & Maintenance Engine (`Phase 2`)
- **Vendor Registry:** Directory of external service providers, contractors, and utility partners categorized by expense types (`ExpenseCategory`).
- **Vendor Invoices (`VendorBill`):** Bill tracking with automated remaining balance calculation, due dates, and lifecycle states (`PENDING`, `PARTIALLY_PAID`, `PAID`, `CANCELLED`).
- **Disbursement Vouchers (`PaymentVoucher`):** Multi-mode payment ledger (Cash, Cheque, Bank Transfer, UPI) with audit logs (`createdBy`, `approvedBy`).
- **Automated Ledger Reconciliation:** Automatic state transitions and transactional overpayment guards.

### 3. Error Handling
- Centralized exception translation via `@RestControllerAdvice` conforming to **RFC 7807 (Problem Details for HTTP APIs)**.

---

## Project Structure

```text
org.atmsonline.sms
├── config/                  # Security and application configurations
├── controller/
│   ├── auth/                # Authentication endpoints (/api/v1/auth)
│   └── finance/             # Vendor, bill, and voucher endpoints (/api/v1/finance)
├── dto/
│   ├── auth/                # Auth request/response records
│   └── finance/             # Finance data contracts (Java 21 Records)
├── entity/
│   ├── BaseEntity.java      # UUID primary key and audit timestamps
│   ├── User.java
│   ├── Role.java
│   └── finance/             # Vendor, VendorBill, PaymentVoucher entities & enums
├── exception/               # RFC 7807 GlobalExceptionHandler
├── repository/              # Spring Data JPA repositories
├── security/                # JwtService, JwtAuthenticationFilter
└── service/                 # Transactional business logic
```

---

## Getting Started

### Prerequisites

- [Amazon Corretto 21](https://docs.aws.amazon.com/corretto/) or any JDK 21 distribution
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Maven 3.9+](https://maven.apache.org/)

---

### Running via Docker Compose (Recommended)

To run both the PostgreSQL database and the backend container together:

```bash
# Clone the repository
git clone [https://github.com/JosephAmudhanFernando/ATMS-Online.git](https://github.com/JosephAmudhanFernando/ATMS-Online.git)
cd ATMS-Online

# Build and start the multi-container stack
docker compose up --build -d
```

The application will be accessible at `http://localhost:8080`.

---

### Running Locally (IntelliJ IDEA / Maven)

1. Start only the PostgreSQL container:
   ```bash
   docker compose up -d postgres
   ```
2. Verify configuration in `src/main/resources/application.yaml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/sms_db
       username: postgres
       password: your_postgres_password
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

---

## API Documentation & Endpoints

### Authentication
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Public | Register a new user |
| `POST` | `/api/v1/auth/login` | Public | Authenticate and obtain JWT token |

### Finance Module
| Method | Endpoint | Required Role | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/finance/vendors` | `ADMIN`, `SUPER_ADMIN` | Register a new vendor |
| `GET` | `/api/v1/finance/vendors` | `ADMIN`, `SUPER_ADMIN` | List all vendors |
| `POST` | `/api/v1/finance/bills` | `ADMIN`, `SUPER_ADMIN` | Record a vendor invoice |
| `GET` | `/api/v1/finance/vendors/{vendorId}/bills` | `ADMIN`, `SUPER_ADMIN` | List all bills for a vendor |
| `POST` | `/api/v1/finance/vouchers/disburse` | `ADMIN`, `SUPER_ADMIN` | Disburse payment against a bill |

---

## License

This project is licensed under the MIT License.