package org.example.mutation

import com.expediagroup.graphql.server.operations.Mutation
import graphql.schema.DataFetchingEnvironment
import org.example.configuration.graphql.GraphQLCustomContext
import org.example.entity.User
import org.example.service.AuthService
import org.springframework.stereotype.Component

@Component
class AuthMutation(private val authService: AuthService) : Mutation {

    suspend fun signUp(email: String, name: String, password: String): User {
        return authService.signUp(email, name, password)
    }

    suspend fun login(env: DataFetchingEnvironment, email: String, password: String): AuthService.LoginResult? {
        val result = authService.login(email, password)

        if (result != null) {
            env.getContext<GraphQLCustomContext>().setRefreshToken(result.refreshToken)
        }

        return result
    }

    suspend fun refresh(env: DataFetchingEnvironment): AuthService.LoginResult? {
        val result = authService.refresh(env.getContext<GraphQLCustomContext>().accessJWT)

        if (result != null) {
            env.getContext<GraphQLCustomContext>().setRefreshToken(result.refreshToken)
        }

        return result
    }
}