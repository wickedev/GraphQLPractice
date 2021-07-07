package org.example.configuration.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.directives.KotlinFieldDirectiveEnvironment
import com.expediagroup.graphql.generator.directives.KotlinSchemaDirectiveWiring
import graphql.introspection.Introspection
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import org.example.entity.User
import org.example.service.AuthService


@GraphQLDirective(
    name = "isAuthenticated",
    locations = [Introspection.DirectiveLocation.FIELD, Introspection.DirectiveLocation.FIELD_DEFINITION]
)
annotation class IsAuthenticated

@GraphQLDirective(
    name = "hasRole",
    locations = [Introspection.DirectiveLocation.FIELD, Introspection.DirectiveLocation.FIELD_DEFINITION]
)
annotation class HasRole(val role: User.Role)

class AuthDataFetcher(
    private val authService: AuthService,
    private val originalDataFetcher: DataFetcher<*>,
    private val directiveEnvironment: KotlinFieldDirectiveEnvironment
) : DataFetcher<Any> {
    override fun get(environment: DataFetchingEnvironment?): Any {
        if (environment != null) {
            val context = environment.getContext<GraphQLCustomContext>()

            if (directiveEnvironment.directive.name == "isAuthenticated" && !authService.isAuthenticated(context.jwt)) {
                throw AuthenticationError()
            }

            val hasRoleDirective = directiveEnvironment.directive.name == "hasRole"
            val requiredRole = if (hasRoleDirective) directiveEnvironment.directive.getArgument("role").value else null

            if (requiredRole is User.Role && !authService.isAuthorized(requiredRole, context.jwt)) {
                throw ForbiddenError()
            }
        }

        return originalDataFetcher.get(environment)
    }
}


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

class CustomDirectiveWiringFactory(
    authSchemaDirectiveWiring: AuthSchemaDirectiveWiring
) : KotlinDirectiveWiringFactory(
    manualWiring = mapOf<String, KotlinSchemaDirectiveWiring>(
        "isAuthenticated" to authSchemaDirectiveWiring,
        "hasRole" to authSchemaDirectiveWiring
    )
)
