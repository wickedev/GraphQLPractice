@file:Suppress("DEPRECATION")

package org.example.configuration.r2dbc

import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.getBean
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy
import org.springframework.data.r2dbc.repository.support.R2dbcRepositoryFactoryBean
import org.springframework.data.repository.Repository
import org.springframework.data.repository.core.support.RepositoryFactorySupport
import org.springframework.r2dbc.core.DatabaseClient
import java.util.*

class CustomR2dbcRepositoryFactoryBean<T : Repository<S, ID>, S, ID : java.io.Serializable>(
    repositoryInterface: Class<out T?>,
) : R2dbcRepositoryFactoryBean<T, S, ID>(repositoryInterface), BeanFactoryAware {

    private lateinit var beanFactory: BeanFactory

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    private val additionalIsNewStrategy: AdditionalIsNewStrategy by lazy { beanFactory.getBean() }

    override fun getFactoryInstance(
        client: DatabaseClient,
        dataAccessStrategy: ReactiveDataAccessStrategy
    ): RepositoryFactorySupport {
        return CustomSimpleR2dbcRepositoryFactory(client, dataAccessStrategy, additionalIsNewStrategy)
    }

    override fun getFactoryInstance(operations: R2dbcEntityOperations): RepositoryFactorySupport {
        return CustomSimpleR2dbcRepositoryFactory(operations, additionalIsNewStrategy)
    }
}