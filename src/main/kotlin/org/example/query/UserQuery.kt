package org.example.query

import com.expediagroup.graphql.server.operations.Query
import graphql.schema.DataFetchingEnvironment
import org.example.configuration.graphql.IsAuthenticated
import org.example.entity.User
import org.example.input.UserWhereUniqueInput
import org.example.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UserQuery(
    val userService: UserService
) : Query {
    private val log = LoggerFactory.getLogger(UserQuery::class.java)

    @IsAuthenticated
    suspend fun user(where: UserWhereUniqueInput): User? {
        log.info("user() called with: where = $where")
        return userService.user(where)
    }

    @IsAuthenticated
    suspend fun users(environment: DataFetchingEnvironment): List<User> {
        log.info("users() called")
        return userService.users()
    }
}