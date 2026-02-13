# Liberty Jakarta EE Application

This is a Jakarta EE 10 application running on Open Liberty, converted from the Quarkus application. It demonstrates a RESTful API for managing fruits with PostgreSQL database integration.

## Technology Stack

- **Java**: 21
- **Jakarta EE**: 10.0
- **MicroProfile**: 6.1
- **Open Liberty**: Latest
- **Database**: PostgreSQL
- **Build Tool**: Maven

## Project Structure

```
liberty-jakartaee/
├── src/
│   ├── main/
│   │   ├── java/org/acme/
│   │   │   ├── domain/          # JPA entities
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── mapping/         # Entity-DTO mappers
│   │   │   ├── repository/      # JPA repositories
│   │   │   ├── rest/            # JAX-RS REST controllers
│   │   │   └── service/         # Business logic services
│   │   ├── liberty/config/
│   │   │   └── server.xml       # Liberty server configuration
│   │   └── resources/
│   │       └── META-INF/
│   │           ├── persistence.xml  # JPA configuration
│   │           └── import.sql       # Database initialization
│   └── test/
├── pom.xml
├── Dockerfile
└── README.md
```

## Prerequisites

- Java 21 or later
- Maven 3.8+
- PostgreSQL 12+ (for production)
- Docker (optional, for containerized deployment)

## Database Setup

### PostgreSQL

Create a database and user:

```sql
CREATE DATABASE fruits;
CREATE USER fruits WITH PASSWORD 'fruits';
GRANT ALL PRIVILEGES ON DATABASE fruits TO fruits;
```

## Building the Application

### Build WAR file

```bash
mvn clean package
```

This creates `target/liberty-jakartaee.war`

## Running the Application

### Development Mode with Liberty Maven Plugin

```bash
mvn liberty:dev
```

The application will be available at:
- Application: http://localhost:9080
- API endpoints: http://localhost:9080/api/fruits

Press `Ctrl+C` to stop the server.

### Run with Liberty Server

```bash
mvn liberty:run
```

### Production Mode

1. Build the application:
```bash
mvn clean package
```

2. Deploy the WAR file to Liberty server or use Docker (see below)

## Docker Deployment

### Build Docker Image

```bash
mvn clean package
docker build -t liberty-jakartaee:latest .
```

### Run with Docker Compose

Create a `docker-compose.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: fruits
      POSTGRES_USER: fruits
      POSTGRES_PASSWORD: fruits
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data

  liberty-app:
    image: liberty-jakartaee:latest
    ports:
      - "9080:9080"
      - "9443:9443"
    environment:
      - DB_HOST=postgres
      - DB_PORT=5432
      - DB_NAME=fruits
      - DB_USER=fruits
      - DB_PASSWORD=fruits
    depends_on:
      - postgres

volumes:
  postgres-data:
```

Run:
```bash
docker-compose up
```

## API Endpoints

### Get All Fruits
```bash
curl http://localhost:9080/api/fruits
```

### Get Fruit by Name
```bash
curl http://localhost:9080/api/fruits/Apple
```

### Create New Fruit
```bash
curl -X POST http://localhost:9080/api/fruits \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Peach",
    "description": "Sweet stone fruit"
  }'
```

## Configuration

### Server Configuration

The Liberty server is configured in `src/main/liberty/config/server.xml`:

- **Features**: Jakarta EE 10.0, MicroProfile 6.1
- **HTTP Ports**: 9080 (HTTP), 9443 (HTTPS)
- **DataSource**: PostgreSQL connection pool
- **Logging**: Console and file logging

### Database Configuration

Database connection is configured in `server.xml`:

```xml
<dataSource id="DefaultDataSource" jndiName="jdbc/fruitsDB">
    <jdbcDriver libraryRef="postgresql-library"/>
    <properties.postgresql 
        serverName="localhost"
        portNumber="5432"
        databaseName="fruits"
        user="fruits"
        password="fruits"/>
</dataSource>
```

### JPA Configuration

JPA is configured in `src/main/resources/META-INF/persistence.xml`:

- **Persistence Unit**: fruitsPU
- **Transaction Type**: JTA
- **Schema Generation**: drop-and-create (development)
- **SQL Load Script**: import.sql

## Key Differences from Quarkus

1. **Dependency Injection**: Uses `@Inject` instead of constructor injection
2. **Repository**: Standard JPA `EntityManager` instead of Panache
3. **REST Configuration**: Requires `@ApplicationPath` for JAX-RS
4. **Transactions**: Uses `jakarta.transaction.Transactional`
5. **Server Configuration**: XML-based server.xml instead of application.yml
6. **Packaging**: WAR file instead of JAR
7. **Virtual Threads**: Configured at server level, not per-endpoint

## Testing

Run tests:
```bash
mvn test
```

Run integration tests:
```bash
mvn verify
```

## Monitoring and Health

Liberty provides built-in health checks via MicroProfile Health:

- Health: http://localhost:9080/health
- Liveness: http://localhost:9080/health/live
- Readiness: http://localhost:9080/health/ready

## Performance Considerations

- Connection pooling configured with min 5, max 20 connections
- JPA second-level cache enabled for Store entities
- Lazy loading configured for relationships
- Virtual threads support via Java 21

## Troubleshooting

### Application won't start
- Check PostgreSQL is running and accessible
- Verify database credentials in server.xml
- Check Liberty logs in `target/liberty/wlp/usr/servers/defaultServer/logs/`

### Database connection issues
- Ensure PostgreSQL JDBC driver is in the correct location
- Verify network connectivity to database
- Check firewall rules

### Port conflicts
- Change ports in server.xml or via bootstrap properties
- Default ports: 9080 (HTTP), 9443 (HTTPS)

## Additional Resources

- [Open Liberty Documentation](https://openliberty.io/docs/)
- [Jakarta EE 10 Specification](https://jakarta.ee/specifications/platform/10/)
- [MicroProfile Documentation](https://microprofile.io/)

## License

This project is provided as-is for demonstration purposes.