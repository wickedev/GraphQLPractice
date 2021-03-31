package org.example.repository

import com.expediagroup.graphql.generator.scalars.ID
import org.example.util.Identifier
import org.example.model.User
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

    fun templateFindAll(): Flux<User> {
        return entityTemplate.select(empty(), User::class.java)
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun rawSqlFindBy(id: Identifier): Mono<User?> {
        // databaseClient == entityTemplate.databaseClient
        return databaseClient.sql("""
            SELECT * FROM user WHERE id = :id
        """.trimIndent())
            .bind("id", id)
            .map { row, _ ->
                User(
                    id = row["id"] as Identifier,
                    email = row["email"] as String,
                    name = row["name"] as String
                )
            }
            .one()
    }
}
