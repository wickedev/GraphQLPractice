@file:Suppress("unused")

package org.example.resolvers.user

import com.expediagroup.graphql.spring.operations.Mutation
import com.expediagroup.graphql.spring.operations.Query
import org.example.model.User
import org.example.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UserQuery(
    val userService: UserService
) : Query {
    private val log = LoggerFactory.getLogger(UserQuery::class.java)

    suspend fun user(where: UserWhereUniqueInput): User? {
        log.info("user() called with: where = $where")
        return userService.user(where)
    }

    suspend fun users(): List<User> {
        log.info("users() called")
        return userService.users()
    }
}

@Component
class UserMutation(
    val userService: UserService
) : Mutation {
    private val log = LoggerFactory.getLogger(UserMutation::class.java)

    suspend fun createUser(data: UserCreateInput): User {
        log.info("createUser() called with: data = $data")
        return userService.createUser(data)
    }

    suspend fun updateUser(data: UserUpdateInput, where: UserWhereUniqueInput): User? {
        log.info("updateUpdate() called with: data = $data, where = $where")
        return userService.updateUser(data, where)
    }

    suspend fun deleteUser(where: UserWhereUniqueInput): User? {
        log.info("deleteUser() called with: where = $where")
        return userService.deleteUser(where)
    }
}
