package org.example.resolver

import com.expediagroup.graphql.server.operations.Mutation
import io.github.wickedev.coroutine.reactive.extensions.mono.await
import org.example.entity.User
import org.example.repository.UserRepository
import org.springframework.stereotype.Component

@Component
class UserMutation(
    val userRepository: UserRepository
) : Mutation {

    suspend fun createDummyUser(): User {
        return userRepository.save(
            User(
                email = "fuck",
                name = "name",
                hashSalt = "hashSlat",
                role = "ROlE_USER",
            )
        ).await()
    }
}