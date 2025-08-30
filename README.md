Social Network Backend

A production-ready backend for a modern social networking platform built with Spring Boot (Java 17).
Features: secure JWT auth + blacklist, Redis-powered rate limiting & Pub/Sub, WebSocket/STOMP real-time messaging, Cloudinary media uploads, modular REST APIs (users, posts, comments, friends, messaging, notifications, admin), a Smart Feed with advanced filters and Saved Filters, moderation & blur workflows, and production-focused performance testing.

This README is a single-file, complete GitHub-style documentation for the project — drop into README.md.

Table of Contents

Highlights

Architecture

Tech Stack

Feature Overview

Smart Feed — Design & API

Scoring & Tuning

Pagination & Cursor Behavior

Filtering System & Saved Filters

Filter fields & semantics

Applying filters — HTTP & JSON

Saved filters endpoints

Trending & Discovery

Moderation, Reports & Blurring

Detailed API Endpoints (complete)

Auth, Profile, Posts & Feed, Comments, Votes, Follow, Friends, Messages & Groups, Notifications, Upload, Admin, Filters & Search

Data Model & Indexing Recommendations

Security & Rate Limiting

Real-Time (WebSocket/STOMP) Details & Examples

Testing & Performance

Run / Deployment

Recruiter Talking Points

Next Improvements / Roadmap

Highlights

✅ JWT authentication (stateless) + Redis-backed token blacklist for immediate logout/revocation.

✅ Redis rate limiter (login protection) using INCR + EXPIRE.

✅ Scalable real-time chat: WebSocket + STOMP + Redis Pub/Sub for horizontal scale.

✅ Cloudinary integration for media uploads (posts/profile).

✅ Smart Feed: viewer-aware personalized feed with multi-criteria filters, saved filters, cursor pagination, and ranking.

✅ Moderation: reports, admin queues, blurring (with blurReason) and admin actions.

✅ Stress-tested: batches simulating 200k users & 500k posts — feed & analytics queries optimized.

Architecture
Clients (Web/Mobile)
    ├── REST API (Spring Boot controllers)
    ├── WebSocket STOMP (/ws) for real-time
    ↓
Spring Boot App
    ├── Controllers → Services → Repositories (Spring Data JPA)
    ├── JwtService, JwtAuthFilter, JwtBlacklistService
    ├── Redis:
    │     ├─ String ops for RateLimiter
    │     └─ RedisTemplate + Pub/Sub (chat:messages) for real-time
    └── Cloudinary integration (media)
Database (Postgres / MySQL)

Tech Stack

Java 17, Spring Boot (web, security, data-jpa, validation, websocket)

Spring Security + JWT (custom JwtService + JwtAuthFilter)

Redis (Lettuce): rate limiting, cache primitives, Pub/Sub

WebSocket + STOMP (SockJS fallback), SimpMessagingTemplate

Cloudinary (media upload service)

Lombok, DTOs, Bean Validation, Global Exception Handler

JUnit + integration & concurrency stress tests

Feature Overview (concise)

User: register/login/logout, profiles, update avatar/cover, follow/unfollow, friend requests (send/accept/reject/block).

Posts: create/edit/delete posts (text + media), tags, language & country metadata, blur flag + blur reason.

Smart Feed: personalized ranking (follows/friends/engagement/recency) + filters, saved filters, cursor pagination.

Trending: time-windowed trending endpoint.

Comments & Votes: add/edit/delete comments; upvote/downvote posts and comments.

Messaging: private & group messages; persisted and delivered real-time.

Notifications: event-driven, mark-as-read.

Moderation: reports, moderator views, most-reported-posts, blur workflows.

Admin: ban users, delete posts/users, stats, top authors.

Uploads: multipart file upload -> Cloudinary -> return URL.

Smart Feed — Design & API

The Smart Feed is first-class: personalized, filterable, and performant.

Goals

Personalization: prioritize content from followed users, friends, or users the viewer interacts with.

Freshness: boost recent content with configurable decay.

Quality: favor high-voted, low-reported content.

Safety: honor user blocked lists, hide blurred content by default.

Filters: multi-criteria filters (language, media, tags, country, minUpvotes, date ranges, friendsOnly, etc.).

Cursor-based pagination for robust infinite scroll.

Feed scoring (conceptual)
score(post, viewer) =
    w_follow   * isFollowed(viewer, author)
  + w_friend   * isFriend(viewer, author)
  + w_votes    * log(1 + upvotes - downvotes)
  + w_recency  * recencyBoost(ageSeconds)
  + w_engage   * engagementScore(post)
  - w_reports  * log(1 + reports)
  + w_viewed   * viewerAffinityScore(viewer, author)


recencyBoost(age) = 1 / (1 + age / halfLife) (halfLife configurable, e.g. 6h)

engagementScore = normalized comments + shares over last 24h

viewerAffinityScore increases if viewer has previously upvoted or commented on author’s posts

Configurable weights (example defaults)

follow = 3, friend = 4, votes = 2, recency = 3, reports = 5, engagement = 2

Implementations can precompute authorReputation and store aggregated upvotes, reports on posts to speed calculation.

Feed API
GET /api/posts/feed

Query parameters

viewerId (required if personalized)

page (default 0) or cursor (opaque token for cursor pagination)

size (default 20)

sortBy (score | recent | upvotes | comments)

direction (desc | asc)

Filters (see next section): mediaOnly, tags, language, country, minUpvotes, since, followingOnly, friendsOnly, etc.

Response

{
  "page": 0,
  "size": 20,
  "cursor": "opaque-token",
  "data": [ PostResponseDTO, ... ]
}


PostResponseDTO (essential fields)

id, content, authorId, authorName, upvotes, downvotes, score, mediaUrl, blurred, blurReason, createdAt, tags, language, country

Notes

If cursor supplied, page is ignored. Server returns cursor for next page.

Default behavior hides blurred=true posts unless user is moderator or filter explicitly asks for hasBlur=true.

Pagination & Cursor Behavior

Cursor is an opaque base64 token containing (last_score,last_created_at) or a server-generated ID that encodes position.

Cursor-based pagination ensures stable ordering even when new posts are created.

For large pages use size <= 50 recommended.

Filtering System & Saved Filters

A flexible server-side filter system supports simple and advanced queries.

Filter fields & semantics

authorId (exact)

authorName (exact or partial)

tags (match any by default; optionally matchAll=true)

mediaOnly (true/false) — requires mediaUrl not null

hasBlur (true/false) — show only blurred items (moderator)

minUpvotes, maxUpvotes

minScore, maxScore

since (ISO datetime) / until

language (ISO)

country

followingOnly (true) — only authors viewer follows

friendsOnly (true) — only friends

contentContains — keyword full-text

reportedOnly — moderation queue

mutuals — mutual following authors

Applying filters — HTTP or JSON

Simple querystring

GET /api/posts/feed?viewerId=5&mediaOnly=true&tags=travel,food&minUpvotes=10


Complex JSON body (POST)

POST /api/posts/feed/filtered
Content-Type: application/json
{
  "viewerId":5,
  "page":0,
  "size":20,
  "sortBy":"score",
  "filters": {
    "mediaOnly": true,
    "tags": ["travel","food"],
    "minUpvotes": 10,
    "since": "2025-08-01T00:00:00Z",
    "followingOnly": true
  }
}


Response is same shape as GET /api/posts/feed.

Saved filters (user presets)

POST /api/filters — Save a filter:

{ "userId": 5, "name": "OnlyPhotos", "filter": { ... } }


GET /api/filters/{userId} — List saved filters.

GET /api/filters/{userId}/{filterId} — Retrieve a saved filter.

DELETE /api/filters/{filterId} — Delete saved filter.

Use saved filter: GET /api/posts/feed?viewerId=5&filterId=abc123

Saved filters are persisted as JSON and can be applied server-side.

Trending & Discovery

GET /api/posts/trending?timeWindow=24h&tags=...&size=10

Trending algorithm (example):

trending_score = (upvotes * log(1+comments)) * freshnessMultiplier / (1 + log(1+reports))

freshnessMultiplier increases for recent posts (e.g., double for posts < 1h).

Support timeWindow (24h, 7d, 30d) to scope trends.

Use Redis sorted sets to cache top-K trending across segments (global, by-country, tag-based).

Moderation, Reports & Blurring
Report workflow

POST /api/reports/post/{postId}/user/{userId} — file report with reason body.

Reports increment reportsCount on post; if reports exceed threshold post may be auto-blurred or flagged for review.

Blurring

Posts have blurred (boolean) and blurReason (string).

Default client behavior: show blurred preview with CTA. Moderator or explicit user action displays content.

Moderators can PUT /api/posts/{postId}/moderate to set blurred/unblurred and blurReason.

Moderator views

GET /api/admin/most-reported-posts — paged, sorted by reportsCount.

GET /api/admin/reports — list of report objects for review.

Detailed API Endpoints (complete)

For brevity this section lists endpoints with parameters and examples. Try to keep your API controllers consistent with these contracts.

Auth

POST /api/auth/signup
Body: { "username", "email", "password" }
-> returns AuthResponse { token, type, expiresIn }

POST /api/auth/login
Body: { "email", "password" }
-> returns AuthResponse

POST /api/auth/logout
Header: Authorization: Bearer <jwt>
-> 200 OK (blacklists token)

Profile

GET /api/profile/{userId} -> UserProfileDTO

PUT /api/profile/{userId} -> accept UserProfileDTO to update

Posts & Feed

POST /api/posts/{authorId} ?content=...&mediaUrl=... -> create post

GET /api/posts/feed -> Smart feed (see above)

POST /api/posts/feed/filtered -> advanced filtered feed

GET /api/posts/user/{authorId}/{viewerId} -> posts by user (viewer context)

PUT /api/posts/{postId}/{userId} -> edit post

DELETE /api/posts/{postId}/{userId} -> delete post

Comments

POST /api/comments/{postId}/{userId} (body: raw content) -> adds comment

GET /api/comments/post/{postId}?page=&size= -> list comments

PUT /api/comments/{commentId}/{userId} -> edit

DELETE /api/comments/{commentId}/{userId} -> delete

Votes

POST /api/votes/post/{postId}/user/{userId}?voteType=UP|DOWN

GET /api/votes/post/{postId}/upvotes / downvotes

Same endpoints for comment votes

Follow

POST /api/follow/{followerId}/follow/{followingId}

DELETE /api/follow/{followerId}/unfollow/{followingId}

GET /api/follow/{userId}/followers / following

Friendship (bidirectional)

POST /api/friends/send/{requesterId}/{receiverId}

POST /api/friends/accept/{friendshipId}

POST /api/friends/reject/{friendshipId}

POST /api/friends/block/{friendshipId}

GET /api/friends/list/{userId}

GET /api/friends/pending/received/{userId}

GET /api/friends/pending/sent/{userId}

Messaging (REST)

POST /api/messages/send/{senderId}/{receiverId} (body: content) -> persists & returns MessageResponseDTO

GET /api/messages/conversation/{user1Id}/{user2Id}?page=&size= -> conversation history

DELETE /api/messages/{messageId}/{userId} -> delete message

Groups (chat)

POST /api/groups/create?name=... Body: [memberIds] -> create group

POST /api/groups/{groupId}/add-member/{userId} -> add member

POST /api/groups/{groupId}/send/{senderId} -> post group message

GET /api/groups/{groupId}/messages -> group history

Notifications

POST /api/notifications/{recipientId} Body: message -> create notification

GET /api/notifications/{userId} -> list notifications

POST /api/notifications/read/{notificationId} -> mark as read

Uploads

POST /api/upload multipart file -> returns { "url": "<cloudinary_url>" }

Admin

POST /api/admin/ban-user/{userId}

DELETE /api/admin/delete-user/{userId}

DELETE /api/admin/delete-post/{postId}

GET /api/admin/user-stats

GET /api/admin/post-stats

GET /api/admin/top-authors?page=&size=&sortBy=&direction=

GET /api/admin/most-reported-posts?page=&size=&sortBy=&direction=

Filters & Search

POST /api/filters save filter

GET /api/filters/{userId}

GET /api/filters/{userId}/{filterId}

DELETE /api/filters/{filterId}

GET /api/search/posts?q=...&page=&size=&tags=&language=

Data Model & Indexing Recommendations

Key entities: User, Post, Comment, Vote, Message, Group, Friendship, Follow, Notification, Report, SavedFilter.

Essential DB indexes

posts(created_at) — recency queries

posts(author_id) — author filters

posts(language), posts(country) — locale filters

post_tags(post_id, tag_id) composite index

aggregated counts posts.upvotes, posts.reports — store as fields updated transactionally

messages(conversation_id, created_at) — conversation retrieval

text index (Postgres tsvector) on posts.content, posts.title (if any) for contentContains search

Materialized / cache suggestions

Precompute top_posts_{country|tag} as materialized view refreshed hourly

Redis sorted sets for top-K trending per segment

Cache feed top results per segment (country/follow bucket) and personalized deltas for real-time freshness

Security & Rate Limiting

JWT: security.jwt.secret + security.jwt.expiration (seconds)

Logout: blacklisted tokens stored in Redis with TTL = remaining token life

Rate limiter: login rl:login:{email}:{ip} using INCR & EXPIRE with defaults e.g., loginMaxAttempts=5 in loginWindowSeconds=60 (configurable)

RBAC: Spring Security roles (ROLE_USER, ROLE_ADMIN) enforced in SecurityConfig

Custom error handlers: AuthenticationEntryPoint for 401, AccessDeniedHandler for 403, and ApiExceptionHandler for validation errors

Real-Time (WebSocket/STOMP) — Details & Examples

Endpoint: /ws (SockJS fallback). STOMP app prefix /app, broker /topic & /queue, user prefix /user.

Handshake: JwtHandshakeInterceptor validates Authorization: Bearer <token> and attaches Principal to WebSocket session.

STOMP destinations

Client subscribe to private: /user/queue/private (receive 1:1 messages)

Group: /topic/group.{groupId}

Send mappings

/app/private.send — payload PrivateChatMessage { receiverId, content }

/app/group.send — payload GroupChatMessage { groupId, content }

JS example

const sock = new SockJS('http://localhost:8080/ws');
const client = Stomp.over(sock);

client.connect({ Authorization: 'Bearer ' + token }, () => {
  client.subscribe('/user/queue/private', m => console.log(JSON.parse(m.body)));
  client.subscribe('/topic/group.7', m => console.log('group', JSON.parse(m.body)));
  client.send('/app/private.send', {}, JSON.stringify({ receiverId: 42, content: 'Hello' }));
});


Scaling: server publishes incoming messages to Redis channel chat:messages. Each instance subscribes and forwards to its local sessions.

Testing & Performance

Unit tests: services, controllers, utility functions (feed scoring, filter parsing).

Integration tests: auth flows, JWT blacklisting, WebSocket handshake + messaging (mock STOMP).

Stress tests: concurrency test harness generates ~200k users & 500k posts and validates feed generation and analytics (top authors) in ~1.3 minutes — achieved via optimized queries + indexing + caching.

Load recommendations: run feed endpoints behind a caching layer (Redis/HTTP cache), materialize heavy aggregates, use optimistic locking for counters.

Run / Deployment
Environment (application.yml or env vars)
security:
  jwt:
    secret: <your_secret>
    expiration: 36000

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/socialnetwork
    username: dbuser
    password: dbpass

spring:
  redis:
    host: localhost
    port: 6379

cloudinary:
  cloud_name: <your_cloud>
  api_key: <your_key>
  api_secret: <your_secret>

rate:
  limit:
    loginMaxAttempts: 5
    loginWindowSeconds: 60

Run locally
git clone https://github.com/YoussefHassanDEV/socialnetwork-backend.git
cd socialnetwork-backend
# set env variables or application.yml
./mvnw clean package
./mvnw spring-boot:run

Docker / K8s suggestions (next steps)

Dockerfile for app; use manifests for Postgres + Redis + Cloudinary secrets.

Horizontal pods behind an ingress; enable sticky sessions only if not using STOMP user destinations + Redis.

Recruiter Talking Points

Built a production-grade backend: auth, security, role-based access, token revocation.

Designed & implemented personalized Smart Feed + filters and saved filters — highlights backend product thinking.

Implemented scalable real-time chat using WebSocket/STOMP + Redis Pub/Sub.

Cloud media integration (Cloudinary) decouples storage concerns.

Performed concurrency stress testing to validate scaling assumptions (200k users, 500k posts).

Modular codebase: controllers → services → repositories, DTOs, validation, and global exceptions.
