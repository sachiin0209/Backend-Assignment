# Quick Start Guide - Finance Dashboard API

## 🚀 Fastest Way to Run (Docker Compose)

### Prerequisites
- Docker and Docker Compose installed
- Git

### Steps (5 minutes)

```bash
# 1. Clone repository
git clone <repository-url>
cd Backend-Assignment

# 2. Start everything
docker-compose up -d

# 3. Wait for services to be healthy
docker ps  # Check both services are running

# 4. Access the API
curl http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"password123"}'

# 5. View Swagger UI (if enabled in config)
# http://localhost:8080/api/swagger-ui.html

# 6. Stop everything
docker-compose down
```

---

## 💻 Local Development Setup

### Prerequisites
- Java 17+
- PostgreSQL 13+
- Maven 3.6+
- Git

### Steps (10 minutes)

```bash
# 1. Clone repository
git clone <repository-url>
cd Backend-Assignment

# 2. Create PostgreSQL database
psql -U postgres -c "CREATE DATABASE finance_db;"

# 3. Update database config (optional)
# Edit: src/main/resources/application.yml
# Update postgresql connection details if needed

# 4. Build project
mvn clean install

# 5. Run application
mvn spring-boot:run

# 6. API is ready at
# http://localhost:8080/api

# 7. Login with sample credentials
curl http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"password123"}'
```

---

## 🧪 Sample Credentials

### Test Users (all use password: `password123`)

| Email | Role | Permissions |
|-------|------|-------------|
| admin@example.com | ADMIN | User management, all records |
| john.analyst@example.com | ANALYST | Create/edit own records, dashboard |
| jane.viewer@example.com | VIEWER | Dashboard view only |
| bob.smith@example.com | ANALYST | Create/edit own records |

---

## 📚 First API Call

### 1. Login and get JWT Token

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.analyst@example.com",
    "password": "password123"
  }'

# Response:
# {
#   "token": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
#   "type": "Bearer",
#   "userId": 2,
#   "email": "john.analyst@example.com",
#   "role": "ANALYST"
# }

# Save token for next calls
TOKEN="eyJhbGciOi..."  # Replace with actual token
```

### 2. Use Token to Access Protected Endpoints

```bash
# Get user profile
curl -X GET http://localhost:8080/api/users/2 \
  -H "Authorization: Bearer $TOKEN"

# Create a financial record
curl -X POST http://localhost:8080/api/records \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 1000.00,
    "type": "INCOME",
    "category": "Bonus",
    "transactionDate": "2024-01-15",
    "description": "Quarterly bonus"
  }'

# Get dashboard summary
curl -X GET http://localhost:8080/api/dashboard/summary \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🔧 Configuration

### Environment Variables

Create `.env` file (optional, already has example):

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/finance_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# JWT
JWT_SECRET=your-super-secret-key-minimum-32-chars
JWT_EXPIRATION=86400000

# Spring Profile
SPRING_PROFILES_ACTIVE=prod
```

### Application Profiles

- **Development**: `application.yml` (default)
  - SQL logging enabled
  - Swagger UI enabled
  - Full error details

- **Production**: `application-prod.yml`
  - SQL logging disabled
  - Swagger UI disabled
  ```bash
  SPRING_PROFILES_ACTIVE=prod
  ```

---

## 📊 Import Postman Collection

1. **Download**: `Finance-Dashboard-API.postman_collection.json`

2. **Open Postman** → Import → Select file

3. **Set Environment Variables**:
   - `base_url`: `http://localhost:8080/api`
   - `token`: (auto-set after login)
   - `userId`: (auto-set after login)

4. **Use Collections**:
   - Run "Auth → Login" first
   - Then use other endpoints

---

## 🧪 Run Tests

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=UserServiceTest

# Generate coverage report
mvn test jacoco:report
# Report: target/site/jacoco/index.html
```

---

## 🐛 Troubleshooting

### Port Already in Use

```bash
# Find process using port 8080
lsof -i :8080
# Kill process
kill -9 <PID>
```

### Database Connection Failed

```bash
# Check PostgreSQL is running
psql -U postgres -c "SELECT 1"

# Check database exists
psql -U postgres -l | grep finance_db

# Create database if missing
psql -U postgres -c "CREATE DATABASE finance_db;"
```

### JWT Token Expired

```bash
# Get new token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"password123"}'
```

### 403 Forbidden

```bash
# Check you're using right user role
# ADMIN: Full access
# ANALYST: Records only  
# VIEWER: Dashboard only

# Use admin token for user management:
# Email: admin@example.com
# Password: password123
```

---

## 📈 Next Steps

1. **View API Documentation**
   - Swagger: `http://localhost:8080/api/swagger-ui.html`
   - OpenAPI: `http://localhost:8080/api/v3/api-docs`

2. **Explore Endpoints**
   - Check `README.md` for full endpoint list
   - See `DESIGN_DECISIONS.md` for architecture details

3. **Create Test Data**
   - Create users with different roles
   - Add financial records
   - View dashboard analytics

4. **Customize**
   - Modify JWT expiration
   - Change roles as needed
   - Add new fields/endpoints

---

## 📚 Documentation

- **README.md** - Complete API documentation
- **DESIGN_DECISIONS.md** - Architecture & design patterns
- **Finance-Dashboard-API.postman_collection.json** - API examples

---

## 🆘 Need Help?

1. Check existing README.md
2. Review Swagger UI at `/swagger-ui.html`
3. Check application logs for errors
4. Review DESIGN_DECISIONS.md for architecture
5. Check test files for usage examples

---

**Version**: 1.0.0  
**Last Updated**: 2024-01-15  
**Status**: Production Ready ✅
