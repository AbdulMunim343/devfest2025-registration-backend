# Security Configuration Guide

## Overview

This document outlines the security configurations implemented in the GDG Kolachi DevFest Registration Backend.

---

## 1. CORS (Cross-Origin Resource Sharing)

### Configuration Location
`src/main/java/com/regbackend/registrationbackend/config/SecurityConfig.java`

### Allowed Origins
```
http://localhost:5173          → Local development (Vite)
https://devfest.gdgkolachi.com → Production
https://devfest2025-registration-frontend.vercel.app → Vercel preview
http://devfest.gdgkolachi.com  → Production (HTTP - consider removing)
```

### Allowed Methods
```
GET, POST, PUT, DELETE, OPTIONS
```

### How It Works
- Only requests from the listed origins can access the API
- Browser blocks requests from any other domain
- Preflight OPTIONS requests are automatically permitted

### Adding New Origins
Edit `SecurityConfig.java`:
```java
config.setAllowedOrigins(List.of(
    "http://localhost:5173",
    "https://devfest.gdgkolachi.com",
    "https://your-new-domain.com"  // Add here
));
```

### Important
- Do NOT add `@CrossOrigin(origins = "*")` to controllers - it overrides these settings
- Keep production origins HTTPS only for security

---

## 2. Rate Limiting

### Configuration Location
`src/main/java/com/regbackend/registrationbackend/security/RateLimitFilter.java`

### Rate Limits

| Endpoint Type | Limit | Time Window | Purpose |
|---------------|-------|-------------|---------|
| `/api/auth/*` | 10 requests | 1 minute | Prevent brute force login attacks |
| All other endpoints | 100 requests | 1 minute | General API protection |

### How It Works
1. Each client IP address gets its own "bucket" of tokens
2. Each request consumes 1 token
3. Tokens refill automatically over time
4. When bucket is empty → `429 Too Many Requests`

### Response When Rate Limited
```json
{
  "status": 429,
  "message": "Too many requests. Please try again later."
}
```

### IP Detection
The filter checks for `X-Forwarded-For` header first (for proxied requests), then falls back to `remoteAddr`.

### Adjusting Limits
Edit `RateLimitFilter.java`:

```java
// Standard endpoints - change 100 to your desired limit
private Bucket createStandardBucket(String key) {
    Bandwidth limit = Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1)));
    return Bucket.builder().addLimit(limit).build();
}

// Auth endpoints - stricter limit
private Bucket createAuthBucket(String key) {
    Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
    return Bucket.builder().addLimit(limit).build();
}
```

### Dependencies
```xml
<dependency>
    <groupId>com.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.10.1</version>
</dependency>
```

---

## 3. JWT Authentication

### Configuration
- Secret Key: Stored in `application.properties` as `jwt.secret`
- Expiration: 24 hours (86400000 ms)
- Algorithm: HS256

### Protected vs Public Endpoints

| Endpoint | Auth Required |
|----------|---------------|
| `POST /api/auth/register` | No |
| `POST /api/auth/login` | No |
| `POST /api/registrations/register` | No |
| `GET /api/registrations/filter` | No |
| `GET /api/registrations/stats` | No |
| `POST /api/registrations/scanQR` | No |
| All other endpoints | Yes (JWT required) |

### Using JWT
Include in request header:
```
Authorization: Bearer <your-jwt-token>
```

---

## 4. Security Checklist

### Environment Variables (Recommended)
Move these from `application.properties` to environment variables:

```bash
export DB_PASSWORD=your_password
export JWT_SECRET=your_secret_key
export RESEND_API_KEY=your_api_key
```

Then in `application.properties`:
```properties
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
resend.api.key=${RESEND_API_KEY}
```

### Production Recommendations
- [ ] Remove `http://devfest.gdgkolachi.com` (keep HTTPS only)
- [ ] Move secrets to environment variables
- [ ] Consider protecting `/api/auth/register` endpoint
- [ ] Add security headers (X-Content-Type-Options, etc.)
- [ ] Remove debug logging in `JwtAuthFilter.java`

---

## 5. Filter Chain Order

```
Request
   ↓
RateLimitFilter (checks rate limit)
   ↓
JwtAuthFilter (validates JWT token)
   ↓
Spring Security (authorization check)
   ↓
Controller
```

---

## 6. Testing

### Test CORS
```bash
# Should work (allowed origin)
curl -H "Origin: https://devfest.gdgkolachi.com" \
     -I http://localhost:8080/api/registrations/stats

# Should be blocked (not allowed)
curl -H "Origin: https://evil-site.com" \
     -I http://localhost:8080/api/registrations/stats
```

### Test Rate Limiting
```bash
# Run 15 times quickly - last 5 should get 429
for i in {1..15}; do
  curl -s -o /dev/null -w "%{http_code}\n" \
    http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"email":"test@test.com","password":"wrong"}'
done
```

---

## 7. Quick Reference

| Security Feature | Status | Location |
|-----------------|--------|----------|
| CORS | ✅ Configured | `SecurityConfig.java` |
| Rate Limiting | ✅ Enabled | `RateLimitFilter.java` |
| JWT Auth | ✅ Enabled | `JwtAuthFilter.java`, `JwtService.java` |
| Password Hashing | ✅ BCrypt | `SecurityConfig.java` |
| CSRF | Disabled | Stateless API, not needed |
| Session | Stateless | JWT-based |
