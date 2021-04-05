@file:Suppress("DEPRECATION")

package org.example.configuration.r2dbc

import org.springframework.data.r2dbc.convert.R2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactory
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty
import org.springframework.data.relational.repository.query.RelationalEntityInformation
import org.springframework.data.repository.core.RepositoryInformation
import org.springframework.r2dbc.core.DatabaseClient

typealias MappingContext = org.springframework.data.mapping.context.MappingContext<
    out RelationalPersistentEntity<*>?,
    out RelationalPersistentProperty?
    >

class CustomSimpleR2dbcRepositoryFactory : R2dbcRepositoryFactory {
    private var mappingContext: MappingContext? = null
    private var converter: R2dbcConverter? = null
    private var operations: R2dbcEntityOperations? = null
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
        this.mappingContext = converter?.mappingContext
        this.operations = R2dbcEntityTemplate(databaseClient, dataAccessStrategy)
        this.additionalIsNewStrategy = additionalIsNewStrategy
    }

    constructor(
        operations: R2dbcEntityOperations,
        additionalIsNewStrategy: AdditionalIsNewStrategy?
    ) : super(operations) {
        val dataAccessStrategy = operations.dataAccessStrategy
        this.converter = dataAccessStrategy.converter
        this.mappingContext = converter?.mappingContext
        this.operations = operations
        this.additionalIsNewStrategy = additionalIsNewStrategy
    }

    override fun <T : Any?, ID : Any?> getEntityInformation(domainClass: Class<T>): RelationalEntityInformation<T, ID> {
        return getEntityInformation(domainClass, null)
    }

    private fun <T, ID> getEntityInformation(
        domainClass: Class<T>,
        information: RepositoryInformation?
    ): RelationalEntityInformation<T, ID> {
        val entity = mappingContext?.getRequiredPersistentEntity(domainClass)
        return CustomMappingRelationalEntityInformation(entity as RelationalPersistentEntity<T>,additionalIsNewStrategy)
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
}

