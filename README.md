# Smart Resume Analyzer
**Java + Spring Boot + MySQL | Resume Parsing, Scoring & Analysis Backend**

A backend application that analyzes uploaded resumes, extracts key information, and generates a structured score based on skills, education, and experience. Built using Java, Spring Boot, REST APIs, MySQL, Apache Tika, and regex-based scoring logic.

---

## Features
- Upload resume in PDF or DOCX format
- Extract text using Apache Tika
- Detect skills, experience keywords and education level
- Score resumes based on configurable logic
- Provide clean JSON responses for API consumers
- Store analysis reports in a MySQL database
- Layered architecture: Controller → Service → Repository
- Validation and basic exception handling included

---

## Tech Stack
| Layer | Technology |
|-------|------------|
| Backend | Java 17, Spring Boot |
| Parsing | Apache Tika |
| Database | MySQL |
| ORM | Spring Data JPA |
| Build Tool | Maven |
| Testing | Postman |
| Version Control | Git & GitHub |

---

## Project Structure
Smart-Resume-Analyzer/
├── src/
│ ├── main/
│ │ ├── java/com/ifham/analyzer/
│ │ │ ├── controller/
│ │ │ ├── service/
│ │ │ ├── entity/
│ │ │ ├── repository/
│ │ │ ├── dto/
│ │ │ └── SmartResumeAnalyzerrApplication.java
│ └── resources/
│ └── application.properties
├── pom.xml
├── mvnw / mvnw.cmd
├── .gitignore
└── README.md

yaml
Copy code

---

## API Endpoints

**1) Upload resume**
POST /api/resume/upload
Content-Type: multipart/form-data

sql
Copy code

**2) Get all reports**
GET /api/resume/all

pgsql
Copy code

**3) Fetch report by ID**
GET /api/resume/{id}

yaml
Copy code

---

## Sample JSON Response
```json
{
  "skills": ["Java", "Spring Boot", "SQL"],
  "education": "B.E. Computer Engineering",
  "experienceScore": 7,
  "skillScore": 8,
  "totalScore": 78
}
```
How to run
Create the database:

pgsql
Copy code
CREATE DATABASE resume_analyzer_project;
Update application.properties (example):

ini
Copy code
spring.datasource.url=jdbc:mysql://localhost:3306/resume_analyzer_project
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
Build and run:

Using Maven wrapper (recommended if present):

arduino
Copy code
./mvnw spring-boot:run
Or using installed Maven:

arduino
Copy code
mvn spring-boot:run

Developer
Mohammad Ifhamuddin
Java Backend Developer
LinkedIn: <your-link-here>
GitHub: https://github.com/Ifhamuddin

