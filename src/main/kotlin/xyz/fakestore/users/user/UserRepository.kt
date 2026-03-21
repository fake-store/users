package xyz.fakestore.users.user

import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.table
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
class UserRepository(private val dsl: DSLContext) {

    private val USERS = table("users")
    private val ID = field("id", UUID::class.java)
    private val EMAIL = field("email", String::class.java)
    private val USERNAME = field("username", String::class.java)
    private val PASSWORD_HASH = field("password_hash", String::class.java)
    private val CREATED_AT = field("created_at", LocalDateTime::class.java)

    private fun Record.toUser() = User(
        id = get(ID),
        email = get(EMAIL),
        username = get(USERNAME),
        passwordHash = get(PASSWORD_HASH),
        createdAt = get(CREATED_AT)
    )

    fun findByEmail(email: String): User? =
        dsl.select().from(USERS).where(EMAIL.eq(email)).fetchOne { it.toUser() }

    fun findById(id: UUID): User? =
        dsl.select().from(USERS).where(ID.eq(id)).fetchOne { it.toUser() }

    fun existsByEmail(email: String): Boolean =
        dsl.fetchExists(USERS, EMAIL.eq(email))

    fun existsByUsername(username: String): Boolean =
        dsl.fetchExists(USERS, USERNAME.eq(username))

    fun insert(user: User): User {
        dsl.insertInto(USERS)
            .set(ID, user.id)
            .set(EMAIL, user.email)
            .set(USERNAME, user.username)
            .set(PASSWORD_HASH, user.passwordHash)
            .set(CREATED_AT, user.createdAt)
            .execute()
        return user
    }

    fun updateEmail(id: UUID, email: String) {
        dsl.update(USERS)
            .set(EMAIL, email)
            .where(ID.eq(id))
            .execute()
    }

    fun count(): Long = dsl.fetchCount(USERS).toLong()

    fun deleteAll(): Long {
        val count = count()
        dsl.deleteFrom(USERS).execute()
        return count
    }
}
