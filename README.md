# 🏥 Clinic Appointment Booking API

A robust, enterprise-grade RESTful API for clinic appointment scheduling built with **Java 17**, **Spring Boot**, **Spring Data JPA**, and **H2 In-Memory Database**. 

This system automates doctor and appointment management, prevents double bookings, intelligently schedules subsequent slots, and enforces strict state transitions.

---

## 🛠️ Tech Stack & Tools

* **Language:** Java 17
* **Framework:** Spring Boot
* **Persistence & ORM:** Spring Data JPA / Hibernate
* **Database:** H2 In-Memory Database
* **Validation:** Jakarta Bean Validation (`@NotBlank`, `@Email`, `@Future`, `@NotNull`)
* **Testing:** JUnit 5, Mockito (`@WebMvcTest`, `@MockitoBean`, `MockMvc`)
* **Build Tool:** Maven

---

## 🏛️ Architecture & Key Engineering Highlights

### 1. Layered Architecture
Strict separation of concerns:
```
Controller (REST API & HTTP contract)
   └── Service (Business Logic & State Validation)
         └── Repository (Spring Data JPA queries)
               └── Database (H2 In-Memory)
```

### 2. Rich Enum State Machine
Status transitions are encapsulated inside the `AppointmentStatus` enum itself:
```
         ┌───────────────┐
         │    BOOKED     │ (Initial status)
         └───┬───────┬───┘
             │       │
    Cancel   │       │ Complete
             ▼       ▼
┌──────────────┐   ┌──────────────┐
│  CANCELLED   │   │  COMPLETED   │ (Terminal states: No further transitions allowed)
└──────────────┘   └──────────────┘
```
Attempting an invalid transition (such as cancelling an already cancelled appointment) throws an `IllegalStateException` and returns a `400 Bad Request`.

### 3. Dynamic Slot Scheduling (20-Minute Gap Algorithm)
When a patient books an appointment:
1. The service queries the doctor's latest `BOOKED` slot using:
   ```java
   findTopByDoctorIdAndStatusOrderByAppointmentTimeDesc(...)
   ```
2. The next slot is automatically alloted **20 minutes after the last scheduled appointment**.
3. If no prior active appointment exists or the prior appointment was in the past, it schedules from the current time forward.

### 4. Comprehensive Unit Test Suite
* **`DoctorServiceTest` & `AppointmentServiceTest`**: Fast, isolated business logic testing with Mockito.
* **`DoctorControllerTest` & `AppointmentControllerTest`**: HTTP slice testing with `MockMvc`, verifying routing, status codes, and JSON serialization.

---

## 📋 API Endpoints

### 🩺 Doctor Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/doctors/create` | Register a new doctor |
| `GET` | `/doctors/{id}` | Get doctor details by ID |
| `PATCH` | `/doctors/{id}` | Update doctor details |
| `GET` | `/doctors/get-all` | Get all doctors (supports pagination & filtering by specialization) |

#### Register Doctor Request Example:
```http
POST /doctors/create
Content-Type: application/json

{
  "name": "Dr. Sarah Jenkins",
  "specialization": "Cardiology",
  "email": "sarah.jenkins@hospital.com"
}
```

---

### 📅 Appointment Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/appointments` | Book a new appointment (auto-calculates slot) |
| `GET` | `/appointments/by-id/{id}` | Get appointment by ID |
| `GET` | `/appointments` | Get all appointments (Paginated) |
| `GET` | `/appointments/by-doctor/{doctorId}` | Get appointments for a specific doctor (Paginated) |
| `PATCH` | `/appointments/cancel/{id}` | Cancel a booked appointment |
| `PATCH` | `/appointments/complete/{id}` | Mark a booked appointment as completed |

#### Book Appointment Request Example:
```http
POST /appointments
Content-Type: application/json

{
  "patientName": "Rahul Sharma",
  "patientEmail": "rahul@gmail.com",
  "doctorId": 1,
  "reason": "Regular checkup"
}
```

#### Response Example:
```json
{
  "patientName": "Rahul Sharma",
  "patientEmail": "rahul@gmail.com",
  "reason": "Regular checkup",
  "status": "BOOKED",
  "allotedTime": "2026-11-17T10:20:00"
}
```

---

## 🧪 Running the Tests

To run the entire automated unit test suite:

```bash
# Windows
.\mvnw.cmd test

# Linux / macOS
./mvnw test
```

---

## 🚀 Running the Application Locally

1. **Clone the repository:**
   ```bash
   git clone <your-repository-url>
   cd appointment_booking_system
   ```

2. **Run the Spring Boot application:**
   ```bash
   # Windows
   .\mvnw.cmd spring-boot:run

   # Linux / macOS
   ./mvnw spring-boot:run
   ```

3. **Access the H2 Database Console:**
   * URL: `http://localhost:8080/h2-console`
   * JDBC URL: `jdbc:h2:mem:appointmentdb`
   * Username: `sa`
   * Password: *(empty)*

---

## 🔒 Concurrency & Double Booking (Race Condition Handling)

In high-concurrency environments, two simultaneous requests might pass the availability check before either saves. In production, this can be resolved by:
1. **Database-level Unique Constraint:** A composite unique index on `(doctor_id, appointment_time)` for active records.
2. **Pessimistic / Optimistic Locking:** Using JPA's `@Lock(LockModeType.PESSIMISTIC_WRITE)` when querying doctor availability during high-traffic booking windows.
