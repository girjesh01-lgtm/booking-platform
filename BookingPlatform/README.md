# 🎬 Booking Platform (Movie Ticket Booking System)

## 🚀 Overview

This project is a simplified **Movie Ticket Booking Platform** designed to demonstrate:

* Solution thinking
* System design and architecture
* Concurrency handling
* Clean coding practices

The system supports:

* Viewing available seats
* Locking seats
* Confirming bookings

---

## 🧠 Scope & Approach

This assignment focuses on **core booking flow and concurrency handling**.

### ✅ Implemented

* Seat availability check
* Seat locking (write-heavy scenario)
* Booking lifecycle (LOCKED → CONFIRMED)
* Pessimistic locking for concurrency

### ❌ Skipped (Intentionally)

* Full UI
* Distributed systems (kept monolithic for simplicity)
* Real payment gateway (mocked)

---

## 🏗️ High-Level Architecture

```
Client (Postman)
        ↓
Controller Layer
        ↓
Service Layer (Business Logic)
        ↓
Repository Layer (JPA)
        ↓
MySQL (Docker)
```

### Design Pattern Used

* Layered Architecture
* Repository Pattern
* DTO Pattern

---

## 🔁 Booking Flow (Write Scenario)

```
1. User selects seats
2. Seats are locked (DB transaction)
3. Booking created (LOCKED state)
4. Payment simulated
5. Booking CONFIRMED / FAILED
```

---

## 📖 Read Scenario

* Fetch available seats for a show
* Filter by `AVAILABLE` status

---

## 🗄️ Data Model

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

## 🔌 API Contracts

### 1. Lock Seats

`POST /bookings/lock`

Request:

```json
{
  "userId": 1,
  "showId": 101,
  "seats": ["A1", "A2"]
}
```

Response:

```json
{
  "id": 1,
  "status": "LOCKED"
}
```

---

### 2. Confirm Booking

`POST /bookings/{bookingId}/confirm`

Response:

```
Booking Confirmed!
```

---

## 🔐 Concurrency Handling (Key Focus)

Used **Pessimistic Locking**:

* `SELECT ... FOR UPDATE`
* Ensures only one transaction can access seats

### Trade-off:

* ✅ Strong consistency
* ❌ Reduced throughput under high contention

---

## ⚖️ Design Decisions & Trade-offs

### 1. Pessimistic Locking

* Chosen for correctness over performance

### 2. Separate Lock & Confirm APIs

* Helps handle payment failures cleanly

### 3. Monolithic Design

* Easier to implement and explain
* Suitable for assignment scope

---

## ⚠️ Assumptions

* Single region deployment
* Low latency DB access
* No authentication layer
* Payment is mocked

---

## 📈 Non-Functional Requirements

### Scalability

* Add Redis caching for seat reads
* Read replicas for scaling reads

### Availability

* Retry mechanisms for transient failures

### Security

* JWT-based authentication (future)

### Payment Integration

* External service (Stripe/Razorpay)
* Webhooks for async confirmation

---

## 🐳 Running MySQL (Docker)

```bash
docker-compose up -d
```

---

## ⚙️ Application Config

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/booking_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=app_user
spring.datasource.password=app_pass
```

---

## 🔮 Future Improvements

* Seat lock expiry (timeout handling)
* Booking-seat mapping table
* Redis-based distributed locking
* Event-driven architecture (Kafka)

---

## 🧠 Interview Talking Points

* Designed system to prevent double booking
* Used transactional boundaries for atomicity
* Handled concurrency using DB-level locking
* Clearly separated read vs write flows
* Considered production-scale improvements

---

## ▶️ Run Application

```bash
./mvnw spring-boot:run
```

---

## ✅ Conclusion

This project demonstrates strong fundamentals in:

* System design
* Backend architecture
* Concurrency handling

while keeping the implementation clean and focused.
