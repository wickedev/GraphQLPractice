package org.example.configuration

import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import com.fasterxml.jackson.databind.ObjectMapper
import org.example.configuration.graphql.*
import org.example.service.AuthService
import org.example.service.JwtService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*


@Configuration
class GraphQLConfiguration {

    @Bean
    fun kotlinDataFetcherFactoryProvider(objectMapper: ObjectMapper) =
        CustomKotlinDataFetcherFactoryProvider(objectMapper)

    @Bean
    fun dataFetcherExceptionHandler() = CustomDataFetcherExceptionHandler()

    @Bean
    fun authSchemaDirectiveWiring(authService: AuthService) = AuthSchemaDirectiveWiring(authService)

    @Bean
    fun directiveWiringFactory(authSchemaDirectiveWiring: AuthSchemaDirectiveWiring) =
        CustomDirectiveWiringFactory(authSchemaDirectiveWiring)

    @Bean
    fun graphQLContextFactory(jwtService: JwtService) = GraphQLCustomContextFactory(jwtService)

    @Bean
    fun customSchemaGeneratorHooks(resolvers: Optional<List<FederatedTypeResolver<*>>>) =
        CustomSchemaGeneratorHooks(resolvers.orElse(emptyList()))
}

