
# Study Group Finder & Collaboration Platform

[![React](https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=white)](https://reactjs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)

---

## 🎯 Project Statement
The **Study Group Finder & Collaboration Platform** is a modern, full-stack web application designed to help students connect with peers in the same courses to form effective study groups. Users can create profiles, list their enrolled courses, discover classmates, and collaborate using built-in communication and productivity tools. This platform enhances academic networking, improves study efficiency, and simplifies group work coordination.

---

## Team Members
- **Vamsidhar Reddy** – Frontend & Backend Developer
- **Rahul** –  Frontend Developer
- **Mahita** – Frontend Developer  
- **Santosh** – Backend Developer
    
## ✨ Features

### ✅ Completed
- **Authentication & Security**
  - JWT-based login/registration
  - Password hashing and security with Spring Security
  - Email-based password reset
  - Session management (Remember Me)
- **User Profile Management**
  - Full profile creation with academic details
  - Avatar upload (via Cloudinary)
  - University, degree, and personal bio
- **Course Management**
  - Browse and search courses
  - Enroll/unenroll in courses
  - Track peers in courses
- **Dashboard**
  - Display enrolled courses
  - Joined study groups count
  - Suggested peers count

### 🚧 In Progress
- **Study Groups**
  - Create and manage public/private groups
  - Join group requests and approvals
  - Member management
  - Group discovery and filtering
- **Communication**
  - Real-time chat
  - Group messaging
  - Direct messaging
- **Calendar & Scheduling**
  - Schedule study sessions
  - Event reminders (email/push notifications)
  - Group calendar integration

---

## 🛠️ Tech Stack

### Frontend
- React 18
- Vite
- Tailwind CSS
- React Router
- Lucide React (Icons)

### Backend
- Spring Boot 3.5.6
- Spring Security
- Spring Data JPA
- MySQL 8
- JWT Authentication
- Lombok
- SpringDoc OpenAPI (Swagger)


### Third-Party Services
- Cloudinary (image storage)
- Gmail SMTP (email notifications)

---

## 📦 Prerequisites
- Java 17+
- Node.js 18.x+
- npm
- MySQL 8+
- Maven 3.8+
- Git

---

## 🚀 Installation

### 1. Clone the Repository
```bash
git clone https://github.com/ChinthaVamsidharReddy/Study-Group-Finder-Collaboration-Platform-Group-2.git
cd study-group-finder-and-collaboration-platform
````

### 2. Database Setup

Create MySQL database:

```sql
CREATE DATABASE studygroup;
```

> The application will automatically create tables on first run.

### 3. Backend Setup

```bash
cd backend
mvn clean install -DskipTests
```

### 4. Frontend Setup

```bash
cd frontend
npm install
```

---

## ⚙️ Configuration

### Backend (`application.properties`)

```properties
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/studygroup?useSSL=false&serverTimezone=UTC
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD

jwt.secret=YOUR_JWT_SECRET_KEY
jwt.expiration=86400000

spring.mail.username=YOUR_EMAIL@gmail.com
spring.mail.password=YOUR_APP_PASSWORD

cloudinary.cloud-name=YOUR_CLOUD_NAME
cloudinary.api-key=YOUR_API_KEY
cloudinary.api-secret=YOUR_API_SECRET
```

### Frontend

Edit `frontend/src/app.js`:

```ts
const API_BASE_URL = 'http://localhost:8080/';
```

---

## 🚀 Running the Application

### Start Backend

```bash
cd backend
mvn spring-boot:run
```

### Start Frontend

```bash
cd frontend
npm run dev
```

### Access the App

[Frontend](http://localhost:5173) | [Swagger Docs](http://localhost:8080/swagger-ui.html)

---

## 📚 API Endpoints (Highlights)

### Authentication

* `POST /auth/register`
* `POST /auth/login`
* `POST /auth/forgot-password`
* `POST /auth/reset-password`
* `POST /auth/update-password`

### User Profile

* `GET /user/profile`
* `PUT /user/profile`
* `POST /user/upload-avatar`
* `DELETE /user/remove-avatar`

### Courses

* `GET /courses` – List all courses
* `GET /courses/my-courses` – Enrolled courses
* `POST /courses/{courseId}/enroll`
* `DELETE /courses/{courseId}/unenroll`
* `GET /courses/{userId}/peers` – Suggested peers

### Study Groups

* `GET /groups/created/{userId}`
* `GET /groups/joined/{userId}`
* `GET /groups/available/{userId}`
* `POST /groups/create`
* `POST /groups/join`
* `PUT /groups/approve/{memberId}`
* `DELETE /groups/leave/{groupId}`

---

## 📁 Project Structure

```
study-group-finder/
├── backend/
│   ├── src/main/java/com/studygroup/backend/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── entity/
│   │   ├── repository/
│   │   └── service/
│   └── resources/application.properties
├── frontend/
│   ├── public/
│   ├── src/components/
│   ├── src/contexts/
│   ├── src/pages/
│   ├── App.js
│   └── main.js
└── README.md
```

---

## 📝 License

MIT License

---

## 🙌 Acknowledgments

* React & React Router
* Tailwind CSS
* Spring Boot Documentation
* Cloudinary
* Open-source contributors

```
