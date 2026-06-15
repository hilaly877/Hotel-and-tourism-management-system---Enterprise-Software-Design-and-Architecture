# Notification Service — Hotel Management System

Handles email and SMS notifications for guests.
Runs on port **8085**. Built with Node.js, Express, and MongoDB.

---

## Tech Stack
- Node.js
- Express.js
- MongoDB + Mongoose
- Axios (for calling other microservices)
- dotenv

---

## Setup Instructions

### Step 1 — Install MongoDB
Download from: https://www.mongodb.com/try/download/community
- Install and start MongoDB
- MongoDB runs on port 27017 by default
- No need to manually create the database — Mongoose creates it automatically

### Step 2 — Install Node.js
Download from: https://nodejs.org (LTS version recommended)

### Step 3 — Install Dependencies
Open a terminal in the `notification-service` folder and run:
```bash
npm install
```

### Step 4 — Configure Environment
Open `.env` and update if needed:
```env
PORT=8085
MONGO_URI=mongodb://localhost:27017/hotel_notification_db
GUEST_SERVICE_URL=http://localhost:8081
BOOKING_SERVICE_URL=http://localhost:8083
```

### Step 5 — Run the Service

**Normal mode:**
```bash
npm start
```

**Development mode (auto-restart on file changes):**
```bash
npm run dev
```

App runs on: **http://localhost:8085**

---

## Open in IntelliJ IDEA
1. Open IntelliJ → File → Open → select `notification-service` folder
2. Open the built-in terminal (View → Tool Windows → Terminal)
3. Run `npm install` then `npm start`

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/notifications | Get all notifications |
| GET | /api/notifications/:id | Get notification by MongoDB ID |
| GET | /api/notifications/guest/:guestId | Get all notifications for a guest |
| GET | /api/notifications/booking/:bookingId | Get all notifications for a booking |
| POST | /api/notifications/email | Send an email notification |
| POST | /api/notifications/sms | Send an SMS notification |
| DELETE | /api/notifications/:id | Delete a notification |

## Notification Categories
- BOOKING_CONFIRMATION
- BOOKING_CANCELLATION
- PAYMENT_SUCCESS
- PAYMENT_REFUND
- CHECK_IN_REMINDER
- CHECK_OUT_REMINDER
- GENERAL

---

## Postman Testing Examples

### ⚠️ Before testing — make sure:
- Guest Service (8081) is running
- A guest exists with ID 1

---

### Send Booking Confirmation Email (POST)
- URL: `http://localhost:8085/api/notifications/email`
- Method: `POST`
- Body (JSON):
```json
{
    "guestId": 1,
    "bookingId": 1,
    "subject": "Booking Confirmation - Hotel Stay",
    "message": "Dear Guest, your booking has been confirmed. Check-in: 2026-06-01, Check-out: 2026-06-05. We look forward to welcoming you!",
    "recipientEmail": "john@example.com",
    "category": "BOOKING_CONFIRMATION"
}
```

### Send Cancellation Email (POST)
- URL: `http://localhost:8085/api/notifications/email`
- Method: `POST`
- Body (JSON):
```json
{
    "guestId": 1,
    "bookingId": 1,
    "subject": "Booking Cancellation Notice",
    "message": "Dear Guest, your booking has been cancelled as requested. If this was a mistake, please contact us.",
    "recipientEmail": "john@example.com",
    "category": "BOOKING_CANCELLATION"
}
```

### Send Payment Success SMS (POST)
- URL: `http://localhost:8085/api/notifications/sms`
- Method: `POST`
- Body (JSON):
```json
{
    "guestId": 1,
    "bookingId": 1,
    "subject": "Payment Received",
    "message": "Payment of LKR 20,000 received for your booking. Thank you!",
    "recipientPhone": "0771234567",
    "category": "PAYMENT_SUCCESS"
}
```

### Send Check-in Reminder SMS (POST)
- URL: `http://localhost:8085/api/notifications/sms`
- Method: `POST`
- Body (JSON):
```json
{
    "guestId": 1,
    "bookingId": 1,
    "subject": "Check-in Reminder",
    "message": "Reminder: Your check-in is tomorrow at 2:00 PM. We look forward to seeing you!",
    "recipientPhone": "0771234567",
    "category": "CHECK_IN_REMINDER"
}
```

### Get All Notifications (GET)
- URL: `http://localhost:8085/api/notifications`
- Method: `GET`

### Get Notifications for a Guest (GET)
- URL: `http://localhost:8085/api/notifications/guest/1`
- Method: `GET`

### Get Notifications for a Booking (GET)
- URL: `http://localhost:8085/api/notifications/booking/1`
- Method: `GET`

### Delete a Notification (DELETE)
- URL: `http://localhost:8085/api/notifications/{mongodb_id}`
- Method: `DELETE`
- Note: Use the `_id` value from a GET response (looks like: 6574abc123def456...)

### Health Check (GET)
- URL: `http://localhost:8085/health`
- Method: `GET`
