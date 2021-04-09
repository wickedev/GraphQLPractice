package org.example.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.configuration.graphql.CustomKotlinDataFetcherFactoryProvider
import org.example.configuration.graphql.CustomSchemaGeneratorHooks
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class GraphQLConfiguration {

    @Bean
    fun customSchemaGeneratorHooks() = CustomSchemaGeneratorHooks()

    @Bean
    fun kotlinDataFetcherFactoryProvider(objectMapper: ObjectMapper) =
        CustomKotlinDataFetcherFactoryProvider(objectMapper)
}

