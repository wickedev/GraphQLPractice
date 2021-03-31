package org.example.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class GraphQLConfiguration {

    @Bean
    fun customSchemaGeneratorHooks() = CustomSchemaGeneratorHooks()
}
