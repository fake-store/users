package xyz.fakestore.users.user

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import xyz.fakestore.users.auth.JwtUtil
import xyz.fakestore.users.dto.LoginRequest
import xyz.fakestore.users.dto.LoginResponse
import xyz.fakestore.users.dto.RegisterRequest
import xyz.fakestore.users.dto.UpdateEmailRequest
import xyz.fakestore.users.dto.UserResponse
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

        val user = userRepository.save(
            User(
                email = request.email,
                username = request.username,
                passwordHash = passwordEncoder.encode(request.password)
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
        val user = userRepository.findById(userId).orElseThrow { NoSuchElementException("User not found") }
        return UserResponse(userId = user.id, username = user.username, email = user.email)
    }

    fun updateEmail(userId: UUID, request: UpdateEmailRequest): UserResponse {
        if (request.email.isBlank()) throw IllegalArgumentException("Email must not be blank")
        if (userRepository.existsByEmail(request.email)) throw IllegalArgumentException("Email already in use")
        val user = userRepository.findById(userId).orElseThrow { NoSuchElementException("User not found") }
        user.email = request.email
        userRepository.save(user)
        return UserResponse(userId = user.id, username = user.username, email = user.email)
    }
}
