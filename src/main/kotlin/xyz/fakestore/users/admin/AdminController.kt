package xyz.fakestore.users.admin

import org.springframework.web.bind.annotation.*
import xyz.fakestore.users.dto.UserResponse
import xyz.fakestore.users.user.UserService

data class UserCountResponse(val count: Long)

@RestController
@RequestMapping("/api/admin")
class AdminController(private val userService: UserService) {

    @GetMapping("/users")
    fun listUsers(): List<UserResponse> = userService.getAllUsers()

    @GetMapping("/users/count")
    fun count(): UserCountResponse = UserCountResponse(userService.countUsers())

    @DeleteMapping("/users")
    fun deleteAll(): UserCountResponse = UserCountResponse(userService.deleteAllUsers())
}
