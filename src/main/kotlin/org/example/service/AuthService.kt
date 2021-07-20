package org.example.service

import com.auth0.jwt.interfaces.DecodedJWT
import org.example.configuration.graphql.AuthenticationError
import org.example.entity.User
import org.example.repository.UserRepository
import org.example.util.coroutine.mono.await
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    class DuplicateNameException : Exception("DUPLICATE_NAME")

    data class LoginResult(
        val accessToken: JwtService.Token,
        val refreshToken: JwtService.Token,
    )

    suspend fun signUp(email: String, name: String, password: String): User {
        val emailExist = userRepository.existsByEmail(email).await()

        if (emailExist) {
            throw DuplicateNameException()
        }

        val nameExist = userRepository.existsByName(name).await()

        if (nameExist) {
            throw DuplicateNameException()
        }

        val user = User(
            email = email,
            name = name,
            hashSalt = passwordEncoder.encode(password),
            role = User.Role.USER
        )

        return userRepository.save(user).await()
    }

    suspend fun login(email: String, password: String): LoginResult? {
        val user = userRepository.findByEmail(email).await()
            ?: return null

        return LoginResult(
            accessToken = jwtService.createToken(user, JwtService.TokenType.Access),
            refreshToken = jwtService.createToken(user, JwtService.TokenType.Refresh)
        )
    }

    suspend fun refresh(jwt: DecodedJWT?): LoginResult? {
        val user: User = jwtService.getUserId(jwt)?.let {
            userRepository.findById(it).await()
        } ?: return null

        return LoginResult(
            accessToken = jwtService.createToken(user, JwtService.TokenType.Access),
            refreshToken = jwtService.createToken(user, JwtService.TokenType.Refresh)
        )
    }

    fun isAuthenticated(jwt: DecodedJWT?): Boolean {
        return jwt?.let { jwtService.isAuthenticated(it) } ?: false
    }

    fun isAuthorized(requiredRole: User.Role, jwt: DecodedJWT?): Boolean {
        return jwt?.let { jwtService.isAuthorized(requiredRole, it) } ?: false
    }
}
