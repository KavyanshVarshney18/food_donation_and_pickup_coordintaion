# Food Donation System

A full-stack food donation and pickup coordination platform built with:

- `Spring Boot 4`
- `MongoDB`
- `React 19`
- `Vite`
- `Tailwind CSS`
- `STOMP + SockJS` for live donation updates

The project helps donors post surplus food, lets volunteers or NGOs discover nearby donations, and allows admins to view a donor leaderboard based on meals saved.

## Project Structure

This repository contains two main applications:

- `food-donation-system/` - Spring Boot backend
- `frontend/` - React + Vite frontend

## Core Features

- User registration and login
- Role-based experience for:
  - `DONOR`
  - `VOLUNTEER`
  - `ADMIN`
- Donation creation with:
  - food type
  - quantity
  - description
  - best-before time
  - pickup location
  - optional image URL
- Nearby donation discovery for volunteers within a `10 km` radius
- Claim flow for volunteers
- OTP generation and pickup verification
- Donation completion with distribution proof URL
- Live donation updates through WebSocket
- Admin leaderboard based on total meals donated
- Automatic expiration for stale donations

## How The System Works

### Donor Flow

1. Register or log in as a `DONOR`
2. Create a donation
3. Wait for a volunteer to claim it
4. Generate an OTP for handoff
5. Share the OTP with the volunteer during pickup
6. OTP verification marks the donation as `PICKED_UP`

### Volunteer / NGO Flow

1. Register or log in as a `VOLUNTEER`
2. View nearby available donations
3. Receive live updates when new donations are created or updated
4. Claim a donation
5. Collect food from the donor
6. Complete distribution with a proof URL

### Admin Flow

1. Register or log in as `ADMIN`
2. View the top donors leaderboard

## Tech Stack

### Backend

- `Spring Boot 4.0.6`
- `Spring Web MVC`
- `Spring Security`
- `Spring Validation`
- `Spring Data MongoDB`
- `Spring WebSocket`
- `Lombok`

### Frontend

- `React 19`
- `TypeScript`
- `Vite 8`
- `React Router`
- `Axios`
- `Tailwind CSS 4`
- `@stomp/stompjs`
- `sockjs-client`

## Backend Overview

### Base Configuration

- App name: `food-donation-system`
- Backend port: `8080`
- MongoDB URI: `mongodb://localhost:27017/food_donation_db`

### Authentication Model

This project currently uses a simple demo authentication approach:

- Users can register and log in with `phone` and `password`
- Passwords are encrypted with `BCrypt`
- Login returns a demo token like `demo-token-...`
- Donation APIs identify the user using the `X-User-Id` request header
- All requests are currently permitted by Spring Security

This is fine for a demo or college project, but it is not production-grade authentication yet.

### Main Backend Modules

- `controller/` - REST endpoints
- `service/` - business logic
- `repository/` - MongoDB data access
- `model/` - domain entities and enums
- `config/` - CORS, security, and WebSocket configuration
- `websocket/` - event publishing to clients

### Data Models

#### User

- `id`
- `name`
- `phone`
- `password`
- `role`
- `latitude`
- `longitude`
- `totalMealsDonated`

#### Donation

- `id`
- `foodType`
- `quantity`
- `description`
- `bestBeforeTime`
- `createdAt`
- `pickupLocation`
- `status`
- `donorId`
- `volunteerId`
- `imageUrl`
- `distributionProofUrl`

#### OTPVerification

- `id`
- `donationId`
- `otp`
- `expiryTime`
- `verified`

### Donation Status Lifecycle

- `AVAILABLE`
- `CLAIMED`
- `PICKED_UP`
- `DISTRIBUTED`
- `EXPIRED`

### Business Rules

- Nearby donations are filtered within `10 km`
- OTP is only generated after a donation is claimed
- OTP expires after `10 minutes`
- OTP verification marks a donation as `PICKED_UP`
- Completion is only allowed after pickup
- Donor meal count is updated when the donation is distributed
- Meals are estimated using `quantity * 4`
- A scheduled task checks every `60 seconds` for expired donations

## REST API Summary

### Auth

- `POST /auth/register`
- `POST /auth/login`

### Donations

- `POST /donations/create`
- `GET /donations/nearby?lat=...&lng=...`
- `GET /donations/my`
- `POST /donations/{id}/claim`
- `POST /donations/{id}/generate-otp`
- `POST /donations/{id}/verify-otp`
- `POST /donations/{id}/complete`

### Admin

- `GET /admin/leaderboard`

### Header Requirement

The following donation endpoints use:

- `X-User-Id: <user-id>`

This is required for:

- `POST /donations/create`
- `GET /donations/my`
- `POST /donations/{id}/claim`

## WebSocket / Live Updates

The backend exposes a SockJS STOMP endpoint at:

- `/ws`

The frontend subscribes to:

- `/topic/donations`

Published event types include:

- `DONATION_CREATED`
- `DONATION_CLAIMED`
- `DONATION_PICKED_UP`
- `DONATION_DISTRIBUTED`
- `DONATION_EXPIRED`

Allowed frontend origin is currently:

- `http://localhost:5173`

## Frontend Overview

### Routes

- `/` - login / signup page
- `/dashboard` - role-based dashboard router

### Dashboards

#### Donor Dashboard

- Create donations
- View your donation list
- Generate OTP after a claim
- Verify OTP submitted by volunteer

#### Volunteer Dashboard

- View nearby donations
- Auto-refresh via WebSocket when donations change
- Claim donations
- Mark donations as distributed with a proof URL

#### Admin Dashboard

- View top donors by meals saved

### Frontend State

- Logged-in user is stored in `localStorage`
- Key used: `fds_user`

### Frontend API Base URL

- `http://localhost:8080`

## Prerequisites

Make sure you have:

- `Java 21`
- `Maven`
- `Node.js 18+` or newer
- `npm`
- `MongoDB` running locally on port `27017`

## Local Development Setup

### 1. Start MongoDB

Make sure MongoDB is running locally:

```powershell
mongod
```

If MongoDB is already installed as a service, just ensure the service is running.

### 2. Start the Backend

From the repository root:

```powershell
cd food-donation-system
./mvnw spring-boot:run
```

Backend will run at:

- `http://localhost:8080`

### 3. Start the Frontend

From the repository root:

```powershell
cd frontend
npm install
npm run dev
```

Frontend will run at:

- `http://localhost:5173`

## Build Commands

### Backend

```powershell
cd food-donation-system
./mvnw test
./mvnw package
```

### Frontend

```powershell
cd frontend
npm run build
```

## Example API Payloads

### Register

```json
{
  "name": "Kavya",
  "phone": "9876543210",
  "password": "secret123",
  "role": "DONOR",
  "latitude": 12.9716,
  "longitude": 77.5946
}
```

### Login

```json
{
  "phone": "9876543210",
  "password": "secret123"
}
```

### Create Donation

Headers:

```http
X-User-Id: <user-id>
```

Body:

```json
{
  "foodType": "Cooked Rice",
  "quantity": 5,
  "description": "Freshly prepared rice for 15 to 20 people",
  "bestBeforeTime": "2026-05-05T18:30:00Z",
  "lat": 12.9716,
  "lng": 77.5946,
  "imageUrl": "https://example.com/food.jpg"
}
```

### Verify OTP

```json
{
  "otp": "1234"
}
```

### Complete Donation

```json
{
  "distributionProofUrl": "https://example.com/proof.jpg"
}
```

## Troubleshooting

### Frontend error: `global is not defined`

If the frontend crashes with a SockJS-related error such as:

```text
Uncaught ReferenceError: global is not defined
```

the current project already includes a browser-safe fix in:

- `frontend/vite.config.ts`
- `frontend/index.html`

If you still see the old error:

1. Stop the Vite dev server
2. Start it again with `npm run dev`
3. Hard refresh the browser

### CORS or WebSocket connection issues

Check that:

- backend is running on `http://localhost:8080`
- frontend is running on `http://localhost:5173`
- MongoDB is running locally
- WebSocket endpoint `/ws` is reachable

### Login works but donation requests fail

Make sure the request includes:

```http
X-User-Id: <user-id>
```

The frontend already sends this automatically for the supported actions.

## Current Limitations

- Authentication is demo-only and not JWT-validated on the server
- All backend routes are currently open
- No file upload support yet, only URL fields for images and proof
- No pagination or filtering for large datasets
- No production deployment configuration
- Nearby search uses in-memory distance filtering instead of Mongo geospatial queries
- Admin access is UI-based and not strongly enforced on the backend

## Suggested Future Improvements

- Add proper JWT authentication and authorization
- Protect admin and donation routes by role
- Add image upload support using cloud storage
- Use MongoDB geospatial indexes for faster nearby search
- Add form validation and error handling in the UI
- Add unit and integration tests for controllers and services
- Add Docker support for backend, frontend, and MongoDB
- Add deployment guides for Render, Railway, Vercel, or AWS

## Notes For Developers

- The backend folder is named `food-donation-system/` inside the repository root, so the repo contains both `food-donation-system/` and `frontend/`
- The frontend stores only the user object in local storage, not the returned demo token
- Volunteer live updates are driven by STOMP over SockJS

## License

No license file is currently included in this repository.
