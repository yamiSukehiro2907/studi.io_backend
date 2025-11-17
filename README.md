# Studi.io Backend

A comprehensive Spring Boot backend for Studi.io - a collaborative study platform with real-time chat, room management, and secure authentication.

## ✨ Features

### 🎯 Core Features

* 🚪 **Create & Join Rooms** – Easily create or join public/private study rooms
* 💬 **Real-time Chat** – Instant messaging with WebSocket (STOMP) for seamless communication
* 🔒 **Privacy Control** – Public or private rooms with customizable access
* 🖼️ **Room Customization** – Set custom names, descriptions, and room images
* ☁️ **Cloud Image Uploads** – Profile and room images hosted on Cloudinary
* ⚙️ **Room Management** – Full CRUD operations for room owners and admins
* 👥 **Member Management** – Admin controls and member permissions
* 🔐 **Secure Authentication** – JWT-based auth with access/refresh tokens stored in HTTP-only cookies
* 📧 **Email Service** – OTP verification and email notifications via SendGrid
* 🛡️ **Rate Limiting** – Bucket4j-based rate limiting for API endpoints (general, login, signup, user-specific)
* 📊 **Message Pagination** – Efficient message retrieval with pagination support

### 🚀 Upcoming Features

* 📝 **Collaborative Whiteboard** – Real-time drawing and note-taking
* 🗂️ **ResourceHub** – Centralized document and link management
* 🎥 **Video Chat** – Built-in video conferencing
* 📊 **Study Analytics** – Track study time and productivity
* 🔔 **Push Notifications** – Browser push notifications for updates

## 🛠️ Tech Stack

### Backend
- **Framework:** Spring Boot 3.x
- **Language:** Java 17+
- **Database:** MongoDB (via Spring Data MongoDB)
- **Authentication:** JWT (JJWT)
- **Real-time:** WebSocket (STOMP protocol)
- **File Upload:** Cloudinary
- **Email:** SendGrid (JavaMail)
- **Rate Limiting:** Bucket4j + Caffeine Cache
- **Security:** Spring Security 6
- **Validation:** Jakarta Validation

### Dependencies
```xml
- spring-boot-starter-web
- spring-boot-starter-data-mongodb
- spring-boot-starter-security
- spring-boot-starter-websocket
- spring-boot-starter-mail
- jjwt-api / jjwt-impl / jjwt-jackson
- cloudinary-http44
- bucket4j-core
- caffeine
- dotenv-java
- lombok
```

## 📁 Project Structure

```
src/main/java/io/studi/backend/
├── config/
│   ├── websocket/          # WebSocket configuration & session management
│   ├── AppInfoConfig.java
│   ├── CloudinaryConfig.java
│   ├── CorsConfig.java
│   ├── DotenvProcessor.java
│   ├── MongoConfig.java
│   ├── RateLimitFilter.java
│   └── SecurityConfig.java
├── controllers/            # REST API endpoints
├── dtos/                   # Data Transfer Objects
│   ├── Requests/
│   ├── common/
│   ├── messages/
│   ├── rooms/
│   └── users/
├── exceptions/             # Custom exceptions & global handler
├── helpers/                # Utility classes
├── models/                 # MongoDB entities
├── repositories/           # Data access layer
├── security/               # Authentication & authorization
├── services/               # Business logic
└── utils/                  # JWT utilities
```

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MongoDB instance (local or cloud)
- Cloudinary account
- SendGrid account

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yamiSukehiro2907/studi.io_backend
   cd studi-backend
   ```

2. **Configure environment variables**
   
   Create a `.env` file in the root directory:
   ```env
   MONGODB_URI=mongodb://localhost:27017/studiodb
   MONGODB_DB=studiodb
   
   ACCESS_TOKEN_SECRET=your_access_token_secret_min_32_chars
   REFRESH_TOKEN_SECRET=your_refresh_token_secret_min_32_chars
   ACCESS_TOKEN_EXPIRATION=3600000
   REFRESH_TOKEN_EXPIRATION=604800000
   
   EMAIL=your-email@example.com
   SENDGRID_APIKEY=your_sendgrid_api_key
   
   CLOUDINARY_CLOUDNAME=your_cloud_name
   CLOUDINARY_APISECRET=your_api_secret
   CLOUDINARY_APIKEY=your_api_key
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The server will start on `http://localhost:8081`

## 📚 API Documentation

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Login and receive JWT tokens |
| POST | `/auth/logout` | Logout and clear tokens |
| POST | `/auth/refresh` | Refresh access token |

### User Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/user/profile` | Get current user profile |
| PUT | `/user/update` | Update user profile (multipart) |
| PUT | `/user/change-password` | Change password (authenticated) |
| PUT | `/user/change-password-with-current` | Change password with current verification |

### Study Room Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/rooms/create` | Create a new study room |
| GET | `/rooms/` | Get all rooms for current user |
| GET | `/rooms/{id}` | Get room details by ID |
| POST | `/rooms/{id}/join` | Join a public room |
| PUT | `/rooms/{id}/update` | Update room info (multipart) |
| DELETE | `/rooms/{id}` | Delete a room (owner only) |

### Message Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/messages/{id}?page=1` | Get paginated messages for a room |

### OTP Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/otp/send-otp` | Send OTP to email |
| POST | `/otp/verify-otp` | Verify OTP |

### WebSocket Endpoints

| Endpoint | Description |
|----------|-------------|
| `/ws` | WebSocket connection endpoint |
| `/server/sendMessage` | Client sends message |
| `/server/room/{roomId}` | Subscribe to room updates |

## 🔒 Security Features

- **JWT Authentication** with access and refresh tokens
- **HTTP-only cookies** for secure token storage
- **CORS configuration** with allowed origins
- **Rate limiting** at multiple levels:
  - General: 100 requests/minute per IP
  - Login: 5 attempts/minute per IP
  - Signup: 2 attempts/minute per IP
  - User-specific: 100 requests/minute per user
- **Password encryption** with BCrypt
- **Email verification** with OTP

## 🌐 WebSocket Architecture

The application uses STOMP over WebSocket for real-time communication:

- **Connection:** `/ws` with JWT authentication via handshake interceptor
- **Message Broker:** Simple in-memory broker on `/client` prefix
- **Application Prefix:** `/server` for client-to-server messages
- **Session Management:** Custom session registry tracks user-room mappings
- **Events:** Automatic user-joined/user-left notifications

## 📦 Rate Limiting

Rate limits are enforced using Bucket4j with Caffeine cache:

```java
- Global: 100 requests/min per IP
- Login: 5 attempts/min per IP
- Signup: 2 attempts/min per IP
- Authenticated: 100 requests/min per user
```

## 🗄️ Database Schema

### Collections

- **users** - User accounts and profiles
- **studyrooms** - Study room information and settings
- **messages** - Chat messages with sender and room references
- **otps** - Time-limited OTP codes (10 min expiry via TTL index)

## 🔧 Configuration

Key configuration files:

- `application.yaml` - Main application configuration
- `.env` - Environment variables (not committed)
- `src/main/resources/META-INF/spring.factories` - Environment post-processor registration

## 🧪 Testing

Run tests with:
```bash
mvn test
```

## 👥 Author

**Vimal Kumar Yadav**
- Email: vimalyadavkr001@gmail.com
- GitHub: [@yamiSukehiro2907](https://github.com/yamiSukehiro2907)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request
