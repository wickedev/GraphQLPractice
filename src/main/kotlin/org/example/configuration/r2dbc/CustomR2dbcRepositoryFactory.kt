@file:Suppress("DEPRECATION")

package org.example.configuration.r2dbc

import org.example.configuration.repository.common.InvalidRepositoryImplementationException
import org.example.configuration.repository.common.SimpleGraphQLRelayNodeRepository
import org.example.configuration.repository.common.isAssignableFrom
import org.example.configuration.repository.interfaces.GraphQLRelayNodeRepository
import org.example.configuration.repository.interfaces.SoftDeleteRepository
import org.example.configuration.repository.simple.SimpleR2DbcRepository
import org.example.configuration.repository.softdelete.SoftDeleteR2DbcRepository
import org.springframework.core.NestedRuntimeException
import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty
import org.springframework.data.relational.repository.query.RelationalEntityInformation
import org.springframework.data.repository.Repository
import org.springframework.data.repository.core.RepositoryInformation
import org.springframework.data.repository.core.RepositoryMetadata
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor
import org.springframework.r2dbc.core.DatabaseClient
import kotlin.reflect.KClass

typealias MappingContext = org.springframework.data.mapping.context.MappingContext<
    out RelationalPersistentEntity<*>?,
    out RelationalPersistentProperty?
    >

class CustomSimpleR2dbcRepositoryFactory : R2dbcRepositoryFactory {
    private val mappingContext: MappingContext
    private val converter: R2dbcConverter
    private val operations: R2dbcEntityOperations
    private val additionalIsNewStrategy: AdditionalIsNewStrategy?

    constructor(
        databaseClient: DatabaseClient,
        dataAccessStrategy: ReactiveDataAccessStrategy,
        additionalIsNewStrategy: AdditionalIsNewStrategy?,
    ) : super(
        databaseClient,
        dataAccessStrategy
    ) {
        this.converter = dataAccessStrategy.converter
        this.mappingContext = converter.mappingContext
        this.operations = R2dbcEntityTemplate(databaseClient, dataAccessStrategy)
        this.additionalIsNewStrategy = additionalIsNewStrategy
    }

    constructor(
        operations: R2dbcEntityOperations,
        additionalIsNewStrategy: AdditionalIsNewStrategy?
    ) : super(operations) {
        val dataAccessStrategy = operations.dataAccessStrategy
        this.converter = dataAccessStrategy.converter
        this.mappingContext = converter.mappingContext
        this.operations = operations
        this.additionalIsNewStrategy = additionalIsNewStrategy
    }

    override fun <T : Any?, ID : Any?> getEntityInformation(domainClass: Class<T>): RelationalEntityInformation<T, ID> {
        return getEntityInformation(domainClass, null)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun <T, ID> getEntityInformation(
        domainClass: Class<T>,
        information: RepositoryInformation?
    ): RelationalEntityInformation<T, ID> {
        val entity = mappingContext.getRequiredPersistentEntity(domainClass)
        @Suppress("UNCHECKED_CAST")
        return CustomMappingRelationalEntityInformation(
            entity as RelationalPersistentEntity<T>,
            additionalIsNewStrategy
        )
    }

    override fun getTargetRepository(information: RepositoryInformation): Any? {
        val entityInformation: RelationalEntityInformation<*, Any> = getEntityInformation(
            information.domainType,
            information
        )
        return getTargetRepositoryViaReflection(
            information, entityInformation,
            operations, converter
        )
    }

    override fun getRepositoryBaseClass(metadata: RepositoryMetadata): Class<*> {
        val repositoryInterface = metadata.repositoryInterface
        val isGraphQLRelayNodeRepository =
            GraphQLRelayNodeRepository::class.isAssignableFrom(repositoryInterface)
        val isEntityRepository = Repository::class.isAssignableFrom(repositoryInterface)
        val isSoftDeleteRepository = SoftDeleteRepository::class.isAssignableFrom(repositoryInterface)
        val isQueryByExample = ReactiveQueryByExampleExecutor::class.isAssignableFrom(repositoryInterface)

        if (isSoftDeleteRepository and isQueryByExample) {
            val msg = "SoftDeleteRepository and ReactiveQueryByExampleExecutor cannot both be implemented"
            throw InvalidRepositoryImplementationException(msg)
        }

        return if (isGraphQLRelayNodeRepository and !isEntityRepository)
            SimpleGraphQLRelayNodeRepository::class.java
        else if (isSoftDeleteRepository)
            SoftDeleteR2DbcRepository::class.java
        else
            SimpleR2DbcRepository::class.java
    }
}
