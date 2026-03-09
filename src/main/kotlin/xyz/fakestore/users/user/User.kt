package xyz.fakestore.users.user

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(unique = true, nullable = false)
    var email: String,

    @Column(unique = true, nullable = false)
    val username: String,

    @Column(nullable = false)
    var passwordHash: String,

    val createdAt: Instant = Instant.now()
)
