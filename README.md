Smart Resume Analyzer

Java + Spring Boot + MySQL | Resume Parsing, Scoring & Analysis Backend

A backend application that analyzes uploaded resumes, extracts key information, and generates a structured score based on skills, education, and experience. Built using Java, Spring Boot, REST APIs, MySQL, Apache Tika, and regex-based scoring logic.

Features

Upload resume in PDF or DOCX format

Extract text using Apache Tika

Detect skills, experience keywords and education level

Score resumes based on configurable logic

Provide clean JSON responses

Store analysis reports in a MySQL database

Layered architecture: Controller → Service → Repository

Basic validation and exception handling

Tech Stack
Layer	Technology
Backend	Java 17, Spring Boot
Parsing	Apache Tika
Database	MySQL
ORM	Spring Data JPA
Build Tool	Maven
Testing	Postman
Version Control	Git & GitHub
Project Structure
Smart-Resume-Analyzer/
 ├── src/
 │   ├── main/
 │   │   ├── java/com/ifham/analyzer/
 │   │   │   ├── controller/
 │   │   │   ├── service/
 │   │   │   ├── entity/
 │   │   │   ├── repository/
 │   │   │   ├── dto/
 │   │   │   └── SmartResumeAnalyzerrApplication.java
 │   └── resources/
 │       └── application.properties
 ├── pom.xml
 ├── mvnw / mvnw.cmd
 ├── .gitignore
 └── README.md

API Endpoints
Upload Resume
POST /api/resume/upload
Content-Type: multipart/form-data

Get All Reports
GET /api/resume/all

Fetch Report by ID
GET /api/resume/{id}

Sample JSON Response
{
  "skills": ["Java", "Spring Boot", "SQL"],
  "education": "B.E. Computer Engineering",
  "experienceScore": 7,
  "skillScore": 8,
  "totalScore": 78
}

How to run
1. Create the database:
CREATE DATABASE resume_analyzer_project;

2. Update application.properties:
spring.datasource.url=jdbc:mysql://localhost:3306/resume_analyzer_project
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

3. Build and run:
mvn spring-boot:run


Or using Maven wrapper:

./mvnw spring-boot:run

Developer

Mohammad Ifhamuddin
Java Backend Developer
LinkedIn: <your-link-here>
GitHub: https://github.com/Ifhamuddin
