package xyz.fakestore.users.admin

import org.springframework.web.bind.annotation.*
import xyz.fakestore.users.user.UserService

data class UserCountResponse(val count: Long)

@RestController
@RequestMapping("/api/admin")
class AdminController(private val userService: UserService) {

    @GetMapping("/users/count")
    fun count(): UserCountResponse = UserCountResponse(userService.countUsers())

    @DeleteMapping("/users")
    fun deleteAll(): UserCountResponse = UserCountResponse(userService.deleteAllUsers())
}
