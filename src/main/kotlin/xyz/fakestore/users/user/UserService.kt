package xyz.fakestore.users.user

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import xyz.fakestore.users.auth.JwtUtil
import xyz.fakestore.users.dto.LoginRequest
import xyz.fakestore.users.dto.LoginResponse
import xyz.fakestore.users.dto.RegisterRequest
import xyz.fakestore.users.dto.UpdateEmailRequest
import xyz.fakestore.users.dto.UserResponse
import java.time.LocalDateTime
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {

    fun register(request: RegisterRequest): LoginResponse {
        if (userRepository.existsByEmail(request.email))
            throw IllegalArgumentException("Email already in use")
        if (userRepository.existsByUsername(request.username))
            throw IllegalArgumentException("Username already taken")

        val user = userRepository.insert(
            User(
                id = UUID.randomUUID(),
                email = request.email,
                username = request.username,
                passwordHash = passwordEncoder.encode(request.password),
                createdAt = LocalDateTime.now()
            )
        )
        val token = jwtUtil.generateToken(user.id, user.email, user.username)
        return LoginResponse(token = token, userId = user.id, username = user.username, email = user.email)
    }

    fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("Invalid credentials")
        if (!passwordEncoder.matches(request.password, user.passwordHash))
            throw IllegalArgumentException("Invalid credentials")

        val token = jwtUtil.generateToken(user.id, user.email, user.username)
        return LoginResponse(token = token, userId = user.id, username = user.username, email = user.email)
    }

    fun getById(userId: UUID): UserResponse {
        val user = userRepository.findById(userId) ?: throw NoSuchElementException("User not found")
        return UserResponse(userId = user.id, username = user.username, email = user.email)
    }

    fun updateEmail(userId: UUID, request: UpdateEmailRequest): UserResponse {
        if (request.email.isBlank()) throw IllegalArgumentException("Email must not be blank")
        if (userRepository.existsByEmail(request.email)) throw IllegalArgumentException("Email already in use")
        val user = userRepository.findById(userId) ?: throw NoSuchElementException("User not found")
        userRepository.updateEmail(userId, request.email)
        return UserResponse(userId = user.id, username = user.username, email = request.email)
    }

    fun getAllUsers(): List<UserResponse> =
        userRepository.findAll().map { UserResponse(userId = it.id, username = it.username, email = it.email) }

    fun countUsers(): Long = userRepository.count()

    fun deleteAllUsers(): Long = userRepository.deleteAll()
}
