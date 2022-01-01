package org.example.resolver

import com.expediagroup.graphql.server.operations.Mutation
import io.github.wickedev.coroutine.reactive.extensions.mono.await
import io.github.wickedev.graphql.Auth
import io.github.wickedev.graphql.types.ID
import org.example.entity.User
import org.example.input.UserCreateInput
import org.example.input.UserUpdateInput
import org.example.input.UserWhereUniqueInput
import org.example.repository.UserRepository
import org.slf4j.Logger
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Criteria.where
import org.springframework.data.relational.core.query.Update
import org.springframework.data.relational.core.sql.SqlIdentifier.quoted
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserMutation(
    private val log: Logger,
    private val userRepository: UserRepository
) : Mutation {

    suspend fun createUser(data: UserCreateInput): User {
        log.info("createUser() called with: data = $data")

        return userRepository.save(User(
            email = data.email,
            hashSalt = data.password,
            name = data.name,
            roles = listOf("ROLE_USER")
        )).await()
    }

    @Transactional
    @Auth(requires = ["ROLE_USER"])
    suspend fun updateUser(where: UserWhereUniqueInput, data: UserUpdateInput): User? {
        log.info("updateUser() called with: where = $where, data = $data")

        val criteria = Criteria.from(
            listOfNotNull(
                where.id?.let { where("id").`is`(it) },
                where.email?.let { where("email").`is`(it) },
            )
        )

        val update = Update.from(
            buildMap {
                if (data.email != null) {
                    put(quoted("email"), data.email.set)
                }
                if (data.name != null) {
                    put(quoted("name"), data.name.set)
                }
            }
        )

        userRepository.update(criteria, update).await()

        return userRepository.findBy(criteria).await()
    }

    @Auth(requires = ["ROLE_ADMIN"])
    suspend fun deleteUser(where: UserWhereUniqueInput): ID? {
        log.info("deleteUser() called with: where = $where")

        val criteria = Criteria.from(
            listOfNotNull(
                where.id?.let { where("id").`is`(it) },
                where.email?.let { where("email").`is`(it) },
            )
        )

        userRepository.delete(criteria).await()

        return where.id
    }
}