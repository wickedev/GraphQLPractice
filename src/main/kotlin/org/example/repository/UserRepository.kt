package org.example.repository

import org.example.configuration.r2dbc.ReactiveOrderedSortingRepository
import org.example.entity.User
import org.example.util.Identifier
import org.example.util.writeValue
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.relational.core.query.Query.empty
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CustomUserRepository {
    fun templateFindAll(): Flux<User>

    fun rawSqlFindBy(id: Identifier): Mono<User?>
}

@Repository
interface UserRepository : ReactiveOrderedSortingRepository<User, Identifier>, CustomUserRepository {
    fun findByEmail(email: String): Mono<User?>

    fun existsByEmail(email: String): Mono<Boolean>

    fun existsByName(name: String): Mono<Boolean>

    @Query("""SELECT * FROM user WHERE id = :id""")
    fun annotationFindBy(id: Identifier): Mono<User?>


}

@Repository
class UserRepositoryImpl(
    private val entityTemplate: R2dbcEntityTemplate,
    private val databaseClient: DatabaseClient,
    private val converter: MappingR2dbcConverter,
) : CustomUserRepository {

    override fun templateFindAll(): Flux<User> {
        return entityTemplate.select(empty(), User::class.java)
    }

    override fun rawSqlFindBy(id: Identifier): Mono<User?> {
        return databaseClient.sql("SELECT * FROM user WHERE id = :id")
            .bind("id", converter.writeValue(id))
            .map { row, meta -> converter.read(User::class.java, row, meta) }
            .one()
    }
}

