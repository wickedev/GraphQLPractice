@file:Suppress("DEPRECATION")

package org.example.configuration.r2dbc

import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.data.relational.repository.query.RelationalEntityInformation
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.r2dbc.core.DatabaseClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.ZonedDateTime

@NoRepositoryBean
interface SoftDeleteRepository<T, ID> :
    ReactiveSortingRepository<T, ID>,
    ReactiveOrderedSortingRepository<T, ID>

@NoRepositoryBean
class CustomSoftDeleteRepository<T : Node, ID> : CustomBaseR2DbcRepository<T, ID>, SoftDeleteRepository<T, ID> {

    @Suppress("unused")
    constructor(
        entity: RelationalEntityInformation<T, ID>, entityOperations: R2dbcEntityOperations,
        converter: R2dbcConverter
    ) : super(entity, entityOperations, converter)

    @Suppress("unused")
    constructor(
        entity: RelationalEntityInformation<T, ID>, databaseClient: DatabaseClient,
        converter: R2dbcConverter, accessStrategy: ReactiveDataAccessStrategy
    ) : super(entity, databaseClient, converter, accessStrategy)


    override fun findAll(): Flux<T> {
        return entityOperations.select(Query.query(Criteria.where("deleted_at").isNull), entity.javaType)
    }

    override fun deleteById(id: ID): Mono<Void> {
        return entityOperations.update(
            getIdQuery(id),
            Update.update("deleted_at", ZonedDateTime.now()),
            entity.javaType
        ).then()
    }
}