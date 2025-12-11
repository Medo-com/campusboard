# Campus Job Board System

A full-stack Spring Boot + Thymeleaf web application designed to connect students, employers, and administrators within a campus environment. Students browse and apply for jobs, employers post opportunities, and administrators moderate the platform.

---

## Features

### Student Features
- View all approved job listings
- Search and filter jobs
- Submit applications
- View application status (SUBMITTED, ACCEPTED, REJECTED)
- Clean, modern dashboard interface

### Employer Features
- Post new job opportunities
- Edit or delete job postings
- View approval status (PENDING, APPROVED, REJECTED)
- View all applicants for each job
- Organized employer dashboard

### Admin Features
- Approve or reject newly created job postings
- Manage user accounts (activate/deactivate)
- View all pending jobs
- Full administrator dashboard

---

## Tech Stack

### Backend
- Java 17
- Spring Boot 3+
- Spring MVC
- Spring Data JPA
- Spring Security
- Hibernate ORM

### Frontend
- Thymeleaf Templates
- Bootstrap 5
- Font Awesome Icons

### Database
- MySQL (User, Job, JobApplication tables)

---

## Database Schema

### User Table
| Column     | Type                           | Description       |
|------------|--------------------------------|-------------------|
| user_id    | BIGINT                         | Primary key       |
| full_name  | VARCHAR                        | User's name       |
| email      | VARCHAR                        | Login email       |
| password   | VARCHAR                        | Hashed password   |
| role       | ENUM(STUDENT, EMPLOYER, ADMIN) | Access level      |
| status     | ENUM(ACTIVE, INACTIVE)         | Account status    |

### Job Table
| Column       | Type                                |
|--------------|-------------------------------------|
| job_id       | BIGINT                              |
| employer_id  | BIGINT (FK → user.user_id)          |
| title        | VARCHAR                             |
| description  | TEXT                                |
| status       | ENUM(PENDING, APPROVED, REJECTED)   |

### JobApplication Table
| Column         | Type                               |
|----------------|------------------------------------|
| application_id | BIGINT                              |
| student_id     | BIGINT (FK → user.user_id)          |
| job_id         | BIGINT (FK → job.job_id)            |
| applied_at     | TIMESTAMP                           |
| status         | ENUM(SUBMITTED, ACCEPTED, REJECTED) |

---

## Authentication and Authorization

The project uses Spring Security to enforce access rules.

| Role     | Access Rights |
|----------|----------------|
| STUDENT  | Browse/apply for jobs, student dashboard |
| EMPLOYER | Manage jobs, view applicants |
| ADMIN    | Approve/reject jobs, manage user accounts |

Password hashing and route protection are fully implemented.
