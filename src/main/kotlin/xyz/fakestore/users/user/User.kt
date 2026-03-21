package xyz.fakestore.users.user

import java.time.LocalDateTime
import java.util.UUID

data class User(
    val id: UUID,
    val email: String,
    val username: String,
    val passwordHash: String,
    val createdAt: LocalDateTime
)
