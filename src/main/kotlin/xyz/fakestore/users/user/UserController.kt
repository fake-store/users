package xyz.fakestore.users.user

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import xyz.fakestore.users.dto.LoginRequest
import xyz.fakestore.users.dto.LoginResponse
import xyz.fakestore.users.dto.RegisterRequest
import xyz.fakestore.users.dto.UserResponse
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody request: RegisterRequest): UserResponse =
        userService.register(request)

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): LoginResponse =
        userService.login(request)

    @GetMapping("/me")
    fun me(): UserResponse {
        val userId = SecurityContextHolder.getContext().authentication.principal as String
        return userService.getById(UUID.fromString(userId))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> =
        ResponseEntity.badRequest().body(mapOf("error" to (ex.message ?: "Bad request")))

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to (ex.message ?: "Not found")))
}
