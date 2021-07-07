package org.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.Claim
import com.auth0.jwt.interfaces.DecodedJWT
import com.expediagroup.graphql.generator.scalars.ID
import org.example.configuration.AuthConfig
import org.example.entity.User
import org.example.util.Identifier
import org.example.util.toDate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*


private const val JWT_TYPE_KEY = "type"
private const val JWT_USER_ID = "userId"
private const val JWT_USER_NAME = "username"
private const val JWT_ROLE = "role"

@Service
class JwtService(
    private val algorithm: Algorithm,
    private val authConfig: AuthConfig,
) {
    enum class TokenType {
        Access,
        Refresh
    }

    data class Token(
        val value: String,
        val expiredAt: LocalDateTime
    )

    private val verifier: JWTVerifier = JWT.require(algorithm)
        .acceptLeeway(2)
        .build()

    fun isAuthenticated(jwt: DecodedJWT): Boolean {
        return try {
            verifier.verify(jwt)
            true
        } catch (e: JWTVerificationException) {
            false
        }
    }

    fun isAuthorized(requiredRole: User.Role, jwt: DecodedJWT): Boolean {
        return getRole(jwt)?.let { it >= requiredRole } ?: false
    }

    fun createToken(user: User, tokenType: TokenType): Token {
        val now = LocalDateTime.now()

        val expiredAt = if (tokenType == TokenType.Refresh)
            now.plusMonths(1)
        else
            now.plusWeeks(1)

        return Token(
            value = JWT.create()
                .withIssuedAt(Date())
                .withIssuer(authConfig.jwtIssuer)
                .withClaim(JWT_TYPE_KEY, tokenType.name)
                .withClaim(JWT_USER_ID, user.id.value)
                .withClaim(JWT_USER_NAME, user.name)
                .withClaim(JWT_ROLE, user.role.name)
                .withExpiresAt(expiredAt.toDate())
                .sign(algorithm),
            expiredAt = expiredAt
        )
    }

    fun decode(token: String): DecodedJWT? {
        return try {
            JWT.decode(token)
        } catch (e: JWTDecodeException) {
            null
        }
    }

    fun getTokenType(jwt: DecodedJWT?): TokenType? =
        jwt?.getClaim(JWT_TYPE_KEY)?.asString()?.let { TokenType.valueOf(it) }

    fun getUserId(jwt: DecodedJWT?): Identifier? = jwt?.getClaim(JWT_USER_ID)?.asIdentifier()

    fun getUsername(jwt: DecodedJWT?): String? = jwt?.getClaim(JWT_USER_NAME)?.asString()

    fun getRole(jwt: DecodedJWT?): User.Role? = jwt?.getClaim(JWT_ROLE)?.asString()?.let { User.Role.valueOf(it) }
}

private fun Claim.asIdentifier(): Identifier = ID(asString())