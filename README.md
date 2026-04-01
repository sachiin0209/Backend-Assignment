# Finance Dashboard API - Production-Quality Backend System

A comprehensive, production-ready Spring Boot backend system for managing financial data with role-based access control, JWT authentication, and advanced dashboard analytics.

## 🎯 Overview

The Finance Dashboard API provides a complete solution for financial record management with the following capabilities:

- **User Management**: Role-based access control (VIEWER, ANALYST, ADMIN)
- **Financial Records**: Create, read, update, and delete financial transactions
- **Dashboard Analytics**: Real-time aggregation and insights on financial data
- **Access Control**: JWT-based authentication with role-based authorization
- **Audit Trail**: Automatic tracking of record creation/updates
- **API Documentation**: Auto-generated Swagger/OpenAPI documentation

## 🏗️ Architecture & Design

### Layered Architecture

```
┌─────────────────┐
│   Controllers   │ (REST API endpoints)
├─────────────────┤
│    Services     │ (Business logic)
├─────────────────┤
│  Repositories   │ (Data access)
├─────────────────┤
│   Entities      │ (Domain models)
├─────────────────┤
│   Database      │ (PostgreSQL)
└─────────────────┘
```

### Key Design Patterns

- **DTO Pattern**: Request/response objects for clean API contracts
- **Repository Pattern**: Data access abstraction
- **Service Pattern**: Business logic encapsulation
- **Exception Handling**: Global exception handler for consistent error responses
- **JWT Authentication**: Stateless, token-based authentication
- **Role-Based Access Control (RBAC)**: Method-level security

### Technology Stack

```
Language:           Java 17
Framework:          Spring Boot 3.2.0
Security:           Spring Security + JWT (JJWT)
Database:           PostgreSQL 16
Migrations:         Liquibase
ORM:                JPA/Hibernate
API Documentation: SpringDoc OpenAPI (Swagger UI)
Testing:            JUnit 5 + Mockito
Build Tool:         Maven
Containerization:   Docker & Docker Compose
```

## 📊 Data Model

### Users Table
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'VIEWER',          -- VIEWER, ANALYST, ADMIN
    status VARCHAR(50) DEFAULT 'ACTIVE',        -- ACTIVE, INACTIVE
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_users_email (email)
);
```

### Financial Records Table
```sql
CREATE TABLE financial_records (
    id SERIAL PRIMARY KEY,
    amount DECIMAL(19,2) NOT NULL,
    type VARCHAR(50) NOT NULL,                  -- INCOME, EXPENSE
    category VARCHAR(100) NOT NULL,
    transaction_date DATE NOT NULL,
    description TEXT,
    created_by INTEGER NOT NULL,
    is_deleted BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_financial_records_created_by (created_by),
    INDEX idx_financial_records_transaction_date (transaction_date),
    INDEX idx_financial_records_type (type),
    INDEX idx_financial_records_category (category),
    INDEX idx_financial_records_is_deleted (is_deleted)
);
```

## 🔐 Role-Based Access Control

### VIEWER Role
- ✅ View dashboard analytics
- ✅ View own financial records
- ❌ Cannot create/edit/delete records
- ❌ Cannot manage users

### ANALYST Role
- ✅ View dashboard analytics
- ✅ Full CRUD operations on own financial records
- ✅ Filter records by date, type, category
- ✅ Advanced analytics access
- ❌ Cannot manage users

### ADMIN Role
- ✅ All ANALYST permissions
- ✅ Full user management (create, update, activate/deactivate)
- ✅ Assign roles to users
- ✅ Access all users' records for reporting
- ✅ System-wide analytics

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- PostgreSQL 13 or higher
- Maven 3.6+
- Docker & Docker Compose (optional)

### Installation

#### Option 1: Local Setup (with existing PostgreSQL)

1. **Clone the repository**
```bash
git clone <repository-url>
cd Backend-Assignment
```

2. **Configure database** (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/finance_db
    username: postgres
    password: postgres
```

3. **Build the project**
```bash
mvn clean install
```

4. **Run the application**
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/api`

#### Option 2: Docker Compose Setup (Recommended)

1. **Start services**
```bash
docker-compose up -d
```

2. **Verify services**
```bash
docker ps
```

Services will be available at:
- API: `http://localhost:8080/api`
- PostgreSQL: `localhost:5432`

3. **Stop services**
```bash
docker-compose down
```

### Initial Setup & Sample Data

The database schema is automatically created using Liquibase migrations on first run.

**Create first ADMIN user via SQL:**
```sql
-- Hash password: password123
INSERT INTO users (name, email, password, role, status) VALUES 
('Admin User', 'admin@example.com', '$2a$10$slYQmyNdGzin7olVN3p5Be7DFH0KpZbP6PfRrFS53mLBGAMUlPl6m', 'ADMIN', 'ACTIVE');
```

**Or create via API after initial setup:**
```bash
# First, login to get a token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"password123"}'

# Create new user (requires ADMIN role)
curl -X POST http://localhost:8080/api/users \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Analyst",
    "email": "john.analyst@example.com",
    "password": "password123",
    "role": "ANALYST"
  }'
```

## 📚 API Endpoints

### Base URL: `http://localhost:8080/api`

### Authentication

#### Login
```
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "user@example.com",
  "role": "ANALYST"
}
```

### User Management (ADMIN only)

#### Create User
```
POST /users
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "New User",
  "email": "newuser@example.com",
  "password": "password123",
  "role": "ANALYST"
}
```

#### Get All Users
```
GET /users
Authorization: Bearer <token>
```

#### Get User by ID
```
GET /users/{userId}
Authorization: Bearer <token>
```

#### Update User
```
PUT /users/{userId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Updated Name",
  "role": "ADMIN"
}
```

#### Activate User
```
POST /users/{userId}/activate
Authorization: Bearer <token>
```

#### Deactivate User
```
POST /users/{userId}/deactivate
Authorization: Bearer <token>
```

#### Assign Role
```
POST /users/{userId}/assign-role/{role}
Authorization: Bearer <token>
```

### Financial Records (ANALYST & ADMIN)

#### Create Record
```
POST /records
Authorization: Bearer <token>
Content-Type: application/json

{
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "transactionDate": "2024-01-15",
  "description": "Monthly salary"
}
```

#### Get All Records (Paginated)
```
GET /records?page=0&size=10
Authorization: Bearer <token>
```

#### Get Record by ID
```
GET /records/{recordId}
Authorization: Bearer <token>
```

#### Filter by Date Range
```
GET /records/filter/date-range?startDate=2024-01-01&endDate=2024-12-31
Authorization: Bearer <token>
```

#### Filter by Type
```
GET /records/filter/type/INCOME
Authorization: Bearer <token>
```

#### Filter by Category
```
GET /records/filter/category/Salary
Authorization: Bearer <token>
```

#### Update Record
```
PUT /records/{recordId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "amount": 5500.00,
  "description": "Updated salary"
}
```

#### Delete Record (Soft Delete)
```
DELETE /records/{recordId}
Authorization: Bearer <token>
```

### Dashboard Analytics (VIEWER & above)

#### Get Complete Dashboard Summary
```
GET /dashboard/summary
Authorization: Bearer <token>

Response:
{
  "totalIncome": 50000.00,
  "totalExpenses": 15000.00,
  "netBalance": 35000.00,
  "categoryWiseTotals": {
    "Salary": 50000.00,
    "Groceries": 5000.00,
    "Utilities": 2000.00
  },
  "monthlyTrends": [
    {
      "month": "2024-01",
      "income": 5000.00,
      "expenses": 1500.00,
      "netAmount": 3500.00
    }
  ],
  "totalTransactions": 25
}
```

#### Get Total Income
```
GET /dashboard/income
Authorization: Bearer <token>
```

#### Get Total Expenses
```
GET /dashboard/expenses
Authorization: Bearer <token>
```

#### Get Net Balance
```
GET /dashboard/balance
Authorization: Bearer <token>
```

#### Get Category-wise Totals
```
GET /dashboard/category-totals
Authorization: Bearer <token>
```

#### Get Monthly Trends
```
GET /dashboard/monthly-trends
Authorization: Bearer <token>
```

#### Get Recent Transactions
```
GET /dashboard/recent-transactions?limit=10
Authorization: Bearer <token>
```

## 📋 Error Handling

All errors follow a consistent format:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid request parameters",
  "path": "/api/records",
  "details": {
    "amount": "Amount must be greater than 0"
  }
}
```

### HTTP Status Codes

- `200 OK`: Successful GET, PUT request
- `201 Created`: Successful POST request
- `204 No Content`: Successful DELETE request
- `400 Bad Request`: Invalid input/validation failure
- `401 Unauthorized`: Missing or invalid authentication
- `403 Forbidden`: Insufficient permissions
- `404 Not Found`: Resource not found
- `500 Internal Server Error`: Server error

## 🧪 Testing

Run unit tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=UserServiceTest
```

Test coverage:
```bash
mvn test jacoco:report
```

## 📖 API Documentation

### Swagger UI
Access auto-generated API documentation at:
```
http://localhost:8080/api/swagger-ui.html
```

### OpenAPI JSON Schema
```
http://localhost:8080/api/v3/api-docs
```

## 📦 Postman Collection

Import the provided Postman collection:
1. Open Postman
2. Click "Import" → Select `Finance-Dashboard-API.postman_collection.json`
3. Set environment variables:
   - `base_url`: `http://localhost:8080/api`
   - `token`: (automatically set after login request)
   - `userId`: (automatically set after login request)

## 🔒 Security Considerations

### JWT Configuration
- **Algorithm**: HS512 (HMAC with SHA-512)
- **Expiration**: 24 hours (configurable)
- **Secret Key**: Minimum 256 bits (MUST be changed in production)

### Password Security
- Using BCrypt hashing with configurable strength
- Minimum 6 characters required
- Stored securely in database

### Production Security Checklist
- [ ] Change JWT secret to secure random string (minimum 256 bits)
- [ ] Use HTTPS/TLS for all communications
- [ ] Enable CORS only for trusted domains
- [ ] Use environment variables for sensitive configuration
- [ ] Set up rate limiting on authentication endpoints
- [ ] Enable SQL query logging only in development
- [ ] Use connection pooling with minimum connections
- [ ] Regular security audits and dependency updates

## 📈 Performance Optimizations

### Database
- Indexed columns: email, created_by, transaction_date, type, category, is_deleted
- Connection pooling configured
- Soft deletes reduces database operations

### Queries
- Pagination for large datasets
- Calculated fields using native SQL for aggregations
- Lazy loading for relationships

### Caching Opportunities (Future)
- Cache user roles (Redis)
- Cache dashboard summaries
- Cache category lookups

## 📝 Assumptions & Design Decisions

1. **Soft Deletes**: Financial records are soft-deleted (is_deleted flag) for audit trail and compliance
2. **User Isolation**: Records are isolated by user - users only see their own data
3. **JWT Stateless**: No session storage required; scaling is straightforward
4. **Email Uniqueness**: Email is unique identifier for users
5. **Pagination**: Default page size of 10 to manage large datasets
6. **UTC Timezone**: All timestamps use UTC

## 🐛 Troubleshooting

### Database Connection Issues
```
Error: Connection refused
Solution: Ensure PostgreSQL is running on localhost:5432
Check: psql -U postgres -d postgres -c "SELECT 1"
```

### JWT Token Expire
```
Error: Token expired or invalid
Solution: Login again to get new token
```

### Insufficient Permissions
```
Error: 403 Forbidden - You don't have permission
Solution: Use ADMIN token for user management endpoints
```

### CORS Issues
```
Error: Cross-origin request blocked
Solution: Configure CORS in SecurityConfig if needed
```

## 📚 Key Classes & Components

| Component | Purpose |
|-----------|---------|
| `FinanceDashboardApplication` | Spring Boot entry point |
| `SecurityConfig` | Security & JWT configuration |
| `JwtAuthenticationFilter` | JWT token validation |
| `GlobalExceptionHandler` | Centralized error handling |
| `UserService` | User business logic |
| `FinancialRecordService` | Record management logic |
| `DashboardService` | Analytics & aggregation |
| `AuthService` | Authentication logic |
| Custom Exceptions | Domain-specific errors |

## 🔄 Development Workflow

1. Make changes to code
2. Run tests: `mvn test`
3. Build: `mvn clean install`
4. Run locally: `mvn spring-boot:run`
5. Test via Postman or Swagger UI
6. Commit changes

## 📦 Dependencies

Key dependencies with versions:
- Spring Boot: 3.2.0
- Spring Security: 6.2.x
- Spring Data JPA: 3.2.x
- PostgreSQL Driver: Latest
- JJWT (JWT): 0.12.3
- Liquibase: Latest
- Lombok: Latest
- SpringDoc OpenAPI: 2.1.0

## 🚦 CI/CD Considerations

### Build Pipeline
1. Checkout code
2. Run tests
3. Build Docker image
4. Push to registry
5. Deploy to staging
6. Run integration tests
7. Deploy to production

## 📄 License

This project is licensed under the Apache License 2.0.

## 👥 Support

For issues or questions:
1. Check existing documentation
2. Review Swagger UI for endpoint details
3. Check test files for usage examples
4. Review application logs

## 📞 Contact

Finance Dashboard Team
support@finance-dashboard.com

---

**Version**: 1.0.0  
**Last Updated**: 2024-01-15  
**Status**: Production Ready ✅