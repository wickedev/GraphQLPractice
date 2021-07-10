package org.example.mutation

import com.expediagroup.graphql.server.operations.Mutation
import org.example.configuration.graphql.HasRole
import org.example.entity.User
import org.example.input.UserCreateInput
import org.example.input.UserUpdateInput
import org.example.input.UserWhereUniqueInput
import org.example.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UserMutation(
    val userService: UserService
) : Mutation {
    private val log = LoggerFactory.getLogger(UserMutation::class.java)

    @HasRole(role = User.Role.ADMIN)
    suspend fun createUser(data: UserCreateInput): User {
        log.info("createUser() called with: data = $data")
        return userService.createUser(data)
    }

    @HasRole(role = User.Role.USER)
    suspend fun updateUser(data: UserUpdateInput, where: UserWhereUniqueInput): User? {
        log.info("updateUpdate() called with: data = $data, where = $where")
        return userService.updateUser(data, where)
    }

    @HasRole(role = User.Role.ADMIN)
    suspend fun deleteUser(where: UserWhereUniqueInput): User? {
        log.info("deleteUser() called with: where = $where")
        return userService.deleteUser(where)
    }
}