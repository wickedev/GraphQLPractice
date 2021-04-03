@file:Suppress("DEPRECATION")

package org.example.configuration.r2dbc

import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactoryBean
import org.springframework.data.repository.Repository
import org.springframework.data.repository.core.support.RepositoryFactorySupport
import org.springframework.r2dbc.core.DatabaseClient

class CustomR2dbcRepositoryFactoryBean<T : Repository<S, ID>, S, ID : java.io.Serializable>(
    repositoryInterface: Class<out T?>,
    private val isNewEntityStrategy: IsNewEntityStrategy?
) : R2dbcRepositoryFactoryBean<T, S, ID>(repositoryInterface) {

    override fun getFactoryInstance(
        client: DatabaseClient,
        dataAccessStrategy: ReactiveDataAccessStrategy
    ): RepositoryFactorySupport {
        return CustomSimpleR2dbcRepositoryFactory(client, dataAccessStrategy, isNewEntityStrategy)
    }

    override fun getFactoryInstance(operations: R2dbcEntityOperations): RepositoryFactorySupport {
        return CustomSimpleR2dbcRepositoryFactory(operations, isNewEntityStrategy)
    }
}