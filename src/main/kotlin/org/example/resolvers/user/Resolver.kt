@file:Suppress("unused")

package org.example.resolvers.user

import com.expediagroup.graphql.server.operations.Mutation
import com.expediagroup.graphql.server.operations.Query
import com.expediagroup.graphql.server.operations.Subscription
import org.example.channel.UserCreatedChannel
import org.example.model.User
import org.example.service.UserService
import org.example.util.asFlux
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux

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
class UserSubscription(private val userCreatedChannel: UserCreatedChannel) : Subscription {
    private val log = LoggerFactory.getLogger(UserSubscription::class.java)

    fun users(): Flux<User> {
        log.info("users() called")
        return userCreatedChannel.asFlux()
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
