package xyz.fakestore.users.user

import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import xyz.fakestore.users.dto.LoginRequest
import xyz.fakestore.users.dto.LoginResponse
import xyz.fakestore.users.dto.RegisterRequest
import xyz.fakestore.users.dto.UpdateEmailRequest
import xyz.fakestore.users.dto.UserResponse
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(
        @RequestBody request: RegisterRequest,
        @RequestHeader(value = "X-Trace-Id", required = false) traceId: String?
    ): UserResponse {
        if (traceId != null) MDC.put("traceId", traceId)
        return try { userService.register(request) } finally { if (traceId != null) MDC.remove("traceId") }
    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequest,
        @RequestHeader(value = "X-Trace-Id", required = false) traceId: String?
    ): LoginResponse {
        if (traceId != null) MDC.put("traceId", traceId)
        return try { userService.login(request) } finally { if (traceId != null) MDC.remove("traceId") }
    }

    @GetMapping("/me")
    fun me(@RequestHeader(value = "X-Trace-Id", required = false) traceId: String?): UserResponse {
        if (traceId != null) MDC.put("traceId", traceId)
        return try {
            val userId = SecurityContextHolder.getContext().authentication.principal as String
            userService.getById(UUID.fromString(userId))
        } finally { if (traceId != null) MDC.remove("traceId") }
    }

    @PatchMapping("/me/email")
    fun updateEmail(
        @RequestBody request: UpdateEmailRequest,
        @RequestHeader(value = "X-Trace-Id", required = false) traceId: String?
    ): UserResponse {
        if (traceId != null) MDC.put("traceId", traceId)
        return try {
            val userId = SecurityContextHolder.getContext().authentication.principal as String
            userService.updateEmail(UUID.fromString(userId), request)
        } finally { if (traceId != null) MDC.remove("traceId") }
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> =
        ResponseEntity.badRequest().body(mapOf("error" to (ex.message ?: "Bad request")))

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<Map<String, String>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to (ex.message ?: "Not found")))
}
