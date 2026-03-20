# 🎬 Booking Platform (Movie Ticket Booking System)

## 🚀 Overview

This project is a simplified **Movie Ticket Booking Platform** designed to demonstrate strong system design, concurrency handling, and backend architecture using **Spring Boot + MySQL (Docker)**.

The system allows users to:

* View available seats for a show
* Lock seats temporarily
* Confirm booking after payment

---

## 🧠 Design Goals

* Prevent **double booking**
* Ensure **data consistency** under concurrent requests
* Maintain a clean **layered architecture**
* Demonstrate **real-world booking flow**

---

## 🏗️ High-Level Architecture

```
Client (Postman / UI)
        ↓
Controller Layer
        ↓
Service Layer (Business Logic)
        ↓
Repository Layer (JPA)
        ↓
MySQL (Docker)
```

---

## ⚙️ Tech Stack

* Java 17+
* Spring Boot
* Spring Data JPA
* MySQL (Dockerized)
* Lombok

---

## 🗄️ Database Design

### Seat

* id
* seat_number
* show_id
* status (AVAILABLE, LOCKED, BOOKED)

### Booking

* id
* user_id
* show_id
* status (LOCKED, CONFIRMED, FAILED)
* created_at

---

## 🔁 Booking Lifecycle

```
AVAILABLE → LOCKED → CONFIRMED
                 ↘ FAILED → AVAILABLE
```

---

## 🔐 Concurrency Handling

To prevent double booking, **pessimistic locking** is used:

* `SELECT ... FOR UPDATE` via JPA
* Ensures only one transaction can modify seats at a time

### Why this approach?

* Guarantees **strong consistency**
* Prevents race conditions during booking

---

## 🔌 API Endpoints

### 1. Lock Seats

`POST /bookings/lock`

**Request:**

```json
{
  "userId": 1,
  "showId": 101,
  "seats": ["A1", "A2"]
}
```

**Response:**

```json
{
  "id": 1,
  "status": "LOCKED"
}
```

---

### 2. Confirm Booking

`POST /bookings/{bookingId}/confirm`

**Response:**

```
Booking Confirmed!
```

---

## 🐳 Running MySQL via Docker

### docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: mysql-booking
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: booking_db
      MYSQL_USER: app_user
      MYSQL_PASSWORD: app_pass
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

### Run:

```
docker-compose up -d
```

---

## ⚙️ Application Configuration

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/booking_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=app_user
spring.datasource.password=app_pass

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## ⚖️ Design Decisions

### 1. Separate Lock & Confirm Flow

* Allows handling payment failures
* Improves user experience

### 2. Pessimistic Locking

* Chosen over optimistic locking for strict consistency

### 3. Monolithic Architecture

* Simpler for assignment scope
* Easier to explain in interview

---

## ⚠️ Assumptions

* Single region deployment
* No real payment gateway (simulated)
* No authentication/authorization

---

## 📈 Non-Functional Considerations

### Scalability

* Can add **Redis caching** for seat availability
* Use **read replicas** for heavy read traffic

### Availability

* Retry mechanisms for transient failures

### Security

* JWT-based authentication (future scope)

### Payment Integration

* External service (Razorpay/Stripe)

---

## 🔮 Future Improvements

* Seat lock expiry (auto release after timeout)
* Booking-Seat mapping table
* Distributed locking using Redis
* Microservices architecture

---

## 🧠 Interview Talking Points

* Used **transactional boundaries** to ensure atomic operations
* Prevented race conditions using **pessimistic locking**
* Designed system with **real-world booking lifecycle**
* Considered scalability and production improvements

---

## ▶️ Run Application

```bash
./mvnw spring-boot:run
```

---

## ✅ Conclusion

This project focuses on **correctness, concurrency handling, and system design clarity**, which are critical aspects of real-world booking systems.
