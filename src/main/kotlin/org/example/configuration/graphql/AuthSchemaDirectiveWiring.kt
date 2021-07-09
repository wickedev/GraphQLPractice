package org.example.configuration.graphql

import com.expediagroup.graphql.generator.directives.KotlinFieldDirectiveEnvironment
import com.expediagroup.graphql.generator.directives.KotlinSchemaDirectiveWiring
import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import org.example.service.AuthService


class AuthSchemaDirectiveWiring(
    private val authService: AuthService,
) : KotlinSchemaDirectiveWiring {
    override fun onField(environment: KotlinFieldDirectiveEnvironment): GraphQLFieldDefinition {
        val field = environment.element
        val originalDataFetcher: DataFetcher<*> = environment.getDataFetcher()

        val fetcher = AuthDataFetcher(authService, originalDataFetcher, environment)
        environment.setDataFetcher(fetcher)

        return field
    }
}
