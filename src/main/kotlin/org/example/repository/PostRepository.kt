package org.example.repository

import org.example.configuration.r2dbc.ReactiveOrderedSortingRepository
import org.example.entity.Post
import org.example.util.Identifier
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.data.relational.core.query.Update
import org.springframework.data.relational.repository.query.RelationalEntityInformation
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import org.springframework.data.util.Lazy
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.ZonedDateTime
import kotlin.reflect.KClass


interface SoftDeleteRepository<T, ID> {
    fun findAll(): Flux<T>
    fun deleteById(id: ID): Mono<Void>
}

abstract class SoftDeleteRepositoryImpl<T : Any, ID>(
    entityType: KClass<T>,
    private val entityOperations: R2dbcEntityTemplate,
    private val converter: R2dbcConverter
) : SoftDeleteRepository<T, ID> {

    @Suppress("UNCHECKED_CAST")
    val entity: RelationalEntityInformation<T, ID> =
        MappingRelationalEntityInformation(converter.mappingContext.getRequiredPersistentEntity(entityType.java) as RelationalPersistentEntity<T>)

    private val idProperty: Lazy<RelationalPersistentProperty> = Lazy.of {
        converter
            .mappingContext
            .getRequiredPersistentEntity(entity.javaType)
            .requiredIdProperty
    }

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

    private fun getIdProperty(): RelationalPersistentProperty {
        return idProperty.get()
    }

    private fun getIdQuery(id: ID): Query {
        return Query.query(
            Criteria.where(getIdProperty().name).`is`(id as Any) //
                .and(Criteria.where("deleted_at").isNotNull)
        )
    }
}

interface CustomPostRepository<T, ID> : SoftDeleteRepository<T, ID>

@Repository
interface PostRepository :
    ReactiveOrderedSortingRepository<Post, Identifier>,
    ReactiveSortingRepository<Post, Identifier>,
    CustomPostRepository<Post, Identifier> {
    fun findByAuthorId(authorId: Identifier): Flux<Post>
}

@Repository
class CustomPostRepositoryImpl(
    entityOperations: R2dbcEntityTemplate,
    converter: R2dbcConverter
) : SoftDeleteRepositoryImpl<Post, Identifier>(Post::class, entityOperations, converter),
    CustomPostRepository<Post, Identifier>
