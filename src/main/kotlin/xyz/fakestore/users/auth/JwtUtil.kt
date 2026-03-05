package xyz.fakestore.users.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import java.util.UUID

@Component
class JwtUtil(@Value("\${jwt.secret}") private val secret: String) {

    private val key by lazy { Keys.hmacShaKeyFor(secret.toByteArray()) }
    private val expirationMs = 24 * 60 * 60 * 1000L  // 24 hours

    fun generateToken(userId: UUID, email: String, username: String): String =
        Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("username", username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expirationMs))
            .signWith(key)
            .compact()

    fun validateAndGetUserId(token: String): UUID? = runCatching {
        val claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        UUID.fromString(claims.subject)
    }.getOrNull()

    fun validateAndGetClaims(token: String): Map<String, Any>? = runCatching {
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
            .let { mapOf("userId" to it.subject, "email" to it["email"]!!, "username" to it["username"]!!) }
    }.getOrNull()
}
