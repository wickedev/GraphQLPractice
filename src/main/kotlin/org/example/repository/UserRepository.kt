package org.example.repository

import com.expediagroup.graphql.generator.scalars.ID
import org.example.configuration.convert
import org.example.entity.User
import org.example.util.Identifier
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.query.Query.empty
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface R2dbcUserRepository : ReactiveSortingRepository<User, Identifier> {
    fun findByEmail(email: String): Mono<User?>

    @Query("SELECT * FROM user WHERE id = :id")
    fun annotationFindBy(id: Identifier): Mono<User?>
}

@Repository
class UserRepository(
    repository: R2dbcUserRepository,
    private val entityTemplate: R2dbcEntityTemplate,
    private val databaseClient: DatabaseClient,
) : R2dbcUserRepository by repository {

    private val converter = entityTemplate.converter.conversionService

    fun templateFindAll(): Flux<User> {
        return entityTemplate.select(empty(), User::class.java)
    }

    fun rawSqlFindBy(id: Identifier): Mono<User?> {
        val convertedId = converter.convert(id, Long::class.java)

        // databaseClient == entityTemplate.databaseClient
        return databaseClient.sql(
            """
            SELECT * FROM user WHERE id = :id
            """.trimIndent()
        )
            .bind("id", convertedId)
            .map { row, _ ->
                User(
                    id = converter.convert(row["id"]),
                    email = converter.convert(row["email"]),
                    name = converter.convert(row["name"]),
                )
            }
            .one()
    }
}