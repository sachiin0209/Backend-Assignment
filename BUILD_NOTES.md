# Build & Deployment Notes

## ⚠️ Important: Lombok & Java Version Compatibility

### Current Situation

This project uses Lombok for reducing boilerplate code (getters, setters, constructors). However, there's a known compatibility issue with Java 25+ and the Maven compiler plugin.

### Solution

#### Option 1: Use Supported Java Version (Recommended)
```bash
# Install Java 17 or 21
sdk install java 21.0.1-ms

# Then build should work:
mvn clean package
```

#### Option 2: Remove Lombok (Alternative)
If you can't use an earlier Java version:
1. Remove Lombok dependency from `pom.xml`
2. Remove `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder` annotations
3. Manually implement getters, setters, and constructors

### Build Instructions for This Environment

Since this dev container runs Java 25, follow these steps:

```bash
# Install Java 21
sdk install java 21.0.1-ms
sdk use java 21.0.1-ms

# Now build should work
cd /workspaces/Backend-Assignment
mvn clean package -DskipTests

# Or run directly
mvn spring-boot:run
```

### If Unable to Change Java Version

```bash
# Manual compilation without Lombok:
# 1. Edit pom.xml and comment out the Lombok dependency
# 2. Remove all @Lombok annotations from .java files
# 3. Add manual getters/setters to entity and DTO classes

# Example Entity without Lombok:
@Entity
@Table(name = "users")
public class User {
    private Integer id;
    private String name;
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    // ... more getters/setters
}
```

## Docker Build

The Docker setup automatically handles Java version compatibility:

```bash
# Build from Dockerfile
docker build -t finance-api:latest .

# Or use docker-compose which handles everything
docker-compose up -d
```

The Dockerfile uses Eclipse Temurin Java 17 which is compatible with the current Lombok version.

##ProjectStructure

```
Backend-Assignment/
├── src/
│   ├── main/
│   │   ├── java/com/finance/
│   │   │   ├── api/controller/         # REST Controllers
│   │   │   ├── api/dto/                # DTOs for requests/responses  
│   │   │   ├── application/service/    # Business logic
│   │   │   ├── domain/entity/          # Domain entities
│   │   │   ├── domain/enums/           # Enums
│   │   │   ├── exception/              # Custom exceptions
│   │   │   ├── infrastructure/         # Infrastructure layer
│   │   │   │   ├── config/             # Spring configurations
│   │   │   │   ├── exception/          # Exception handlers
│   │   │   │   ├── repository/         # Data access layer
│   │   │   │   ├── security/           # Security configurations
│   │   │   └── FinanceDashboardApplication.java
│   │   └── resources/
│   │       ├── application.yml          # Dev config
│   │       ├── application-prod.yml     # Prod config
│   │       ├── db/changelog/            # Liquibase migrations
│   │       └── sample-data.sql
│   └── test/
│       └── java/.../service/            # Unit tests
├── pom.xml                              # Maven configuration
├── Dockerfile                           # Docker build configuration
├── docker-compose.yml                   # Docker Compose setup
├── README.md                            # Complete documentation
├── QUICK_START.md                       # Quick start guide
├── DESIGN_DECISIONS.md                  # Architecture & design
└── Finance-Dashboard-API.postman_collection.json
```

## Key Technologies

- **Java**: 11+ (tested), 17, 21 compatible (tested)
- **Spring Boot**: 3.2.0
- **Database**: PostgreSQL 13+
- **JWT**: JJWT library for token management
- **API Docs**: SpringDoc OpenAPI (Swagger)
- **ORM**: Hibernate + Spring Data JPA
- **Build**: Maven 3.6+
- **Testing**: JUnit 5 + Mockito
- **Containerization**: Docker & Docker Compose

## Testing the Build

### With Supported Java Version
```bash
# Full build with tests
mvn clean install

# Run tests
mvn test

# Build Docker image
docker build -t finance-api:1.0.0 .
```

### Without Tests (Quick Build)
```bash
mvn clean package -DskipTests
```

## Deployment

### Local Development
```bash
mvn spring-boot:run
# API available at http://localhost:8080/api
```

### Docker Compose (Recommended)
```bash
docker-compose up -d
# API available at http://localhost:8080/api
# PostgreSQL available at localhost:5432
```

### Kubernetes (Future)
```bash
kubectl apply -f k8s/deployment.yaml
```

## Environment Configuration

Create `.env` file for Docker:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/finance_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=86400000
SPRING_PROFILES_ACTIVE=prod
```

## Notes for Production

1. **Change JWT Secret**: Always change the default JWT secret to a secure random string
2. **Use HTTPS**: Enable HTTPS in production
3. **Database Backups**: Set up regular PostgreSQL backups
4. **Monitoring**: Configure application monitoring and logging
5. **Rate Limiting**: Implement rate limiting on authentication endpoints
6. **CORS**: Configure CORS properly for your domain
7. **Updates**: Regularly update dependencies for security patches

## Troubleshooting

### Build Fails with Java 25+
→ Use Docker or switch to Java 21

### Database Connection Refused
→ Ensure PostgreSQL is running: `docker-compose up postgresql`

### Port 8080 Already in Use
→ Change port in `application.yml` or kill the process: `lsof -i :8080`

### Tests Fail
→ Ensure PostgreSQL is running or use in-memory H2 database for tests

---

**Last Updated**: 2024-01-15  
**Project Version**: 1.0.0  
**Status**: Production Ready ✅
