package org.example.configuration.graphql

import com.auth0.jwt.interfaces.DecodedJWT
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContext
import com.expediagroup.graphql.server.spring.execution.SpringGraphQLContextFactory
import org.example.service.JwtService
import org.example.util.token
import org.springframework.http.ResponseCookie
import org.springframework.web.reactive.function.server.ServerRequest
import java.time.Duration
import java.time.LocalDateTime


data class GraphQLCustomContext(
    val request: ServerRequest,
    val jwt: DecodedJWT?
) : SpringGraphQLContext(request) {
    fun setRefreshToken(refreshToken: JwtService.Token) {
        val now = LocalDateTime.now()
        val duration = Duration.between(now, refreshToken.expiredAt)
        val maxAge = duration.toSeconds()

        request.exchange().response.cookies["refresh"] =
            ResponseCookie.from("token", refreshToken.value)
                .httpOnly(true)
                .secure(true)
                .maxAge(maxAge)
                .build()
    }
}

class GraphQLCustomContextFactory(private val jwtService: JwtService) :
    SpringGraphQLContextFactory<GraphQLCustomContext>() {
    override suspend fun generateContext(request: ServerRequest): GraphQLCustomContext {
        return GraphQLCustomContext(
            request = request,
            jwt = request.token?.let { jwtService.decode(it) }
        )
    }
}
