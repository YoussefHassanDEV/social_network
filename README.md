🌐 Social Network Backend

A production-ready backend for a social networking platform built with Spring Boot.
It includes secure JWT authentication with blacklist, Redis-powered rate limiting & pub/sub, real-time WebSocket/STOMP messaging, Cloudinary media uploads, and modular REST APIs for users, posts, comments, friends, messaging, notifications, and admin tools.

This project demonstrates scalable system design, performance testing, and enterprise-level practices — making it an excellent recruiter-facing project.

📑 Table of Contents

✨ Highlights

🏗 Architecture

⚙ Tech Stack

🚀 Features

📊 Data Model

🔐 Security

⚡ Real-Time Messaging

🗂 API Endpoints

⚙ Configuration

🧪 Testing & Performance

▶ How to Run

💼 Recruiter Talking Points

📌 Next Improvements

✨ Highlights

✅ JWT Auth with Token Blacklist → Secure login/logout with revocation.

✅ Redis Rate Limiting → Prevent brute force attacks.

✅ Real-Time Messaging → WebSocket/STOMP + Redis Pub/Sub for scaling.

✅ Cloudinary Uploads → Media storage for posts & profiles.

✅ Smart Feed & Trending Posts → SQL + custom queries for relevance.

✅ Admin Tools → Ban users, delete posts, fetch analytics.

✅ Concurrency Testing → Stress tests with 200k users & 500k posts in under 1.3 minutes.

🏗 Architecture
Clients (Web/Mobile)
     ⬇
Spring Boot REST APIs  ←→  Services  ←→  JPA Repositories  ←→  Database
     ⬇
 WebSocket/STOMP (ws://.../ws) + Redis Pub/Sub
     ⬇
 Real-Time Chat, Notifications

⚙ Tech Stack

Java 17 + Spring Boot

Spring Security + JWT (auth & role-based access)

Spring Data JPA (repositories & queries)

Redis (rate limiting, pub/sub)

WebSocket + STOMP (real-time chat)

Cloudinary API (media storage)

JUnit + Stress Tests

Lombok, DTOs, Validation, Exception Handlers

🚀 Features
👤 User

Register/Login/Logout with JWT.

Profile: view & update (bio, job, location, age, images).

Follow/unfollow, list followers/following.

Friendship system (send/accept/reject/block requests).

📝 Posts

Create/edit/delete posts with text/media.

Blur posts when reported (moderation).

Trending posts (based on votes & recency).

Smart feed (personalized, with filters).

💬 Comments & Votes

Add/edit/delete comments.

Upvote/downvote posts & comments.

✉️ Messaging

Private chat (persisted).

Group chat (create group, add members, chat).

WebSocket + Redis → real-time delivery.

🔔 Notifications

Event-driven notifications.

Mark as read.

🛡️ Admin

Ban users, soft-delete accounts/posts.

Stats (user counts, post counts).

Top authors, most reported posts.

📂 Media Upload

Upload images/videos via /api/upload → stored in Cloudinary.

📊 Data Model (Key Entities)

User (id, username, email, password, role, profile fields).

Post (id, author, content, mediaUrl, language, country, blurred, reports).

Comment (id, post, author, content).

Vote (linked to post/comment + user).

Message/GroupMessage (chat system).

Friendship, Follow, Notification, Report (social graph + moderation).

🔐 Security

JWT Authentication (with expiration).

Blacklist on logout → immediate revocation.

Role-based Access → Admin endpoints restricted.

Redis Rate Limiter → Protect login from brute-force.

Global Exception Handler → Consistent JSON errors.

⚡ Real-Time Messaging

Handshake auth with JWT during WebSocket connection.

Subscriptions:

/user/queue/private → private chat.

/topic/group.{id} → group chat.

Send:

/app/private.send

/app/group.send

Scaling: Redis pub/sub → supports multiple server instances.

🗂 API Endpoints (Highlights)
🔐 Auth

POST /api/auth/signup → Register new user.

POST /api/auth/login → Login, returns JWT.

POST /api/auth/logout → Logout, blacklist token.

👤 User

GET /api/profile/{id} → Get profile.

PUT /api/profile/{id} → Update profile.

📝 Posts

POST /api/posts/{authorId} → Create post.

GET /api/posts/feed → Smart feed.

GET /api/posts/user/{authorId}/{viewerId} → User posts.

DELETE /api/posts/{id}/{userId} → Delete post.

💬 Comments

POST /api/comments/{postId}/{userId} → Add comment.

GET /api/comments/post/{id} → Post comments.

👍 Votes

POST /api/votes/post/{id}/user/{id}?voteType=UPVOTE

POST /api/votes/comment/{id}/user/{id}?voteType=DOWNVOTE

👥 Social Graph

POST /api/follow/{id}/follow/{id}

POST /api/friends/send/{from}/{to} → Friend request.

✉️ Messaging

POST /api/messages/send/{from}/{to} → Private message.

GET /api/messages/conversation/{id1}/{id2} → History.

POST /api/groups/create?name=X → Create group.

🔔 Notifications

GET /api/notifications/{userId}

POST /api/notifications/read/{id}

🛡️ Admin

POST /api/admin/ban-user/{id}

GET /api/admin/user-stats

GET /api/admin/top-authors

📂 Upload

POST /api/upload → Upload file to Cloudinary.

⚙ Configuration

Environment variables / application.yml:

security.jwt.secret: <your_secret>
security.jwt.expiration: 36000
spring.datasource.url: jdbc:mysql://localhost:3306/socialnetwork
spring.datasource.username: root
spring.datasource.password: root
spring.redis.host: localhost
spring.redis.port: 6379
cloudinary.cloud_name: <your_cloud>
cloudinary.api_key: <your_key>
cloudinary.api_secret: <your_secret>

🧪 Testing & Performance

RepositoryConcurrentStressTest → Validates performance under concurrency.

Generates 200k users + 500k posts.

Runs concurrent queries for Top Authors & Smart Feed in ~1.3 minutes.

Demonstrates scalability & optimization of queries.

▶ How to Run
# clone
git clone https://github.com/YoussefHassanDEV/socialnetwork-backend.git
cd socialnetwork-backend

# set env in application.yml or as environment variables
# run
./mvnw spring-boot:run

💼 Recruiter Talking Points

Built secure, scalable backend (JWT blacklist, Redis rate limiter).

Implemented real-time messaging with WebSocket + Redis pub/sub.

Integrated Cloudinary for media storage.

Added admin dashboards for analytics & moderation.

Performed concurrency stress tests (200k users, 500k posts).

Designed modular APIs covering full social graph & content workflows.

📌 Next Improvements

Add Swagger/OpenAPI docs.

Expand E2E tests (WebSocket + auth flows).

Add monitoring/metrics (Prometheus/Grafana).

Implement AI-based content moderation.

✅ This is a complete recruiter-ready README.md.

Would you like me to now format it directly as a polished Markdown file (README.md) so you can drop it into your GitHub repo?

You said:
SO i wanna make Github Documentation on Everything 
ChatGPT said:

Perfect 🚀 You want a full GitHub-ready documentation (README.md) that covers everything in your project — features, APIs, architecture, tech stack, testing, and recruiter highlights.

I’ll make it Markdown-formatted so you can just copy it into your repo’s README.md.

Here’s the complete documentation:

🌐 Social Network Backend

A production-ready backend for a social networking platform built with Spring Boot.
It includes secure JWT authentication with blacklist, Redis-powered rate limiting & pub/sub, real-time WebSocket/STOMP messaging, Cloudinary media uploads, and modular REST APIs for users, posts, comments, friends, messaging, notifications, and admin tools.

This project demonstrates scalable system design, performance testing, and enterprise-level practices — making it an excellent recruiter-facing project.

📑 Table of Contents

✨ Highlights

🏗 Architecture

⚙ Tech Stack

🚀 Features

📊 Data Model

🔐 Security

⚡ Real-Time Messaging

🗂 API Endpoints

⚙ Configuration

🧪 Testing & Performance

▶ How to Run

💼 Recruiter Talking Points

📌 Next Improvements

✨ Highlights

🔐 JWT Auth with Token Blacklist → Secure login/logout with revocation.

🚦 Redis Rate Limiting → Prevent brute-force attacks.

⚡ Real-Time Messaging → WebSocket/STOMP + Redis Pub/Sub for horizontal scaling.

☁ Cloudinary Uploads → Media storage for posts & profiles.

📰 Smart Feed & Trending Posts → SQL + custom queries for relevance.

🛡 Admin Tools → Ban users, delete posts, fetch analytics.

🧪 Concurrency Testing → Stress tests with 200k users & 500k posts in under 1.3 minutes.

🏗 Architecture
Clients (Web/Mobile)
     ⬇
Spring Boot REST APIs  ←→  Services  ←→  JPA Repositories  ←→  Database
     ⬇
 WebSocket/STOMP (ws://.../ws) + Redis Pub/Sub
     ⬇
 Real-Time Chat, Notifications

⚙ Tech Stack

Java 17 + Spring Boot

Spring Security + JWT (auth & role-based access)

Spring Data JPA (repositories & queries)

Redis (rate limiting, pub/sub)

WebSocket + STOMP (real-time chat)

Cloudinary API (media storage)

JUnit + Stress Tests

Lombok, DTOs, Validation, Exception Handlers

🚀 Features
👤 User

Register/Login/Logout with JWT.

Profile: view & update (bio, job, location, age, images).

Follow/unfollow, list followers/following.

Friendship system (send/accept/reject/block requests).

📝 Posts

Create/edit/delete posts with text/media.

Blur posts when reported (moderation).

Trending posts (based on votes & recency).

Smart feed (personalized, with filters).

💬 Comments & Votes

Add/edit/delete comments.

Upvote/downvote posts & comments.

✉️ Messaging

Private chat (persisted).

Group chat (create group, add members, chat).

WebSocket + Redis → real-time delivery.

🔔 Notifications

Event-driven notifications.

Mark as read.

🛡️ Admin

Ban users, soft-delete accounts/posts.

Stats (user counts, post counts).

Top authors, most reported posts.

📂 Media Upload

Upload images/videos via /api/upload → stored in Cloudinary.

📊 Data Model (Key Entities)

User → id, username, email, password, role, profile fields.

Post → author, content, mediaUrl, language, country, blurred, reports.

Comment → post, author, content.

Vote → linked to post/comment + user.

Message/GroupMessage → chat system.

Friendship, Follow, Notification, Report → social graph + moderation.

🔐 Security

JWT Authentication (with expiration).

Blacklist on logout → immediate revocation.

Role-based Access → Admin endpoints restricted.

Redis Rate Limiter → Protect login from brute-force.

Global Exception Handler → Consistent JSON errors.

⚡ Real-Time Messaging

Handshake auth with JWT during WebSocket connection.

Subscriptions:

/user/queue/private → private chat.

/topic/group.{id} → group chat.

Send:

/app/private.send

/app/group.send

Scaling: Redis pub/sub → supports multiple server instances.

🗂 API Endpoints (Highlights)
🔐 Auth

POST /api/auth/signup → Register new user.

POST /api/auth/login → Login, returns JWT.

POST /api/auth/logout → Logout, blacklist token.

👤 User

GET /api/profile/{id} → Get profile.

PUT /api/profile/{id} → Update profile.

📝 Posts

POST /api/posts/{authorId} → Create post.

GET /api/posts/feed → Smart feed.

GET /api/posts/user/{authorId}/{viewerId} → User posts.

DELETE /api/posts/{id}/{userId} → Delete post.

PUT /api/posts/{id}/{userId} → Edit post.

💬 Comments

POST /api/comments/{postId}/{userId} → Add comment.

GET /api/comments/post/{id} → Post comments.

DELETE /api/comments/{id}/{userId} → Delete comment.

PUT /api/comments/{id}/{userId} → Edit comment.

👍 Votes

POST /api/votes/post/{id}/user/{id}?voteType=UPVOTE/DOWNVOTE

POST /api/votes/comment/{id}/user/{id}?voteType=UPVOTE/DOWNVOTE

GET /api/votes/post/{id}/upvotes / downvotes

GET /api/votes/comment/{id}/upvotes / downvotes

👥 Social Graph

POST /api/follow/{followerId}/follow/{followingId} → Follow user.

DELETE /api/follow/{followerId}/unfollow/{followingId} → Unfollow user.

GET /api/follow/{userId}/followers / following

👫 Friendship

POST /api/friends/send/{from}/{to} → Send request.

POST /api/friends/accept/{id} → Accept request.

POST /api/friends/reject/{id} → Reject request.

POST /api/friends/block/{id} → Block request/friendship.

GET /api/friends/list/{id} → Friend list.

GET /api/friends/pending/received/{id} → Incoming requests.

GET /api/friends/pending/sent/{id} → Outgoing requests.

✉️ Messaging

POST /api/messages/send/{from}/{to} → Private message.

GET /api/messages/conversation/{id1}/{id2} → History.

DELETE /api/messages/{id}/{userId} → Delete message.

👨‍👩‍👧 Group Chat

POST /api/groups/create?name=X → Create group.

POST /api/groups/{id}/add-member/{userId} → Add member.

POST /api/groups/{id}/send/{senderId} → Send group message.

GET /api/groups/{id}/messages → Group messages.

🔔 Notifications

POST /api/notifications/{userId} → Create notification.

GET /api/notifications/{userId} → List notifications.

POST /api/notifications/read/{id} → Mark as read.

🛡️ Admin

POST /api/admin/ban-user/{id} → Ban user.

DELETE /api/admin/delete-user/{id} → Delete user.

DELETE /api/admin/delete-post/{id} → Delete post.

GET /api/admin/user-stats / post-stats

GET /api/admin/top-authors?page=x&size=y

GET /api/admin/most-reported-posts?page=x&size=y

📂 Upload

POST /api/upload → Upload file (returns Cloudinary URL).

⚙ Configuration

Environment variables / application.yml:

security.jwt.secret: <your_secret>
security.jwt.expiration: 36000
spring.datasource.url: jdbc:mysql://localhost:3306/socialnetwork
spring.datasource.username: root
spring.datasource.password: root
spring.redis.host: localhost
spring.redis.port: 6379
cloudinary.cloud_name: <your_cloud>
cloudinary.api_key: <your_key>
cloudinary.api_secret: <your_secret>

🧪 Testing & Performance

RepositoryConcurrentStressTest → Validates performance under concurrency.

Generates 200k users + 500k posts.

Runs concurrent queries for Top Authors & Smart Feed.

All completed in ~1.3 minutes.

▶ How to Run
# clone
git clone https://github.com/YoussefHassanDEV/socialnetwork-backend.git
cd socialnetwork-backend

# run with maven
./mvnw spring-boot:run

💼 Recruiter Talking Points

🔐 JWT blacklist & Redis rate limiter (production-ready security).

⚡ WebSocket + Redis Pub/Sub (scalable real-time chat).

☁ Cloudinary integration (media storage).

🛡 Admin analytics & moderation tools.

🧪 Concurrency stress tests (200k users, 500k posts).

🏗 Clean modular architecture (DTOs, services, controllers).

📌 Next Improvements

Add Swagger/OpenAPI docs.

Expand E2E tests (WebSocket + auth).

Add metrics/monitoring (Prometheus/Grafana).

Implement AI-driven content moderation.
