package org.example.configuration.graphql

import com.expediagroup.graphql.generator.execution.SimpleKotlinDataFetcherFactoryProvider
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetcherFactory
import org.example.datafetcher.PostsByAuthorIdDataFetcher
import org.example.entity.User
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.getBean
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class CustomKotlinDataFetcherFactoryProvider(
    objectMapper: ObjectMapper
) : SimpleKotlinDataFetcherFactoryProvider(objectMapper), BeanFactoryAware {

    private lateinit var beanFactory: BeanFactory

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory
    }

    override fun propertyDataFetcherFactory(kClass: KClass<*>, kProperty: KProperty<*>): DataFetcherFactory<Any?> {

        @Suppress("UNCHECKED_CAST")
        return when (kProperty) {
            User::posts -> DataFetcherFactory { beanFactory.getBean<PostsByAuthorIdDataFetcher>() as DataFetcher<Any?> }
            else -> super.propertyDataFetcherFactory(kClass, kProperty)
        }
    }
}