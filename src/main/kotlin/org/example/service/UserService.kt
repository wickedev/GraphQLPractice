package org.example.service

import org.example.util.coroutine.flux.await
import org.example.util.coroutine.mono.await
import org.example.model.User
import org.example.repository.UserRepository
import org.example.resolvers.user.UserCreateInput
import org.example.resolvers.user.UserUpdateInput
import org.example.resolvers.user.UserWhereUniqueInput
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepository: UserRepository) {
    private val log = LoggerFactory.getLogger(UserService::class.java)

    @Transactional
    suspend fun user(where: UserWhereUniqueInput): User? {
        log.info("user() called with: where = $where")

        if (where.email != null && where.id != null) {
            throw Error("Please fill only one field, either id or email")
        }

        if (where.id != null) {
            return userRepository.findById(where.id).await()
        }

        if (where.email != null) {
            return userRepository.findByEmail(where.email).await()
        }

        throw Error("Either id or email field is required")
    }

    suspend fun users(): List<User> {
        log.info("users() called")

        return userRepository.findAll().await()
    }

    suspend fun createUser(data: UserCreateInput): User {
        log.info("createUser() called with: data = $data")

        return userRepository.save(User(email = data.email, name = data.name)).await()
    }

    @Transactional
    suspend fun updateUser(data: UserUpdateInput, where: UserWhereUniqueInput): User? {
        log.info("updateUser() called with: data = $data, where = $where")

        val user = this.user(where) ?: return null

        return userRepository.save(
            user.copy(
                email = if (data.email != null) data.email.set else user.email,
                name = if (data.name != null) data.name.set else user.name,
            )
        ).await()
    }

    @Transactional
    suspend fun deleteUser(where: UserWhereUniqueInput): User? {
        log.info("deleteUser() called with: where = $where")

        val user = this.user(where) ?: return null
        userRepository.deleteById(user.id).await()
        return user
    }
}