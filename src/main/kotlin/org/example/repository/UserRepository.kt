package org.example.repository

import io.github.wickedev.graphql.spring.data.r2dbc.repository.interfaces.GraphQLR2dbcRepository
import org.example.entity.User
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono


@Repository
interface UserRepository : GraphQLR2dbcRepository<User>, CustomUserRepository

interface CustomUserRepository {
    fun findBy(criteria: Criteria): Mono<User?>

    fun update(criteria: Criteria, update: Update): Mono<Int>

    fun delete(criteria: Criteria): Mono<Int>
}

@Repository
class UserRepositoryImpl(
    private val entityTemplate: R2dbcEntityTemplate
) : CustomUserRepository {
    override fun findBy(criteria: Criteria): Mono<User?> {
        return entityTemplate.select(Query.query(criteria).limit(1), User::class.java)
            .singleOrEmpty()
    }

    override fun update(criteria: Criteria, update: Update): Mono<Int> {
        return entityTemplate.update(Query.query(criteria), update, User::class.java)
    }

    override fun delete(criteria: Criteria): Mono<Int> {
        return entityTemplate.delete(Query.query(criteria), User::class.java)
    }
}