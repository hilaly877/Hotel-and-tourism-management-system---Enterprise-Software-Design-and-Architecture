# Hotel & Tourism Management System

A microservices-based enterprise application designed to streamline hotel operations, enhance guest experiences, and support efficient management of reservations, rooms, payments, and notifications.

## Overview

The **Hotel & Tourism Management System** is developed to manage the day-to-day activities of hotels and resorts through a scalable and modular architecture. The system supports essential hospitality operations including guest management, room administration, booking workflows, payment processing, and automated notifications.

The project adopts a **Microservices Architecture**, allowing each functional component to operate independently while collaborating through well-defined APIs. This approach improves maintainability, scalability, and fault isolation.

## Features

* Guest registration and profile management
* Loyalty point tracking for returning guests
* Room listing and categorization
* Room availability management
* Dynamic room pricing based on room type and season
* Online reservation creation and management
* Booking modification and cancellation support
* Group and multi-room reservation handling
* Secure payment and billing processing
* Invoice generation and refund management
* Email and SMS notifications for booking events
* Administrative reporting and inventory oversight

## System Architecture

### Guest Service

Responsible for managing guest-related information.

**Functions:**

* Guest registration
* Profile management
* Loyalty program tracking
* Guest information retrieval

### Room Service

Handles all room and property management operations.

**Functions:**

* Room inventory management
* Room type classification
* Amenity management
* Pricing rule administration
* Availability tracking

### Booking Service

Acts as the core service responsible for reservation workflows.

**Functions:**

* Reservation creation
* Booking modifications
* Booking cancellations
* Availability verification
* Group reservation processing

### Payment Service

Manages all financial transactions related to reservations.

**Functions:**

* Payment processing
* Billing management
* Invoice generation
* Refund handling
* Integration with third-party payment gateways

### Notification Service

Handles communication with users regarding reservation activities.

**Functions:**

* Booking confirmations
* Reminder notifications
* Cancellation alerts
* Email notifications
* SMS notifications

## Intended Users

### Guests / Tourists

* Browse available accommodations
* Make reservations
* Modify or cancel bookings
* View booking history

### Receptionists / Hotel Staff

* Manage walk-in reservations
* Handle check-ins and check-outs
* Assist guests with booking inquiries

### Managers / Administrators

* Monitor room inventory
* Manage pricing strategies
* Access reports and operational insights

### External Systems

* Payment gateways
* Messaging providers
* Other third-party integrations

## Technology Approach

This project follows a **Microservices Architecture** to provide:

* **Scalability** – Services can scale independently based on demand.
* **Maintainability** – Each service can be developed and deployed separately.
* **Fault Isolation** – Failures in one service have minimal impact on others.
* **Flexibility** – Different technologies can be adopted for different services when required.

## Business Objectives

The Hotel & Tourism Management System aims to:

* Improve operational efficiency in hotels and resorts.
* Enhance guest satisfaction through digital services.
* Reduce manual administrative workloads.
* Support business growth with scalable infrastructure.
* Ensure secure and reliable reservation management.

## Team

**Group 18**

| Registration Number |
| ------------------- |
| PS/2022/123         |
| EC/2022/040         |
| EC/2022/061         |
| EC/2022/039         |

## Project Status

🚧 **Milestone 01 – System Planning and Architecture Design**

Current activities include:

* Requirements gathering and analysis
* Identification of core business domains
* Microservice decomposition
* Architectural planning and documentation
