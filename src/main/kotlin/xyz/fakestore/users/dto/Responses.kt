package xyz.fakestore.users.dto

import java.util.UUID

data class LoginResponse(
    val token: String,
    val userId: UUID,
    val username: String,
    val email: String
)

data class UserResponse(
    val userId: UUID,
    val username: String,
    val email: String
)
