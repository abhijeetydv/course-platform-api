# Course Platform API

A RESTful API for a Learning Management System built with **Spring Boot 3.2.2**. This API provides course browsing, user authentication, enrollment management, and progress tracking functionality.

## 🚀 Features

- **User Authentication** - JWT-based registration and login
- **Course Management** - Browse and view detailed course information
- **Search** - Full-text search across courses, topics, and subtopics
- **Enrollment** - Enroll in courses and track enrollments
- **Progress Tracking** - Mark subtopics as complete and track learning progress
- **API Documentation** - Interactive Swagger UI

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming Language |
| Spring Boot | 3.2.2 | Backend Framework |
| Spring Security | 6.x | Authentication & Authorization |
| Spring Data JPA | 3.x | Data Persistence |
| PostgreSQL | - | Database |
| JWT (jjwt) | 0.12.5 | Token-based Authentication |
| SpringDoc OpenAPI | 2.3.0 | API Documentation |
| Lombok | 1.18.34 | Boilerplate Reduction |
| Maven | 3.x | Build Tool |

## 📁 Project Structure

```
src/main/java/com/courseplatform/
├── config/          # Security and OpenAPI configuration
├── controller/      # REST API endpoints
├── dto/             # Data Transfer Objects (request/response)
├── entity/          # JPA entities
├── exception/       # Custom exceptions and global handler
├── repository/      # Spring Data JPA repositories
├── security/        # JWT filter and authentication
└── service/         # Business logic layer
```

## 🔧 Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **PostgreSQL** database

## ⚙️ Configuration

### Environment Variables

Set the following environment variables for production deployment:

| Variable | Description | Default |
|----------|-------------|---------|
| `PORT` | Server port | `8080` |
| `DATABASE_URL` | PostgreSQL connection URL | - |
| `DATABASE_USERNAME` | Database username | - |
| `DATABASE_PASSWORD` | Database password | - |
| `JWT_SECRET` | Secret key for JWT signing | - |
| `JWT_EXPIRATION` | Token expiration in milliseconds | `86400000` (24h) |

### Local Development

1. Clone the repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/course-platform-api.git
   cd course-platform-api
   ```

2. Configure your database in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/courseplatform
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   jwt.secret=your_secret_key
   ```

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

4. Access the API at `http://localhost:8080`

## 📖 API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

## 🔐 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and get JWT token |

### Courses
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/courses` | Get all courses |
| GET | `/api/courses/{id}` | Get course details with topics |

### Search
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/search?q={query}` | Search courses, topics, subtopics |

### Enrollments (Requires Authentication)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/enrollments/{courseId}` | Enroll in a course |
| GET | `/api/enrollments` | Get user's enrollments |
| DELETE | `/api/enrollments/{courseId}` | Unenroll from a course |

### Progress (Requires Authentication)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/progress/{subtopicId}/complete` | Mark subtopic complete |
| DELETE | `/api/progress/{subtopicId}/complete` | Mark subtopic incomplete |
| GET | `/api/progress/courses/{courseId}` | Get course progress |

## 🏗️ Building for Production

```bash
# Build the JAR
./mvnw clean package -DskipTests

# Run the JAR
java -jar target/course-platform-api-1.0.0.jar
```

## 🧪 Running Tests

```bash
./mvnw test
```

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

## 👤 Author

Your Name

---

