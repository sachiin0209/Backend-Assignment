# Finance Dashboard API - Architecture & Design Decisions

## Overview

This document outlines the key architectural decisions and design patterns used in the Finance Dashboard API.

## 1. Architecture

### 1.1 Layered Architecture

```
┌─────────────────────────────────────────────────────────┐
│            REST Controllers (API Layer)                 │
│  AuthController | UserController | RecordController    │
│          DashboardController | Error Handlers           │
└──────────────────┬──────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────────┐
│            Business Logic (Service Layer)               │
│  UserService | AuthService | FinancialRecordService    │
│       DashboardService | JwtService                     │
└──────────────────┬──────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────────┐
│          Data Access (Repository Layer)                 │
│  UserRepository | FinancialRecordRepository             │
└──────────────────┬──────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────────┐
│              Domain Models (Entity Layer)               │
│  User | FinancialRecord | Enums                         │
└──────────────────┬──────────────────────────────────────┘
                   │
┌──────────────────▼──────────────────────────────────────┐
│           Database (PostgreSQL)                         │
└─────────────────────────────────────────────────────────┘
```

**Rationale:**
- Clear separation of concerns
- Easy to test each layer independently
- Scalable and maintainable
- Standard Spring Boot best practices

### 1.2 Design Patterns

#### 1. DTO Pattern (Data Transfer Object)
**Files:** `api/dto/*`

**Purpose:** Decouple API contracts from domain models

**Benefits:**
- Request/response validation independently
- API versioning without changing domain models
- Security (hide sensitive fields)
- Domain model changes don't break API clients

**Implementation:**
- Request DTOs: CreateUserRequest, CreateFinancialRecordRequest
- Response DTOs: UserResponse, FinancialRecordResponse
- Separate commands (Create/Update) from queries (Get/List)

#### 2. Repository Pattern
**Files:** `infrastructure/repository/*`

**Purpose:** Abstract data access logic

**Benefits:**
- Swap database implementations easily
- Testable with mocks
- Centralized query logic
- Query optimization in one place

**Implementation:**
- UserRepository: User queries by email, role
- FinancialRecordRepository: Complex aggregation queries
- Spring Data JPA for CRUD and custom queries

#### 3. Service Pattern
**Files:** `application/service/*`

**Purpose:** Encapsulate business logic

**Benefits:**
- Transaction management
- Business rule enforcement
- Reusable across controllers
- Easier testing

**Implementation:**
- UserService: User lifecycle management
- FinancialRecordService: Record CRUD & filtering
- DashboardService: Analytics aggregation
- AuthService: Authentication flow

#### 4. Global Exception Handler Pattern
**Files:** `infrastructure/exception/GlobalExceptionHandler.java`

**Purpose:** Centralized error handling and consistent error responses

**Benefits:**
- Consistent error API across all endpoints
- Cleaner controller code
- Easier to add logging/monitoring
- Better user experience

**HTTP Status Mapping:**
- 400: Bad Request / Validation Errors
- 401: Unauthorized (missing token)
- 403: Forbidden (insufficient permissions)
- 404: Not Found
- 500: Internal Server Error

#### 5. JWT Authentication Pattern
**Files:** `infrastructure/security/*`

**Purpose:** Stateless authentication and authorization

**Benefits:**
- Scalable (no sessions on server)
- Mobile-friendly
- Microservice-ready
- Reduced bandwidth

**Implementation:**
- JwtService: Token generation and validation
- JwtAuthenticationFilter: Extract and validate token
- SecurityConfig: Spring Security configuration
- Role-based access control via @PreAuthorize

#### 6. Configuration Pattern
**Files:** `infrastructure/config/*`

**Purpose:** Centralized Spring configuration

**Implementation:**
- OpenAPIConfig: Swagger/Springdoc configuration
- SecurityConfig: Security and JWT configuration
- Externalized configuration via application.yml

## 2. Database Design

### 2.1 Database Schema

#### Users Table
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,          -- BCrypt hashed
    role VARCHAR(50) DEFAULT 'VIEWER',       -- ENUM: VIEWER, ANALYST, ADMIN
    status VARCHAR(50) DEFAULT 'ACTIVE',     -- ENUM: ACTIVE, INACTIVE
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    INDEX idx_users_email (email)
);
```

**Design Decisions:**
- Email as unique identifier (allows social login in future)
- Role-based access (3 levels: VIEWER, ANALYST, ADMIN)
- Status field for soft deactivation (no deletion)
- Audit timestamps (created_at, updated_at)

#### Financial Records Table
```sql
CREATE TABLE financial_records (
    id SERIAL PRIMARY KEY,
    amount DECIMAL(19,2) NOT NULL,           -- Precision: 19, Scale: 2
    type VARCHAR(50) NOT NULL,               -- ENUM: INCOME, EXPENSE
    category VARCHAR(100) NOT NULL,
    transaction_date DATE NOT NULL,
    description TEXT,
    created_by INTEGER NOT NULL,             -- Foreign key to users
    is_deleted BOOLEAN DEFAULT FALSE,        -- Soft delete
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_created_by (created_by),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_type (type),
    INDEX idx_category (category),
    INDEX idx_is_deleted (is_deleted)
);
```

**Design Decisions:**
- DECIMAL(19,2): Financial data precision
- Type and Category as VARCHAR (allows flexibility vs ENUM)
- User isolation (every record has creator)
- Soft delete via is_deleted flag (audit trail, compliance)
- Multiple indexes for filtering queries

### 2.2 Index Strategy

**Indexes Created:**
1. `idx_users_email`: Fast user login lookup
2. `idx_financial_records_created_by`: User record filtering
3. `idx_financial_records_transaction_date`: Date range queries
4. `idx_financial_records_type`: Type filtering (INCOME/EXPENSE)
5. `idx_financial_records_category`: Category grouping
6. `idx_financial_records_is_deleted`: Soft delete filtering

**Rationale:**
- Optimize common query paths
- Support dashboard aggregations
- Minimum indexes to avoid write overhead

## 3. Security Design

### 3.1 Authentication Flow

```
1. User sends POST /auth/login with email + password
2. AuthService validates credentials
3. If valid, JwtService generates JWT token
4. JwtAuthenticationFilter validates token on each request
5. SecurityContext stores user principal
```

### 3.2 Authorization Flow

```
1. User makes request with JWT token
2. JwtAuthenticationFilter extracts authorities from token
3. SecurityContext set with user and roles
4. @PreAuthorize evaluates role requirements
5. Request proceeds or returns 403 Forbidden
```

### 3.3 Role-Based Access Control (RBAC)

**Three Roles:**
- **VIEWER**: Dashboard read-only access
- **ANALYST**: Full record management (own records only)
- **ADMIN**: User management + system-wide access

**Implementation:**
```java
@PreAuthorize("hasRole('ADMIN')")           // ADMIN only
@PreAuthorize("hasAnyRole('ANALYST','ADMIN')") // ANALYST or ADMIN
@PreAuthorize("hasAnyRole('VIEWER','ANALYST','ADMIN')") // All authenticated
```

### 3.4 Password Security

- **Algorithm:** BCrypt with strength 10
- **Minimum Length:** 6 characters
- **Storage:** Never store plain text
- **Hash Verification:** Automatic in AuthService

### 3.5 Data Isolation

**User Isolation Principle:**
- Users see only their own records
- UserService.getUserByEmail() retrieves user-scoped service
- FinancialRecordService checks ownership before operations
- DashboardService aggregates user-specific data

## 4. API Design

### 4.1 RESTful Conventions

**Resource URLs:**
- `POST /users` - Create user (ADMIN)
- `GET /users/{id}` - Get user
- `PUT /users/{id}` - Update user (ADMIN)
- `GET /records` - List records (pagination)
- `POST /records` - Create record (ANALYST)
- `PUT /records/{id}` - Update record
- `DELETE /records/{id}` - Soft delete record
- `GET /dashboard/summary` - Analytics dashboard

**HTTP Methods:**
- GET: Safe, idempotent - retrieve data
- POST: Create - new resources
- PUT: Replace - update resources
- DELETE: Remove - soft deletes via is_deleted flag

### 4.2 Pagination

**Default:**
- Page size: 10
- Page number: 0-based

**Query Parameters:**
```
GET /records?page=0&size=10
GET /records?page=1&size=20
```

### 4.3 Error Responses

**Standard Error Format:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/records",
  "details": {
    "amount": "Must be positive"
  }
}
```

### 4.4 Soft Deletes

**Why:**
- Audit trails
- Compliance requirements
- Data recovery
- Historical data preservation

**Implementation:**
- is_deleted flag (BOOLEAN)
- All queries filter: `WHERE is_deleted = false`
- DELETE endpoint sets flag instead of removing
- Hard delete never happens

## 5. Performance Optimizations

### 5.1 Database Optimizations

**N+1 Prevention:**
- Use parameterized queries
- Lazy loading for relationships
- Aggregate queries at database level

**Example - Dashboard Aggregations:**
```sql
-- Native SQL for aggregations (more efficient)
SELECT TO_CHAR(transaction_date, 'YYYY-MM') as month,
       SUM(CASE WHEN type='INCOME' THEN amount ELSE 0 END) as income,
       SUM(CASE WHEN type='EXPENSE' THEN amount ELSE 0 END) as expenses
FROM financial_records
WHERE is_deleted = false AND created_by = ?
GROUP BY TO_CHAR(transaction_date, 'YYYY-MM')
```

### 5.2 Query Optimization

- **Pagination:** Limit result sets (default 10 items)
- **Indexes:** Strategic indexes on filter columns
- **Native Queries:** For complex aggregations
- **Connection Pooling:** HikariCP with 20 max connections

### 5.3 API Optimization

- **Compression:** HTTP compression for responses
- **No N+1 Queries:** Avoid loading nested entities
- **Caching Ready:** Structure allows Redis caching

### 5.4 Future Optimizations

- Redis caching for dashboard summaries
- Database result caching for aggregations
- Query result pagination for large datasets
- Async processing for heavy operations

## 6. Testing Strategy

### 6.1 Unit Tests

**Coverage Areas:**
- UserService: CRUD, role assignment, activation
- FinancialRecordService: CRUD, filtering, ownership checks
- AuthService: Login flow, credential validation

**Test Files:**
- `UserServiceTest.java`: 11 test cases
- `FinancialRecordServiceTest.java`: 9 test cases

**Mocking:**
- Repository mocks via Mockito
- No database dependency
- Fast test execution

### 6.2 Test Execution

```bash
mvn test                           # Run all tests
mvn test -Dtest=UserServiceTest   # Run specific test
mvn test jacoco:report            # Generate coverage report
```

## 7. Configuration Management

### 7.1 Environment Profiles

**Development (application.yml)**
- DDL: create-drop
- SQL logging: enabled
- Swagger: enabled
- JWT expiration: 24 hours

**Production (application-prod.yml)**
- DDL: validate
- SQL logging: disabled
- Swagger: disabled
- External configuration via environment variables

### 7.2 Externalized Configuration

**Via Environment Variables:**
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://...
SPRING_DATASOURCE_USERNAME=...
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000
```

**Via .env file (Docker):**
```bash
source .env
docker-compose up
```

## 8. Deployment Architecture

### 8.1 Docker Setup

**Components:**
1. Finance API Container (Spring Boot)
2. PostgreSQL Container (Database)
3. Docker Network (Internal communication)

**Benefits:**
- Isolated environments
- Easy scaling
- Consistent across machines
- Production-ready

### 8.2 Health Checks

**API Health Endpoint:**
```
GET /actuator/health
```

**Docker Health Check:**
```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health
```

## 9. Key Dependencies

| Dependency | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 3.2.0 | Framework |
| Spring Security | 6.2.x | Authentication |
| Spring Data JPA | 3.2.x | ORM |
| JJWT | 0.12.3 | JWT tokens |
| PostgreSQL Driver | Latest | Database |
| Liquibase | Latest | Migrations |
| Lombok | Latest | Boilerplate |
| SpringDoc OpenAPI | 2.1.0 | Swagger UI |
| JUnit 5 | Latest | Testing |
| Mockito | Latest | Mocking |

## 10. Future Enhancements

### 10.1 Short Term
- [ ] Rate limiting (Spring Cloud Gateway)
- [ ] Request/Response logging (Spring Cloud Sleuth)
- [ ] Advanced caching (Redis)
- [ ] Integration tests
- [ ] API versioning

### 10.2 Long Term
- [ ] Real-time notifications (WebSockets)
- [ ] Bulk operations (batch API)
- [ ] File exports (PDF, Excel)
- [ ] Advanced analytics (ML predictions)
- [ ] Audit logs (Spring Data Envers)
- [ ] OAuth2/OIDC integration
- [ ] Multi-tenancy support

## 11. Assumptions

1. **Email Uniqueness**: One email per user
2. **User Isolation**: Users only see their data
3. **No Hierarchical Roles**: Roles are flat (no inheritance)
4. **UTC Timezone**: All times in UTC
5. **Synchronous Operations**: No async processing
6. **Single Database**: No sharding or federation
7. **Numeric IDs**: Using database sequences for IDs

## 12. Known Limitations & Trade-offs

1. **No Soft Deletes for Users**: Simplifies user management
2. **No Built-in Audit Logging**: Can be added via Spring Data Envers
3. **No Full-Text Search**: Can use PostgreSQL full-text or Elasticsearch
4. **No GraphQL**: REST only (GraphQL layer could wrap this)
5. **Single JWT Secret**: Harder to rotate keys (can use key management service)

## Conclusion

This design prioritizes:
- **Maintainability**: Clear code structure and patterns
- **Security**: JWT auth, RBAC, input validation
- **Performance**: Database optimization, pagination
- **Scalability**: Stateless, Docker-ready, extensible
- **Testability**: Dependency injection, mocking-friendly

The architecture follows Spring Boot best practices and is suitable for production use with proper configuration and monitoring.
